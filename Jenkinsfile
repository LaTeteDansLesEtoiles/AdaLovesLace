// AdaLovesLace — product-oriented CI (see src/main/resources/doc/ci-pipeline.md, src/main/resources/doc/MIGRATION_CI_PIPELINE.md)
//
// CI mode policy:
// - auto (default):
//   - nightly timer trigger -> nightly (full tests + OWASP + scaffolds)
//   - push on develop/main -> release (nightly gates + packaging + smoke/checksums)
//   - any other push -> pr (fast feedback gates)
// - pr/nightly/release can still be selected manually as an explicit override.
pipeline {
    agent any

    options {
        timestamps()
    }

    triggers {
        // Nightly full validation.
        cron('H 2 * * *')
    }

    parameters {
        choice(
            name: 'PIPELINE_MODE',
            choices: ['auto', 'pr', 'nightly', 'release'],
            description: 'Default auto mode: push -> pr, nightly timer -> nightly, push on develop/main -> release. Use pr/nightly/release only for manual override.'
        )
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-25-openjdk-amd64/'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Select pipeline mode') {
            steps {
                script {
                    // Sandbox-safe trigger detection (no getRawBuild/script approval required).
                    // BUILD_CAUSE_TIMERTRIGGER is exported on cron-triggered builds; keep a fallback
                    // for installations that only expose BUILD_CAUSE.
                    def isNightlyTrigger =
                        (env.BUILD_CAUSE_TIMERTRIGGER ?: 'false').toBoolean() ||
                        ((env.BUILD_CAUSE ?: '').contains('TIMERTRIGGER'))
                    def branch = env.BRANCH_NAME ?: ''
                    def isReleaseBranchPush = !isNightlyTrigger && (branch == 'develop' || branch == 'main')

                    // Keep backward compatibility: explicit parameter wins.
                    if (params.PIPELINE_MODE in ['pr', 'nightly', 'release']) {
                        env.CI_MODE = params.PIPELINE_MODE
                    } else if (isNightlyTrigger) {
                        env.CI_MODE = 'nightly'
                    } else if (isReleaseBranchPush) {
                        env.CI_MODE = 'release'
                    } else {
                        env.CI_MODE = 'nightly'
                    }

                    echo "Resolved CI mode: ${env.CI_MODE} (branch=${branch}, nightlyTrigger=${isNightlyTrigger})"
                }
            }
        }

        stage('Toolchain') {
            steps {
                sh """
                    set -eux
                    echo "PIPELINE_MODE=${env.CI_MODE}"
                    echo "JAVA_HOME=\${JAVA_HOME}"
                    command -v java
                    command -v javac
                    java -version
                    javac -version
                """
            }
        }

        stage('Compile') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -batch-mode -q clean compile test-compile'
            }
        }

        stage('Unit tests') {
            steps {
                sh './mvnw -batch-mode test -P unit-tests'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration and functional tests') {
            when {
                anyOf {
                    expression { env.CI_MODE == 'nightly' }
                    expression { env.CI_MODE == 'release' }
                }
            }
            steps {
                wrap([$class: 'Xvfb', screen: '3840x2160x24', timeout: 25]) {
                    sh '''
                        ./mvnw -batch-mode integration-test -P ci-all-it \
                          -DSLEEP_TIME=1000 -DGRID_PIXEL_ASSERT_WAIT_MS=30000 -DWAIT_TIME=15000
                    '''
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
                }
            }
        }

        stage('Quality gate (verify)') {
            steps {
                script {
                    if (env.CI_MODE == 'pr') {
                        sh './mvnw -batch-mode -e verify -P ci-pr -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
                    } else {
                        sh './mvnw -batch-mode -e verify -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
                    }
                }
            }
            post {
                always {
                    // HTML/XML from the same accumulated .exec that jacoco:check used during verify (nightly/release).
                    // Includes unit + integration + functional coverage: Failsafe uses @{argLine} with JaCoCo.
                    sh './mvnw -batch-mode -q jacoco:report || true'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/site/jacoco/**/*', fingerprint: true
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/coverage-reports/*.exec', fingerprint: true
                }
            }
        }

        stage('Static analysis (Warnings NG)') {
            steps {
                script {
                    def cpd = scanForIssues(tool: cpd(pattern: '**/target/cpd*.xml'))
                    def pmd = scanForIssues(tool: pmdParser(pattern: '**/target/pmd*.xml'))
                    def spot = scanForIssues(tool: spotBugs(pattern: '**/target/spotbugsXml.xml', useRankAsPriority: true))
                    publishIssues(issues: [cpd, pmd, spot])
                }
            }
        }

        stage('OWASP dependency-check') {
            when {
                anyOf {
                    expression { env.CI_MODE == 'nightly' }
                    expression { env.CI_MODE == 'release' }
                }
            }
            steps {
                script {
                    withCredentials([string(credentialsId: 'NVD_API_KEY', variable: 'NVD_API_KEY', optional: true)]) {
                        sh '''
                            set -eu
                            if [ -n "${NVD_API_KEY:-}" ]; then
                              ./mvnw -batch-mode -V -U -e -DskipTests -Dnvd.api.key="$NVD_API_KEY" dependency-check:check
                            else
                              ./mvnw -batch-mode -V -U -e -DskipTests dependency-check:check
                            fi
                        '''
                    }
                }
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/dependency-check-report.*', fingerprint: true
                    script {
                        def dc = scanForIssues(tool: owaspDependencyCheck(pattern: '**/target/dependency-check-report.xml'))
                        publishIssues(issues: [dc])
                    }
                }
            }
        }

        stage('Dependency and plugin freshness (informational)') {
            when {
                expression { env.CI_MODE == 'nightly' }
            }
            steps {
                sh '''
                    mkdir -p target
                    ./mvnw -batch-mode -q org.codehaus.mojo:versions-maven-plugin:2.17.1:display-dependency-updates \
                      org.codehaus.mojo:versions-maven-plugin:2.17.1:display-plugin-updates > target/ci-freshness.txt || true
                '''
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/ci-freshness.txt', fingerprint: true
                }
            }
        }

        stage('Visual regression scaffold') {
            when {
                expression { env.CI_MODE == 'nightly' }
            }
            steps {
                sh 'bash scripts/ci/visual-regression.sh'
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/ci/visual-regression/**', fingerprint: true
                }
            }
        }

        stage('Performance sanity scaffold') {
            when {
                expression { env.CI_MODE == 'nightly' }
            }
            steps {
                sh 'bash scripts/ci/performance-sanity.sh'
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/ci/performance/**', fingerprint: true
                }
            }
        }

        stage('Linux packaging') {
            when {
                expression { env.CI_MODE == 'release' }
            }
            steps {
                sh './mvnw -batch-mode package -P linux -P ci-release -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
            }
        }

        stage('Packaged artifact smoke') {
            when {
                expression { env.CI_MODE == 'release' }
            }
            steps {
                sh 'bash scripts/ci/verify-packaged-artifacts.sh'
                sh 'bash scripts/ci/release-checksums.sh'
            }
            post {
                always {
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/artifacts/*.deb,**/target/artifacts/*.rpm,**/target/artifacts/SHA256SUMS', fingerprint: true
                }
            }
        }
    }
}

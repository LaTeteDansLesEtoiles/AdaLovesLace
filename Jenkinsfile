// AdaLovesLace — product-oriented CI (see src/main/resources/doc/ci-pipeline.md, src/main/resources/doc/MIGRATION_CI_PIPELINE.md)
pipeline {
    agent any

    options {
        timestamps()
    }

    parameters {
        choice(
            name: 'PIPELINE_MODE',
            choices: ['pr', 'nightly', 'release'],
            description: 'pr: fast gates; nightly: full tests + OWASP + scaffolds; release: nightly + Linux packages + checksums + smoke'
        )
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/temurin-24-jdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Toolchain') {
            steps {
                sh """
                    set -eux
                    echo "PIPELINE_MODE=${params.PIPELINE_MODE}"
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
                    expression { params.PIPELINE_MODE == 'nightly' }
                    expression { params.PIPELINE_MODE == 'release' }
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
                    if (params.PIPELINE_MODE == 'pr') {
                        sh './mvnw -batch-mode -e verify -P ci-pr -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
                    } else {
                        sh './mvnw -batch-mode -e verify -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
                    }
                }
            }
            post {
                always {
                    sh './mvnw -batch-mode -q -DskipTests jacoco:report || true'
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
                    expression { params.PIPELINE_MODE == 'nightly' }
                    expression { params.PIPELINE_MODE == 'release' }
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
                expression { params.PIPELINE_MODE == 'nightly' }
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
                expression { params.PIPELINE_MODE == 'nightly' }
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
                expression { params.PIPELINE_MODE == 'nightly' }
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
                expression { params.PIPELINE_MODE == 'release' }
            }
            steps {
                sh './mvnw -batch-mode package -P linux -P ci-release -DskipTests=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true'
            }
        }

        stage('Packaged artifact smoke') {
            when {
                expression { params.PIPELINE_MODE == 'release' }
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

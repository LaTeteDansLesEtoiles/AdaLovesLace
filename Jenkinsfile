#!/usr/bin/env groovy

node {
    environment {
        JAVA_HOME = '/usr/lib/jvm/temurin-24-jdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        NVD_API_KEY = credentials('NVD_API_KEY')   // id from Credentials store, not the API key string
    }
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh '''
          set -eux
          echo "JAVA_HOME=$JAVA_HOME"
          which java
          which javac
          java -version
          javac -version
        '''
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    stage('unit tests') {
        try {
            sh "./mvnw clean test -P unit-tests"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/*.xml'
        }
    }

    stage('integration tests') {
        try {
            sh "./mvnw test -P integration-tests"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/*.xml'
        }
    }

    wrap([$class: 'Xvfb', screen: '3840x2160x24', timeout: 25]) {

        stage('functional tests') {
          try {
            sh "./mvnw -P functional-tests test -P linux -DSLEEP_TIME=1000 -DGRID_PIXEL_ASSERT_WAIT_MS=30000 -DWAIT_TIME=15000"
            } catch(err) {
            throw err
          } finally {
            junit '**/target/failsafe-reports/TEST-*.xml'
          }
        }

        // Do not use step([$class: 'JacocoPublisher', ...]) — it needs the optional "JaCoCo" Jenkins plugin.
        // Without that plugin you get: UnsupportedOperationException: ... SimpleBuildStep is named JacocoPublisher
        stage('test coverage') {
            sh './mvnw -DskipTests jacoco:report'
            archiveArtifacts artifacts: 'target/site/jacoco/**/*', fingerprint: true, allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/coverage-reports/*.exec', fingerprint: true, allowEmptyArchive: true
        }

        stage('static code analysis') {
            sh './mvnw -batch-mode -V -U -e pmd:cpd pmd:check spotbugs:spotbugs'

            def cpd_report =        scanForIssues(
                tool:
                    cpd         (pattern: '**/target/cpd*.xml')
            )

            publishIssues(
                issues: [cpd_report]
            )

            def java_report =       scanForIssues(
                tool:
                    java        (pattern: 'target/*classes/**/*.class')
            )

            publishIssues(
                issues: [java_report]
            )

            def maven_report =      scanForIssues(
                tool:
                    mavenConsole()
            )

            publishIssues(
                issues: [maven_report]
            )

            def pmd_report =        scanForIssues(
                tool:
                    pmdParser   (pattern: '**/target/pmd*.xml')
            )

            publishIssues(
                issues: [pmd_report]        )

            def spotBugs_report =   scanForIssues(
                tool:
                    spotBugs    (pattern: '**/target/spotbugsXml.xml', useRankAsPriority: true)
            )

            publishIssues(
                issues: [spotBugs_report]
            )
        }
    }

    // OWASP Dependency-Check: use Maven plugin (pom.xml) — no Jenkins "Dependency-Check" plugin required.
    // Bind a "Secret text" credential to env NVD_API_KEY (e.g. in job config or withCredentials) so the key is masked in logs.
    stage ('OWASP Check') {
        sh '''
          set -eu
          if [ -n "${NVD_API_KEY:-}" ]; then
            ./mvnw -batch-mode -V -U -e -DskipTests -Dnvd.api.key="$NVD_API_KEY" dependency-check:check
          else
            ./mvnw -batch-mode -V -U -e -DskipTests dependency-check:check
          fi
        '''
        archiveArtifacts artifacts: 'target/dependency-check-report.*', fingerprint: true, allowEmptyArchive: true

        def dc_report = scanForIssues(
            tool: owaspDependencyCheck(pattern: '**/target/dependency-check-report.xml')
        )
        publishIssues(issues: [dc_report])
    }

    stage('packaging') {
        // OWASP already ran in dedicated stage; skip second run on verify
        sh "./mvnw package -P linux -DskipUTs=true -DskipITs=true -DskipFTs=true -Ddependency-check.skip=true"
        // AppImage disabled in pom (JavaPackager’s appimagetool download URL is 404); keep pattern optional for local builds
        archiveArtifacts artifacts: '**/target/artifacts/*.deb,**/target/artifacts/*.rpm', fingerprint: true, allowEmptyArchive: true
    }

}

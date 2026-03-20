#!/usr/bin/env groovy

node {
    environment {
        JAVA_HOME = '/usr/lib/jvm/temurin-24-jdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
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
            sh "./mvnw -P functional-tests test -P linux -Dtestfx.launch.timeout=5000 -Dtestfx.setup.timeout=5000 -DSLEEP_TIME=1000 -DWAIT_TIME=5000"
            } catch(err) {
            throw err
          } finally {
            junit '**/target/failsafe-reports/TEST-*.xml'
          }
        }

        stage('test coverage') {
            step([$class: 'JacocoPublisher',
                  execPattern:      'target/**/*.exec',
                  classPattern:     'target/classes',
                  sourcePattern:    'src/main/java',
                  exclusionPattern: 'src/test*'
            ])
        }

        stage('static code analysis') {
            sh './mvnw -batch-mode -V -U -e pmd:cpd pmd:pmd spotbugs:spotbugs'

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

    stage ('OWASP Check') {

        dependencyCheck additionalArguments: '''
            -o "./"
            -s "./"
            -f "ALL"
            --prettyPrint''', odcInstallation: 'OWASP-DC'

        dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
    }

    stage('packaging') {
        sh "./mvnw install -P linux -DskipUTs=true -DskipFTs=true"
        archiveArtifacts artifacts: '**/target/artifacts/*.deb,**/target/artifacts/*.rpm,**/target/artifacts/*.AppImage', fingerprint: true
    }

}

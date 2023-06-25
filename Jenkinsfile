#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        env.PATH="/usr/lib/jvm/jdk-19/bin:${env.PATH}"
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    stage('unit tests') {
        try {
            sh "./mvnw clean test -DskipFTs=true"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/*.xml'
        }
    }

    wrap([$class: 'Xvfb', screen: '1920x1080x24', timeout: 25]) {

        stage('functional tests') {
          try {
            sh "./mvnw clean integration-test -P linux -DskipUTs=true -Dtestfx.launch.timeout=5000 -Dtestfx.setup.timeout=5000 -DSLEEP_TIME=500 -DWAIT_TIME=15000"
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

        stage('packaging') {
            sh "./mvnw package -P linux -DskipUTs=true -DskipFTs=true"
            archiveArtifacts artifacts: '**/target/artifacts/*.deb,**/target/artifacts/*.rpm,**/target/artifacts/*.AppImage', fingerprint: true
        }
    }
}

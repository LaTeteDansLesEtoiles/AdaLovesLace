#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        env.JAVA_HOME="${tool 'OpenJDK_17'}"
        env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    wrap([$class: 'Xvfb']) {
        stage('unit tests') {
            try {
                sh "./mvnw test"
            } catch(err) {
                throw err
            } finally {
                junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/Unit-TEST-*.xml'
            }
        }

        stage('integration & functional tests') {
            try {
                sh "./mvnw verify"
            } catch(err) {
                throw err
            } finally {
                junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'
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
                    java        (pattern: 'target/classes/**/*.class')
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
            sh "./mvnw package -DskipTests"
            archiveArtifacts(
              artifacts(**/target/*.jar, **/target/*.deb, **/target/*.rpm, **/target/*.exe),
              fingerprint(true)
            )
        }
    }
}

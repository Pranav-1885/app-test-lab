pipeline {
    agent any

    // ── Tool aliases configured in Jenkins → Global Tool Configuration ──────
    tools {
        maven 'Maven-3.9'   // change to the name you gave Maven in Jenkins
        jdk   'JDK-17'      // change to the name you gave JDK 17 in Jenkins
    }

    // ── Build-wide environment variables ─────────────────────────────────────
    environment {
        APP_NAME    = 'app-testcase-lab'
        MAVEN_OPTS  = '-Xmx512m'
    }

    // ── Pipeline Options ──────────────────────────────────────────────────────
    options {
        timestamps()                         // prefix every log line with a timestamp
        timeout(time: 15, unit: 'MINUTES')   // abort if the build hangs
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        // ── Stage 1: Checkout ─────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "==> Checking out source code for ${APP_NAME}"
                checkout scm
            }
        }

        // ── Stage 2: Compile ──────────────────────────────────────────────────
        stage('Compile') {
            steps {
                echo '==> Compiling main sources'
                sh 'mvn clean compile -B'
                /*
                 * -B (batch mode) suppresses the interactive download
                 *    progress bars — keeps logs readable in Jenkins.
                 */
            }
        }

        // ── Stage 3: Test ─────────────────────────────────────────────────────
        stage('Test') {
            steps {
                echo '==> Running unit tests (JUnit 5 via maven-surefire-plugin)'
                sh 'mvn test -B'
            }
            post {
                always {
                    // Publish JUnit XML reports so Jenkins shows pass/fail counts
                    junit testResults: '**/target/surefire-reports/*.xml',
                          allowEmptyResults: false
                }
                failure {
                    echo '✖ Tests FAILED — check the Test Results tab for details.'
                }
                success {
                    echo '✔ All tests PASSED.'
                }
            }
        }

        // ── Stage 4: Package ──────────────────────────────────────────────────
        stage('Package') {
            steps {
                echo '==> Packaging the application (skip tests — already run)'
                sh 'mvn package -B -DskipTests'
            }
            post {
                success {
                    // Archive the JAR so it is downloadable from Jenkins
                    archiveArtifacts artifacts: 'target/*.jar',
                                     fingerprint: true
                }
            }
        }
    }

    // ── Global post actions ───────────────────────────────────────────────────
    post {
        always {
            echo "==> Build finished: ${currentBuild.currentResult}"
            cleanWs()    // free workspace disk space after every build
        }
        success {
            echo "✔ Pipeline for ${APP_NAME} completed successfully."
        }
        failure {
            echo "✖ Pipeline for ${APP_NAME} FAILED. Review the console output."
        }
    }
}

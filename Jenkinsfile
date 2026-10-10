pipeline {
    agent any

    environment {
        APP_NAME    = 'app-testcase-lab'
        MAVEN_OPTS  = '-Xmx512m'
    }

    options {
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                echo "==> Checking out source code for ${APP_NAME}"
                checkout scm
            }
        }
        stage('Compile') {
            steps {
                echo '==> Compiling main sources'
                sh 'mvn clean compile -B'
            }
        }
        stage('Test') {
            steps {
                echo '==> Running unit tests (JUnit 5 via maven-surefire-plugin)'
                sh 'mvn test -B'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: false
                }
                failure {
                    echo '✖ Tests FAILED — check the Test Results tab for details.'
                }
                success {
                    echo '✔ All tests PASSED.'
                }
            }
        }
        stage('Package') {
            steps {
                echo '==> Packaging the application (skip tests — already run)'
                sh 'mvn package -B -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo "==> Build finished: ${currentBuild.currentResult}"
            cleanWs()
        }
        success {
            echo "✔ Pipeline for ${APP_NAME} completed successfully."
        }
        failure {
            echo "✖ Pipeline for ${APP_NAME} FAILED. Review the console output."
        }
    }
}
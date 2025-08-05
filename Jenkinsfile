@Library('jenkis-build-files') _

pipeline {
    agent any

    environment {
      BASE_TARGET = "${env.CHANGE_TARGET ?: env.GIT_BASE}"
        }

    stages {
        stage('Checkout') {
            steps {
                echo '======== Executing Checkout Stage ========'
                checkout scm
                sh 'git fetch origin ${env.CHANGE_TARGET}'
            }
        }

        // Call the lint and format function
        // stage('Lint and Format Code') {
        //     steps {
        //         script {
        //         }
        //     }
        // }

        // Call the microservices processing function
        stage('Process Microservices') {
            steps {
                script {
                    detectAndBuildMicroservices(
                        services: ["service-A", "service-B", "service-C"],
                        buildCommands: ["npm install", "npm test"],
                        dockerRegistry: "your-docker-registry",
                        dockerOrg: "your-org",
                        dockerCredsId: "your-docker-registry-id"
                    )
                }
            }
        }
    }

    post {
        always {
            echo '======== Pipeline Always Cleanup ========'
        }
        success {
            echo '======== Pipeline Executed Successfully ========'
        }
        failure {
            echo '======== Pipeline Execution Failed ========'
        }
    }
}
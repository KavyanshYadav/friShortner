@Library('jenkis-build-files') _

pipeline {
    agent {
        docker { image 'node:20-alphine' }
    }

    environment {
      TARGET_BRANCH = "${env.CHANGE_TARGET ?: env.BRANCH_NAME}"
    }

    stages {


        stage("Checking environment"){
            steps{
              script {
                echo "====++++executing checking environment ++++===="
                sh "node -v"
                
              }
            }
        }


        stage('Checkout') {
            steps {
                echo "======== Executing Checkout Stage ========"
                echo "Building for target branch: ${TARGET_BRANCH}"

                checkout scm
                sh "apt-get update && apt-get install -y git"

                sh "git fetch origin ${TARGET_BRANCH}"
            }
        }
        

        stage('Process Microservices') {
            steps {
                script {
                  // This is already a good approach
                  detectAndBuildMicroservices(
                      services: ["frishortner-redirect-service"],
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
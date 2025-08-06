@Library('jenkis-build-files') _

pipeline {
    agent {
        docker { image 'node:20-aplpine-localstack' }
    }

    environment {
      TARGET_BRANCH = "${env.CHANGE_TARGET ?: env.BRANCH_NAME}"
        AWS_REGION = 'us-east-1'
        AWS_ACCESS_KEY_ID = 'test'            // Dummy creds for LocalStack
        AWS_SECRET_ACCESS_KEY = 'test'
        LOCALSTACK_ENDPOINT = 'http://localstackaws:4566'
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

                sh "git fetch origin ${TARGET_BRANCH}"
            }
        }

        stage('Create S3 bucket on LocalStack') {
            steps {
                sh '''
                  aws --endpoint-url=$LOCALSTACK_ENDPOINT s3 mb s3://my-local-bucket
                '''
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
@Library('jenkis-build-files') _

pipeline {
    agent any

    environment {
      // This is the key fix.
      // For a PR, this will be the target branch (e.g., 'main').
      // For a direct branch build, this will be the branch itself (e.g., 'main').
      // This makes the variable reliable in all scenarios.
      TARGET_BRANCH = "${env.CHANGE_TARGET ?: env.BRANCH_NAME}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "======== Executing Checkout Stage ========"
                echo "Building for target branch: ${TARGET_BRANCH}"
                checkout scm
                
                // This command is often not necessary because 'checkout scm' already
                // fetches the correct code. You would only need this for advanced
                // git operations, like diffing against the target branch.
                // If you do need it, it's now safe to run.
                sh "git fetch origin ${TARGET_BRANCH}"
            }
        }

        stage('Process Microservices') {
            steps {
                script {
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
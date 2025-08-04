pipeline{
    agent{
         docker {
      image 'node:18-alpine'       // Use Node.js Docker image
      args  '-v $HOME/.npm:/root/.npm'  // Optional: reuse npm cache
    }
    }
    environment {
    GIT_BASE = 'origin/main'
  }
    stages{
        stage("checkout"){
            steps{
                echo "========executing A========"
                checkout scm
            }
            post{
                always{
                    echo "========always========"
                }
                success{
                    echo "========A executed successfully========"
                }
                failure{
                    echo "========A execution failed========"
                }
            }
        }
    }
    post{
        always{
            echo "========always========"
        }
        success{
            echo "========pipeline executed successfully ========"
        }
        failure{
            echo "========pipeline execution failed========"
        }
    }
}
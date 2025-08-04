pipeline {
  agent any

  environment {
    GIT_BASE = 'origin/main'
  }

  stages {
    stage('Checkout') {
      steps {
        echo '======== Executing Checkout Stage ========'
        checkout scm
      }
    }

    stage('Lint and Format') {
      steps {
        script {
          docker.image('node:18-alpine').inside('-v $HOME/.npm:/root/.npm') {
            sh 'npm install'
            sh 'npx eslint . --ext .js,.jsx,.ts,.tsx || true'
            sh 'npx prettier --check . || true'
          }
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

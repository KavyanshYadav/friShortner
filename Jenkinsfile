pipeline {
  agent {
    docker {
      image 'node:18-alpine'
      args '-v $HOME/.npm:/root/.npm'
    }
  }

  environment {
    GIT_BASE = 'origin/main'
  }

  stages {
    stage('Checkout') {
      steps {
        echo '======== Executing Checkout Stage ========'
        checkout scm
      }
      post {
        always {
          echo '======== Checkout Stage Completed (Always) ========'
        }
        success {
          echo '======== Checkout Stage Succeeded ========'
        }
        failure {
          echo '======== Checkout Stage Failed ========'
        }
      }
    }

    stage('Install Dependencies') {
      steps {
        echo '======== Installing NPM Dependencies ========'
        sh 'npm install'
      }
    }

    stage('Run ESLint') {
      steps {
        echo '======== Running ESLint ========'
        sh 'npx eslint . --ext .js,.jsx,.ts,.tsx || true'  // continue even if lint errors found
      }
    }

    stage('Run Prettier Check') {
      steps {
        echo '======== Running Prettier Check ========'
        sh 'npx prettier --check . || true'
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

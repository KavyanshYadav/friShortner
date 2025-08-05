// my-jenkins-library/vars/detectAndBuildMicroservices.groovy

def call(Map config) {
    def changedServices = []

    // Stage 1: Detect Changed Microservices (This part was correct)
    stage('Detect Changed Microservices') {
        script {
            try {
                def diffCommand
                if (env.CHANGE_TARGET) {
                    echo "Pull Request build detected. Comparing against target branch: ${env.CHANGE_TARGET}"
                    diffCommand = "git diff --name-only HEAD origin/${env.CHANGE_TARGET}"
                } else {
                    echo "Branch push build detected. Comparing HEAD with its parent commit."
                    diffCommand = "git diff --name-only HEAD~1 HEAD"
                }

                def changedFiles = sh(returnStdout: true, script: diffCommand).trim().split('\\n')
                echo "Files changed in this event:\n- ${changedFiles.join('\n- ')}"

                config.services.each { serviceName ->
                    def servicePath = "packages/${serviceName}/"
                    if (changedFiles.any { it.startsWith(servicePath) }) {
                        echo "✅ Detected changes in ${serviceName}."
                        changedServices.add(serviceName)
                    }
                }
                
                if (changedServices.isEmpty()) {
                    echo "No microservices were changed. Subsequent stages will be skipped."
                } else {
                    echo "Microservices queued for build: ${changedServices.join(', ')}"
                }
            } catch (e) {
                error "FATAL: Failed to detect changes. Error: ${e.getMessage()}"
            }
        }
    }

    // ✅ FIX IS HERE: Wrap the stage in an 'if' block
    if (!changedServices.isEmpty()) {
        stage('Build and Test Changed Microservices') {
            parallel changedServices.collectEntries { serviceName ->
                ["Build: ${serviceName}": {
                    node {
                        def servicePath = "packages/${serviceName}/"
                        dir(servicePath) {
                            echo "🚀 Starting build and test for ${serviceName}..."
                            
                            config.buildCommands.each { cmd ->
                                sh cmd
                            }
                            
                            def shortCommit = env.GIT_COMMIT.substring(0, 8)
                            def dockerImage = "${config.dockerRegistry}/${config.dockerOrg}/${serviceName}:${shortCommit}"
                            sh "docker build -t ${dockerImage} ."
                            
                            // if (config.dockerCredsId) {
                            //     withCredentials([usernamePassword(credentialsId: config.dockerCredsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            //         sh "docker login -u '${DOCKER_USER}' -p '${DOCKER_PASS}' ${config.dockerRegistry}"
                            //         sh "docker push ${dockerImage}"
                            //     }
                            //     echo "⚓ Successfully built and pushed ${dockerImage}"
                            // } else {
                            //     echo "Skipping Docker push: 'dockerCredsId' not provided."
                            // }
                        }
                    }
                }]
            }
        }
    }
}
// my-jenkins-library/vars/detectAndBuildMicroservices.groovy

def call(Map config) {
    // A local variable is cleaner than a global environment variable.
    def changedServices = []

    // Stage 1: Detect Changed Microservices
    stage('Detect Changed Microservices') {
        script {
            try {
                def diffCommand
                // This logic correctly handles both Pull Request and direct branch builds.
                if (env.CHANGE_TARGET) {
                    // This is a Pull Request build. Compare HEAD against the PR's target branch.
                    echo "Pull Request build detected. Comparing against target branch: ${env.CHANGE_TARGET}"
                    diffCommand = "git diff --name-only HEAD origin/${env.CHANGE_TARGET}"
                } else {
                    // This is a direct push. Compare the latest commit (HEAD) against its parent (HEAD~1).
                    echo "Branch push build detected. Comparing HEAD with its parent commit."
                    diffCommand = "git diff --name-only HEAD~1 HEAD"
                }

                def changedFiles = sh(returnStdout: true, script: diffCommand).trim().split('\\n')
                echo "Files changed in this event:\n- ${changedFiles.join('\n- ')}"

                // Loop through all defined services and check for changes
                config.services.each { serviceName ->
                    def servicePath = "packages/${serviceName}/"
                    def hasChanged = changedFiles.any { it.startsWith(servicePath) }
                    
                    if (hasChanged) {
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
                // Fail the pipeline if change detection fails
                error "FATAL: Failed to detect changes. Error: ${e.getMessage()}"
            }
        }
    }

    // Stage 2: Build and Test Changed Microservices
    stage('Build and Test Changed Microservices') {
        // Use the local 'changedServices' variable to decide if this stage should run.
        when { expression { !changedServices.isEmpty() } }
        
        steps {
            // Building in parallel is much faster for multiple services.
            parallel changedServices.collectEntries { serviceName ->
                // Create a dynamic stage for each service
                ["Build: ${serviceName}": {
                    node { // It's good practice to grab a node for each parallel branch
                        def servicePath = "packages/${serviceName}/"
                        
                        dir(servicePath) {
                            echo "🚀 Starting build and test for ${serviceName}..."
                            
                            config.buildCommands.each { cmd ->
                                sh cmd
                            }
                            
                            // Tag image with the short git commit hash for better traceability
                            def shortCommit = env.GIT_COMMIT.substring(0, 8)
                            def dockerImage = "${config.dockerRegistry}/${config.dockerOrg}/${serviceName}:${shortCommit}"
                            sh "docker build -t ${dockerImage} ."
                            
                            // Only attempt to push if Docker credentials are provided in the config
                            // if (config.dockerCredsId) {
                            //     withCredentials([usernamePassword(credentialsId: config.dockerCredsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            //         sh "docker login -u '${DOCKER_USER}' -p '${DOCKER_PASS}' ${config.dockerRegistry}"
                            //         sh "docker push ${dockerImage}"
                            //     }
                            //     echo "⚓ Successfully built and pushed ${dockerImage}"
                            } else {
                                echo "Skipping Docker push: 'dockerCredsId' not provided."
                            }
                        }
                    }
                }]
            }
        }
    }
}
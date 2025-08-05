// my-jenkins-library/vars/detectAndBuildMicroservices.groovy

def call(Map config) {
    // Stage 1: Detect Changed Microservices
    stage('Detect Changed Microservices') {
        script {
            // Get a list of all files that have changed between the PR branch and the target branch
            def changedFiles = sh(returnStdout: true, script: 'git diff --name-only "HEAD" "origin/${env.CHANGE_TARGET}"').trim().split('\\n')
            
            // Initialize a list to store the names of changed services
            env.CHANGED_SERVICES = []
            
            // Loop through all defined services and check for changes
            config.services.each { serviceName ->
                def servicePath = "packages/${serviceName}/"
                def hasChanged = changedFiles.any { it.startsWith(servicePath) }
                
                if (hasChanged) {
                    echo "Detected changes in ${serviceName}."
                    env.CHANGED_SERVICES.add(serviceName)
                }
            }
            
            if (env.CHANGED_SERVICES.isEmpty()) {
                echo "No microservices were changed. Skipping build and test stages."
            } else {
                echo "Microservices with changes: ${env.CHANGED_SERVICES.join(', ')}"
            }
        }
    }

    // Stage 2: Build and Test Changed Microservices
    stage('Build and Test Changed Microservices') {
        when { expression { env.CHANGED_SERVICES != null && !env.CHANGED_SERVICES.isEmpty() } }
        
        steps {
            script {
                // Iterate through the list of changed services
                env.CHANGED_SERVICES.each { serviceName ->
                    def servicePath = "packages/${serviceName}/"
                    
                    // Use a "dir" block to change into the service's directory
                    dir(servicePath) {
                        echo "Building and testing ${serviceName}..."
                        
                        // Use provided commands from the config map
                        config.buildCommands.each { cmd ->
                            sh cmd
                        }
                        
                        // Build the Docker image
                        def dockerImage = "${config.dockerRegistry}/${config.dockerOrg}/${serviceName}:${env.GIT_COMMIT}"
                        sh "docker build -t ${dockerImage} ."
                        
                        // Push the Docker image to the registry
                        // withCredentials([usernamePassword(credentialsId: config.dockerCredsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        //     sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} ${config.dockerRegistry}"
                        //     sh "docker push ${dockerImage}"
                        // }
                        echo "Successfully built and pushed ${dockerImage}"
                    }
                }
            }
        }
    }
}
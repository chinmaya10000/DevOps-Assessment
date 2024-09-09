pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    environment {
        IMAGE_NAME = "1.0"
        SCANNER_HOME = tool 'sonar-scanner'
    }

    stages {
        stage("Checkout from Git") {
            steps {
                script {
                    git 'https://github.com/chinmaya10000/DevOps-Assessment.git'
                }
            }
        }
        stage("Build jar") {
            steps {
                script {
                    echo "Building the app.."
                    sh 'mvn clean package'
                }
            }
        }
        stage("Unit Test") {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }
        stage("Integration Test") {
            steps {
                script {
                    sh 'mvn verify -DskipUnitTests'
                }
            }
        }
        stage("SonarQube Analysis") {
            steps {
                script {
                    echo "Running SonarQube analysis..."
                    withSonarQubeEnv('sonar-server') {
                        sh "mvn sonar:sonar -Dsonar.projectKey=java-maven-app -Dsonar.projectName='java-maven-app'"
                    }
                }
            }
        }
        stage("build and push image") {
            steps {
                script {
                    echo "build and push the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t chinmayapradhan/java-maven-app:${IMAGE_NAME} ."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push chinmayapradhan/java-maven-app:${IMAGE_NAME}"
                    }
                }
            }
        }
        stage("TRIVY Scan") {
            steps {
                script {
                    echo "Scan image with trivy.."
                    sh 'trivy image chinmayapradhan/java-maven-app:${IMAGE_NAME} > trivyimage.txt'
                }
            }
        }
        stage("Deploy to Devlopment") {
            steps {
                script {
                    echo "Deploying to Devlopment..."
                    deployToEnvironment("Devlopment", "3.138.155.130", "docker-compose.yml")
                }
            }
        }
        stage("Deploy to QA") {
            steps {
                input "Proceed to QA?"
                script {
                    echo "Deploying to QA..."
                    deployToEnvironment("qa", "3.138.155.130", "docker-compose.yml")
                }
            }
        }
        stage("Deploy to UAT") {
            steps {
                input "Proceed to UAT?"
                script {
                    echo "Deploying to UAT..."
                    deployToEnvironment("uat", "3.138.155.130", "docker-compose.yml")
                }
            }
        }
        stage("Deploy to Staging") {
            steps {
                input "Proceed to Staging?"
                script {
                    echo "Deploying to Staging..."
                    deployToEnvironment("staging", "3.138.155.130", "docker-compose.yml")
                }
            }
        }
        stage("Deploy to Prod") {
            steps {
                input "Proceed to Prod?"
                script {
                    echo "Deploying to Prod..."
                    deployToEnvironment("prod", "3.138.155.130", "docker-compose.yml")
                }
            }
        }
    }
}
def deployToEnvironment(environment, serverIp, composeFile) {
    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME} ${environment}"
    sshagent(['ec2-server-key']) {
        sh "scp server-cmds.sh ec2-user@${serverIp}:/home/ec2-user"
        sh "scp ${composeFile} ec2-user@${serverIp}:/home/ec2-user/docker-compose.yml"
        sh "ssh -T -o StrictHostKeyChecking=no ec2-user@${serverIp} '${shellCmd}'"
    }
}
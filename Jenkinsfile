pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    environment {
        IMAGE_NAME = "1.0"
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
        stage("deploy") {
            environment {
                DOCKER_CREDS = credentials('docker-hub-repo')
            }
            steps {
                script {
                    echo "deploy docker image to EC2.."
                    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME} ${DOCKER_CREDS_USR} ${DOCKER_CREDS_PSW}"
                    def ec2Instance = 'ec2-user@3.138.155.130'
                    sshagent(['ec2-server-key']) {
                        sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yml ${ec2Instance}:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} '${shellCmd}'"
                    }
                }
            }
        }
    }
}    
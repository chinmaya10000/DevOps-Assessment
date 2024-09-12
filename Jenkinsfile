#!/usr/bin/env groovy


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
        stage('TRIVY FS SCAN') {
            steps {
                sh "trivy fs . > trivyfs.txt"
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
        stage("Provision Server") {
            environment {
                AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
                AWS_SECRET_ACCESS_KEY = credentials('jenkins_aws_secret_access_key')
                TF_VAR_env_prefix = 'test'
            }
            steps {
                script {
                    dir('terraform') {
                        sh "terraform init"
                        sh "terraform apply --auto-approve"
                        EC2_PUBLIC_IP = sh(
                            script: "terraform output ec2_public_ip",
                            returnStdout: true
                        ).trim()
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
                    echo "waiting for EC2 server to initialize"
                    sleep(time: 90, unit: "SECONDS")

                    echo "Deploying the application.."
                    echo "${EC2_PUBLIC_IP}"
                    
                    def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME} ${DOCKER_CREDS_USR} ${DOCKER_CREDS_PSW}"
                    def ec2Instance = "ec2-user@${EC2_PUBLIC_IP}"

                    sshagent(['server-ssh-key']) {
                        sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yml ${ec2Instance}:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                    }
                }
            }
        }
    }
}











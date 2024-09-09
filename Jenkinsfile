pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    environment {
        IMAGE_NAME = "chinmayapradhan/java-maven-app"
        IMAGE_TAG = "1.0"
    }

    stages {
        stage("Checkout from Git") {
            steps {
                script {
                    git branch: 'master', url: 'https://github.com/chinmaya10000/DevOps-Assessment.git'
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
                        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    }
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    sh 'docker-compose up -d'
                }
            }
        }
    }
}    
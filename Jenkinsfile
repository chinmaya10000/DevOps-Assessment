pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout from Git') {
            steps {
                echo 'Clone the repo'
                git 'https://gitlab.com/chinmaya10000/java-maven-app.git'
            }
        }
        stage("build jar") {
            steps {
                script {
                    echo "building jar"
                    sh 'mvn clean package'
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Run SonarQube Scanner to analyze the code.."
                    withSonarQubeEnv('sonar-server') {
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }
        stage("build and push image") {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t chinmayapradhan/java-maven-app:1.0 ."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push chinmayapradhan/java-maven-app:1.0"
                    }
                }
            }
        }
        stage("Scan Image with Trivy"){
            steps{
                script {
                    sh "trivy image chinmayapradhan/java-maven-app:1.0 > trivyimage.txt"
                } 
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "deploying docker image to EC2...."
                    def dockerCmd = 'docker run -d -p 8080:8080 chinmayapradhan/java-maven-app:1.0'
                    sshagent(['ec2-server-key']) {
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@13.59.54.211 '${dockerCmd}'"
                    }
                }
            }
        }
    }
}

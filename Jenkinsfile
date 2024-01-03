def gv

pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    gv.buildJar()
                }
            }
        }
        stage("SonarQube Analysis") {
            steps {
                script {
                    gv.sonarqubeAnalysis()
                }
            }
        }
        stage("build and push image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }
        stage("Scan image with trivy") {
            steps {
                script {
                    gv.imageScan()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "deploy docker image to EC2.."
                    def dockerCmd = 'docker run -d -p 8080:8080 chinmayapradhan/java-maven-app:2.0'
                    sshagent(['server-ssh-key']) {
                        sh "ssh -o StrictHostKeyChecking=no ec2-user@18.222.113.29 '${dockerCmd}'"
                    }
                }
            }
        }
    }
}    
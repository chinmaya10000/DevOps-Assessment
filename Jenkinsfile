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
    }
}    
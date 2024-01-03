def buildJar() {
    echo "building jar"
    sh 'mvn clean package'
}

def sonarqubeAnalysis() {
    echo "Run SonarQube Scanner to analyze the code.."
    withSonarQubeEnv('Sonar-Server') {
        sh "mvn sonar:sonar"
    }
}

def buildImage() {
    echo "build and push the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t chinmayapradhan/java-maven-app:2.0 .'
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh 'docker push chinmayapradhan/java-maven-app:2.0'
    }
}

return this
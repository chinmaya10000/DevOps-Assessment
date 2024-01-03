def buildJar() {
    echo "building jar"
    sh 'mvn clean package'
}

def sonarqubeAnalysis() {
    echo "Run SonarQube Scanner to analyze the code.."
    withSonarQubeEnv('Sonar-Server-10.1') {
        sh "mvn sonar:sonar"
    }
}
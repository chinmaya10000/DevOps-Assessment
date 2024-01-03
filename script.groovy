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
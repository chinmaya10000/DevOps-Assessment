def buildJar() {
    echo "building jar"
    sh 'mvn clean package'
}
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package'                
            }
        }
		stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
		/*stage('Deliver') {
            steps {
                sh './jenkins/scripts/deliver.sh'
            }
        }*/
    }
}
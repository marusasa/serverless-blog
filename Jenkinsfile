pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
                sh 'mv target/*.jar target/runner.jar'
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
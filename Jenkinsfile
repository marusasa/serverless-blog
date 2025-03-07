pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Build') { 
			agent {
				docker { 
					image 'maven:3.9-eclipse-temurin-21-alpine'
					args '-v $HOME/.m2:/root/.m2'
				}
			}
            steps {
                sh 'mvn -B -DskipTests clean package'                
            }
        }
		stage('Test') {
			agent {
				docker { 
					image 'maven:3.9-eclipse-temurin-21-alpine'
					args '-v $HOME/.m2:/root/.m2'
				}
			}
            steps {
                sh 'mvn test'
            }
        }
		stage('Deliver') {
            steps {
                sh './jenkins/deliver.sh'
            }
        }
    }
}
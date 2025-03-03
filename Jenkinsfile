pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
                sh 'pwd'
                sh 'cd target'
                sh "mv '*.jar' 'runner.jar'"
                sh '../'
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
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
                script {
	                fileOperations([
	                    fileRenameOperation(
	                        destination: 'target/runner.jar',
	                        source: 'target/*.jar'
	                    )
	                ])
	            }
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
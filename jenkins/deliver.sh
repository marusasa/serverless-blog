sh 'gcloud auth activate-service-account --key-file=/gcloud_folder/serverless-test-keyfile.json'
sh 'alias python=python3'
sh 'mvn clean package -Dmaven.test.skip appengine:deploy'
# What is this?

A blog platform designed to run on cloud serverless PaaS Environment.

Currently supporting Google Cloud, but planned to support AWS in the future.

The server is currently running my personal blog at: [https://sasagu.com](https://sasagu.com).

Notice: The application is missing some basic features now.

# Highlight

- Run it in a highly scalable, reliable and fast PaaS environment.
- Running systems on PaaS platform is cost effective. Zero costs when idle.

# Technology Stack - Backend

- Java 21
- Javalin Server
- Google App Engine
- Google Cloud Datastore (Firestore)
- Maven

# Technology Stack - Frontend

- React JS
- TypeScript
- Tailwind CSS
- DaisyUI
- Vite Build
- NPM

# Requirements to run

- An account with Google Cloud Platform with billing enabled.
- A project in Google Cloud

# How to deploy

- Install gcloud CLI and initialize your environment:

```
	gcloud init
```

- Edit pom.xml, update app engine config:
	
```
	<configuration>
		<version>0-1-0</version>
		<!-- 
			set your google cloud project id here.
			Or set it with system property.
			Example: clean package -Dapp.deploy.projectId=[your_project_id] appengine:deploy
		-->
		<projectId></projectId>
	</configuration>	
```

- Run this Maven command/goal to deploy it to Google App Engine: 	

```
	clean package appengine:deploy
```
	
- Once deployed, access the url of the project to see the blog page. Go to Google Cloud Log Viewer. If you see the following, 
then the application was deployed and initialized successfully.

```
	***************************************************************
	* Initial Data Preparation Completed.
	***************************************************************
```

- The initialization process should have created some data. Go to '(default)' datastore to verify the data created.
	
# Things to consider

- src/main/appengine/app.yaml - This configures the instance that will be deployed to Google App Engine. 
By default, it creates an F1 class instance with max 1 instance running.

- Data will be stored in Google Cloud Datastore. 



# What is this?

A blog platform designed to run on cloud serverless PaaS Environment.

Currently supporting Google Cloud, but planned to support AWS in the future.

The server is currently running my personal blog at: [https://sasagu.com](https://sasagu.com).

**Notice: The application is in Alpha stage. It is missing some basic features.**

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
- To enable AI Summary, 'Gemini API' must be enabled within the Google Cloud project.

# How to deploy

- Install gcloud CLI and initialize your environment:

```
	gcloud init
```

- Create an App Engine application, if you haven't done it yet.

```
	gcloud app create
```

- By default, the '(default)' datastore is in non-native 'Datastore' mode. Convert it to Firestore native. 
Optionally, you can delete the existing one and create a new database in navive firestore mode, setting your
desired db location. 
Name it '(default)'.

```
	gcloud firestore databases update --type=firestore-native
```

- Add needed indexes.

```
	gcloud firestore indexes composite create --collection-group=page-components --field-config=field-path=ref_account_id,order=ascending --field-config=field-path=view_order,order=ascending
```
```
	gcloud firestore indexes composite create --collection-group=articles --field-config=field-path=ref_account_id,order=ascending --field-config=field-path=status,order=ascending --field-config=field-path=published_at,order=descending
```
```
	gcloud firestore indexes composite create --collection-group=articles --field-config=field-path=ref_account_id,order=ascending --field-config=field-path=created_at,order=descending
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



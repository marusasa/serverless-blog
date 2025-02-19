package ssg.serverlessblog.autotests;

import static org.junit.jupiter.api.Assertions.fail;

import ssg.serverlessblog.system.Env;

public class TestingUtil {

	public static void checkReqEnvironment() {
		if(System.getenv("GCLOUD_PROJECT") == null || System.getenv("GOOGLE_APPLICATION_CREDENTIALS") == null) {
			fail("Environment variable not found. It is needed to connect to cloud datastore for testing.");
		}
		if(!Env.isGae()) {
			fail("At this time, unit tests only run with GAE cloud datastore. Review serverless-blog.properties.");
		}
	}
}

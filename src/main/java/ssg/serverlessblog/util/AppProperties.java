package ssg.serverlessblog.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Class that holds environment specific properties. Make sure to review this
 * before every deployment to cloud PaaS.
 */
public class AppProperties {

	private static Properties properties = null;
	private static Properties get() {
		if(properties == null) {
			try {
				// Load the properties file from the classpath
				properties = new Properties();
				properties.load(AppProperties.class.getClassLoader().getResourceAsStream("serverless-blog.properties"));
			} catch (IOException e) {
				throw new RuntimeException("Failed  to load properties file.", e);
			}
		}
		return properties;
	}
	
	public static String getString(String name) {
		return get().getProperty(name);
	}
	
	public static boolean getBoolean(String name) {
		return Boolean.parseBoolean(get().getProperty(name));
	}
}

package ssg.serverlessblog.interfaces;

import ssg.serverlessblog.util.CloudDocument;

/**
 * Interface for Data Util.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface DataUtilInt {
	public String getUtcString(CloudDocument doc, String fieldId);
}

package ssg.serverlessblog.interfaces;

import java.util.Date;

import ssg.serverlessblog.util.CloudDocument;

/**
 * Interface for Data Util.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface DataUtilInt {
	public Date getDate(CloudDocument doc, String fieldId);
}

package ssg.serverlessblog.gae.util;

import java.io.IOException;
import java.util.Date;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Utility class specific for Google Cloud environment.
 */
public class FirestoreDbUtil implements DataUtilInt {
	
	public static Firestore getFirestoreDbObj() throws IOException  {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		return db;
	}

	@Override
	public Date getDate(CloudDocument doc, String fieldId) {
		Timestamp ts = (Timestamp)doc.get(fieldId); 
		return ts.toDate();
	}
	

}

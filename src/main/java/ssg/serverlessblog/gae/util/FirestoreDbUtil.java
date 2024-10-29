package ssg.serverlessblog.gae.util;

import java.io.IOException;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Utility class specific for Google Cloud environment.
 */
public class FirestoreDbUtil implements DataUtilInt {
	
	private static Firestore db = null;
	
	public static Firestore getFirestoreDbObj() throws IOException  {
		if(db == null) {
			db = FirestoreOptions.getDefaultInstance().getService();
		}
		return db;
	}
	


	@Override
	public String getUtcString(CloudDocument doc, String fieldId) {
		Timestamp ts = (Timestamp)doc.get(fieldId);
		//toString() produces '2024-10-25T04:36:17.743000000Z', change this to
		//'2024-10-25T04:36:17.743Z', which is UTC timezone compatible
		//in Javascript.
		var result = ts.toString().substring(0, 23) + "Z";
		return result;
	}
	

}

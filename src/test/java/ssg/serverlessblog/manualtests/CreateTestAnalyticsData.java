package ssg.serverlessblog.manualtests;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;

import ssg.serverlessblog.analytics.AnalyticsDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;

public class CreateTestAnalyticsData {

	public static void main(String[] args) {
		System.out.println("Start creating test data");
		try	{
			Calendar c = Calendar.getInstance();
			c.set(2024, 11, 1);
			saveEvent(100,"/testPath01","visible",c);
			c.add(Calendar.MINUTE, 1);
			saveEvent(100,"/testPath01","hidden",c);
			saveEvent(100,"/testPath02","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(100,"/testPath02","hidden",c);
			saveEvent(200,"/testPath02","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(200,"/testPath02","hidden",c);
			c.set(2024, 11, 2);
			saveEvent(100,"/testPath01","visible",c);
			c.add(Calendar.MINUTE, 1);
			saveEvent(100,"/testPath01","hidden",c);
			saveEvent(100,"/testPath02","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(100,"/testPath02","hidden",c);
			saveEvent(200,"/testPath02","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(200,"/testPath02","hidden",c);
			c.set(2024, 11, 3);
			saveEvent(300,"/testPath01","visible",c);
			c.add(Calendar.MINUTE, 1);
			saveEvent(300,"/testPath01","hidden",c);
			saveEvent(300,"/","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(300,"/","hidden",c);
			saveEvent(400,"/testPath02","visible",c);
			c.add(Calendar.SECOND, 35);
			saveEvent(400,"/testPath02","hidden",c);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("End creating test data");
	}
	
	private static CollectionReference analytics = null;
	private static CollectionReference collection() throws IOException {
		if(analytics == null) {
			analytics = FirestoreDbUtil.getFirestoreDbObj().collection(AnalyticsDoc.collection);
		}
		return analytics;
	}
	
	static public void saveEvent(int visitorId, String path, String status, Calendar c) throws Exception {
		try {
			String date = (String)Timestamp.of(c.getTime()).toString().subSequence(0, 10);
			
			Map<String, Object> data = new HashMap<>();
			data.put(AnalyticsDoc.field_visitor_id,visitorId);
			data.put(AnalyticsDoc.field_path,path);
			data.put(AnalyticsDoc.field_status,status);
			int index = path.indexOf("_");
			var articleId = "";
			if(index > -1) {
				articleId = path.substring(index+1);
			}
			data.put(AnalyticsDoc.field_article_id, articleId);			
			//save date in yyyy-MM-dd format
			data.put(AnalyticsDoc.field_date_string, Timestamp.of(c.getTime()).toString().subSequence(0, 10));
			data.put(AnalyticsDoc.field_created_at, Timestamp.of(c.getTime()));
			data.put(AnalyticsDoc.field_created_at_millisec, Timestamp.of(c.getTime()).toDate().getTime());
			ApiFuture<DocumentReference> docRef = collection().document(date).collection("analytics-daily").add(data);
			//ApiFuture<DocumentReference> docRef = collection().add(data);
			docRef.get();
		}catch(Exception e) {
			throw e;
		}
	}

}

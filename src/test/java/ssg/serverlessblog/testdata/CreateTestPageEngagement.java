package ssg.serverlessblog.testdata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;

import ssg.serverlessblog.analytics.AnalyticsDoc;
import ssg.serverlessblog.analytics.DailyVisitsDoc;
import ssg.serverlessblog.analytics.PageEngagementDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;

public class CreateTestPageEngagement {

	public static void main(String[] args) {
		System.out.println("Start creating test data for: " + PageEngagementDoc.collection);
		
		try	{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2023);
			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);
			Calendar cTo = Calendar.getInstance();
			cTo.set(Calendar.YEAR, 2026);
			cTo.set(Calendar.MONTH, Calendar.JANUARY);
			cTo.set(Calendar.DATE, 1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String listOfPaths = "/,/path_first_one,/another_path_name,/third_path,/path_04,/path_05,/path_06";
			List<String> pathList = Arrays.asList(listOfPaths.split(","));
			
			while(c.before(cTo)) {
				System.out.println("Generating data for %s".formatted(c.toString()));
				for(String path:pathList) {
					int totalVisits = (int) (Math.random() * 100) + 100; // Random total visits between 100 and 199
		            int humanVisits = (int) (Math.random() * totalVisits); // Random human visits less than total visits
		            int date = Integer.parseInt(sdf.format(c.getTime()));
		            int average = (int) (Math.random() * 50);
		            saveData(date, totalVisits, humanVisits,path,average);
				};				
	            c.add(Calendar.DATE, 1);
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("End creating test data");
	}
	
	private static CollectionReference pathEngagement = null;
	private static CollectionReference collection() throws IOException {
		if(pathEngagement == null) {
			pathEngagement = FirestoreDbUtil.getFirestoreDbObj().collection(PageEngagementDoc.collection);
		}
		return pathEngagement;
	}
	
	static public void saveData(int date, int countAll, int countActual, String path, int averageTime) throws Exception {
		try {			
			Map<String, Object> data = new HashMap<>();
			data.put(PageEngagementDoc.field_date,date);
			data.put(PageEngagementDoc.field_path, path);
			data.put(PageEngagementDoc.field_count_all,countAll);
			data.put(PageEngagementDoc.field_count_actual,countActual);
			data.put(PageEngagementDoc.field_average_time, averageTime);
			data.put(PageEngagementDoc.field_created_at,Timestamp.now());
			ApiFuture<DocumentReference> docRef = collection().add(data);
			docRef.get();
		}catch(Exception e) {
			throw e;
		}
	}

}

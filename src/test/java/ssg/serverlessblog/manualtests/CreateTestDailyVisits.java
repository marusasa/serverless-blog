package ssg.serverlessblog.manualtests;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;

import ssg.serverlessblog.analytics.DailyVisitsDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;

public class CreateTestDailyVisits {

	public static void main(String[] args) {
		System.out.println("Start creating test data for: " + DailyVisitsDoc.collection);
		
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
			
			while(c.before(cTo)) {
				int totalVisits = (int) (Math.random() * 100) + 100; // Random total visits between 100 and 199
	            int humanVisits = (int) (Math.random() * totalVisits); // Random human visits less than total visits
	            int date = Integer.parseInt(sdf.format(c.getTime()));
	            saveData(date, totalVisits, humanVisits);
	            c.add(Calendar.DATE, 1);
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("End creating test data");
	}
	
	private static CollectionReference daily = null;
	private static CollectionReference collection() throws IOException {
		if(daily == null) {
			daily = FirestoreDbUtil.getFirestoreDbObj().collection(DailyVisitsDoc.collection);
		}
		return daily;
	}
	
	static public void saveData(int date, int countAll, int countActual) throws Exception {
		try {			
			Map<String, Object> data = new HashMap<>();
			data.put(DailyVisitsDoc.field_date,date);
			data.put(DailyVisitsDoc.field_count_all,countAll);
			data.put(DailyVisitsDoc.field_count_actual,countActual);
			data.put(DailyVisitsDoc.field_created_at,Timestamp.now());
			ApiFuture<DocumentReference> docRef = collection().add(data);
			docRef.get();
		}catch(Exception e) {
			throw e;
		}
	}

}

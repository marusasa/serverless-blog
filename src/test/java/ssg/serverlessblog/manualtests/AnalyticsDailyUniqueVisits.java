package ssg.serverlessblog.manualtests;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import ssg.serverlessblog.analytics.AnalyticsDoc;
import ssg.serverlessblog.analytics.Visitor;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;

public class AnalyticsDailyUniqueVisits {
	
	
	public static void main(String[] args) {
		try {
			final HashMap<Long,Visitor> visitorMap = new HashMap<>();
			
			final String date = "2024-12-02";
			
			final Query query = collection().document(date).collection("analytics-daily")
					.orderBy(AnalyticsDoc.field_created_at_millisec, Direction.ASCENDING);
			final ApiFuture<QuerySnapshot> future = query.limit(100).get();
			final QuerySnapshot qs = future.get();
			final List<QueryDocumentSnapshot> list = qs.getDocuments();
			
			//first create list of visitor Map
			list.stream().forEach(doc -> {
				Long visitorId = doc.getLong(AnalyticsDoc.field_visitor_id);
				Visitor v = visitorMap.getOrDefault(visitorId, new Visitor(visitorId));
				visitorMap.put(visitorId, v);
			});
			
			//for each visitors, create list of path and duration.
			visitorMap.values().forEach(visitor -> {				
				list.stream().filter(doc -> doc.getLong(AnalyticsDoc.field_visitor_id).equals(visitor.visitorId))
						.forEach(doc -> {
							final var status = doc.getString(AnalyticsDoc.field_status);
							final var path = doc.getString(AnalyticsDoc.field_path);							
							if(status.equals("visible")) {
								//set visible time. This will be used when 'hidden' is encountered.
								final var visibleTime = doc.getLong(AnalyticsDoc.field_created_at_millisec);
								visitor.pathVisibleTime.put(path,visibleTime);
							}else if(status.equals("hidden")) {
								if(visitor.pathVisibleTime.containsKey(path)) {
									final long duration = visitor.pathAndDuration.getOrDefault(path,0L);									
									final long visibleTime = visitor.pathVisibleTime.get(path);
									final long additionalDuration = doc.getLong(AnalyticsDoc.field_created_at_millisec).longValue() - visibleTime;
									visitor.pathAndDuration.put(path,duration + additionalDuration);
								}
							}else {
								//ignore
							}
						});
			});
			
			long countAll = 0;
			long countHuman = 0;
			
			countAll = visitorMap.size();
			countHuman = visitorMap.values().stream().filter(v -> v.isRealPerson()).count();
			
			visitorMap.values().forEach(visitor -> {
				System.out.println(visitor.visitorId);
				visitor.pathAndDuration.forEach((path,duration) -> {
					System.out.println("Path: %s, duration: %s".formatted(path,duration));
				});
			});

			//delete records
			
			System.out.println("Date: %s, All: %s, Human: %s".formatted(date,countAll,countHuman));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static CollectionReference analytics = null;
	private static CollectionReference collection() throws IOException {
		if(analytics == null) {
			analytics = FirestoreDbUtil.getFirestoreDbObj().collection(AnalyticsDoc.collection);
		}
		return analytics;
	}
	
	/* old logic
	 * //first create list of visitor Map
			list.stream().forEach(doc -> {
				Long visitorId = doc.getLong(AnalyticsDoc.field_visitor_id);
				Visitor v = null;
				if(visitorMap.containsKey(visitorId)) {
					v = visitorMap.get(visitorId);
				}else {
					v = new Visitor(visitorId);
				}
				visitorMap.put(visitorId, v);
			});
			
			//for each visitors, create list of path and duration.
			visitorMap.values().forEach(visitor -> {				
				list.stream().filter(doc -> doc.getLong(AnalyticsDoc.field_visitor_id).equals(visitor.visitorId))
						.forEach(doc -> {
							var status = doc.getString(AnalyticsDoc.field_status);
							String path = doc.getString(AnalyticsDoc.field_path);							
							if(status.equals("visible")) {
								//set visible time. This will be used when 'hidden' is encountered.
								long visibleTime = doc.getLong(AnalyticsDoc.field_created_at_millisec);
								if(visitor.pathVisibleTime.putIfAbsent(path,visibleTime) != null) {	
									visitor.pathVisibleTime.replace(path,visibleTime);
								}
							}else if(status.equals("hidden")) {
								if(visitor.pathVisibleTime.containsKey(path)) {
									long duration = 0L;
									if(visitor.pathAndDuration.get(path) != null) {
										duration = visitor.pathAndDuration.get(path);
									}
									long visibleTime = visitor.pathVisibleTime.get(path);
									long additionalDuration = doc.getLong(AnalyticsDoc.field_created_at_millisec).longValue() - visibleTime;
									duration += additionalDuration;
									if(visitor.pathAndDuration.putIfAbsent(path,duration) != null) {	
										visitor.pathAndDuration.replace(path,duration);
									}
								}
							}else {
								//ignore
							}
						});
			});
	 */
}

package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.analytics.AnalyticsDoc;
import ssg.serverlessblog.analytics.DailyVisitsDoc;
import ssg.serverlessblog.analytics.PageEngagementDoc;
import ssg.serverlessblog.analytics.PathCounts;
import ssg.serverlessblog.analytics.PathSummary;
import ssg.serverlessblog.analytics.Visitor;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.AnalyticsDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the AnalyticsDaoInt.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class AnalyticsDao implements AnalyticsDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsDao.class.getName());
	
	private static CollectionReference analytics = null;
	private static CollectionReference collection() throws IOException {
		if(analytics == null) {
			analytics = FirestoreDbUtil.getFirestoreDbObj().collection(AnalyticsDoc.collection);
		}
		return analytics;
	}
	
	private static CollectionReference analyticsResultDaily = null;
	private static CollectionReference collectionResultDaily() throws IOException {
		if(analyticsResultDaily == null) {
			analyticsResultDaily = FirestoreDbUtil.getFirestoreDbObj().collection(DailyVisitsDoc.collection);
		}
		return analyticsResultDaily;
	}
	
	private static CollectionReference analyticsResultPageEngagement = null;
	private static CollectionReference collectionResultPageEngagement() throws IOException {
		if(analyticsResultPageEngagement == null) {
			analyticsResultPageEngagement = FirestoreDbUtil.getFirestoreDbObj().collection(PageEngagementDoc.collection);
		}
		return analyticsResultPageEngagement;
	}
	
	
	
	@Override
	public boolean dateExistsDailyVisits(int yyyyMMdd) throws Exception {
		final Query query = collectionResultDaily().whereEqualTo(DailyVisitsDoc.field_date, yyyyMMdd);
		final ApiFuture<QuerySnapshot> future = query.get();
		final QuerySnapshot qs = future.get();
		return !qs.isEmpty();
	}

	


	@Override
	public List<PathSummary> getPageEngagementSummaryByPath(int year, int month) throws Exception {
		final HashMap<String,PathSummary> pathMap = new HashMap<>();
		final int limit = 100;
		//build dates for query. Example for params 2024 and 11
		final int dateFrom = year * 10000 + month * 100 + 1;	//20241101
		final int dateTo = dateFrom + 30;	//20241131
		Query page = collectionResultPageEngagement()
				.whereGreaterThanOrEqualTo(PageEngagementDoc.field_date, dateFrom)
				.whereLessThanOrEqualTo(PageEngagementDoc.field_date, dateTo)
				.orderBy(PageEngagementDoc.field_date, Direction.ASCENDING)
				.limit(limit);
		boolean next = true;
		while(next) {
			final ApiFuture<QuerySnapshot> future = page.get();
			final List<QueryDocumentSnapshot> docs = future.get().getDocuments();		
			//first create list of pathMap
			docs.stream().forEach(doc -> {
				final String path = doc.getString(PageEngagementDoc.field_path);
				//add it to map if it doesn't exist
				pathMap.computeIfAbsent(path, p -> new PathSummary(p));
			});
			//for each visitors, create list of path and duration.
			pathMap.values().forEach(pathSum -> {
				docs.stream().filter(doc -> doc.getString(PageEngagementDoc.field_path).equals(pathSum.path))
						.forEach(doc -> {
							pathSum.dateActualMap.put(doc.getLong(PageEngagementDoc.field_date),doc.getLong(PageEngagementDoc.field_count_actual));
							pathSum.dateTotalMap.put(doc.getLong(PageEngagementDoc.field_date),doc.getLong(PageEngagementDoc.field_count_all));
							pathSum.dateAverageMap.put(doc.getLong(PageEngagementDoc.field_date),doc.getLong(PageEngagementDoc.field_average_time));							
						});
			});
			if(docs.size() == limit) {
				final QueryDocumentSnapshot lastDoc = docs.get(docs.size() - 1);
				page = analyticsResultPageEngagement
						.whereGreaterThanOrEqualTo(PageEngagementDoc.field_date, dateFrom)
						.whereLessThanOrEqualTo(PageEngagementDoc.field_date, dateTo)
						.orderBy(PageEngagementDoc.field_date, Direction.ASCENDING)
						.startAfter(lastDoc)
						.limit(limit);				
			}else {
				next = false;
			}
		};
		return new ArrayList<>(pathMap.values());
	}



	@Override
	public void processDailyRawData(String today) throws Exception {
		final HashMap<Long,Visitor> visitorMap = new HashMap<>();
		final int limit = 100;
		Query page = collection().document(today).collection(AnalyticsDoc.sub_collection)
				.orderBy(AnalyticsDoc.field_created_at_millisec, Direction.ASCENDING).limit(limit);
		boolean next = true;
		while(next) {
			final ApiFuture<QuerySnapshot> future = page.get();
			final List<QueryDocumentSnapshot> docs = future.get().getDocuments();		
			//first create list of visitor Map
			docs.stream().forEach(doc -> {
				final Long visitorId = doc.getLong(AnalyticsDoc.field_visitor_id);
				//add it to map if it doesn't exist already
				visitorMap.computeIfAbsent(visitorId, vid -> new Visitor(vid));				
			});
			//for each visitors, create list of path and duration.
			visitorMap.values().forEach(visitor -> {
				docs.stream().filter(doc -> doc.getLong(AnalyticsDoc.field_visitor_id).equals(visitor.visitorId))
						.forEach(doc -> {
							final var status = doc.getString(AnalyticsDoc.field_status);
							final var path = doc.getString(AnalyticsDoc.field_path);
							if (status.equals("visible")) {
								// set visible time. This will be used when 'hidden' is encountered.
								final var visibleTime = doc.getLong(AnalyticsDoc.field_created_at_millisec);
								visitor.pathVisibleTime.put(path, visibleTime);
							} else if (status.equals("hidden")) {
								if (visitor.pathVisibleTime.containsKey(path)) {
									final long duration = visitor.pathAndDuration.getOrDefault(path,0L);
									final long visibleTime = visitor.pathVisibleTime.get(path);
									final long additionalDuration = doc.getLong(AnalyticsDoc.field_created_at_millisec)
											.longValue() - visibleTime;
									visitor.pathAndDuration.put(path, duration + additionalDuration);
								}
							} else {
								//ignore
							}
						});
			});
			if(docs.size() == limit) {
				final QueryDocumentSnapshot lastDoc = docs.get(docs.size() - 1);
				page = collection().document(today).collection(AnalyticsDoc.sub_collection)
						.orderBy(AnalyticsDoc.field_created_at_millisec, Direction.ASCENDING)
						.startAfter(lastDoc).limit(limit);
			}else {
				next = false;
			}
		};
		//get count
		final int dateInt = Integer.parseInt(today.replaceAll("-", ""));
		final long countAll = visitorMap.size();
		final long countActual = visitorMap.values().stream().filter(v -> v.isRealPerson()).count();
		saveResultDaily(dateInt,countAll, countActual);
				
		//step 1, fill map with all the available path
		final Map<String,PathCounts> pathCountMap = new HashMap<>();
		
		visitorMap.values().forEach(visitor -> {
			visitor.pathAndDuration.keySet().forEach(path -> {
				//if it doesn't exist, create new one.
				final PathCounts pathCounts = pathCountMap.computeIfAbsent(path, pth -> new PathCounts(pth));
				pathCounts.totalList.add(visitor.pathAndDuration.get(path));
				if(visitor.isRealPerson()) {
					pathCounts.actualList.add(visitor.pathAndDuration.get(path));
				}
			});			
		});
		
		//summarize value for each path.		
		Set<String> pathKeys = pathCountMap.keySet();
		for(String path:pathKeys) {
			final PathCounts pathCounts = pathCountMap.get(path);
			final long pathCountAll = pathCounts.totalList.size();
			final long pathCountActual = pathCounts.actualList.size();
			final long pathAverage = ((long)pathCounts.actualList.stream().mapToLong(Long::longValue).average().getAsDouble())/1000;
			saveResultPageEngagement(dateInt, pathCounts.path, pathCountAll, pathCountActual, pathAverage);
		};
		
		//If it reaches here then it means data was processed with no errors (Exceptions).
		//Proceed to delete this day's raw analytics data.
		logger.info("Deleting %s".formatted(today));
		boolean deleteContinue = true;
		int deleteTotalCount = 0;
		while(deleteContinue) {
			final ApiFuture<QuerySnapshot> future = collection().document(today).collection(AnalyticsDoc.sub_collection).limit(limit).get();
		    int deleted = 0;
		    // future.get() blocks on document retrieval
		    final List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				document.getReference().delete();
				++deleted;
				++deleteTotalCount;
			}
		    
		    if (deleted < limit) {
		    	deleteContinue = false;
		    }
		}
		logger.info("Total '%s' record deleted: %s".formatted(AnalyticsDoc.sub_collection,deleteTotalCount));
		//finally delete the parent document.
		final DocumentReference docRef = collection().document(today);
		final ApiFuture<WriteResult> writeResult = docRef.delete();
		writeResult.get();
		
		logger.info("Delete records completed.");
	}
	
	private void saveResultPageEngagement(int date, String path, long countAll, long countActual, long averageTime) throws Exception{
//		System.out.println("PageEngagement Date: %s, Path: %s, Actual: %s, All: %s, Average: %s".formatted(date,path,countActual,countAll,averageTime));
		final Map<String, Object> data = new HashMap<>();
		data.put(PageEngagementDoc.field_date,date);
		data.put(PageEngagementDoc.field_path,path);
		data.put(PageEngagementDoc.field_count_all,countAll);
		data.put(PageEngagementDoc.field_count_actual,countActual);
		data.put(PageEngagementDoc.field_average_time,averageTime);
		data.put(DailyVisitsDoc.field_created_at,Timestamp.now());
		final ApiFuture<DocumentReference> docRef = collectionResultPageEngagement().add(data);
		docRef.get();
	}

	private void saveResultDaily(int date, long countAll, long countActual) throws Exception {
//		System.out.println("DailyCount Date: %s, Actual: %s, All: %s".formatted(date,countActual,countAll));
		final Map<String, Object> data = new HashMap<>();
		data.put(DailyVisitsDoc.field_date,date);
		data.put(DailyVisitsDoc.field_count_all,countAll);
		data.put(DailyVisitsDoc.field_count_actual,countActual);
		data.put(DailyVisitsDoc.field_created_at,Timestamp.now());
		final ApiFuture<DocumentReference> docRef = collectionResultDaily().add(data);
		docRef.get();
	}


	@Override
	public List<CloudDocument> getDailyVisits(int year, int month) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			//build dates for query. Example for params 2024 and 11
			final int dateFrom = year * 10000 + month * 100 + 1;	//20241101
			final int dateTo = dateFrom + 30;	//20241131
			
			final Query query = collectionResultDaily()
					.whereGreaterThanOrEqualTo(DailyVisitsDoc.field_date, dateFrom)
					.whereLessThanOrEqualTo(DailyVisitsDoc.field_date, dateTo)
					.orderBy(DailyVisitsDoc.field_date, Direction.ASCENDING);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), document.getData()));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}


	
	//save date in yyyy-MM-dd format
	final static SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	static {
		sdfYYYYMMDD.setTimeZone(TimeZone.getTimeZone("PST"));
	}

	@Override
	public void saveEvent(int visitorId, String path, String status) throws Exception {
		try {
			final Map<String, Object> data = new HashMap<>();
			data.put(AnalyticsDoc.field_visitor_id,visitorId);
			data.put(AnalyticsDoc.field_path,path);
			data.put(AnalyticsDoc.field_status,status);
			final int index = path.indexOf("_");
			var articleId = "";
			if(index > -1) {
				articleId = path.substring(index+1);
			}
			data.put(AnalyticsDoc.field_article_id, articleId);
			final String date = sdfYYYYMMDD.format(Timestamp.now().toDate());
			data.put(AnalyticsDoc.field_date_string,date);
			data.put(AnalyticsDoc.field_created_at, Timestamp.now());
			data.put(AnalyticsDoc.field_created_at_millisec, Timestamp.now().toDate().getTime());			
			
			final ApiFuture<DocumentReference> docRef = collection().document(date).collection(AnalyticsDoc.sub_collection).add(data); 
			docRef.get();
		}catch(Exception e) {
			throw e;
		}
	}

}

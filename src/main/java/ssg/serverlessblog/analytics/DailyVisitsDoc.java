package ssg.serverlessblog.analytics;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class DailyVisitsDoc {
	public final static String collection = "analytics_result_daily";

	public final static String field_date = "date";
	public final static String field_count_all = "count_all";
	public final static String field_count_actual = "count_actual";	
	public final static String field_created_at = "created_at";
}

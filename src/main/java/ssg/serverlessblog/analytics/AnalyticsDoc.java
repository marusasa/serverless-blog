package ssg.serverlessblog.analytics;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class AnalyticsDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "analytics";
	public final static String sub_collection = "analytics-daily";

	public final static String field_visitor_id = "visitor_id";
	public final static String field_path = "path";
	public final static String field_status = "status";
	public final static String field_article_id = "article_id";
	public final static String field_date_string = "date_string";
	public final static String field_created_at = "created_at";
	public final static String field_created_at_millisec = "created_at_millisec";

}

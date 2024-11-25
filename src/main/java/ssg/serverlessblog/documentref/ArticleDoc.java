package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class ArticleDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "articles";

	public final static String field_ref_account_id = "ref_account_id";
	public final static String field_ref_user_id = "ref_user_id";
	public final static String field_title = "title";
	public final static String field_body = "body";
	public final static String field_status = "status";
	public final static String field_created_at = "created_at";
	public final static String field_updated_at = "updated_at";
	public final static String field_published_at = "published_at";
	public final static String field_published_at_millisec = "published_at_millisec";
	public final static String field_summary = "summary";
	public final static String field_summary_ai = "summary_ai";

}

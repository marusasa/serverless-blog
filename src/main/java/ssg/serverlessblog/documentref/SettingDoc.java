package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class SettingDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "settings";

	public final static String field_ref_account_id = "ref_account_id";
	public final static String field_blog_title = "blog_title";
	public final static String field_blog_subtitle = "blog_subtitle";
	public final static String field_icon_url = "icon_url";
	public final static String field_favicon_url = "favicon_url";
	public final static String field_created_at = "created_at";
	public final static String field_updated_at = "updated_at";

}

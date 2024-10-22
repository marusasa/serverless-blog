package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class PageComponentDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "page-components";

	public final static String field_ref_account_id = "ref_account_id";
	public final static String field_comp_type = "comp_type";
	public final static String field_json = "json";
	public final static String field_view_order = "view_order";
	public final static String field_enabled = "enabled";
	public final static String field_created_at = "created_at";
	public final static String field_updated_at = "updated_at";

}

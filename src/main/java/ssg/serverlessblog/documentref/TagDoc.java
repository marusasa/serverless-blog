package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class TagDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "tags";

	public final static String field_name = "name";
	public final static String field_json = "json";
	public final static String field_description = "description";
	public final static String field_created_at = "created_at";
	public final static String field_updated_at = "updated_at";

}

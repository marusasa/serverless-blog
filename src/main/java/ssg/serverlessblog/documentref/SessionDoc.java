package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class SessionDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "sessions";
	
	public final static String field_session_id = "session_id";
	public final static String field_created_at = "created_at";
	public final static String field_updated_at = "updated_at";
	public final static String field_accessed_at = "accessed_at";
	public final static String field_username = "username";
	public final static String field_ref_user_id = "ref_user_id";
	public final static String field_ref_account_id = "ref_account_id";
	

}

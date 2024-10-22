package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class AccountDoc {
	
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "accounts";
	/**
	 * id_ref_name will be used within the program
	 * when handling 'id' of this document.
	 */
	public final static String id_ref_name = "account_id"; 
	
	public final static String field_name = "name";
	public final static String field_created_at = "created_at";
	

}

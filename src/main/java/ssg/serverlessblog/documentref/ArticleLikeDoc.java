package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class ArticleLikeDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "articles_likes";
	
	//id will be the same as articleId;
	public final static String field_like_count = "like_count";
	public final static String field_updated_at = "updated_at";

}

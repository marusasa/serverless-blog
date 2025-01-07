package ssg.serverlessblog.documentref;

/**
 * A class representing the names of collection and document fields
 * for storing data in data store.
 * The program should always use this and other '****Doc' classes
 * instead of hard coding the field name when accessing data store. 
 */
public class ArticleTagDoc {
	/**
	 * Collection name is always plural.
	 */
	public final static String collection = "articles_tags";

	public final static String field_article_id = "article_id";
	public final static String field_tag_id = "tag_id";
	public final static String field_created_at = "created_at";

}

package ssg.serverlessblog.mongodb.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
import ssg.serverlessblog.mongodb.util.MongoDbUtil;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the ArticleDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class ArticleDao implements ArticleDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
	
	private MongoCollection<Document> articles = null;
	private MongoCollection<Document> collection() throws Exception {
		if(articles == null) {
			articles = MongoDbUtil.getDbObj().getCollection(ArticleDoc.collection);
		}
		return articles;
	}
	
	private MongoCollection<Document> articles_likes = null;
	private MongoCollection<Document> collectionLikes() throws Exception {
		if(articles_likes == null) {
			articles_likes = MongoDbUtil.getDbObj().getCollection(ArticleLikeDoc.collection);
		}
		return articles_likes;
	}
	
	private MongoCollection<Document> articles_tags = null;
	private MongoCollection<Document> collectionArticleTags() throws Exception {
		if(articles_tags == null) {
			articles_tags = MongoDbUtil.getDbObj().getCollection(ArticleTagDoc.collection);
		}
		return articles_tags;
	}
	
	@Override
	public List<CloudDocument> getArticleTags(String articleId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		
		MongoCursor<Document> cursor = collectionArticleTags().find(eq(ArticleTagDoc.field_article_id, articleId)).iterator();
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		return result;
	}
	
	
	@Override
	public List<CloudDocument> getArticlesByTag(String tagId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		
		MongoCursor<Document> cursor = collectionArticleTags().find(eq(ArticleTagDoc.field_tag_id, tagId)).iterator();
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		return result;
	}

	@Override
	public boolean isArticleExists(final String articleId) throws Exception {
		boolean result = false;
		Document doc = collection().find(eq("_id", new ObjectId(articleId))).first();
		if(doc != null) {
			result = true;
		}
		return result;

	}

	@Override
	public String createArticle(Map<String, Object> data) throws Exception {
		var doc = new Document();
		doc.putAll(data);		
		InsertOneResult result = collection().insertOne(doc);
		
		return ((BsonObjectId) result.getInsertedId()).getValue().toHexString();
	}
	
	@Override
	public Optional<CloudDocument> getArticleForManage(final String articleId) 
			throws Exception {
				
		Document doc = collection().find(eq("_id", new ObjectId(articleId))).first();
		Optional<CloudDocument> result = Optional.empty();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}else {
			logger.warn("Article document not found: %s".formatted(articleId));
		}
		return result;
	}
	
	@Override
	public Optional<CloudDocument> getArticle(final String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();		
		Document doc = collection().find(eq("_id", new ObjectId(articleId))).first();
		if(doc != null) {
			//likes id is not ObjectId, just regular String that will match 'articleId'.
			Document docLike = collectionLikes().find(eq("_id", articleId)).first();
			long likes = 0l;
			if(docLike != null) {
				likes = docLike.getLong(ArticleLikeDoc.field_like_count);
			}			
			doc.put(ArticleLikeDoc.field_like_count, likes);
			result = Optional.of(new CloudDocument(doc.getObjectId("_id").toHexString(), doc));			
		}else {
			logger.warn("Article document not found: %s".formatted(articleId));
		}	
		return result;
	}

	@Override
	public boolean updateArticle(final String articleId, final Map<String, Object> data) throws Exception {
		Document query = new Document().append("_id",  new ObjectId(articleId)	);
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		UpdateResult result = collection().updateOne(query, updates);
		
		return result.getModifiedCount()>0?true:false;
	}

	@Override
	public List<CloudDocument> getArticlesForManage() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		
		MongoCursor<Document> cursor = collection().find()
                .sort(Sorts.descending(ArticleDoc.field_created_at)).iterator();
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		
		return result;		
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlog( long publishedAtMillisec, int countPerPage) throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		Bson filter = eq(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
		if(publishedAtMillisec > 0) {
			filter = Filters.and(filter, Filters.lt(ArticleDoc.field_published_at_millisec, publishedAtMillisec));
		}
		
		MongoCursor<Document> cursor = collection().find(filter)
                .sort(Sorts.descending(ArticleDoc.field_published_at_millisec))
                .limit(countPerPage+1)
                .iterator();
		
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlogAll() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		Bson filter = eq(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
		
		MongoCursor<Document> cursor = collection().find(filter)
                .sort(Sorts.descending(ArticleDoc.field_published_at_millisec))
                .iterator();
		
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		
		return result;		
	}
	
	

	@Override
	public long getArticlesForBlogTotalCount() throws Exception {
		long result = 0;
		
		Bson filter = eq(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
		result = collection().countDocuments(filter);
		
		return result;
	}



	@Override
	public boolean deleteArticle(final String articleId) throws Exception {
		Bson query = eq("_id", new ObjectId(articleId));		
		DeleteResult result = collection().deleteOne(query);		
		return result.getDeletedCount() > 0?true:false;
	}
	
	@Override
	public boolean deleteArticleTag(final String tagId) throws Exception {
		Bson query = eq("_id", new ObjectId(tagId));		
		DeleteResult result = collectionArticleTags().deleteOne(query);		
		return result.getDeletedCount() > 0?true:false;
	}
	
	@Override
	public void createArticleTag(final Map<String, Object> data) throws Exception {
		var doc = new Document();
		doc.putAll(data);		
		InsertOneResult result = collectionArticleTags().insertOne(doc);
	}
	
	@Override
	public void createArticleLike(String articleId, Map<String, Object> data) throws Exception {		
		var doc = new Document();
		data.put("_id", articleId);
		doc.putAll(data);		
		collectionLikes().insertOne(doc);
	}
	@Override
	public Optional<CloudDocument> getArticleLike(final String articleId) throws Exception{
		Optional<CloudDocument> result = Optional.empty();
		Document doc = collectionLikes().find(eq("_id", articleId)).first();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getString("_id"),doc));
		}
		return result;
	}
	
	@Override
	public boolean updateArticleLike(final String articleId,final Map<String, Object> data) throws Exception{
		Document query = new Document().append("_id",  articleId	);
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		UpdateResult result = collectionLikes().updateOne(query, updates);
		
		return result.getModifiedCount()>0?true:false;
	}
	
	
	
}

package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.AggregateQuery;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
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
	
	private CollectionReference articles = null;
	private CollectionReference collection() throws IOException {
		if(articles == null) {
			articles = FirestoreDbUtil.getFirestoreDbObj().collection(ArticleDoc.collection);
		}
		return articles;
	}
	
	private CollectionReference articles_likes = null;
	private CollectionReference collectionLikes() throws IOException {
		if(articles_likes == null) {
			articles_likes = FirestoreDbUtil.getFirestoreDbObj().collection(ArticleLikeDoc.collection);
		}
		return articles_likes;
	}
	
	private CollectionReference articles_tags = null;
	private CollectionReference collectionArticleTags() throws IOException {
		if(articles_tags == null) {
			articles_tags = FirestoreDbUtil.getFirestoreDbObj().collection(ArticleTagDoc.collection);
		}
		return articles_tags;
	}
	
	@Override
	public List<CloudDocument> getArticleTags(String articleId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final Query query = collectionArticleTags()
					.whereEqualTo(ArticleTagDoc.field_article_id, articleId);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				//make the response light
				final var data = document.getData();
				result.add(new CloudDocument(document.getId(), data));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	
	


	@Override
	public List<CloudDocument> getArticlesByTag(String tagId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final Query query = collectionArticleTags()
					.whereEqualTo(ArticleTagDoc.field_tag_id, tagId);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				final var data = document.getData();
				result.add(new CloudDocument(document.getId(), data));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}



	@Override
	public boolean isArticleExists(final String articleId) throws Exception {
		boolean result = false;
		final DocumentReference docRef = collection().document(articleId);			
		final ApiFuture<DocumentSnapshot> future = docRef.get();
		final DocumentSnapshot document = future.get();
		if(document.exists()) {				
			result = true;
		}
		return result;
	}



	@Override
	public String createArticle(Map<String, Object> data) throws Exception {
		var newId = "";
		
		final ApiFuture<DocumentReference> docRef = collection().add(data);
		newId = docRef.get().getId();
		
		return newId;
	}
	
	@Override
	public Optional<CloudDocument> getArticleForManage(final String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			final DocumentReference docRef = collection().document(articleId);			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			
			//make sure it belongs to this account
			result = Optional.of(new CloudDocument(document.getId(), document.getData()));						
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public Optional<CloudDocument> getArticle(final String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			final DocumentReference docRef = collection().document(articleId);			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(document.exists()) {				
				final DocumentReference docRefLike = collectionLikes().document(articleId);			
				final ApiFuture<DocumentSnapshot> futureLike = docRefLike.get();
				final DocumentSnapshot docLike = futureLike.get();
				final Map<String,Object> data = document.getData();
				long likes = 0l;
				if(docLike.exists()) {
					likes = docLike.getLong(ArticleLikeDoc.field_like_count);
				}
				data.put(ArticleLikeDoc.field_like_count, likes);
				result = Optional.of(new CloudDocument(document.getId(), data));
			}else {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}				
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean updateArticle(final String articleId, final Map<String, Object> data) throws Exception {
		var result = false;
		
		final DocumentReference docRef = collection().document(articleId);			
		final ApiFuture<WriteResult> writeResult = docRef.update(data);
	    writeResult.get();				
		result = true;
		
		return result;
	}

	@Override
	public List<CloudDocument> getArticlesForManage() throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final Query query = collection()
					.orderBy(ArticleDoc.field_created_at, Direction.DESCENDING);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), (DocumentSnapshot)document));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlog( long publishedAtMillisec, int countPerPage) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			Query query = collection()
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH)
					.orderBy(ArticleDoc.field_published_at_millisec, Direction.DESCENDING)
					.limit(countPerPage+1);
			if(publishedAtMillisec > 0) {
				query = query.startAfter(publishedAtMillisec);
			}			
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), (DocumentSnapshot)document));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlogAll() throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH)
					.orderBy(ArticleDoc.field_published_at_millisec, Direction.DESCENDING);			
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), (DocumentSnapshot)document));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	

	@Override
	public long getArticlesForBlogTotalCount() throws Exception {
		long result = 0;
		try {
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
			AggregateQuery countQuery = query.count();
			
			ApiFuture<AggregateQuerySnapshot> future =  countQuery.get();
			result = future.get().getCount();			
		}catch(Exception e) {
			throw e;
		}
		return result;
	}



	@Override
	public boolean deleteArticle(final String articleId) throws Exception {
		var result = false;
		try {
			final DocumentReference docRef = collection().document(articleId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			 			
			ApiFuture<WriteResult> writeResult = docRef.delete();
			writeResult.get();
			result = true;
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public boolean deleteArticleTag(final String tagId) throws Exception {
		var result = false;
		final DocumentReference docRef = collectionArticleTags().document(tagId);					
		final ApiFuture<DocumentSnapshot> future = docRef.get();
		final DocumentSnapshot document = future.get();
		if(document.exists()) {
			final ApiFuture<WriteResult> writeResult = docRef.delete();
			writeResult.get();
			result = true;
		}	
		return result;
	}
	
	@Override
	public void createArticleTag(final Map<String, Object> data) throws Exception {
		final ApiFuture<DocumentReference> docRef = collectionArticleTags().add(data);
		docRef.get();
	}
	
	@Override
	public void createArticleLike(String articleId, Map<String, Object> data) throws Exception {		
		final ApiFuture<WriteResult> wr = collectionLikes().document(articleId).set(data);
		wr.get();
		DocumentReference docRef = collectionLikes().document(articleId);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		future.get();		
	}
	@Override
	public Optional<CloudDocument> getArticleLike(final String articleId) throws Exception{
		Optional<CloudDocument> result = Optional.empty();
		DocumentReference docRef = collectionLikes().document(articleId);			
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if(document.exists()) {
			result = Optional.of(new CloudDocument(document.getId(),document));
		}
		return result;
	}
	
	@Override
	public boolean updateArticleLike(final String articleId,final Map<String, Object> data) throws Exception{
		var result = false;
		DocumentReference docRef = collectionLikes().document(articleId);
		final ApiFuture<WriteResult> writeResult = docRef.update(data);
	    writeResult.get();
	    return result;
	}
	
	
	
}

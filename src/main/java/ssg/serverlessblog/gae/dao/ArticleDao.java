package ssg.serverlessblog.gae.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.ArticleDoc;
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
	
	@Override
	public String createArticle(String accountId,Article article) throws Exception {
		var newId = "";
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			
			//add article to database
			Map<String, Object> data = new HashMap<>();
			data.put(ArticleDoc.field_ref_account_id,accountDocRef);
			data.put(ArticleDoc.field_title, article.title());
			data.put(ArticleDoc.field_body, article.body());
			data.put(ArticleDoc.field_status, article.status());
			data.put(ArticleDoc.field_created_at, Timestamp.now());
			if(article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
				data.put(ArticleDoc.field_published_at, Timestamp.now());
			}else {
				data.put(ArticleDoc.field_published_at, null);
			}
			data.put(ArticleDoc.field_updated_at, null);
			
			ApiFuture<DocumentReference> docRef = db.collection(ArticleDoc.collection).add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

	@Override
	public Optional<CloudDocument> getArticle(String accountId,String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			DocumentReference docRef = db.collection(ArticleDoc.collection).document(articleId);			
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			
			//make sure it belongs to this account
			DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				result = Optional.of(new CloudDocument(document.getId(), document.getData()));
			}else {
				logger.warn("Article %s not authorized with account %s".formatted(articleId,accountId));
				return result;
			}						
		}
		return result;
	}

	@Override
	public boolean updateArticle(String accountId, Article article) throws Exception {
		var result = false;
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			DocumentReference docRef = db.collection(ArticleDoc.collection).document(article.articleId());
			
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(article.articleId()));
				return result;
			}
			
			DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				//update document
				var oldStatus = document.getString(ArticleDoc.field_status);
				Map<String, Object> updates = new HashMap<>();
				updates.put(ArticleDoc.field_title, article.title());
				updates.put(ArticleDoc.field_body, article.body());
				updates.put(ArticleDoc.field_status, article.status());
				updates.put(ArticleDoc.field_updated_at, Timestamp.now());
				
				if(oldStatus.equals(AppConst.ART_STATUS_DRAFT) && article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
					updates.put(ArticleDoc.field_published_at, Timestamp.now());
				}
				ApiFuture<WriteResult> writeResult = docRef.update(updates);
			    writeResult.get();				
				result = true;
			}else {
				logger.warn("Article %s not authorized with account %s".formatted(article.articleId(),accountId));
			}
		}
		return result;
	}

	@Override
	public List<CloudDocument> getArticlesForManage(String accountId) throws Exception {
		List<CloudDocument> result = new ArrayList<CloudDocument>();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			Query query = db.collection(ArticleDoc.collection)
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef);
			
			ApiFuture<QuerySnapshot> future = query.get();
			QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), document.getData()));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlog(String accountId) throws Exception {
		List<CloudDocument> result = new ArrayList<CloudDocument>();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			Query query = db.collection(ArticleDoc.collection)
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
			
			ApiFuture<QuerySnapshot> future = query.get();
			QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), document.getData()));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean deleteArticle(String accountId, String articleId) throws Exception {
		var result = false;
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			DocumentReference docRef = db.collection(ArticleDoc.collection).document(articleId);
			
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			
			DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(!refAccount.equals(accountDocRef)) {
				logger.warn("Article %s not authorized with account %s".formatted(articleId,accountId));
				return result;
			}		
			
			ApiFuture<WriteResult> writeResult = docRef.delete();
			writeResult.get();
			result = true;
		}
		return result;
	}

}

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

import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the PageComponentDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class PageComponentDao implements PageComponentDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentDaoInt.class);
	
	@Override
	public Optional<CloudDocument> getPageComponent(String accountId, String pageComponentId) throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			
			DocumentReference docRef = db.collection(PageComponentDoc.collection).document(pageComponentId);
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			
			DocumentReference refAccount = (DocumentReference)document.get(PageComponentDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				//make sure this document belongs to this user.
				result = Optional.of(new CloudDocument(document.getId(), document.getData()));
			}else {
				logger.warn("PageComponent %s not authorized with account %s".formatted(pageComponentId,accountId));
				return result;
			}
		}catch(Exception e) {
			throw e;
		}
		
		return result;
	}
	
	@Override
	public List<CloudDocument> getPageComponents(String accountId) throws Exception {
		List<CloudDocument> result = new ArrayList<>();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			
			Query query = db.collection(PageComponentDoc.collection)
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.orderBy(PageComponentDoc.field_view_order);
			
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
	public boolean updatePageComponent(String accountId, String pageComponentId, String json,
			long order, boolean enabled) throws Exception {
		var result = false;
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			DocumentReference docRef = db.collection(PageComponentDoc.collection).document(pageComponentId);
			
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			
			DocumentReference refAccount = (DocumentReference)document.get(PageComponentDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				//update document
				Map<String, Object> updates = new HashMap<>();
				updates.put(PageComponentDoc.field_json, json);
				updates.put(PageComponentDoc.field_view_order, order);
				updates.put(PageComponentDoc.field_enabled, enabled);
				updates.put(PageComponentDoc.field_updated_at, Timestamp.now());
				ApiFuture<WriteResult> writeResult = docRef.update(updates);
			    writeResult.get();				
				result = true;
			}else {
				logger.warn("PageComponent %s not authorized with account %s".formatted(pageComponentId,accountId));
			}
		}
		return result;
	}

	@Override
	public String createPageComponent(String accountId, String type, String json, long order, boolean enabled) throws Exception {
		var newId = "";
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = db.collection(AccountDoc.collection).document(accountId);
			
			//add article to database
			Map<String, Object> data = new HashMap<>();
			data.put(PageComponentDoc.field_ref_account_id,accountDocRef);
			data.put(PageComponentDoc.field_comp_type, type);
			data.put(PageComponentDoc.field_json, json);
			data.put(PageComponentDoc.field_view_order, order);
			data.put(PageComponentDoc.field_enabled, enabled);
			data.put(PageComponentDoc.field_created_at, Timestamp.now());
			data.put(PageComponentDoc.field_updated_at, null);
			
			ApiFuture<DocumentReference> docRef = db.collection(PageComponentDoc.collection).add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

}

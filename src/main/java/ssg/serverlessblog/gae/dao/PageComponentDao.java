package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

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
	
	private CollectionReference pageComponents = null;
	private CollectionReference collection() throws IOException {
		if(pageComponents == null) {
			pageComponents = FirestoreDbUtil.getFirestoreDbObj().collection(PageComponentDoc.collection);
		}
		return pageComponents;
	}
	
	@Override
	public boolean deletePageComponent(String accountId, String pageComponentId) throws Exception {
		var result = false;
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final DocumentReference docRef = collection().document(pageComponentId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			
			final DocumentReference refAccount = (DocumentReference)document.get(PageComponentDoc.field_ref_account_id); 
			if(!refAccount.equals(accountDocRef)) {
				logger.warn("Article %s not authorized with account %s".formatted(pageComponentId,accountId));
				return result;
			}		
			
			final ApiFuture<WriteResult> writeResult = docRef.delete();
			writeResult.get();
			result = true;
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public Optional<CloudDocument> getPageComponent(String accountId, String pageComponentId) throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			
			final DocumentReference docRef = collection().document(pageComponentId);
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			
			final DocumentReference refAccount = (DocumentReference)document.get(PageComponentDoc.field_ref_account_id); 
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
		final List<CloudDocument> result = new ArrayList<>();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.orderBy(PageComponentDoc.field_view_order);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
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
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final DocumentReference docRef = collection().document(pageComponentId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			
			final DocumentReference refAccount = (DocumentReference)document.get(PageComponentDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				//update document
				final Map<String, Object> updates = new HashMap<>();
				updates.put(PageComponentDoc.field_json, json);
				updates.put(PageComponentDoc.field_view_order, order);
				updates.put(PageComponentDoc.field_enabled, enabled);
				updates.put(PageComponentDoc.field_updated_at, Timestamp.now());
				final ApiFuture<WriteResult> writeResult = docRef.update(updates);
			    writeResult.get();				
				result = true;
			}else {
				logger.warn("PageComponent %s not authorized with account %s".formatted(pageComponentId,accountId));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public String createPageComponent(String accountId, String type, String json, long order, boolean enabled) throws Exception {
		var newId = "";
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			
			//add article to database
			final Map<String, Object> data = new HashMap<>();
			data.put(PageComponentDoc.field_ref_account_id,accountDocRef);
			data.put(PageComponentDoc.field_comp_type, type);
			data.put(PageComponentDoc.field_json, json);
			data.put(PageComponentDoc.field_view_order, order);
			data.put(PageComponentDoc.field_enabled, enabled);
			data.put(PageComponentDoc.field_created_at, Timestamp.now());
			data.put(PageComponentDoc.field_updated_at, null);
			
			final ApiFuture<DocumentReference> docRef = collection().add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

}

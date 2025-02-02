package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

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
	public boolean deletePageComponent( String pageComponentId) throws Exception {
		var result = false;
		try {
			final DocumentReference docRef = collection().document(pageComponentId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
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
	public Optional<CloudDocument> getPageComponent(String pageComponentId) throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			
			final DocumentReference docRef = collection().document(pageComponentId);
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
//			final Timestamp ts = (Timestamp)document.get(PageComponentDoc.field_created_at);
//			System.out.println(ts.toString());						
			result = Optional.of(new CloudDocument(document.getId(), document.getData()));
			
		}catch(Exception e) {
			throw e;
		}
		
		return result;
	}
	
	@Override
	public List<CloudDocument> getPageComponents() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		try {
			
			final Query query = collection()
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
	public boolean updatePageComponent(String pageComponentId, Map<String, Object> updates) throws Exception {
		var result = false;
		try {
			final DocumentReference docRef = collection().document(pageComponentId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("PageComponent document not found: %s".formatted(pageComponentId));
				return result;
			}
			 
			//update document
			final ApiFuture<WriteResult> writeResult = docRef.update(updates);
		    writeResult.get();				
			result = true;
		
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public String createPageComponent(Map<String, Object> data) throws Exception {
		var newId = "";
		try {
			final ApiFuture<DocumentReference> docRef = collection().add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

}

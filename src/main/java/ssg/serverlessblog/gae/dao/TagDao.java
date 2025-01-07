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
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.interfaces.TagDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the PageComponentDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class TagDao implements TagDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentDaoInt.class);
	
	private CollectionReference tags = null;
	private CollectionReference collection() throws IOException {
		if(tags == null) {
			tags = FirestoreDbUtil.getFirestoreDbObj().collection(TagDoc.collection);
		}
		return tags;
	}
	@Override
	public String createTag(String name, String json) throws Exception {
		var newId = "";
		try {
			//add tag to database
			final Map<String, Object> data = new HashMap<>();
			data.put(TagDoc.field_name, name);
			data.put(TagDoc.field_json, json);
			data.put(TagDoc.field_description, "");
			data.put(TagDoc.field_created_at, Timestamp.now());
			data.put(TagDoc.field_updated_at, null);
			
			final ApiFuture<DocumentReference> docRef = collection().add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}
	@Override
	public boolean updateTag(String tagId, String name, String json, String description) throws Exception {
		var result = false;
		try {
			final DocumentReference docRef = collection().document(tagId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Tag document not found: %s".formatted(tagId));
				return result;
			}
			 
			//update document
			final Map<String, Object> updates = new HashMap<>();
			updates.put(TagDoc.field_name, name);
			updates.put(TagDoc.field_json, json);
			updates.put(TagDoc.field_description, description);
			updates.put(TagDoc.field_updated_at, Timestamp.now());
			final ApiFuture<WriteResult> writeResult = docRef.update(updates);
		    writeResult.get();				
			result = true;
		
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	@Override
	public List<CloudDocument> getTags() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		try {
			final Query query = collection()
					.orderBy(TagDoc.field_name);
			
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
	public Optional<CloudDocument> getTag(String tagId) throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			
			final DocumentReference docRef = collection().document(tagId);
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			
			if(!document.exists()) {
				logger.warn("Tag document not found: %s".formatted(tagId));
				return result;
			}
						
			result = Optional.of(new CloudDocument(document.getId(), document.getData()));
			
		}catch(Exception e) {
			throw e;
		}
		
		return result;
	}
	@Override
	public boolean deleteTag(String tagId) throws Exception {
		var result = false;
		try {
			final DocumentReference docRef = collection().document(tagId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Tag document not found: %s".formatted(tagId));
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
	
	
}

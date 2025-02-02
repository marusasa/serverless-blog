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
	public String createTag(final Map<String, Object> data) throws Exception {
		final ApiFuture<DocumentReference> docRef = collection().add(data);
		return docRef.get().getId();
	}
	@Override
	public void updateTag(String tagId, Map<String, Object> data) throws Exception {
		final DocumentReference docRef = collection().document(tagId);		
		final ApiFuture<WriteResult> writeResult = docRef.update(data);
	    writeResult.get();
	}
	@Override
	public List<CloudDocument> getTags() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		final Query query = collection()
				.orderBy(TagDoc.field_name);
		
		final ApiFuture<QuerySnapshot> future = query.get();
		final QuerySnapshot qs = future.get();
			
		for (QueryDocumentSnapshot document : qs.getDocuments()) {
			result.add(new CloudDocument(document.getId(), document));
		}
		return result;
	}
	@Override
	public Optional<CloudDocument> getTag(String tagId) throws Exception {
		Optional<CloudDocument> result = Optional.empty();
			
		final DocumentReference docRef = collection().document(tagId);
		final ApiFuture<DocumentSnapshot> future = docRef.get();
		final DocumentSnapshot document = future.get();
		
		if(document.exists()) {
			result = Optional.of(new CloudDocument(document.getId(), document));
		}
		
		return result;
	}
	@Override
	public void deleteTag(String tagId) throws Exception {
		final DocumentReference docRef = collection().document(tagId);				
		final ApiFuture<WriteResult> writeResult = docRef.delete();
		writeResult.get();
	}
	
	
}

package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.SettingDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the SettingDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class SettingDao implements SettingDaoInt{	
	
	private CollectionReference settings = null;
	private CollectionReference collection() throws IOException {
		if(settings == null) {
			settings = FirestoreDbUtil.getFirestoreDbObj().collection(SettingDoc.collection);
		}
		return settings;
	}
	
	@Override
	public void updateSetting(String settingId, Map<String, Object> data) throws Exception{
			final DocumentReference docRef = collection().document(settingId);	
			
			final ApiFuture<WriteResult> writeResult = docRef.update(data);
			writeResult.get();			
	}
	
	
	
	@Override
	public void createSetting(Map<String, Object> data) throws Exception {
		final ApiFuture<DocumentReference> docRef = collection().add(data);
		docRef.get();
	}

	@Override
	public Optional<CloudDocument> getSetting() throws Exception{
		Optional<CloudDocument> result = Optional.empty();
		try {			
			final Query query = collection();
			
			//get Settings document
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
			final DocumentSnapshot document = qs.getDocuments().get(0);	//a user should only have one settings document
			result = Optional.of(new CloudDocument(document.getId(),document));
		}catch(Exception e) {
			throw e;
		}
		return result;
	}	
	
}

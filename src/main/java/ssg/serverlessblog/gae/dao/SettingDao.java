package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.data_json.Setting;
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
	public void updateSetting(Setting setting) throws Exception{
		try {
			
			//Search document where account id and setting id matches.
			final Query query = collection()					
					.whereEqualTo(FieldPath.documentId(), setting.settingId());
			
			//get Settings document
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
			if(qs.getDocuments().size() > 0) {
				final DocumentSnapshot document = qs.getDocuments().get(0);	//a user should only have one settings document
				
				//update data.
				final Map<String, Object> data = new HashMap<>();
				data.put(SettingDoc.field_blog_title, setting.blogTitle());
				data.put(SettingDoc.field_blog_subtitle, setting.blogSubTitle());
				data.put(SettingDoc.field_updated_at, Timestamp.now());
				data.put(SettingDoc.field_icon_url, setting.iconUrl());
				data.put(SettingDoc.field_favicon_url, setting.faviconUrl());
				
				final ApiFuture<WriteResult> writeResult = document.getReference().update(data);
				writeResult.get();
			}else {
				throw new Exception("Setting data not found.");
			}
		}catch(Exception e) {
			throw e;
		}
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
			result = Optional.of(new CloudDocument(document.getId(),document.getData()));
		}catch(Exception e) {
			throw e;
		}
		return result;
	}	
	
}

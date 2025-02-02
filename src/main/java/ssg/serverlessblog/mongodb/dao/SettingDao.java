package ssg.serverlessblog.mongodb.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.interfaces.SettingDaoInt;
import ssg.serverlessblog.mongodb.util.MongoDbUtil;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the SettingDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class SettingDao implements SettingDaoInt{	
	
	private MongoCollection<Document> settings = null;
	private MongoCollection<Document> collection() throws Exception {
		if(settings == null) {
			settings = MongoDbUtil.getDbObj().getCollection(SettingDoc.collection);
		}
		return settings;
	}
	
	@Override
	public void updateSetting(String settingId, Map<String, Object> data) throws Exception{
			
		Document query = new Document().append("_id",  new ObjectId(settingId)	);
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		collection().updateOne(query, updates);
			
	}
	
	
	
	@Override
	public void createSetting(Map<String, Object> data) throws Exception {
		var doc = new Document();
		doc.putAll(data);		
		collection().insertOne(doc);
	}

	@Override
	public Optional<CloudDocument> getSetting() throws Exception{
		Optional<CloudDocument> result = Optional.empty();
		Document doc = collection().find().first();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		return result;
		
	}	
	
}

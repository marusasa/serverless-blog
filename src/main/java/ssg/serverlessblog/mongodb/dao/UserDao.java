package ssg.serverlessblog.mongodb.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.mongodb.util.MongoDbUtil;
import ssg.serverlessblog.util.CloudDocument;



/**
 * Data Access Object.
 * MongoDB implementation of the UserDao. 
 */
public class UserDao implements UserDaoInt {

	private MongoCollection<Document> users = null;
	private MongoCollection<Document> collection() throws Exception {
		if(users == null) {
			users = MongoDbUtil.getDbObj().getCollection(UserDoc.collection);
		}
		return users;
	}

	
	@Override
	public Optional<CloudDocument> getUser(String username) throws Exception{
		MongoCollection<Document> collection = collection();
		Document doc = collection.find(eq("_id", username)).first();
		Optional<CloudDocument> result = Optional.empty();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getString("_id"),doc));
		}
		return result;
	}


	@Override
	public void createUser(String userName, Map<String, Object> data) throws Exception {
		var doc = new Document();
		data.put("_id", userName);
		doc.putAll(data);		
		collection().insertOne(doc);		
	}
}

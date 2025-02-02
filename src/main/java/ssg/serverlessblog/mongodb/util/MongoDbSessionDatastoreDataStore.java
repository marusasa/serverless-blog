package ssg.serverlessblog.mongodb.util;

import static com.mongodb.client.model.Filters.eq;

import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;

import ssg.serverlessblog.documentref.SessionDoc;


/**
 * Jetty Session Data Store object implemented using Google Cloud Firestore
 * as a persistent data layer.
 */
public class MongoDbSessionDatastoreDataStore implements SessionDataStore {

	private static final Logger logger = LoggerFactory.getLogger(MongoDbSessionDatastoreDataStore.class.getName());
	private static MongoCollection<Document> sessions;
	
	@Override
	public void initialize(SessionContext context) throws Exception {
		logger.info("initialize");
		try {
			sessions = MongoDbUtil.getDbObj().getCollection(SessionDoc.collection);
		}catch(Exception e) {
			logger.error("Error initializing Mongo DB for session handling. %s".formatted(e.getMessage()));
			e.printStackTrace();
		}
		
	}

	@Override
	public SessionData load(String id) throws Exception {
		final SessionData d = new SessionData(id, "", "", 0, 0, 0,0);
		Document doc = null;
		try {
			doc = sessions.find(eq("_id", id)).first();
		}catch(Exception e) {
			//do nothing by design.
		}
		
		if(doc == null) {
			return null;
		}
		
		doc.entrySet().forEach(obj -> {
			d.setAttribute(obj.getKey(), obj.getValue());
		});
		
		Bson updates = Updates.combine(
                Updates.set(SessionDoc.field_accessed_at, new Date()));
		
		sessions.updateOne(eq("_id", id), updates);
		        
		return d;
	}

	@Override
	public void store(String id, SessionData sd) throws Exception {
		Document doc = null;
		try {
			doc = sessions.find(eq("_id", id)).first();
		}catch(Exception e) {
			//do nothing by design.
		}
		
		if(doc == null) {
			return;
		}

		final Map<String, Object> data = new HashMap<>();
		sd.getAllAttributes().forEach((k,v) -> {
			data.put(k, v);
		});
		data.put(SessionDoc.field_updated_at, new Date());
		
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		
		sessions.updateOne(eq("_id", id), updates);
	}

	@Override
	public boolean delete(String id) throws Exception {		
		Bson query = eq("_id", id);		
		DeleteResult result = sessions.deleteOne(query);		
		return result.getDeletedCount() > 0?true:false;		
	}

	@Override
	public void start() throws Exception {
		//Do nothing by design.
	}

	@Override
	public void stop() throws Exception {
		//Do nothing by design.

	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public boolean isStarted() {
		return true;
	}

	@Override
	public boolean isStarting() {
		return false;
	}

	@Override
	public boolean isStopping() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public boolean addEventListener(EventListener listener) {
		return false;
	}

	@Override
	public boolean removeEventListener(EventListener listener) {
		return false;
	}

	@Override
	public SessionData newSessionData(String id, long created, long accessed, long lastAccessed, long maxInactiveMs) {
		SessionData d = new SessionData(id, "", "", created, accessed, lastAccessed, maxInactiveMs);
		final Map<String, Object> data = new HashMap<>();
		data.put(SessionDoc.field_created_at, new Date());
		data.put(SessionDoc.field_updated_at, null);
		data.put(SessionDoc.field_accessed_at, null);
						
		try {
			var doc = new Document();
			data.put("_id", id);
			doc.putAll(data);		
			sessions.insertOne(doc);
		} catch (Exception e) {
			logger.error("Error creating new session data.", e);
			d = null;
		}
		return d;
	}

	@Override
	public Set<String> getExpired(Set<String> candidates) {
		return Collections.emptySet();
	}

	@Override
	public boolean isPassivating() {
		return false;
	}

	@Override
	public boolean exists(String id) throws Exception {
		Document doc = sessions.find(eq("_id", id)).first();
		return doc == null?false:true;		
	}

}

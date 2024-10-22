package ssg.serverlessblog.gae.util;

import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.documentref.SessionDoc;


/**
 * Jetty Session Data Store object implemented using Google Cloud Firestore
 * as a persistent data layer.
 */
public class FirestoreDataStore implements SessionDataStore {

	private static final Logger logger = LoggerFactory.getLogger(FirestoreDataStore.class.getName());
	private static Firestore firestore;
	private static CollectionReference sessions;
	
	@Override
	public void initialize(SessionContext context) throws Exception {
		logger.info("initialize");
		try {
			firestore = FirestoreDbUtil.getFirestoreDbObj();
			sessions = firestore.collection(SessionDoc.collection);
		}catch(Exception e) {
			logger.error("Error initializing Firestore for session handling. %s".formatted(e.getMessage()));
			e.printStackTrace();
		}
		
	}

	@Override
	public SessionData load(String id) throws Exception {
		final SessionData d = new SessionData(id, "", "", 0, 0, 0,0);
		
		DocumentReference docRef = sessions.document(id);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if(!document.exists()) {
			return null;
		}
		Map<String,Object> data =  document.getData();
		data.forEach((k,v) -> {
			d.setAttribute(k, v);
		});
		
		Map<String, Object> updates = new HashMap<>();
        updates.put(SessionDoc.field_accessed_at, Timestamp.now());
        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        writeResult.get();
        
		return d;
	}

	@Override
	public void store(String id, SessionData sd) throws Exception {
		DocumentReference docRef = sessions.document(id);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if(!document.exists()) {
			return;
		}
		
		Map<String, Object> updates = new HashMap<>();
		
		sd.getAllAttributes().forEach((k,v) -> {
			updates.put(k, v);
		});
		
		updates.put(SessionDoc.field_updated_at, Timestamp.now());
		ApiFuture<WriteResult> writeResult = docRef.update(updates);
        writeResult.get();		
	}

	@Override
	public boolean delete(String id) throws Exception {
		
		DocumentReference docRef = sessions.document(id);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if(!document.exists()) {
			return false;
		}
		
		ApiFuture<WriteResult> writeResult = docRef.delete();
        writeResult.get();        
		return true;
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
		Map<String, Object> data = new HashMap<>();
		data.put(SessionDoc.field_created_at, Timestamp.now());
		data.put(SessionDoc.field_updated_at, null);
		data.put(SessionDoc.field_accessed_at, null);
		ApiFuture<WriteResult> writeResult = sessions.document(id).set(data);
		
		try {
			writeResult.get();
		} catch (InterruptedException e) {
			logger.error("Error creating new session data.", e);
			d = null;
		} catch (ExecutionException e) {
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
		DocumentReference docRef = sessions.document(id);
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		if(document.exists()) {
			return true;
		}else {
			return false;
		}
	}

}

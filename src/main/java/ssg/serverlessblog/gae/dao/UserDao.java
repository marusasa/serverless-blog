package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the UserDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class UserDao implements UserDaoInt {

	private CollectionReference users = null;
	private CollectionReference collection() throws IOException {
		if(users == null) {
			users = FirestoreDbUtil.getFirestoreDbObj().collection(UserDoc.collection);
		}
		return users;
	}
	
	@Override
	public Optional<CloudDocument> getUser(String username) throws Exception{
		Optional<CloudDocument> result = Optional.empty();
		final ApiFuture<DocumentSnapshot> future = collection().document(username).get();
		final DocumentSnapshot docSnapshot = future.get();
		if(docSnapshot.exists()) {
			result = Optional.of(new CloudDocument(docSnapshot.getId(),docSnapshot));
		}
		return result;
	}

	@Override
	public void createUser(String userName, Map<String, Object> data) throws Exception {
		final ApiFuture<WriteResult> future = collection().document(userName).set(data);
		future.get();
	}

	
}

package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.Optional;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.util.PasswordUtil;

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
	public boolean login(String username, String password) throws Exception {
		boolean result = false;
		try {
			
			final ApiFuture<DocumentSnapshot> future = collection().document(username).get();
			final DocumentSnapshot docSnapshot = future.get();
			if(docSnapshot.exists()) {
				//user found.
				final String passInDataStore = docSnapshot.getString(UserDoc.field_password);
				//hash the provided password using a 'salt' value retrieved from data store.
				final String passHash = PasswordUtil.hashPassword(password, 
						docSnapshot.getString(UserDoc.field_salt)).get();
				//compare the hashed value.
				if(passInDataStore.equals(passHash)) {
					//username and password match.
					result = true;
				}	
			}
		}catch(Exception e) {
			throw e;
		}
		
		return result;
	}

}

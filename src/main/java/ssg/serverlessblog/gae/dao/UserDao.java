package ssg.serverlessblog.gae.dao;

import java.util.Optional;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

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

	@Override
	public Optional<String> login(String username, String password) throws Exception {
		Optional<String> result = Optional.empty();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			
			ApiFuture<DocumentSnapshot> future = db.collection(UserDoc.collection).document(username).get();
			DocumentSnapshot docSnapshot = future.get();
			if(docSnapshot.exists()) {
				//user found.
				String passInDataStore = docSnapshot.getString(UserDoc.field_password);
				//hash the provided password using a 'salt' value retrieved from data store.
				String passHash = PasswordUtil.hashPassword(password, 
						docSnapshot.getString(UserDoc.field_salt)).get();
				//compare the hashed value.
				if(passInDataStore.equals(passHash)) {
					//username and password match.
					//return account id
					String accountId = ((DocumentReference)docSnapshot.get(UserDoc.field_ref_account_id)).getId();
					result = Optional.of(accountId);										
				}	
			}
		}
		
		return result;
	}

}

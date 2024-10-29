package ssg.serverlessblog.gae.dao;

import java.io.IOException;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;

import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;

/**
 * Data Access Object for account data.
 * At the moment, only used within gae.dao package.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class AccountDao  {

	private static CollectionReference accounts = null;
	private static CollectionReference collection() throws IOException {
		if(accounts == null) {
			accounts = FirestoreDbUtil.getFirestoreDbObj().collection(AccountDoc.collection);
		}
		return accounts;
	}
	
	public static DocumentReference getAccountDocRef(String accountId) throws Exception{
		return collection().document(accountId);
	}	

}

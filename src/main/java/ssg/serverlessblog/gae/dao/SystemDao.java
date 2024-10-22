package ssg.serverlessblog.gae.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.SystemDaoInt;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.PasswordUtil;
import ssg.serverlessblog.util.SampleDataUtil;

/**
 * Data Access Object.
 * Google Cloud implementation of the SystemDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class SystemDao implements SystemDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(SystemDao.class);

	@Override
	public int getAccountsSize() throws Exception {
		int result = -1;
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			ApiFuture<QuerySnapshot> q = db.collection(AccountDoc.collection).limit(2).get();
			result = q.get().size();
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	

	@Override
	public String getSingleTenantAccoundId() throws Exception {
		var accountId = "";
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			ApiFuture<QuerySnapshot> q = db.collection(AccountDoc.collection).limit(1).get();
			accountId = q.get().getDocuments().getFirst().getId();
		}catch(Exception e) {
			throw e;
		}
		return accountId;
	}



	@Override
	public String createInitialSystemData() throws Exception {
		var accountId = "";
		final ObjectMapper mapper = new ObjectMapper();
		try (Firestore db = FirestoreDbUtil.getFirestoreDbObj();){
			DocumentReference accountDocRef = null;
			//create account id.
			{
				Map<String, Object> data = new HashMap<>();
				data.put(AccountDoc.field_name,"default account");
				data.put(AccountDoc.field_created_at, Timestamp.now());
				ApiFuture<DocumentReference> docRef = db.collection(AccountDoc.collection).add(data);
				accountDocRef = docRef.get();
				logger.info("Account data created.");
				accountId = accountDocRef.getId();
			}
			//create user data.
			{
				Map<String, Object> data = new HashMap<>();
				data.put(UserDoc.field_ref_account_id, accountDocRef);
				Optional<String> salt = PasswordUtil.generateSalt(30);
				Optional<String> password = PasswordUtil.hashPassword("blogPass",salt.get());
				data.put(UserDoc.field_salt, salt.get());
				data.put(UserDoc.field_password, password.get());
				data.put(UserDoc.field_created_at, Timestamp.now());
				data.put(UserDoc.field_updated_at, Timestamp.now());
				ApiFuture<WriteResult> future = db.collection(UserDoc.collection).document("admin").set(data);
				future.get();
				logger.info("User data created.");
			}
			//Create setting data.
			{
				Map<String, Object> data = new HashMap<>();
				data.put(SettingDoc.field_ref_account_id, accountDocRef);
				data.put(SettingDoc.field_blog_title, "My Blog");
				data.put(SettingDoc.field_blog_subtitle, "Subtitle goes here");
				data.put(SettingDoc.field_created_at, Timestamp.now());
				data.put(SettingDoc.field_updated_at, null);					
				ApiFuture<DocumentReference> docRef = db.collection(SettingDoc.collection).add(data);
				docRef.get();
				logger.info("Setting data created.");
			}
			//Create sample article.
			{
				Env.articleDao.createArticle(getSingleTenantAccoundId(), SampleDataUtil.getSampleArticle());
			}
			//Create Page components.
			{
				//profile picture
				Env.pageComponentDao.createPageComponent(accountId, AppConst.PC_TYPE_PROFILE_PIC,
						mapper.writeValueAsString(SampleDataUtil.getSampleProfilePic()), 10L, true);				
			}
			{
				//Link List
				Env.pageComponentDao.createPageComponent(accountId, AppConst.PC_TYPE_LINK_LIST,
						mapper.writeValueAsString(SampleDataUtil.getSampleLinkList()), 20L, true);
			}
		}catch(Exception e) {
			throw e;
		}
		return accountId;
	}
	
	
}

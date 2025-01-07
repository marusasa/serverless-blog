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
	public String createInitialSystemData() throws Exception {
		var accountId = "";
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Firestore db = FirestoreDbUtil.getFirestoreDbObj();			
			//create user data.
			var pass = "";
			{
				final Map<String, Object> data = new HashMap<>();
				final Optional<String> salt = PasswordUtil.generateSalt(30);
				
				//generate password
				pass = "blogPass_" + PasswordUtil.generateSalt(5).get();
				
				final Optional<String> password = PasswordUtil.hashPassword(pass,salt.get());
				data.put(UserDoc.field_salt, salt.get());
				data.put(UserDoc.field_password, password.get());
				data.put(UserDoc.field_created_at, Timestamp.now());
				data.put(UserDoc.field_updated_at, Timestamp.now());
				final ApiFuture<WriteResult> future = db.collection(UserDoc.collection).document("admin").set(data);
				future.get();
				logger.info("User data created.");
			}
			//Create setting data.
			{
				final Map<String, Object> data = new HashMap<>();
				data.put(SettingDoc.field_blog_title, "My Blog");
				data.put(SettingDoc.field_blog_subtitle, "Subtitle goes here");
				data.put(SettingDoc.field_created_at, Timestamp.now());
				data.put(SettingDoc.field_icon_url, "");
				data.put(SettingDoc.field_favicon_url, "");
				data.put(SettingDoc.field_updated_at, null);
				data.put(SettingDoc.field_gae_ai_project_id, "");
				data.put(SettingDoc.field_gae_ai_location, "");
				final ApiFuture<DocumentReference> docRef = db.collection(SettingDoc.collection).add(data);
				docRef.get();
				logger.info("Setting data created.");
			}
			//Create sample article.
			{
				Env.articleDao.createArticle(SampleDataUtil.getSampleArticle(pass));
			}
			//Create Page components.
			{
				//profile picture
				Env.pageComponentDao.createPageComponent(AppConst.PC_TYPE_PROFILE_PIC,
						mapper.writeValueAsString(SampleDataUtil.getSampleProfilePic()), 20L, true);				
			}
			{
				//Link List
				Env.pageComponentDao.createPageComponent(AppConst.PC_TYPE_LINK_LIST,
						mapper.writeValueAsString(SampleDataUtil.getSampleLinkList()), 30L, true);
			}
			{
				//Tags
				Env.pageComponentDao.createPageComponent(AppConst.PC_TYPE_TAGS,
						"{}", 10L, true);
			}
		}catch(Exception e) {
			throw e;
		}
		return accountId;
	}
	
	
}

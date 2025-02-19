package ssg.serverlessblog.system;

import org.eclipse.jetty.server.session.SessionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssg.serverlessblog.interfaces.AiDaoInt;
import ssg.serverlessblog.interfaces.AnalyticsDaoInt;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.interfaces.SettingDaoInt;
import ssg.serverlessblog.interfaces.StorageDaoInt;
import ssg.serverlessblog.interfaces.TagDaoInt;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.AppProperties;

/**
 * This Blog platform is intended to support multiple Serverless Cloud environments.
 * This class uses reflection to determine the implementation that exists in the 
 * classpath.
 * 
 * In addition, it will provide some environment specific methods.
 */
public class Env {

	static Logger logger = LoggerFactory.getLogger(Env.class);
	
	static private boolean isGae = false;
	static private boolean isMongoDb = false;
	static {
		if(AppProperties.getString("env.database").equals(AppConst.DB_FIRESTORE)) { 
			isGae = true;
		}
		if(AppProperties.getString("env.database").equals(AppConst.DB_MONGO)) {
			isMongoDb = true;
		}
	}
	
	static public SettingDaoInt settingDao = null; 
	static public ArticleDaoInt articleDao = null;
	static public UserDaoInt userDao = null;
	static public PageComponentDaoInt pageComponentDao = null;
	static public DataUtilInt dataUtil = null;
	static public SessionDataStore noSqlSessionDataStore = null;
	static public AnalyticsDaoInt analyticsDao = null;
	static public StorageDaoInt storageDao = null;
	static public TagDaoInt tagDao = null;
	static public AiDaoInt aiDao = null;
	
	static {
		try {
			if(isGae) { 
				userDao =  (UserDaoInt)getClassObject("ssg.serverlessblog.gae.dao.UserDao");
				pageComponentDao =  (PageComponentDaoInt)getClassObject("ssg.serverlessblog.gae.dao.PageComponentDao");		
				settingDao =  (SettingDaoInt)getClassObject("ssg.serverlessblog.gae.dao.SettingDao");	
				tagDao = (TagDaoInt)getClassObject("ssg.serverlessblog.gae.dao.TagDao");	
				dataUtil = (DataUtilInt)getClassObject("ssg.serverlessblog.gae.util.FirestoreDbUtil");
				articleDao =  (ArticleDaoInt)getClassObject("ssg.serverlessblog.gae.dao.ArticleDao");
				noSqlSessionDataStore = (SessionDataStore)getClassObject("ssg.serverlessblog.gae.util.FirestoreDataStore");
				//following is only supported in GAE.
				analyticsDao = (AnalyticsDaoInt)getClassObject("ssg.serverlessblog.gae.dao.AnalyticsDao");
				storageDao = (StorageDaoInt)getClassObject("ssg.serverlessblog.gae.dao.StorageDao");
				aiDao = (AiDaoInt)getClassObject("ssg.serverlessblog.gae.dao.AiDao");
			}
			if(isMongoDb) {
				userDao =  (UserDaoInt)getClassObject("ssg.serverlessblog.mongodb.dao.UserDao");
				pageComponentDao =  (PageComponentDaoInt)getClassObject("ssg.serverlessblog.mongodb.dao.PageComponentDao");	
				settingDao =  (SettingDaoInt)getClassObject("ssg.serverlessblog.mongodb.dao.SettingDao");
				tagDao = (TagDaoInt)getClassObject("ssg.serverlessblog.mongodb.dao.TagDao");		
				dataUtil = (DataUtilInt)getClassObject("ssg.serverlessblog.mongodb.util.MongoDbUtil");	
				articleDao =  (ArticleDaoInt)getClassObject("ssg.serverlessblog.mongodb.dao.ArticleDao");
				noSqlSessionDataStore = (SessionDataStore)getClassObject("ssg.serverlessblog.mongodb.util.MongoDbSessionDatastoreDataStore");
			}			
		}catch(Exception e) {
			logger.error("Error loading environment classes.",e);
		}
	}

	public static boolean isMongoDb() {
		return isMongoDb;
	}
	public static boolean isGae() {
		return isGae;
	}
	
	public static boolean isAiSupported() {
		return isGae;		
	}
	
	public static boolean isImageManagerSupported() {
		return isGae;
	}
	
	public static boolean isAnalyticsSupported() {
		return isGae;
	}
	
	
	
	private static Object getClassObject(String className) throws Exception{
		final Class<?> clazz = Class.forName(className);
		final Object instance = clazz.getDeclaredConstructor().newInstance();
        return instance;
	}
		
	private static boolean isClassExists(String className) {
		var result = false;
		try {
			Class.forName(className);
			result = true;
		} catch (ClassNotFoundException e) {
			//do nothing by design.
		}
		return result;
	}	
}

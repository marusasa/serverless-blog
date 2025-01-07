package ssg.serverlessblog.system;

import org.eclipse.jetty.server.session.SessionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssg.serverlessblog.interfaces.AnalyticsDaoInt;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.interfaces.SettingDaoInt;
import ssg.serverlessblog.interfaces.StorageDaoInt;
import ssg.serverlessblog.interfaces.SystemDaoInt;
import ssg.serverlessblog.interfaces.TagDaoInt;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.util.CloudDocument;

/**
 * This Blog platform is intended to support multiple Serverless Cloud environments.
 * This class uses reflection to determine the implementation that exists in the 
 * classpath.
 * 
 * In addition, it will provide some environment specific methods.
 */
public class Env {

	static Logger logger = LoggerFactory.getLogger(Env.class);
	
	static final boolean isGae = isClassExists("ssg.serverlessblog.gae.dao.SettingDao");
	
	static public SettingDaoInt settingDao = null; 
	static public ArticleDaoInt articleDao = null;
	static public UserDaoInt userDao = null;
	static public SystemDaoInt systemDao = null;
	static public PageComponentDaoInt pageComponentDao = null;
	static public DataUtilInt dataUtil = null;
	static public SessionDataStore noSqlSessionDataStore = null;
	static public AnalyticsDaoInt analyticsDao = null;
	static public StorageDaoInt storageDao = null;
	static public TagDaoInt tagDao = null;
	
	static {
		try {
			if(isGae) {
				//load GAE classes
				settingDao =  (SettingDaoInt)getClassObject("ssg.serverlessblog.gae.dao.SettingDao");
				articleDao =  (ArticleDaoInt)getClassObject("ssg.serverlessblog.gae.dao.ArticleDao"); 
				userDao =  (UserDaoInt)getClassObject("ssg.serverlessblog.gae.dao.UserDao");
				pageComponentDao =  (PageComponentDaoInt)getClassObject("ssg.serverlessblog.gae.dao.PageComponentDao");
				systemDao =  (SystemDaoInt)getClassObject("ssg.serverlessblog.gae.dao.SystemDao");
				dataUtil = (DataUtilInt)getClassObject("ssg.serverlessblog.gae.util.FirestoreDbUtil");
				noSqlSessionDataStore = (SessionDataStore)getClassObject("ssg.serverlessblog.gae.util.FirestoreDataStore");
				analyticsDao = (AnalyticsDaoInt)getClassObject("ssg.serverlessblog.gae.dao.AnalyticsDao");
				storageDao = (StorageDaoInt)getClassObject("ssg.serverlessblog.gae.dao.StorageDao");
				tagDao = (TagDaoInt)getClassObject("ssg.serverlessblog.gae.dao.TagDao");
			}
		}catch(Exception e) {
			logger.error("Error loading environment classes.",e);
		}
	}

	public static String getJavaScriptUtcDateTime(CloudDocument doc, String fieldId) {		
		return dataUtil.getUtcString(doc, fieldId);
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

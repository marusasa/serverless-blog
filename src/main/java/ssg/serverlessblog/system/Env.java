package ssg.serverlessblog.system;

import java.util.Optional;

import org.eclipse.jetty.server.session.SessionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.interfaces.AnalyticsDaoInt;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.interfaces.SettingDaoInt;
import ssg.serverlessblog.interfaces.SystemDaoInt;
import ssg.serverlessblog.interfaces.UserDaoInt;
import ssg.serverlessblog.util.AppProperties;
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
	private static Optional<String> singleTenantAccountId = Optional.empty();
	
	static public SettingDaoInt settingDao = null; 
	static public ArticleDaoInt articleDao = null;
	static public UserDaoInt userDao = null;
	static public SystemDaoInt systemDao = null;
	static public PageComponentDaoInt pageComponentDao = null;
	static public DataUtilInt dataUtil = null;
	static public SessionDataStore noSqlSessionDataStore = null;
	static public AnalyticsDaoInt analyticsDao = null;
	
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
	
	public static String getAccountIdToUse(Context ctx) throws Exception {		
		if(AppProperties.getBoolean("env.is-single-tenant")) {
			if(singleTenantAccountId.isEmpty()) {
				singleTenantAccountId = Optional.of(Env.systemDao.getSingleTenantAccoundId());
				logger.info("Default account ID loaded.");
			}			
			return singleTenantAccountId.get();
		}else {
			//TBD...
			logger.error("Account ID requested for non-single tenant environment. This is not implemented yet.");
			return "";
		}
	}
	
	
}

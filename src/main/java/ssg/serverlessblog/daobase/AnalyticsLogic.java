package ssg.serverlessblog.daobase;

import java.util.ArrayList;
import java.util.List;

import ssg.serverlessblog.analytics.PathSummary;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.AppProperties;
import ssg.serverlessblog.util.CloudDocument;

public class AnalyticsLogic {
	
	static public boolean dateExistsDailyVisits(int yyyyMMdd) throws Exception {
		if(Env.isAnalyticsSupported()) { 
			return Env.analyticsDao.dateExistsDailyVisits(yyyyMMdd);
		}else {
			return false;
		}
	}
	
	static public List<PathSummary> getPageEngagementSummaryByPath(int year, int month) throws Exception {
		if(Env.isAnalyticsSupported()) { 
			return Env.analyticsDao.getPageEngagementSummaryByPath(year, month);
		}else {
			return new ArrayList<PathSummary>();
		}
	}

	static public void processDailyRawData(String today) throws Exception {
		if(Env.isAnalyticsSupported()) { 
			Env.analyticsDao.processDailyRawData(today);
		}
	}
		
	static public List<CloudDocument> getDailyVisits(int year, int month) throws Exception {
		if(Env.isAnalyticsSupported()) { 
			return Env.analyticsDao.getDailyVisits(year, month);		
		}else {
			return new ArrayList<CloudDocument>();
		}
	}

	static public void saveEvent(int visitorId, String path, String status) throws Exception {
		if(AppProperties.getString("env.database").equals(AppConst.DB_FIRESTORE)) { 
			Env.analyticsDao.saveEvent(visitorId, path, status);		
		}
	}

}

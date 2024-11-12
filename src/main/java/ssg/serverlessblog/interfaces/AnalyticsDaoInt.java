package ssg.serverlessblog.interfaces;

import java.util.List;

import ssg.serverlessblog.analytics.PathSummary;
import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Users.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface AnalyticsDaoInt {
	public void saveEvent(int visitorId, String path, String status) throws Exception;
	public List<CloudDocument> getDailyVisits(int year, int month) throws Exception;
	public List<PathSummary> getPageEngagementSummaryByPath(int year, int month) throws Exception;
	public boolean dateExistsDailyVisits(int yyyyMMdd) throws Exception;
	public void processDailyRawData(String today) throws Exception;
}

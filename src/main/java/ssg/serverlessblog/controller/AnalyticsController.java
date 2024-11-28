package ssg.serverlessblog.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.analytics.DailyVisitsDoc;
import ssg.serverlessblog.analytics.PathSummary;
import ssg.serverlessblog.analytics.ResultAnalyticsDailyVisits;
import ssg.serverlessblog.analytics.ResultAnalyticsDailyVisits.Datasets;
import ssg.serverlessblog.analytics.ResultAnalyticsPageEngagement;
import ssg.serverlessblog.analytics.ResultAnalyticsPageEngagement.Engagement;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 *  Controller for handling Analytics REST services for management.
 */
public class AnalyticsController {

	private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class.getName());
	
	public static void getPageEngagement(Context ctx) {
		ResultAnalyticsPageEngagement result = new ResultAnalyticsPageEngagement(); 
		try {
			//path parameters
			final String y = ctx.pathParam("year");
			final String m = ctx.pathParam("month");
			final int year = Integer.parseInt(y);
			final int month = Integer.parseInt(m);
						
			//validate
			if(month < 1 || month > 12) {
				result.getMessages().add("Month '%s' is invalid.".formatted(month));				
			}
			
			
			if(result.getMessages().size() == 0) {
				final List<PathSummary> list = Env.analyticsDao.getPageEngagementSummaryByPath(year, month);
				
				//list contains one path info, which contains count per date.
				for(PathSummary path: list) {
					final long countActual = path.dateActualMap.values().stream().mapToLong(Long::longValue).sum();
					final long countTotal = path.dateTotalMap.values().stream().mapToLong(Long::longValue).sum();
					final long average = (long)path.dateAverageMap.values().stream().mapToLong(Long::longValue).average().getAsDouble();
					final Engagement engagement = new Engagement.Builder()
							.path(path.path)
							.countActual(countActual)
							.countTotal(countTotal)
							.averageTime(average + " sec")
							.build();
					result.getEngagements().add(engagement);
				}
				
				result.setResult(AppConst.RESULT_SUCCESS);
			}
		}catch(Exception e) {
			logger.error("Error generating analytics data.",e);
			result.getMessages().add("Error retrieving data.");
		}
		ctx.json(result);
	}
	
	public static void getDailyVisits(Context ctx) {
		ResultAnalyticsDailyVisits result = new ResultAnalyticsDailyVisits();
		try {
			//path parameters
			final String y = ctx.pathParam("year");
			final String m = ctx.pathParam("month");
			final int year = Integer.parseInt(y);
			final int month = Integer.parseInt(m);
						
			//validate
			if(month < 1 || month > 12) {
				result.getMessages().add("Month '%s' is invalid.".formatted(month));				
			}
			
			
			if(result.getMessages().size() == 0) {		
				//create place holder in result.
				final List<Long> actualList = new ArrayList<>();
				final List<Long> allList = new ArrayList<>();
				final List<CloudDocument> list = Env.analyticsDao.getDailyVisits(year, month);
				list.forEach(doc -> {
					actualList.add(doc.getLong(DailyVisitsDoc.field_count_actual));					
					allList.add(doc.getLong(DailyVisitsDoc.field_count_all));					
					final long date = doc.getLong(DailyVisitsDoc.field_date);					
					result.getLabels().add(Long.toString(date%100));	//last 2 digits are date
				});
				//build result
				final Datasets datasetActual = new Datasets.Builder().label("Count Actual").data(actualList).build();
				final Datasets datasetAll = new Datasets.Builder().label("Count All").data(allList).build();
				result.getDatasets().add(datasetActual);
				result.getDatasets().add(datasetAll);
				result.setResult(AppConst.RESULT_SUCCESS);
			}
		}catch(Exception e) {
			logger.error("Error generating analytics data.",e);
			result.getMessages().add("Error retrieving data.");
		}
		ctx.json(result);	
    }	
	
	
}

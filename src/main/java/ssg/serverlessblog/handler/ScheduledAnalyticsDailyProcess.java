package ssg.serverlessblog.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.system.Env;

/**
 * REST service for login.
 */
public class ScheduledAnalyticsDailyProcess implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ScheduledAnalyticsDailyProcess.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		logger.info("ScheduledAnalyticsDailyVisits invoked.");
		try{
			final var appEngineHeader = ctx.req().getHeader("X-Appengine-Cron");
			if(appEngineHeader != null && appEngineHeader.equals("true")) {
				//get today's date.
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final SimpleDateFormat sdfNum = new SimpleDateFormat("yyyyMMdd");
				final Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				final var today = sdf.format(cal.getTime());	//2024-11-15
				final var todayNum = Integer.parseInt(sdfNum.format(cal.getTime()));	//20241115
				logger.info("ScheduledAnalyticsDailyVisits processing %s".formatted(today));
				
				//1st check if result already contains data for this date.
				//if it does, then something is wrong...
				if(Env.analyticsDao.dateExistsDailyVisits(todayNum)) {
					logger.error("Date %s already exists in Daily Visits result datastore. This shouldn't happen.".formatted(todayNum));
				}else {
					Env.analyticsDao.processDailyRawData(today);
				}
			}else {
				logger.warn("X-Appengine-Cron header missing!");
			}
		}catch(Exception e) {
			logger.error("Error saving alaytics data.",e);
		}
	}

}

package ssg.serverlessblog.handler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.ReqAnalytics;
import ssg.serverlessblog.system.Env;

/**
 * REST service for login.
 */
public class AnalyticsHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalyticsHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		try{
			final ReqAnalytics data = ctx.bodyAsClass(ReqAnalytics.class);
			if(data.state().equals("hidden&visible")) {
				Env.analyticsDao.saveEvent(data.visitorId(), data.path(), "hidden");
				Thread.sleep(5);//sleep for short time
				Env.analyticsDao.saveEvent(data.visitorId(), data.path(), "visible");
			}else {
				Env.analyticsDao.saveEvent(data.visitorId(), data.path(), data.state());
			}
		}catch(Exception e) {
			logger.error("Error saving alaytics data.",e);
		}
		ctx.json("{}");
	}

}

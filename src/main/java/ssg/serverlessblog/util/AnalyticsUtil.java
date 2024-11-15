package ssg.serverlessblog.util;

import java.util.Calendar;

import io.javalin.http.Context;

public class AnalyticsUtil {
	
	static public int getVisitorId(Context ctx) {
		//Add unique visitor id for analytics purpose.
		final var c = Calendar.getInstance();
		//ip address + user agent + today's date.
		final String visitorInfo = ctx.req().getRemoteAddr() + ctx.userAgent() + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DATE);
		return visitorInfo.hashCode();
	}

}

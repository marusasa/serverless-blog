package ssg.serverlessblog.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class for handling data time.
 */
public class DateTimeUtil {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String formatDateAndTime(Date date) {
		return sdf.format(date);
	}

}

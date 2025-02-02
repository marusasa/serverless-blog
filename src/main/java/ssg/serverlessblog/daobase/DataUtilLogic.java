package ssg.serverlessblog.daobase;

import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class DataUtilLogic {

	public static String getJavaScriptUtcDateTime(CloudDocument doc, String fieldId) {		
		return Env.dataUtil.getUtcString(doc, fieldId);
	}
}

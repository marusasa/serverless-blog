package ssg.serverlessblog.handler;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.ResultBasicInfo;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting basic info used by the blog.
 * Not used by management functions.
 */
public class BasicInfoHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(BasicInfoHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		ResultBasicInfo result = new ResultBasicInfo();
		try {			
			//Currently multi-tenant is not part of the design.
			//However, account id is used for possible future implementation.
			var accountId = Env.getAccountIdToUse(ctx);
			Optional<CloudDocument> setting = Env.settingDao.getSetting(accountId);
						
			if(setting.isPresent()) {
				var document = setting.get();
				result.setBloggerName("TBD");	//Not used yet
				result.setTitle(document.getString(SettingDoc.field_blog_title));
				result.setSubTitle(document.getString(SettingDoc.field_blog_subtitle));
				result.setIconUrl(document.getString(SettingDoc.field_icon_url));
				result.setFaviconUrl(document.getString(SettingDoc.field_favicon_url));
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Setting data not found for this blog. This should not happen...");
			}
		}catch(Exception e) {
			logger.error("Error processing.",e);
			result.getMessages().add("Error getting basic information");
		}
		ctx.json(result);
	}

}

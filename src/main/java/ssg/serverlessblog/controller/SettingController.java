package ssg.serverlessblog.controller;

import java.util.Optional;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.data_json.ResultSetting;
import ssg.serverlessblog.data_json.Setting;
import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 *  Controller for handling Setting REST services for management.
 */
public class SettingController {

	private static final Logger logger = LoggerFactory.getLogger(SettingController.class.getName());
	
	public static void get(Context ctx) {
		final ResultSetting result = new ResultSetting();
		try {
			final String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			final Optional<CloudDocument> op = Env.settingDao.getSetting(accountId);
			if(op.isPresent()) {
				final CloudDocument doc = op.get();
				final var s = new Setting.Builder()
						.blogTitle(doc.getString(SettingDoc.field_blog_title))
						.blogSubTitle(doc.getString(SettingDoc.field_blog_subtitle))
						.settingId(doc.getId())
						.iconUrl(doc.getString(SettingDoc.field_icon_url))
						.faviconUrl(doc.getString(SettingDoc.field_favicon_url))
						.build();
				result.setSetting(s);						
				result.setResult(AppConst.RESULT_SUCCESS);
			}
		}catch(Exception e) {
			logger.error("Error getting setting.",e);
			result.getMessages().add("Error retrieving data.");
		}
		ctx.json(result);	
    }
	
	public static void update(Context ctx) {
		final ResultBase result = new ResultBase();
		try {
			final Setting setting = ctx.bodyAsClass(Setting.class);			
			final String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);			
			
			Env.settingDao.updateSetting(accountId, setting);
			result.setResult(AppConst.RESULT_SUCCESS);
			logger.info("Setting updated - id:%s".formatted(setting.settingId()));			
		}catch(Exception e) {
			logger.error("Error updating setting.",e);
			result.getMessages().add("Error updating data.");
		}
		ctx.json(result);	
    }
	
	
}

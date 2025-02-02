package ssg.serverlessblog.daobase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.data_json.Setting;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class SettingLogic {
	
	static public void updateSetting(Setting setting) throws Exception{
				
		//preparee data.
		final Map<String, Object> data = new HashMap<>();
		data.put(SettingDoc.field_blog_title, setting.blogTitle());
		data.put(SettingDoc.field_blog_subtitle, setting.blogSubTitle());
		data.put(SettingDoc.field_updated_at, new Date());
		data.put(SettingDoc.field_icon_url, setting.iconUrl());
		data.put(SettingDoc.field_favicon_url, setting.faviconUrl());
		
		Env.settingDao.updateSetting(setting.settingId(), data);
	}
	
	static public Optional<CloudDocument> getSetting() throws Exception{
		return Env.settingDao.getSetting();		
	}	
}

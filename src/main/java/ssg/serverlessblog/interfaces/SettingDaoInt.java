package ssg.serverlessblog.interfaces;

import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Setting.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface SettingDaoInt {

	public Optional<CloudDocument> getSetting() throws Exception;	
	public void updateSetting(String settingId, Map<String, Object> data) throws Exception;
	public void createSetting(Map<String, Object> data) throws Exception;
}

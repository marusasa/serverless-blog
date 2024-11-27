package ssg.serverlessblog.interfaces;

import java.util.Optional;

import ssg.serverlessblog.data_json.Setting;
import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Setting.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface SettingDaoInt {

	public Optional<CloudDocument> getSetting() throws Exception;	
	public void updateSetting( Setting setting) throws Exception;
}

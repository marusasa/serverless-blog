package ssg.serverlessblog.data_json;

public class ResultSetting extends ResultBase {
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private Setting setting = new Setting("","","","","");

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	
	
	

}

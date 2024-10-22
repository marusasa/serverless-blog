package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

import ssg.serverlessblog.util.AppConst;


/**
 * Base class for every response that will be sent to the browser.
 * Every REST service result class should extend this. 
 */
public class ResultBase {
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private List<String> messages = new ArrayList<String>();
	private String result = AppConst.RESULT_FAIL;
	
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
}

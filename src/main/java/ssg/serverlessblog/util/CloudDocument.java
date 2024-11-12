package ssg.serverlessblog.util;

import java.util.Map;

/**
 * A class representing a document-oriented NoSQL database document.
 * The map object will hold field-value pair from a document.
 * 
 * Notice the absence of getTime() method. 
 * This is because Google App Engine uses it's own implementation of Timestamp class.
 * Any environment specific logic is in classes under the environment specific
 * package.
 */
public class CloudDocument {

	private Map<String,Object> data;
	private String id = "";
	
	@SuppressWarnings("unused")
	private CloudDocument() {}
	
	public CloudDocument(String id, Map<String,Object> data) {
		this.data = data;
		this.id = id;
	}
	
	public Object get(String key) {
		return data.get(key);
	}
	
	public String getString(String key) {
		return (String)data.get(key);
	}
	
	public String getId() {
		return id;
	}
	
	public Long getLong(String key) {
		return (Long)data.get(key);
	}
	
	public Boolean getBoolean(String key) {
		return (Boolean)data.get(key);
	}
	
	public boolean isNull(String key) {
		if(data.get(key) == null) {
			return true;
		}else {
			return false;
		}
	}
}

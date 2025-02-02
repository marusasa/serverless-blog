package ssg.serverlessblog.util;

import java.util.Map;

import org.bson.Document;

import com.google.cloud.firestore.DocumentSnapshot;

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
	private Object document;
	private Map<String,Object> data;
	private String id = "";
	
	@SuppressWarnings("unused")
	private CloudDocument() {}
	
	public CloudDocument(String id, Map<String,Object> data) {
		this.data = data;
		this.id = id;
	}
	
	public CloudDocument(String id, Document doc) {
		this.document = doc;
		this.id = id;
	}
	
	public CloudDocument(String id, DocumentSnapshot doc) {
		this.document = doc;
		this.id = id;
	}
	
	public Object get(String key) {
		if(document instanceof Document doc) {
			return doc.get(key);
		}else if(document instanceof DocumentSnapshot doc) {
			return doc.get(key);
		}else {
			return data.get(key);
		}
	}
	
	public String getString(String key) {
		if(document instanceof Document doc) {
			return doc.getString(key);
		}else if(document instanceof DocumentSnapshot doc) {
			return doc.getString(key);
		}else {
			return (String)data.get(key);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public Long getLong(String key) {
		if(document instanceof Document doc) {
			return doc.getLong(key);
		}else if(document instanceof DocumentSnapshot doc) {
			return doc.getLong(key);
		}else {
			return (Long)data.get(key);
		}
	}
	
	public Boolean getBoolean(String key) {
		if(document instanceof Document doc) {
			return doc.getBoolean(key);
		}else if(document instanceof DocumentSnapshot doc) {
			return doc.getBoolean(key);
		}else {
			return (Boolean)data.get(key);
		}
	}
	
	public boolean isNull(String key) {
		Object obj = null;
		if(document instanceof Document doc) {
			obj = doc.get(key);
		}else if(document instanceof DocumentSnapshot doc) {
			obj = doc.get(key);
		}else {
			obj = data.get(key);
		}
		if(obj == null) {
			return true;
		}else {
			return false;
		}
	}
}

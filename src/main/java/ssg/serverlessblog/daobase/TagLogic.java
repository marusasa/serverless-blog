package ssg.serverlessblog.daobase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class TagLogic {
	
	private static final Logger logger = LoggerFactory.getLogger(TagLogic.class);
	
	static public String createTag(String name, String json) throws Exception {
		//add tag to database
		final Map<String, Object> data = new HashMap<>();
		data.put(TagDoc.field_name, name);
		data.put(TagDoc.field_json, json);
		data.put(TagDoc.field_description, "");
		data.put(TagDoc.field_created_at, new Date());
		data.put(TagDoc.field_updated_at, null);
		
		return Env.tagDao.createTag(data);
	}
	
	static public boolean updateTag(String tagId, String name, String json, String description) throws Exception {
		var result = false;
		
		Optional<CloudDocument> doc = Env.tagDao.getTag(tagId);
		
		if(doc.isEmpty()) {
			logger.warn("Tag document not found: %s".formatted(tagId));
			return result;
		}
		 
		//update document
		final Map<String, Object> updates = new HashMap<>();
		updates.put(TagDoc.field_name, name);
		updates.put(TagDoc.field_json, json);
		updates.put(TagDoc.field_description, description);
		updates.put(TagDoc.field_updated_at, new Date());
		
		Env.tagDao.updateTag(tagId, updates);
		result = true;
		
		return result;
	}
	
	static public List<CloudDocument> getTags() throws Exception {
		return Env.tagDao.getTags();
	}
	
	static public Optional<CloudDocument> getTag(String tagId) throws Exception {
		return Env.tagDao.getTag(tagId);		
	}
	
	static public boolean deleteTag(String tagId) throws Exception {
		var opt = Env.tagDao.getTag(tagId);
		
		if(opt.isEmpty()) {
			logger.warn("Tag document not found: %s".formatted(tagId));
			return false;
		}
		
		Env.tagDao.deleteTag(tagId);			
			
		return true;
	}
}

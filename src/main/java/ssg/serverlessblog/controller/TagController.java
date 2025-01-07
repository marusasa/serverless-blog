package ssg.serverlessblog.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import ssg.serverlessblog.data_json.ReqTag;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.data_json.ResultTag;
import ssg.serverlessblog.data_json.ResultTagList;
import ssg.serverlessblog.data_json.Tag;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 *  Controller for handling Page Component REST services for management.
 */
public class TagController {

	private static final Logger logger = LoggerFactory.getLogger(TagController.class.getName());
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void deleteItem(Context ctx) {
		final ResultBase result = new ResultBase();
		try {
			final String tagId = ctx.pathParam("tagId");
			
			if(Env.tagDao.deleteTag(tagId)) {
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Tag %s not deleted.".formatted(tagId));
			}
									
		}catch(Exception e) {
			logger.error("Error deleting data.",e);
			result.getMessages().add("Error deleting data.");
		}
		ctx.json(result);
	}
	
	public static void getItem(Context ctx) {
		final ResultTag result = new ResultTag();
		try {
			final String tagId = ctx.pathParam("tagId");
			final Optional<CloudDocument> data = Env.tagDao.getTag(tagId);
			
			if(data.isPresent()) {
				final CloudDocument document = data.get();
				final var tag = new Tag.Builder()
						.name(document.getString(TagDoc.field_name))
						.json(document.getString(TagDoc.field_json))
						.description(document.getString(TagDoc.field_description))
						.tagId(document.getId())
						.build();
				result.tag = tag;
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Tag not found.");
			}
		}catch(Exception e) {
			logger.error("Error getting data.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}
	
	public static void getList(Context ctx) {
		final ResultTagList result = new ResultTagList();
		try {
			final List<CloudDocument> list = Env.tagDao.getTags();
			
			list.forEach(document -> {
				final var tag = new Tag.Builder()
						.name(document.getString(TagDoc.field_name))
						.json(document.getString(TagDoc.field_json))
						.tagId(document.getId())
						.description(document.getString(TagDoc.field_description))
						.build();
				result.tags.add(tag);
			});
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting  list.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}
	
	
	public static void updateTag(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqTag req = ctx.bodyAsClass(ReqTag.class);
		final String tagId = ctx.pathParam("tagId");
		
		
		try {
			if(Env.tagDao.updateTag(tagId,req.name(),mapper.writeValueAsString(req.articleIds()),req.description())){
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Data not updated.");
			}
		}catch(Exception e) {
			logger.error("Error saving data.", e);
			result.getMessages().add("Error saving data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void createNewDefault(Context ctx) {
		final ResultBase result = new ResultBase();
		
		try {
			Env.tagDao.createTag("New Tag","[]");
			result.setResult(AppConst.RESULT_SUCCESS);
			logger.info("Data created.");
			
		}catch(Exception e) {
			logger.error("Error saving data.", e);
			result.getMessages().add("Error saving data.");
		}
		ctx.json(result);		
	}
		
}

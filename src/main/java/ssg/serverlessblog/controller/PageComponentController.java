package ssg.serverlessblog.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import ssg.serverlessblog.daobase.PageComponentLogic;
import ssg.serverlessblog.data_json.PCProfilePic;
import ssg.serverlessblog.data_json.PageComponent;
import ssg.serverlessblog.data_json.ReqPCLinkList;
import ssg.serverlessblog.data_json.ReqPCNew;
import ssg.serverlessblog.data_json.ReqPCProfilePic;
import ssg.serverlessblog.data_json.ReqPCTags;
import ssg.serverlessblog.data_json.ReqPCTextBox;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.data_json.ResultPageComponent;
import ssg.serverlessblog.data_json.ResultPageComponentList;
import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;
import ssg.serverlessblog.util.SampleDataUtil;

/**
 *  Controller for handling Page Component REST services for management.
 */
public class PageComponentController {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentController.class.getName());
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void deleteItem(Context ctx) {
		final ResultBase result = new ResultBase();
		try {
			final String pageComponentId = ctx.pathParam("pageComponentId");
			
			if(PageComponentLogic.deletePageComponent(pageComponentId)) {
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Page Component %s not deleted.".formatted(pageComponentId));
			}
									
		}catch(Exception e) {
			logger.error("Error deleting data.",e);
			result.getMessages().add("Error deleting data.");
		}
		ctx.json(result);
	}
	
	public static void getItem(Context ctx) {
		final ResultPageComponent result = new ResultPageComponent();
		try {
			final String pageComponentId = ctx.pathParam("pageComponentId");
			final Optional<CloudDocument> data = PageComponentLogic.getPageComponent(pageComponentId);
			
			if(data.isPresent()) {
				final CloudDocument document = data.get();
				final var pc = new PageComponent.Builder()
						.type(document.getString(PageComponentDoc.field_comp_type))
						.json(document.getString(PageComponentDoc.field_json))
						.order(document.getLong(PageComponentDoc.field_view_order))
						.enabled(document.getBoolean(PageComponentDoc.field_enabled))
						.pageComponentId(document.getId())
						.build();
				result.component = pc;
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Data not obtained.");
			}
		}catch(Exception e) {
			logger.error("Error getting Page Component.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}
	
	public static void getList(Context ctx) {
		final ResultPageComponentList result = new ResultPageComponentList();
		try {
			final List<CloudDocument> list = PageComponentLogic.getPageComponents();
			
			list.forEach(document -> {
				final var pc = new PageComponent.Builder()
						.type(document.getString(PageComponentDoc.field_comp_type))
						.json(document.getString(PageComponentDoc.field_json))
						.order(document.getLong(PageComponentDoc.field_view_order))
						.enabled(document.getBoolean(PageComponentDoc.field_enabled))
						.pageComponentId(document.getId())
						.build();
				result.components.add(pc);
			});
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting Page Component list.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}
	
	public static void updateProfilePic(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqPCProfilePic req = ctx.bodyAsClass(ReqPCProfilePic.class);
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		validateRequest(req.data(),result);
		if(result.getMessages().size() > 0) {
			ctx.json(result);
			return;
		}
		
		try {
			if(PageComponentLogic.updatePageComponent(pageComponentId, mapper.writeValueAsString(req.data()),req.order(),req.enabled())){
				logger.info("Profile picture updated.");
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Data not updated.");
			}
		}catch(Exception e) {
			logger.error("Error saving profile picture data.", e);
			result.getMessages().add("Error saving data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void createNewDefault(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqPCNew req = ctx.bodyAsClass(ReqPCNew.class);
		
		try {
			switch(req.type()) {
				case AppConst.PC_TYPE_LINK_LIST:
					PageComponentLogic.createPageComponent( AppConst.PC_TYPE_LINK_LIST,
							mapper.writeValueAsString(SampleDataUtil.getSampleLinkList()), 100L, false);
					result.setResult(AppConst.RESULT_SUCCESS);
					logger.info("Default Link List created.");
					break;
				case AppConst.PC_TYPE_PROFILE_PIC:
					PageComponentLogic.createPageComponent( AppConst.PC_TYPE_PROFILE_PIC,
							mapper.writeValueAsString(SampleDataUtil.getSampleProfilePic()), 101L, false);
					result.setResult(AppConst.RESULT_SUCCESS);
					logger.info("Default Profile Pic created.");
					break;
				case AppConst.PC_TYPE_TEXT_BOX:
					PageComponentLogic.createPageComponent( AppConst.PC_TYPE_TEXT_BOX,
							mapper.writeValueAsString(SampleDataUtil.getSampleTextBox()), 102L, false);
					result.setResult(AppConst.RESULT_SUCCESS);
					logger.info("Default Text Box created.");
					break;
				case AppConst.PC_TYPE_TAGS:
					PageComponentLogic.createPageComponent( AppConst.PC_TYPE_TAGS,
							"{}", 103L, false);
					result.setResult(AppConst.RESULT_SUCCESS);
					logger.info("Default Tags created.");
					break;
				default:
					throw new Exception("Unknown type for createNewDefault: %s".formatted(req.type()));
			}
		}catch(Exception e) {
			logger.error("Error saving page component data.", e);
			result.getMessages().add("Error saving data.");
		}
		ctx.json(result);		
	}
		
	public static void updateLinkList(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqPCLinkList req = ctx.bodyAsClass(ReqPCLinkList.class);	
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		//no validation for now.
		
		try {
			if(PageComponentLogic.updatePageComponent(pageComponentId, 
					mapper.writeValueAsString(req.data()),req.order(),req.enabled() )) {
				result.setResult(AppConst.RESULT_SUCCESS);
				logger.info("Data updated.");
			}else {
				result.getMessages().add("Data not saved.");
			}
			
		}catch(Exception e) {
			logger.error("Error updating data.", e);
			result.getMessages().add("Error updating data.");
		}
		 
		ctx.json(result);		
    }
	
	
	public static void updateTextBox(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqPCTextBox req = ctx.bodyAsClass(ReqPCTextBox.class);	
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		//no validation for now.
		
		try {
			if(PageComponentLogic.updatePageComponent(pageComponentId, 
					mapper.writeValueAsString(req.data()),req.order(),req.enabled() )) {
				result.setResult(AppConst.RESULT_SUCCESS);
				logger.info("Data updated.");
			}else {
				result.getMessages().add("Data not saved.");
			}
			
		}catch(Exception e) {
			logger.error("Error updating data.", e);
			result.getMessages().add("Error updating data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void updateTags(Context ctx) {
		final ResultBase result = new ResultBase();
		final ReqPCTags req = ctx.bodyAsClass(ReqPCTags.class);	
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		//no validation for now.
		
		try {
			if(PageComponentLogic.updatePageComponent(pageComponentId, 
					"{}",req.order(),req.enabled() )) {
				result.setResult(AppConst.RESULT_SUCCESS);
				logger.info("Data updated.");
			}else {
				result.getMessages().add("Data not saved.");
			}
			
		}catch(Exception e) {
			logger.error("Error updating data.", e);
			result.getMessages().add("Error updating data.");
		}
		 
		ctx.json(result);		
    }
	
	private static void validateRequest(PCProfilePic data, ResultBase result) {
		if(data.size() > 24) {
			result.getMessages().add("Size must be less than 25");
		}
		if(data.size() < 1) {
			result.getMessages().add("Size must be larger than 0");
		}
		if(data.url().isBlank()) {
			result.getMessages().add("Image URL missing.");
		}
	}
	
}

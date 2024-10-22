package ssg.serverlessblog.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import ssg.serverlessblog.data_json.PageComponent;
import ssg.serverlessblog.data_json.PCProfilePic;
import ssg.serverlessblog.data_json.ReqPCLinkList;
import ssg.serverlessblog.data_json.ReqPCProfilePic;
import ssg.serverlessblog.data_json.ReqPCTextBox;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.data_json.ResultPageComponent;
import ssg.serverlessblog.data_json.ResultPageComponentList;
import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 *  Controller for handling Page Component REST services for management.
 */
public class PageComponentController {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentController.class.getName());
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void getItem(Context ctx) {
		ResultPageComponent result = new ResultPageComponent();
		try {
			final String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			final String pageComponentId = ctx.pathParam("pageComponentId");
			Optional<CloudDocument> data = Env.pageComponentDao.getPageComponent(accountId, pageComponentId);
			
			if(data.isPresent()) {
				CloudDocument document = data.get();
				var pc = new PageComponent.Builder()
						.type(document.getString(PageComponentDoc.field_comp_type))
						.json(document.getString(PageComponentDoc.field_json))
						.order(document.getLong(PageComponentDoc.field_view_order))
						.enabled(document.getBoolean(PageComponentDoc.field_enabled))
						.pageComponentId(document.getId())
						.build();
				result.component = pc;
			};
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting Page Component.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}
	
	public static void getList(Context ctx) {
		ResultPageComponentList result = new ResultPageComponentList();
		try {
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			List<CloudDocument> list = Env.pageComponentDao.getPageComponents(accountId);
			
			list.forEach(document -> {
				var pc = new PageComponent.Builder()
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
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCProfilePic req = ctx.bodyAsClass(ReqPCProfilePic.class);
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		validateRequest(req.data(),result);
		if(result.getMessages().size() > 0) {
			ctx.json(result);
			return;
		}
		
		try {
			if(Env.pageComponentDao.updatePageComponent(accountId, pageComponentId, mapper.writeValueAsString(req.data()),req.order(),req.enabled())){
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
	
	public static void createProfilePic(Context ctx) {
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCProfilePic req = ctx.bodyAsClass(ReqPCProfilePic.class);	
		
		//validation
		validateRequest(req.data(),result);
		if(result.getMessages().size() > 0) {
			ctx.json(result);
			return;
		}
		
		try {
			String id = Env.pageComponentDao.createPageComponent(accountId, AppConst.PC_TYPE_PROFILE_PIC, 
					mapper.writeValueAsString(req.data()), req.order(), req.enabled() );
			result.setResult(AppConst.RESULT_SUCCESS);
			logger.info("Profile picture created.");
		}catch(Exception e) {
			logger.error("Error saving profile picture data.", e);
			result.getMessages().add("Error saving data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void createLinkList(Context ctx) {
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCLinkList req = ctx.bodyAsClass(ReqPCLinkList.class);	
		
		//validation
		//no validation for now.
		
		try {
			String id = Env.pageComponentDao.createPageComponent(accountId, AppConst.PC_TYPE_LINK_LIST, 
					mapper.writeValueAsString(req.data()),req.order(),req.enabled() );
			result.setResult(AppConst.RESULT_SUCCESS);
			logger.info("Data created.");
		}catch(Exception e) {
			logger.error("Error saving Link List data.", e);
			result.getMessages().add("Error saving data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void updateLinkList(Context ctx) {
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCLinkList req = ctx.bodyAsClass(ReqPCLinkList.class);	
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		//no validation for now.
		
		try {
			if(Env.pageComponentDao.updatePageComponent(accountId, pageComponentId, 
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
	
	public static void createTextBox(Context ctx) {
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCTextBox req = ctx.bodyAsClass(ReqPCTextBox.class);	
		
		//validation
		//no validation for now.
		
		try {
			String id = Env.pageComponentDao.createPageComponent(accountId, AppConst.PC_TYPE_TEXT_BOX, 
					mapper.writeValueAsString(req.data()),req.order(),req.enabled() );
			result.setResult(AppConst.RESULT_SUCCESS);
			logger.info("Data created.");
		}catch(Exception e) {
			logger.error("Error saving data.", e);
			result.getMessages().add("Error saving data.");
		}
		 
		ctx.json(result);		
    }
	
	public static void updateTextBox(Context ctx) {
		ResultBase result = new ResultBase();
		String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
		ReqPCTextBox req = ctx.bodyAsClass(ReqPCTextBox.class);	
		final String pageComponentId = ctx.pathParam("pageComponentId");
		
		//validation
		//no validation for now.
		
		try {
			if(Env.pageComponentDao.updatePageComponent(accountId, pageComponentId, 
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

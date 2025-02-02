package ssg.serverlessblog.handler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.daobase.PageComponentLogic;
import ssg.serverlessblog.data_json.PageComponent;
import ssg.serverlessblog.data_json.ResultPageComponentList;
import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting page components to display on the blog site.
 * Not used by management functions.
 */
public class PageComponentHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(PageComponentHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultPageComponentList result = new ResultPageComponentList();
		try {
			//Currently multi-tenant is not part of the design.
			//However, account id is used for possible future implementation.
			final List<CloudDocument> list = PageComponentLogic.getPageComponents();
			
			list.forEach(document -> {
				if(document.getBoolean(PageComponentDoc.field_enabled)) {
					final var pc = new PageComponent.Builder()
							.type(document.getString(PageComponentDoc.field_comp_type))
							.json(document.getString(PageComponentDoc.field_json))
							.order(document.getLong(PageComponentDoc.field_view_order))
							.enabled(document.getBoolean(PageComponentDoc.field_enabled))
							.pageComponentId(document.getId())
							.build();				
					result.components.add(pc);
				}
			});
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting Page Component list.", e);
			result.getMessages().add("Error getting data.");
		}
		ctx.json(result);
	}

}

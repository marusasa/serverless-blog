package ssg.serverlessblog.daobase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the PageComponentDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class PageComponentLogic {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentLogic.class);
	
	
	public static boolean deletePageComponent( String pageComponentId) throws Exception {
		return Env.pageComponentDao.deletePageComponent(pageComponentId);
	}

	public static Optional<CloudDocument> getPageComponent(String pageComponentId) throws Exception {
		return Env.pageComponentDao.getPageComponent(pageComponentId);		
	}
	
	public static List<CloudDocument> getPageComponents() throws Exception {
		return Env.pageComponentDao.getPageComponents();		
	}

	public static boolean updatePageComponent(String pageComponentId, String json,
			long order, boolean enabled) throws Exception {		
		final Map<String, Object> updates = new HashMap<>();
		updates.put(PageComponentDoc.field_json, json);
		updates.put(PageComponentDoc.field_view_order, order);
		updates.put(PageComponentDoc.field_enabled, enabled);
		updates.put(PageComponentDoc.field_updated_at,  new Date());
		return Env.pageComponentDao.updatePageComponent(pageComponentId,updates);		
	}

	public static String createPageComponent(String type, String json, long order, boolean enabled) throws Exception {
		final Map<String, Object> data = new HashMap<>();
		data.put(PageComponentDoc.field_comp_type, type);
		data.put(PageComponentDoc.field_json, json);
		data.put(PageComponentDoc.field_view_order, order);
		data.put(PageComponentDoc.field_enabled, enabled);
		data.put(PageComponentDoc.field_created_at, new Date());
		data.put(PageComponentDoc.field_updated_at, null);
		return Env.pageComponentDao.createPageComponent(data);		
	}

}

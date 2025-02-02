package ssg.serverlessblog.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Page Component.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface PageComponentDaoInt {
	public String createPageComponent(Map<String, Object> data) throws Exception;
	public boolean updatePageComponent(String pageComponentId,Map<String, Object> data) throws Exception;
	public List<CloudDocument> getPageComponents() throws Exception;
	public Optional<CloudDocument> getPageComponent(String pageComponentId) throws Exception;
	public boolean deletePageComponent(String pageComponentId) throws Exception;	
}

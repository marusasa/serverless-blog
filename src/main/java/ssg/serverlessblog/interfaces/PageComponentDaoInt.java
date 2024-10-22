package ssg.serverlessblog.interfaces;

import java.util.List;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Page Component.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface PageComponentDaoInt {
	public String createPageComponent(String accountId, String type, String json, long order, boolean enabled) throws Exception;
	public boolean updatePageComponent(String accountId, String pageComponentId, String json, long order, boolean enabled) throws Exception;
	public List<CloudDocument> getPageComponents(String accountId) throws Exception;
	public Optional<CloudDocument> getPageComponent(String accountId, String pageComponentId) throws Exception;
}
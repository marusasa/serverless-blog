package ssg.serverlessblog.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Setting.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface TagDaoInt {
	public String createTag(Map<String, Object> data) throws Exception;
	public void updateTag(String tagId, Map<String, Object> data) throws Exception;
	public List<CloudDocument> getTags() throws Exception;
	public Optional<CloudDocument> getTag(String tagId) throws Exception;
	public void deleteTag(String tagId) throws Exception;
}

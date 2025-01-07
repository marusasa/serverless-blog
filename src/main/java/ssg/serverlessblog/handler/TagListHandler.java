package ssg.serverlessblog.handler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.ResultTagList;
import ssg.serverlessblog.data_json.Tag;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting articles to display on the blog site.
 * Not used by management functions.
 */
public class TagListHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(TagListHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultTagList result = new ResultTagList();
		try {
			
			List<CloudDocument> tags = Env.tagDao.getTags();			
			
			for (CloudDocument doc : tags) {			
				final var tag = new Tag.Builder()
						.name(doc.getString(TagDoc.field_name))
						.tagId(doc.getId())
						.build();
				result.tags.add(tag);
			}			
			
			result.setResult(AppConst.RESULT_SUCCESS);
			
		}catch(Exception e) {
			logger.error("Error getting tags : " + e.getMessage());
			result.getMessages().add("Error getting tags.");
			e.printStackTrace();
		}
		ctx.json(result);
	}
	

}

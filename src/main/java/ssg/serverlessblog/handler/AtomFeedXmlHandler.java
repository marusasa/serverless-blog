package ssg.serverlessblog.handler;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.daobase.SettingLogic;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppProperties;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting basic info used by the blog.
 * Not used by management functions.
 */
public class AtomFeedXmlHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(AtomFeedXmlHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		logger.info("/feed Atom RSS invoked.");
		ctx.res().setContentType("application/xml;charset=UTF-8");
		
		final List<CloudDocument> documents = ArticleLogic.getArticlesForBlogAll();						
		
		//Header area
		var result = new StringBuilder();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		result.append("\n");
		result.append("<feed xmlns=\"http://www.w3.org/2005/Atom\">");
		result.append("\n");
		result.append("<title>");
		result.append(escapeXml(getTitle()));
		result.append("</title>");
		result.append("\n");
		result.append("<link href=\"");
		result.append(escapeXml(AppProperties.getString("feed.link"))); 
		result.append("\"/>");
		result.append("\n");
		result.append("<link rel=\"self\" type=\"application/atom+xml\" href=\"");
		result.append(escapeXml(AppProperties.getString("feed.link") + "feed"));
		result.append("\"/>");
		result.append("\n");
		result.append("<author>");
		result.append("\n");
		result.append("<name>");
		result.append(escapeXml(AppProperties.getString("basic.author.name")));
		result.append("</name>");
		result.append("\n");
		result.append("</author>");
		result.append("\n");
		result.append("<id>");
		result.append(escapeXml(AppProperties.getString("feed.id")));
		result.append("</id>");
		result.append("\n");
		if(documents.size() > 0) {
			CloudDocument doc = documents.get(0);
			result.append("<updated>");			
			result.append(getTimestamp(doc,ArticleDoc.field_updated_at));
			result.append("</updated>");
		}
		documents.stream().limit(20).forEach(document -> {
			result.append("<entry>");
			result.append("<title>");
			result.append(escapeXml(document.getString(ArticleDoc.field_title)));
			result.append("</title>");
			result.append("<link href=\"");
			result.append(escapeXml(AppProperties.getString("feed.link") + "post/" + getUrl(document.getId(), document.getString(ArticleDoc.field_title))));
			result.append("\"/>");
			result.append("<id>");
			result.append(escapeXml(AppProperties.getString("feed.id") + "/" + document.getId()));
			result.append("</id>");
			final var upd = getTimestamp(document,ArticleDoc.field_updated_at);
			if(!upd.isBlank()) {
				result.append("<updated>"); 
				result.append(upd);
				result.append("</updated>");
			}
			result.append("<published>");
			result.append(getTimestamp(document,ArticleDoc.field_published_at));
			result.append("</published>");
			result.append("<summary>");
			if(document.getString(ArticleDoc.field_summary) != null && !document.getString(ArticleDoc.field_summary).isBlank()) {
				result.append(escapeXml(document.getString(ArticleDoc.field_summary)));
			}else {
				var body = document.getString(ArticleDoc.field_body);
				if(body.length() > 300) {
					body = body.substring(0, 300) + "...";
				}
				result.append(escapeXml(body));
			}
			result.append("</summary>");
			result.append("</entry>");
		});

		result.append("</feed>");
		ctx.result(result.toString());
	}
	
	private String getTimestamp(CloudDocument doc, String fieldName) {
		final String utcString = Env.dataUtil.getUtcString(doc, fieldName);
		//utcString will be in 2024-10-25T04:36:17.743Z format. change it to 2024-10-25T04:36:17Z
		if(utcString.length() > 19) {
			return utcString.substring(0, 19) + "Z";
		}else {
			return utcString;
		}
	}
	
	private String getTitle() throws Exception {
		var result = "";
		if(AppProperties.getBoolean("basic.use-hardcoded-val")) {
			result = AppProperties.getString("basic.title");
		}else {
			final Optional<CloudDocument> setting = SettingLogic.getSetting();			
			if(setting.isPresent()) {
				result = setting.get().getString(SettingDoc.field_blog_title);
			}
		}
		return escapeXml(result);
	}
	
	private String getUrl(String articleId, String title) {
		title = title.replaceAll(" ", "-");
		final String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";
		// Create a regular expression that matches any character NOT in the
		// allowedChars string
		final String output = title.replaceAll("[^" + allowedCharacters + "]", "");

		// Replace all non-allowed characters with an empty string
		return output + "_" + articleId;
	}
	
	private static String escapeXml(String text) {
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'","&apos;");
	}

}

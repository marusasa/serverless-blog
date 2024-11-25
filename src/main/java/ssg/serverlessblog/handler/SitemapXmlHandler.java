package ssg.serverlessblog.handler;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultBasicInfo;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.AppProperties;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting basic info used by the blog.
 * Not used by management functions.
 */
public class SitemapXmlHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(SitemapXmlHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		logger.info("sitemap.xml invoked.");
		ctx.res().setContentType("application/xml;charset=UTF-8");
		
		var accountId = Env.getAccountIdToUse(ctx);
		final List<CloudDocument> documents = Env.articleDao.getArticlesForBlogAll(accountId);						
		
		var result = new StringBuilder();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		result.append("\n");
		result.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
		
		for (CloudDocument document : documents) {
			result.append("<url>");
			result.append("\n");
			result.append("<loc>");
			result.append("https://sasagu.com/post/");
			result.append(getUrl(document.getId(), document.getString(ArticleDoc.field_title)));
			result.append("</loc>");			
			result.append("\n");
			result.append("<lastmod>");
			if(document.isNull(ArticleDoc.field_updated_at)) {
				result.append(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at).substring(0, 10));
			}else {
				result.append(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_updated_at).substring(0, 10));
			}
			result.append("</lastmod>");			
			result.append("\n");
			result.append("</url>");
			result.append("\n");
		}
		result.append("</urlset>");
		ctx.result(result.toString());
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

}

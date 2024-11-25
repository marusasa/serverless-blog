package ssg.serverlessblog.handler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultArticleList;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.AppProperties;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting articles to display on the blog site.
 * Not used by management functions.
 */
public class ArticleGetList implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleGetList.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultArticleList result = new ResultArticleList();
		try {						
			//Currently multi-tenant is not part of the design.
			//However, account id is used for possible future implementation.
			final var accountId = Env.getAccountIdToUse(ctx);
			final var totalArticlesCount = Env.articleDao.getArticlesForBlogTotalCount(accountId);
			long startAfterMillisec = 0;
			try {
				startAfterMillisec = Long.parseLong(ctx.pathParam("start-after"));
			}catch(Exception e) {
				result.getMessages().add("Invalid 'page' value: " + ctx.pathParam("page"));
			}
			
			if(result.getMessages().size() == 0) {			
				final List<CloudDocument> documents = Env.articleDao.getArticlesForBlog(accountId,startAfterMillisec,AppProperties.getInt("articles.count-per-page"));						
				
				for (CloudDocument document : documents) {
					//make response lighter
					var body = document.getString(ArticleDoc.field_body);
					if(body.length() > 300) {
						body = body.substring(0, 300) + "...";
					}
					final var article = new Article.Builder()
							.title(document.getString(ArticleDoc.field_title))
							.body(body)
							.status(document.getString(ArticleDoc.field_status))
							.articleId(document.getId())
							.createdAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
							.publishedAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at))
							.summary(document.getString(ArticleDoc.field_summary))
							.build();
					result.getArticles().add(article);
				}
				
				result.setResult(AppConst.RESULT_SUCCESS);
			
			}
		}catch(Exception e) {
			logger.error("Error getting articles : " + e.getMessage());
			result.getMessages().add("Error getting articles.");
			e.printStackTrace();
		}
		ctx.json(result);
	}

}

package ssg.serverlessblog.handler;

import java.util.ArrayList;
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
import ssg.serverlessblog.util.CloudDocument;
import ssg.serverlessblog.util.DateTimeUtil;

/**
 * REST service for getting articles to display on the blog site.
 * Not used by management functions.
 */
public class ArticleGetList implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleGetList.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		ResultArticleList result = new ResultArticleList();
		try {			
			List<CloudDocument> documents = new ArrayList<>();
			
			//Currently multi-tenant is not part of the design.
			//However, account id is used for possible future implementation.
			var accountId = Env.getAccountIdToUse(ctx);
			documents = Env.articleDao.getArticlesForBlog(accountId);						
			
			for (CloudDocument document : documents) {
				var article = new Article.Builder()
						.title(document.getString(ArticleDoc.field_title))
						.body(document.getString(ArticleDoc.field_body))
						.status(document.getString(ArticleDoc.field_status))
						.articleId(document.getId())
						.createdAt(DateTimeUtil.formatDateAndTime(Env.getDate(document, ArticleDoc.field_created_at)))
						.publishedAt(DateTimeUtil.formatDateAndTime(Env.getDate(document, ArticleDoc.field_published_at)))
						.build();
				result.getArticles().add(article);
			}
			
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting articles : " + e.getMessage());
			result.getMessages().add("Error getting articles.");
			e.printStackTrace();
		}
		ctx.json(result);
	}

}

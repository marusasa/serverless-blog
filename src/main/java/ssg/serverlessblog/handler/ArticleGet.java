package ssg.serverlessblog.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultArticle;
import ssg.serverlessblog.data_json.ResultArticleList;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;
import ssg.serverlessblog.util.DateTimeUtil;

/**
 * REST service for getting one article to display on the blog site.
 * Not used by management functions.
 */
public class ArticleGet implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleGet.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		ResultArticle result = new ResultArticle();
		try {
			String articleId = ctx.pathParam("articleId");
			//clean artiacle id.
			int index = articleId.indexOf("_");	//the id could be in article-title-that-can-have-many-words_IdOfTheArticle
			articleId = articleId.substring(index+1);		
			
			Optional<CloudDocument>op = Env.articleDao.getArticle(articleId);
			if(op.isPresent() && op.get().getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
				CloudDocument document = op.get();
				var article = new Article.Builder()
						.title(document.getString(ArticleDoc.field_title))
						.body(document.getString(ArticleDoc.field_body))
						.status(document.getString(ArticleDoc.field_status))
						.articleId(document.getId())
						.createdAt(DateTimeUtil.formatDateAndTime(Env.getDate(document, ArticleDoc.field_created_at)))
						.publishedAt(DateTimeUtil.formatDateAndTime(Env.getDate(document, ArticleDoc.field_published_at)))
						.build();
				result.setArticle(article);
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Article not found.");
			}			
		}catch(Exception e) {
			logger.error("Error getting article : " + e.getMessage());
			result.getMessages().add("Error getting article.");
			e.printStackTrace();
		}
		ctx.json(result);
	}

}

package ssg.serverlessblog.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultAiSummary;
import ssg.serverlessblog.data_json.ResultArticle;
import ssg.serverlessblog.data_json.ResultArticleList;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;
import ssg.serverlessblog.util.DateTimeUtil;

/**
 * Controller for handling Article/Post REST services for management.
 */
public class ArticleController {

	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class.getName());
	
	public static void getAiSummary(Context ctx) {
		ResultAiSummary result = new ResultAiSummary();
		try {
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			String articleId = ctx.pathParam("articleId");
			
			//generate
			String summary = Env.articleDao.generateAiSummary(accountId, articleId);
			result.setSummary(summary);
			
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error generating ai summary.",e);
			result.getMessages().add("Error generating ai summary.");
		}
		
		
		ctx.json(result);
	}
	
	public static void createArticle(Context ctx) {
		ResultBase result = new ResultBase();
		try {
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);			
			Article article = ctx.bodyAsClass(Article.class);
			
			String newId = Env.articleDao.createArticle(accountId, article);
			
			logger.info("Data inserted with id: %s".formatted(newId));
			
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error processing new article.",e);
			result.getMessages().add("Error creating new article.");
		}
		ctx.json(result);
    }
	
	public static void getArticle(Context ctx) {
		ResultArticle result = new ResultArticle();
		try {
			final String articleId = ctx.pathParam("articleId");
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			
			Optional<CloudDocument> op = Env.articleDao.getArticleForManage(accountId, articleId); 
						
			if(op.isEmpty()) {
				result.getMessages().add("Article not obtained.");
				ctx.json(result);
				return;
			}
			
			CloudDocument document = op.get();
			var publishedAt = "";
			if(document.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
				publishedAt = Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at);
			}
			var article = new Article.Builder().title(document.getString(ArticleDoc.field_title))
					.body(document.getString(ArticleDoc.field_body))
					.status(document.getString(ArticleDoc.field_status))
					.articleId(document.getId())
					.createdAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
					.publishedAt(publishedAt)
					.summary(document.getString(ArticleDoc.field_summary))
					.build();
			result.setArticle(article);
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting article.",e);
			result.getMessages().add("Error getting article.");
		}
		ctx.json(result);
	}
	public static void updateArticle(Context ctx) {
		ResultBase result = new ResultBase();
		try {
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			Article article = ctx.bodyAsClass(Article.class);
			
			if(Env.articleDao.updateArticle(accountId, article)) {
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Article %s not updated.".formatted(article.articleId()));
			}
									
		}catch(Exception e) {
			logger.error("Error updating article.",e);
			result.getMessages().add("Error updating article.");
		}
		ctx.json(result);
	}
	
	public static void deleteArticle(Context ctx) {
		ResultBase result = new ResultBase();
		try {
			final String articleId = ctx.pathParam("articleId");
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			
			if(Env.articleDao.deleteArticle(accountId, articleId)) {
				result.setResult(AppConst.RESULT_SUCCESS);
			}else {
				result.getMessages().add("Article %s not deleted.".formatted(articleId));
			}
									
		}catch(Exception e) {
			logger.error("Error deleting article.",e);
			result.getMessages().add("Error deleting article.");
		}
		ctx.json(result);
	}
	
	public static void listArticleForManage(Context ctx) {
		ResultArticleList result = new ResultArticleList();
		try {
			String accountId = ctx.sessionAttribute(AccountDoc.id_ref_name);
			List<CloudDocument> list = Env.articleDao.getArticlesForManage(accountId);			
			
			for (CloudDocument document : list) {				
				var publishedAt = "";
				if(document.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
					publishedAt = Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at);
				}
				
				var article = new Article.Builder().title(document.getString(ArticleDoc.field_title))
						.body(document.getString(ArticleDoc.field_body))
						.status(document.getString(ArticleDoc.field_status))
						.articleId(document.getId())
						.createdAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
						.publishedAt(publishedAt)
						.build();								
				result.getArticles().add(article);
			}			
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error getting articles.",e);
			result.getMessages().add("Error getting articles.");
		}
		ctx.json(result);
	}
}

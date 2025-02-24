package ssg.serverlessblog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.daobase.AiLogic;
import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.daobase.DataUtilLogic;
import ssg.serverlessblog.daobase.TagLogic;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ReqAiGrammar;
import ssg.serverlessblog.data_json.ResultAiGrammar;
import ssg.serverlessblog.data_json.ResultAiSummary;
import ssg.serverlessblog.data_json.ResultArticle;
import ssg.serverlessblog.data_json.ResultArticleList;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.data_json.ResultImageList;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Controller for handling Article/Post REST services for management.
 */
public class ArticleController {

	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class.getName());
	
	public static void getAiSummary(Context ctx) {
		final ResultAiSummary result = new ResultAiSummary();
		
		if(!Env.isAiSupported()) {
			result.getMessages().add("AI functionality not available for this environment.");
			
		}else {		
			try {
				final String articleId = ctx.pathParam("articleId");
				
				//generate
				final String summary = AiLogic.generateAiSummary(articleId);
				result.setSummary(summary);
				
				result.setResult(AppConst.RESULT_SUCCESS);
			}catch(Exception e) {
				logger.error("Error generating ai summary.",e);
				result.getMessages().add("Error generating ai summary.");
			}
		}		
		
		ctx.json(result);
	}
	
	public static void getAiGrammarCheck(Context ctx) {
		final ResultAiGrammar result = new ResultAiGrammar();
		
		if(!Env.isAiSupported()) {
			result.getMessages().add("AI functionality not available for this environment.");			
		}else {		
			try {
				final ReqAiGrammar req = ctx.bodyAsClass(ReqAiGrammar.class);
				
				//generate
				final String suggestion = AiLogic.generateAiGrammarCheck(req.prompt(), req.content());
				result.setContent(suggestion);
				
				result.setResult(AppConst.RESULT_SUCCESS);
			}catch(Exception e) {
				logger.error("Error generating ai grammar check.",e);
				result.getMessages().add("Error generating ai grammar check.");
			}
		}
		ctx.json(result);
	}
	
	public static void createArticle(Context ctx) {
		ResultBase result = new ResultBase();
		try {
			final Article article = ctx.bodyAsClass(Article.class);
			
			final String newId = ArticleLogic.createArticle(article);
			
			logger.info("Data inserted with id: %s".formatted(newId));
			
			result.setResult(AppConst.RESULT_SUCCESS);
		}catch(Exception e) {
			logger.error("Error processing new article.",e);
			result.getMessages().add("Error creating new article.");
		}
		ctx.json(result);
    }
	
	public static void getArticle(Context ctx) {
		final ResultArticle result = new ResultArticle();
		try {
			final String articleId = ctx.pathParam("articleId");
			
			final Optional<CloudDocument> op = ArticleLogic.getArticleForManage( articleId); 
			
			final List<CloudDocument> tags = ArticleLogic.getArticleTags(articleId);
			final List<String> tagIds = new ArrayList<>();
			final List<String> tagNames = new ArrayList<>();
			tags.forEach(t -> {
				try {
					Optional<CloudDocument> tag = TagLogic.getTag(t.getString(ArticleTagDoc.field_tag_id));
					tag.ifPresent(tg -> {
						tagIds.add(tg.getId());
						tagNames.add(tg.getString(TagDoc.field_name));
					});
				}catch(Exception e) {
					//do nothing.
				}
			});
			
			if(op.isEmpty()) {
				result.getMessages().add("Article not obtained.");
				ctx.json(result);
				return;
			}
			
			final CloudDocument document = op.get();
			var publishedAt = "";
			if(document.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
				publishedAt = DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at);
			}
			final var article = new Article.Builder().title(document.getString(ArticleDoc.field_title))
					.body(document.getString(ArticleDoc.field_body))
					.status(document.getString(ArticleDoc.field_status))
					.articleId(document.getId())
					.createdAt(DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
					.publishedAt(publishedAt)
					.summary(document.getString(ArticleDoc.field_summary))
					.tagIds(tagIds)
					.tagNames(tagNames)
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
		final ResultBase result = new ResultBase();
		try {
			final Article article = ctx.bodyAsClass(Article.class);
			
			if(ArticleLogic.updateArticle(article) && ArticleLogic.updateArticleTag(article.articleId(), article.tagIds())) {
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
		final ResultBase result = new ResultBase();
		try {
			final String articleId = ctx.pathParam("articleId");
			
			if(ArticleLogic.deleteArticle( articleId)) {
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
		final ResultArticleList result = new ResultArticleList();
		try {
			final List<CloudDocument> list = ArticleLogic.getArticlesForManage();			
			
			for (CloudDocument document : list) {				
				var publishedAt = "";
				if(document.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
					publishedAt = DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at);
				}
				
				final var article = new Article.Builder().title(document.getString(ArticleDoc.field_title))
						.status(document.getString(ArticleDoc.field_status))
						.articleId(document.getId())
						.createdAt(DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
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
		
	public static void addImage(Context ctx) {
		final ResultBase result = new ResultBase();
		if(!Env.isImageManagerSupported()) {
			result.getMessages().add("Image Manager functionality not supported.");			
		}else {	
			try {			
				final var files = ctx.uploadedFiles();
				final String articleId = ctx.pathParam("articleId");
				files.forEach(uploadedFile -> {
					try {
						Env.storageDao.addFile("ARTICLES/" + articleId, uploadedFile.filename(), uploadedFile.content());
					}catch(Exception e) {
						e.printStackTrace();
					}
				 	
				});
				result.setResult(AppConst.RESULT_SUCCESS);
			}catch(Exception e) {
				logger.error("Error getting articles.",e);
				result.getMessages().add("Error getting articles.");
			}
		}
		ctx.json(result);
	}
	
	public static void getImages(Context ctx) {
		final var result = new ResultImageList();
		if(!Env.isImageManagerSupported()) {
			result.getMessages().add("Image Manager functionality not supported.");			
		}else {	
			final String articleId = ctx.pathParam("articleId");
			try {
				result.images = Env.storageDao.getImages(articleId);
				result.setResult(AppConst.RESULT_SUCCESS);
			}catch(Exception e) {
				logger.error("Error getting images.",e);
				result.getMessages().add("Error getting images.");
			}		
		}
		ctx.json(result);
	}
}

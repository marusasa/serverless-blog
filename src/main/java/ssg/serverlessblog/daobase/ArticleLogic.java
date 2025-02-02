package ssg.serverlessblog.daobase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.gae.dao.ArticleDao;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

public class ArticleLogic {
private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
		
	static public List<CloudDocument> getArticleTags(String articleId) throws Exception {
		return Env.articleDao.getArticleTags(articleId);		
	}	
	
	static public boolean updateArticleTag(String articleId, List<String> tagIds) throws Exception {
		var result = false;
		try {
			List<CloudDocument> oldTags = getArticleTags(articleId);
			//convert CloudDocument list to list of String
			List<String> oldIds  = oldTags.stream().map(doc -> doc.getString(ArticleTagDoc.field_tag_id)).toList();
			List<String> tagsToDelete = new ArrayList<>();
			List<String> tagsToAdd = new ArrayList<>();
			
			//populate tagsToDelete
			oldIds.stream().filter(oldId -> !tagIds.contains(oldId))
					.forEach(oldId -> tagsToDelete.add(oldId));
			
			//populate tagsToAdd
			if(tagIds.size() != oldTags.size()) {
				tagIds.stream().filter(newId -> !oldIds.contains(newId)).forEach(newId -> tagsToAdd.add(newId));
			}
	 				
			//delete unneeded tags
			for(String deleteTagId: tagsToDelete) {
				for(CloudDocument cd: oldTags) {
					if(cd.getString(ArticleTagDoc.field_tag_id).equals(deleteTagId)) {
						Env.articleDao.deleteArticleTag(cd.getId());										
					}
				}
			}
			
			//add new tags
			for(String addTagId: tagsToAdd) {
				final Map<String, Object> data = new HashMap<>();				
				data.put(ArticleTagDoc.field_article_id, articleId);
				data.put(ArticleTagDoc.field_tag_id, addTagId);
				data.put(ArticleTagDoc.field_created_at, new Date());
				Env.articleDao.createArticleTag(data);
			}
			
			result = true;
		}catch(Exception e) {
			logger.error("Error while updating Article Tag",e);			
		}
		return result;
	}
		
	static public List<CloudDocument> getArticlesByTag(final String tagId) throws Exception {
		return Env.articleDao.getArticlesByTag(tagId);		
	}

	
	static public long incrementArticleLike(final String articleId) throws Exception {
		Optional<CloudDocument> like = Env.articleDao.getArticleLike(articleId);
		long count = 0L;
		if(like.isEmpty()) {				
			//create new record.
			count = 1L;
			final Map<String, Object> data = new HashMap<>();
			data.put(ArticleLikeDoc.field_like_count,count);
			data.put(ArticleLikeDoc.field_updated_at, new Date());
			Env.articleDao.createArticleLike(articleId, data);
		}else {
			//Update record. Increment value.
			count = like.get().getLong(ArticleLikeDoc.field_like_count);
			count++;
			final Map<String, Object> updates = new HashMap<>();
			updates.put(ArticleLikeDoc.field_like_count,count);
			updates.put(ArticleLikeDoc.field_updated_at, new Date());
			Env.articleDao.updateArticleLike(articleId, updates);
		}
		return count;		
	}



	
	static public boolean isArticleExists(final String articleId) throws Exception {
		return Env.articleDao.isArticleExists(articleId);		
	}



	
	static public String createArticle(final Article article) throws Exception {
		var newId = "";
		try {
			//add article to database
			final Map<String, Object> data = new HashMap<>();
			data.put(ArticleDoc.field_title, article.title());
			data.put(ArticleDoc.field_body, article.body());
			data.put(ArticleDoc.field_status, article.status());
			data.put(ArticleDoc.field_created_at, new Date());
			if(article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
				Date now = new Date();
				data.put(ArticleDoc.field_published_at, now);
				data.put(ArticleDoc.field_published_at_millisec, now.getTime());
			}else {
				data.put(ArticleDoc.field_published_at, null);
				data.put(ArticleDoc.field_published_at_millisec, null);
			}
			data.put(ArticleDoc.field_updated_at, null);
			data.put(ArticleDoc.field_summary, "");	//initially blank
			data.put(ArticleDoc.field_summary_ai, true);
			
			newId = Env.articleDao.createArticle(data);
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

	
	static public Optional<CloudDocument> getArticleForManage(final String articleId) 
			throws Exception {
		return Env.articleDao.getArticleForManage(articleId);		
	}
	
	
	static public Optional<CloudDocument> getArticle(final String articleId) 
			throws Exception {
		return Env.articleDao.getArticle(articleId);		
	}

	
	static public boolean updateArticle(final Article article) throws Exception {
		var result = false;
		try {
			Optional<CloudDocument> articleDoc = Env.articleDao.getArticleForManage(article.articleId());
			
			if(articleDoc.isEmpty()) {
				logger.warn("Article document not found: %s".formatted(article.articleId()));
				return result;
			}
			 
			//update document
			final var oldStatus = articleDoc.get().getString(ArticleDoc.field_status);
			final Map<String, Object> updates = new HashMap<>();
			updates.put(ArticleDoc.field_title, article.title());
			updates.put(ArticleDoc.field_body, article.body());
			updates.put(ArticleDoc.field_status, article.status());
			updates.put(ArticleDoc.field_updated_at, new Date());
			updates.put(ArticleDoc.field_summary, article.summary());
			updates.put(ArticleDoc.field_summary_ai, true);
			
			//published at logic
			if(oldStatus.equals(AppConst.ART_STATUS_DRAFT) && article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
				Date now = new Date();
				updates.put(ArticleDoc.field_published_at, now);
				updates.put(ArticleDoc.field_published_at_millisec, now.getTime());
			}			
			
			result = Env.articleDao.updateArticle(article.articleId(), updates);
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	
	static public List<CloudDocument> getArticlesForManage() throws Exception {
		return Env.articleDao.getArticlesForManage();
	}
	
	
	static public List<CloudDocument> getArticlesForBlog( long publishedAtMillisec, int countPerPage) throws Exception {
		return Env.articleDao.getArticlesForBlog(publishedAtMillisec, countPerPage);		
	}
	
	
	static public List<CloudDocument> getArticlesForBlogAll() throws Exception {
		return Env.articleDao.getArticlesForBlogAll();		
	}
		
	static public long getArticlesForBlogTotalCount() throws Exception {
		return Env.articleDao.getArticlesForBlogTotalCount();		
	}
	static public boolean deleteArticle(final String articleId) throws Exception {
		return Env.articleDao.deleteArticle(articleId);		
	}
}

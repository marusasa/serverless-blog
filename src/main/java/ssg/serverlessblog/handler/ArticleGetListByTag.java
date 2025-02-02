package ssg.serverlessblog.handler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.daobase.DataUtilLogic;
import ssg.serverlessblog.daobase.TagLogic;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultTagArticleList;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting articles to display on the blog site.
 * Not used by management functions.
 */
public class ArticleGetListByTag implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleGetListByTag.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultTagArticleList result = new ResultTagArticleList();
		try {
			String tagId = ctx.pathParam("tagId");
			final int index = tagId.indexOf("_");	//the id could be in article-title-that-can-have-many-words_IdOfTheArticle
			tagId = tagId.substring(index+1);
			
			Optional<CloudDocument> o = TagLogic.getTag(tagId);
			if(o.isPresent()) {
				CloudDocument tagDoc = o.get(); 
				result.setTagName(tagDoc.getString(TagDoc.field_name));
				result.setTagDescription(tagDoc.getString(TagDoc.field_description));
			}else {
				result.getMessages().add("Tag not found for: " + tagId);
			}
			
			if(result.getMessages().size() == 0) {
				final List<CloudDocument> documents = ArticleLogic.getArticlesByTag(tagId);
				for (CloudDocument doc : documents) {
					Optional<CloudDocument> op = ArticleLogic.getArticleForManage(doc.getString(ArticleTagDoc.field_article_id));
					if(op.isPresent()) {
						CloudDocument docArticle = op.get();						
						if(docArticle.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
							final var article = new Article.Builder()
									.title(docArticle.getString(ArticleDoc.field_title))
									.status(docArticle.getString(ArticleDoc.field_status))
									.articleId(docArticle.getId())
									.publishedAt(DataUtilLogic.getJavaScriptUtcDateTime(docArticle, ArticleDoc.field_published_at))
									.summary(docArticle.getString(ArticleDoc.field_summary))
									.build();
							result.getArticles().add(article);
						}
					}
				}
				
				//sort the list by Published At 
				Collections.sort(result.getArticles(),new ArticleComparator());
				
				result.setResult(AppConst.RESULT_SUCCESS);
			
			}
		}catch(Exception e) {
			logger.error("Error getting articles : " + e.getMessage());
			result.getMessages().add("Error getting articles.");
			e.printStackTrace();
		}
		ctx.json(result);
	}
	
	class ArticleComparator implements java.util.Comparator<Article> {
	    @Override
	    public int compare(Article a, Article b) {
	        return b.publishedAt().compareTo(a.publishedAt());
	    }
	}

}

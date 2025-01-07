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
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * REST service for getting one article to display on the blog site.
 * Not used by management functions.
 */
public class ArticleGet implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleGet.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultArticle result = new ResultArticle();
		try {
			String articleId = ctx.pathParam("articleId");
			//clean artiacle id.
			final int index = articleId.indexOf("_");	//the id could be in article-title-that-can-have-many-words_IdOfTheArticle
			articleId = articleId.substring(index+1);		
			
			final List<CloudDocument> tags = Env.articleDao.getArticleTags(articleId);
			final List<String> tagIds = new ArrayList<>();
			final List<String> tagNames = new ArrayList<>();
			tags.forEach(t -> {
				try {
					Optional<CloudDocument> tag = Env.tagDao.getTag(t.getString(ArticleTagDoc.field_tag_id));
					tag.ifPresent(tg -> {
						tagIds.add(tg.getId());
						tagNames.add(tg.getString(TagDoc.field_name));
					});
				}catch(Exception e) {
					//do nothing.
				}
			});
			
			final Optional<CloudDocument>op = Env.articleDao.getArticle(articleId);
			if(op.isPresent() && op.get().getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH)) {
				final CloudDocument document = op.get();
				final var article = new Article.Builder()
						.title(document.getString(ArticleDoc.field_title))
						.body(document.getString(ArticleDoc.field_body))
						.status(document.getString(ArticleDoc.field_status))
						.articleId(document.getId())
						.createdAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
						.publishedAt(Env.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at))
						.likes(document.getLong(ArticleLikeDoc.field_like_count))//likes
						.tagIds(tagIds)
						.tagNames(tagNames)
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

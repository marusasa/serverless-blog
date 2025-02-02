package ssg.serverlessblog.handler;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.data_json.ResultArticleLike;
import ssg.serverlessblog.util.AnalyticsUtil;
import ssg.serverlessblog.util.AppConst;

/**
 * REST service for incrementing article like.
 */
public class ArticleLikeUpdate implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleLikeUpdate.class.getName());
	final static HashSet<String> visitorSet = new HashSet<>();
	
	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		final ResultArticleLike result = new ResultArticleLike();
		try {
			final String articleId = ctx.pathParam("articleId");
			int visitorId = AnalyticsUtil.getVisitorId(ctx);
			String key = articleId + "_" + visitorId;
			
			if(!visitorSet.contains(key)) {
				if(ArticleLogic.isArticleExists(articleId)) {
					result.setCount(ArticleLogic.incrementArticleLike(articleId));
					result.setResult(AppConst.RESULT_SUCCESS);
				}else {
					result.getMessages().add("Article not found.");
				}
				visitorSet.add(key);
			}else {
				result.getMessages().add("Multi-like not allowed.");
			}
						
												
		}catch(Exception e) {
			logger.error("Error updating article like.",e);
			result.getMessages().add("Error updating article like.");
		}
		ctx.json(result);
	}

}

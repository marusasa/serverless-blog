package ssg.serverlessblog.handler;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.daobase.DataUtilLogic;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.ResultArticleList;
import ssg.serverlessblog.documentref.ArticleDoc;
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
			long startAfterMillisec = 0;
			try {
				startAfterMillisec = Long.parseLong(ctx.pathParam("start-after"));
			}catch(Exception e) {
				result.getMessages().add("Invalid 'page' value: " + ctx.pathParam("page"));
			}
			int countPerPage = AppProperties.getInt("articles.count-per-page");
			if(startAfterMillisec == 0) {
				//get total page. Only for the first load.
				final int countTotal = (int)ArticleLogic.getArticlesForBlogTotalCount();				
				int pageTotal = (int) Math.ceil((double) countTotal / countPerPage);
				result.setPageTotal(pageTotal);				
			}
			
			if(result.getMessages().size() == 0) {
				final List<CloudDocument> documents = ArticleLogic.getArticlesForBlog(startAfterMillisec,countPerPage);						
				
				if(documents.size() > countPerPage) {
					//dao tries to retrieve count per page +1.
					//if +1, this indicates that there are more records ('pages').
					result.setHasMore(true);
				}
				
				var count = 0;
				for (CloudDocument document : documents) {
					//if count larger than countPerPage, don't include it.
					//this extra record was used to figure out if it 'hasMore'.
					if(count < countPerPage) {
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
								.createdAt(DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_created_at))
								.publishedAt(DataUtilLogic.getJavaScriptUtcDateTime(document, ArticleDoc.field_published_at))
								.summary(document.getString(ArticleDoc.field_summary))
								.build();
						result.getArticles().add(article);
						//eventually, the correct value will be set.
						result.setLastQueryVal(document.getLong(ArticleDoc.field_published_at_millisec));
					}
					count++;
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

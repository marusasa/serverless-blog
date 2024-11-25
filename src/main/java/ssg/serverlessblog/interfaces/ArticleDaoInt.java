package ssg.serverlessblog.interfaces;

import java.util.List;
import java.util.Optional;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Article.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface ArticleDaoInt {
	public String createArticle(String accountId,Article article) throws Exception;
	public Optional<CloudDocument> getArticleForManage(String accountId,String articleId) throws Exception;
	public Optional<CloudDocument> getArticle(String articleId) throws Exception;
	public boolean updateArticle(String accountId,Article article) throws Exception;
	public List<CloudDocument> getArticlesForManage(String accountId) throws Exception;
	public List<CloudDocument> getArticlesForBlog(String accountId, long publishedAtMillisec, int countPerPage) throws Exception;
	public List<CloudDocument> getArticlesForBlogAll(String accountId) throws Exception;
	public long getArticlesForBlogTotalCount(String accountId) throws Exception;
	public boolean deleteArticle(String accountId, String articleId) throws Exception;	
	public String generateAiSummary(String accountId, String articleId) throws Exception;
	public long incrementArticleLike(String articleId) throws Exception;
	public boolean isArticleExists(String articleId) throws Exception;
}

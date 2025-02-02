package ssg.serverlessblog.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Article.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface ArticleDaoInt {
	public String createArticle(Map<String, Object> data) throws Exception;
	public Optional<CloudDocument> getArticleForManage(String articleId) throws Exception;
	public Optional<CloudDocument> getArticle(String articleId) throws Exception;
	public boolean updateArticle(String articleId, Map<String, Object> data) throws Exception;
	public List<CloudDocument> getArticlesForManage() throws Exception;
	public List<CloudDocument> getArticlesForBlog( long publishedAtMillisec, int countPerPage) throws Exception;
	public List<CloudDocument> getArticlesForBlogAll() throws Exception;
	public List<CloudDocument> getArticlesByTag(String tagId) throws Exception;
	public long getArticlesForBlogTotalCount() throws Exception;
	public boolean deleteArticle(String articleId) throws Exception;
//	public long incrementArticleLike(String articleId) throws Exception;
	public boolean isArticleExists(String articleId) throws Exception;
	public List<CloudDocument> getArticleTags(String articleId) throws Exception;
	//public boolean updateArticleTag(String articleId, List<String> tagIds) throws Exception;
	public boolean deleteArticleTag(String tagId) throws Exception;
	public void createArticleTag(Map<String, Object> data) throws Exception;
	public void createArticleLike(String articleId, Map<String, Object> data) throws Exception;
	public Optional<CloudDocument> getArticleLike(final String articleId) throws Exception;
	public boolean updateArticleLike(String articleId,Map<String, Object> data) throws Exception;
}


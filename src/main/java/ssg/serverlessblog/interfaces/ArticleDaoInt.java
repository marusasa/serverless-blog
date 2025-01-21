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
	public String createArticle(Article article) throws Exception;
	public Optional<CloudDocument> getArticleForManage(String articleId) throws Exception;
	public Optional<CloudDocument> getArticle(String articleId) throws Exception;
	public boolean updateArticle(Article article) throws Exception;
	public List<CloudDocument> getArticlesForManage() throws Exception;
	public List<CloudDocument> getArticlesForBlog( long publishedAtMillisec, int countPerPage) throws Exception;
	public List<CloudDocument> getArticlesForBlogAll() throws Exception;
	public List<CloudDocument> getArticlesByTag(String tagId) throws Exception;
	public long getArticlesForBlogTotalCount() throws Exception;
	public boolean deleteArticle(String articleId) throws Exception;	
	public String generateAiSummary(String articleId) throws Exception;
	public String generateAiGrammarCheck(String prompt, String content) throws Exception;
	public long incrementArticleLike(String articleId) throws Exception;
	public boolean isArticleExists(String articleId) throws Exception;
	public List<CloudDocument> getArticleTags(String articleId) throws Exception;
	public boolean updateArticleTag(String articleId, List<String> tagIds) throws Exception;
}

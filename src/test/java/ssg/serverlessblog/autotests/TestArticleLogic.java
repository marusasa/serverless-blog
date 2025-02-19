package ssg.serverlessblog.autotests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ssg.serverlessblog.daobase.ArticleLogic;
import ssg.serverlessblog.daobase.TagLogic;
import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.ArticleTagDoc;
import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.util.AppConst;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll; 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

@TestMethodOrder(OrderAnnotation.class)
public class TestArticleLogic {

	private static String articleId = "";
	private static String tagId = "";
	
	@BeforeAll
	static void checkEnvironment() {
		TestingUtil.checkReqEnvironment();
	}
	
	@Test
	@Order(1)
    void createArticle() {
		try {
			final var article = new Article.Builder().body("test article")
					.title("test title")
					.status(AppConst.ART_STATUS_DRAFT)
					.build();
			articleId = ArticleLogic.createArticle(article);
			var a = ArticleLogic.getArticle(articleId).get();
			assertTrue(a.getString(ArticleDoc.field_body).equals("test article"));
			assertTrue(a.getString(ArticleDoc.field_title).equals("test title"));
			assertTrue(a.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_DRAFT));
			return;			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}
		
    }
	
	@Test
    void updateArticle() {
		try {
			final var article = new Article.Builder()
					.articleId(articleId)
					.body("test article 2")
					.title("test title 2")
					.status(AppConst.ART_STATUS_PUBLISH)
					.summary("summary.")
					.build();
			ArticleLogic.updateArticle(article);			
			var a = ArticleLogic.getArticle(articleId).get();
			assertTrue(a.getString(ArticleDoc.field_body).equals("test article 2"));
			assertTrue(a.getString(ArticleDoc.field_title).equals("test title 2"));
			assertTrue(a.getString(ArticleDoc.field_status).equals(AppConst.ART_STATUS_PUBLISH));
			
			assertTrue(ArticleLogic.isArticleExists(articleId));
			return;			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}		
    }
	
	@Test
    void articleLikes() {
		try {
			var a = ArticleLogic.getArticle(articleId);
			assertTrue(a.get().getLong(ArticleLikeDoc.field_like_count) == 0L);
			
			//increment likes
			ArticleLogic.incrementArticleLike(articleId);
			
			//likes should be 1
			a = ArticleLogic.getArticle(articleId);
			assertTrue(a.get().getLong(ArticleLikeDoc.field_like_count) == 1L);
			
			return;			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}		
    }
	
	@Test
    void articleTags() {
		try {
			tagId = TagLogic.createTag("testtag", "[]");
			
			var tag = TagLogic.getTag(tagId).get();
			
			assertTrue(tag.get(TagDoc.field_name).equals("testtag"));
			
			TagLogic.updateTag(tagId, tag.getString(TagDoc.field_name), "[]", "test tag.");
			
			
			ArticleLogic.updateArticleTag(articleId, List.of(tagId));
			
			var articleTags = ArticleLogic.getArticleTags(articleId);
			assertTrue(articleTags.getFirst().getString(ArticleTagDoc.field_tag_id).equals(tagId));
			assertTrue(articleTags.size() == 1);
			
			var articlesByTag = ArticleLogic.getArticlesByTag(tagId);
			assertTrue(articlesByTag.size() == 1);
			assertTrue(articlesByTag.getFirst().getString(ArticleTagDoc.field_article_id).equals(articleId));
			
			ArticleLogic.updateArticleTag(articleId, List.of());
			
			//check for 0
			articleTags = ArticleLogic.getArticleTags(articleId);
			assertTrue(articleTags.size() == 0);
			return;			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}		
    }
	
	@AfterAll
	static void cleanUp() {
		try {
			if(TagLogic.deleteTag(tagId)) {
				System.out.println("Deleted tag: %s".formatted(tagId));
			}else {
				fail("Failed to delete tag: %s".formatted(tagId));
			}
		}catch(Exception e) {
			e.printStackTrace();
			fail("Failed to delete tag: %s".formatted(tagId));
		}
		try {
			if(ArticleLogic.deleteArticle(articleId)) {
				System.out.println("Deleted article: %s".formatted(articleId));
			}else {
				fail("Failed to delete article: %s".formatted(articleId));
			}
		}catch(Exception e) {
			e.printStackTrace();
			fail("Failed to delete article: %s".formatted(articleId));
		}
		return;
	}
}

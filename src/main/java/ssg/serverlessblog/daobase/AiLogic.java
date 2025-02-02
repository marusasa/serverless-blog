package ssg.serverlessblog.daobase;

import java.util.Optional;

import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class AiLogic {
	static public String generateAiSummary(final String articleId) throws Exception {
		var result = "";
		
		final Optional<CloudDocument> op = ArticleLogic.getArticleForManage(articleId);
		
		if(op.isPresent()) {
			result = Env.aiDao.generateAiSummary(op.get().getString(ArticleDoc.field_body));
		}
		return result;		
	}
	
	static public String generateAiGrammarCheck(final String prompt, final String content) throws Exception {
		
		return Env.aiDao.generateAiGrammarCheck(prompt, content);
		
	}
}

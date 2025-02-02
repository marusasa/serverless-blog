package ssg.serverlessblog.interfaces;

/**
 * DAO interface for Article.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface AiDaoInt {
	public String generateAiSummary(String content) throws Exception;
	public String generateAiGrammarCheck(String prompt, String content) throws Exception;
}

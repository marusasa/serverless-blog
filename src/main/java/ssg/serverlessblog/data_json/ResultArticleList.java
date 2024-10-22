package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

public class ResultArticleList extends ResultBase {
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private List<Article> articles = new ArrayList<>();

	public List<Article> getArticles() {
		return articles;
	}
	
	

}
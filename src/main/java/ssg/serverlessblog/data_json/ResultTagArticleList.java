package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

public class ResultTagArticleList extends ResultBase {
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private List<Article> articles = new ArrayList<>();
	private String tagName = "";
	private String tagDescription = "";

	public List<Article> getArticles() {
		return articles;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	

}

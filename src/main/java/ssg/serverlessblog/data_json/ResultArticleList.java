package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

public class ResultArticleList extends ResultBase {
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private List<Article> articles = new ArrayList<>();
	private int pageCurrent = 0;
	private int pageMax = 0; 

	public List<Article> getArticles() {
		return articles;
	}

	public int getPageCurrent() {
		return pageCurrent;
	}

	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public int getPageMax() {
		return pageMax;
	}

	public void setPageMax(int pageMax) {
		this.pageMax = pageMax;
	}
	
	

}

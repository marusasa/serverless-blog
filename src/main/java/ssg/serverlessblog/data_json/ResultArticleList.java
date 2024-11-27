package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

public class ResultArticleList extends ResultBase {
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private List<Article> articles = new ArrayList<>();
	private long lastQueryVal = 0;
	private boolean hasMore = false; 
	private int pageTotal = 0;

	public List<Article> getArticles() {
		return articles;
	}

	public long getLastQueryVal() {
		return lastQueryVal;
	}

	public void setLastQueryVal(long lastQueryVal) {
		this.lastQueryVal = lastQueryVal;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	

}

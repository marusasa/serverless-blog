package ssg.serverlessblog.data_json;

public class ResultArticle extends ResultBase {
	
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	private Article article = new Article("","","","","","");

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	
	

}

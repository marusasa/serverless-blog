package ssg.serverlessblog.data_json;

public class ResultBasicInfo extends ResultBase{
	private String title = "";
	private String subTitle = "";
	private String bloggerName = "";
	private String apiUrl = "";
	private String iconUrl = "";
	private String faviconUrl = "";
	
	
	public String getFaviconUrl() {
		return faviconUrl;
	}
	public void setFaviconUrl(String faviconUrl) {
		this.faviconUrl = faviconUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getBloggerName() {
		return bloggerName;
	}
	public void setBloggerName(String bloggerName) {
		this.bloggerName = bloggerName;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
}

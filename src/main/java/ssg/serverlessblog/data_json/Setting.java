package ssg.serverlessblog.data_json;


/**
 * A record class used for communicating the 'Setting' data.
 * 
 * This record type uses Builder Pattern since it has many fields.
 */
public record Setting (String blogTitle, String blogSubTitle, String settingId, String iconUrl, String faviconUrl) {
	
	public static class Builder {
        private String blogTitle;
        private String blogSubTitle;
        private String settingId;
        private String iconUrl;
        private String faviconUrl;
        
		public Builder blogTitle(String blogTitle) {
			this.blogTitle = blogTitle;
			return this;
		}
		public Builder blogSubTitle(String blogSubTitle) {
			this.blogSubTitle = blogSubTitle;
			return this;
		}
		public Builder settingId(String settingId) {
			this.settingId = settingId;
			return this;
		}
		public Builder iconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
			return this;
		}
		public Builder faviconUrl(String faviconUrl) {
			this.faviconUrl = faviconUrl;
			return this;
		}
		
		public Setting build() {
            return new Setting(blogTitle,blogSubTitle,settingId,iconUrl,faviconUrl);
        }
    }
	
}

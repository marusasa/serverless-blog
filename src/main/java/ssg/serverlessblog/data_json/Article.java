package ssg.serverlessblog.data_json;

import java.util.List;

/**
 * Class representing a blog entry.
 * On backend Java side, the classes are represented by the use of word 'Article'.
 * On the frontend Javascript side, the classes are represented by the use of word 'Post'.
 * 
 * This record type uses Builder Pattern since it has many fields.
 */
public record Article (String title, String body, String status, String articleId, 
		String createdAt, String publishedAt, String summary, long likes, List<String> tagIds, List<String> tagNames) {
	
	public static class Builder {
        private String title;
        private String body;
        private String status;
        private String articleId;
        private String createdAt;
        private String publishedAt;
        private String summary;
        private long likes;
        private List<String> tagIds;
        private List<String> tagNames;
        
        public Builder likes(long likes) {
        	this.likes = likes;
        	return this;
        }
        
        public Builder summary(String summary) {
        	this.summary = summary;
        	return this;
        }
        
        public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public Builder articleId(String articleId) {
			this.articleId = articleId;
			return this;
		}

		public Builder createdAt(String createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder publishedAt(String publishedAt) {
			this.publishedAt = publishedAt;
			return this;
		}
		public Builder tagIds(List<String> tagIds) {
			this.tagIds = tagIds;
			return this;
		}
		public Builder tagNames(List<String> tagNames) {
			this.tagNames = tagNames;
			return this;
		}
		
		public Article build() {
            return new Article(title,body,status,articleId,createdAt,publishedAt,summary,likes,tagIds,tagNames);
        }
    }
}

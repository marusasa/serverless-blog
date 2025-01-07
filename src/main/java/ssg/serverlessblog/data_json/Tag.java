package ssg.serverlessblog.data_json;

/**
 * A class representing a page component.
 * Different page component will have different JSON structure used for the field 'json'.
 * When the data is passed to the browser, they are all passed using this class.
 * 
 * This record type uses Builder Pattern since it has many fields.
 */
public record Tag (String name, String json, String tagId, String description) {
	
	public static class Builder {
        private String name;
        private String description;
        private String json;
        private String tagId;

        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder json(String json) {
            this.json = json;
            return this;
        }

        public Builder tagId(String tagId) {
            this.tagId = tagId;
            return this;
        }

        public Tag build() {
            return new Tag(name, json, tagId, description);
        }
    }
	
}

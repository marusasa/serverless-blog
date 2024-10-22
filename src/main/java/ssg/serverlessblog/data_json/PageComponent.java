package ssg.serverlessblog.data_json;

/**
 * A class representing a page component.
 * Different page component will have different JSON structure used for the field 'json'.
 * When the data is passed to the browser, they are all passed using this class.
 * 
 * This record type uses Builder Pattern since it has many fields.
 */
public record PageComponent (String type, String json, long order, boolean enabled, String pageComponentId) {
	
	public static class Builder {
		private String type;
		private String json;
		private long order;
		private boolean enabled;
		private String pageComponentId;
		
		public Builder type(String type) {
			this.type = type;
			return this;
		}
		public Builder json(String json) {
			this.json = json;
			return this;
		}
		public Builder order(long order) {
			this.order = order;
			return this;
		}
		public Builder enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}
		public Builder pageComponentId(String pageComponentId) {
			this.pageComponentId = pageComponentId;
			return this;
		}
		
		public PageComponent build() {
			return new PageComponent(type,json,order,enabled,pageComponentId);
		}
		
		
	}
	
}

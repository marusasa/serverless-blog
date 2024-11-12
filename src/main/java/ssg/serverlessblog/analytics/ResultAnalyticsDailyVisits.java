package ssg.serverlessblog.analytics;

import java.util.ArrayList;
import java.util.List;

import ssg.serverlessblog.data_json.ResultBase;

/**
 * Result class for Daily Visits.
 * This will be converted to json and sent to browser. 
 */
public class ResultAnalyticsDailyVisits extends ResultBase {
	private List<String> labels = new ArrayList<>();
	private List<Datasets> datasets = new ArrayList<>();
	
	public record Datasets(String label, List<Long> data) {
		public static class Builder {
			private String label;
			private List<Long> data;

			public Builder label(String label) {
				this.label = label;
				return this;
			}

			public Builder data(List<Long> data) {
				this.data = data;
				return this;
			}

			public Datasets build() {
				return new Datasets(label, data);
			}
		}
	}

	public List<String> getLabels() {
		return labels;
	}

	public List<Datasets> getDatasets() {
		return datasets;
	}
	
	
}

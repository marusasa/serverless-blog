package ssg.serverlessblog.analytics;

import java.util.ArrayList;
import java.util.List;

import ssg.serverlessblog.data_json.ResultBase;

/**
 * Result class for Daily Visits.
 * This will be converted to json and sent to browser. 
 */
public class ResultAnalyticsPageEngagement extends ResultBase {
	private List<Engagement> engagements = new ArrayList<>();
	
	public record Engagement(String path, long countTotal, long countActual, String averageTime) {
		public static class Builder {
			private String path;
			private long countTotal;
			private long countActual;
			private String averageTime;

			public Builder path(String path) {
				this.path = path;
				return this;
			}

			public Builder countTotal(long countTotal) {
				this.countTotal = countTotal;
				return this;
			}

			public Builder countActual(long countActual) {
				this.countActual = countActual;
				return this;
			}

			public Builder averageTime(String averageTime) {
				this.averageTime = averageTime;
				return this;
			}

			public Engagement build() {
				return new Engagement(path, countTotal, countActual, averageTime);
			}
		}
	}

	public List<Engagement> getEngagements() {
		return engagements;
	}
	
}

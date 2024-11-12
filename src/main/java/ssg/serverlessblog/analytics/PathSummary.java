package ssg.serverlessblog.analytics;

import java.util.HashMap;

/**
 * Class used by analytics functionality. 
 */
public class PathSummary {
	public String path = "";
	public HashMap<Long, Long> dateActualMap = new HashMap<>();
	public HashMap<Long, Long> dateTotalMap = new HashMap<>();
	public HashMap<Long, Long> dateAverageMap = new HashMap<>();
	
	public PathSummary(String path) {
		this.path = path;
	}
	
	@SuppressWarnings("unused")
	private PathSummary() {};
	
	
	
}

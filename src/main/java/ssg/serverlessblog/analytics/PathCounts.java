package ssg.serverlessblog.analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used by analytics functionality. 
 */
public class PathCounts {
	public String path = "";
	public List<Long> totalList = new ArrayList<>();
	public List<Long> actualList = new ArrayList<>();
	
	public PathCounts(String path) {
		this.path = path;
	}
	
	@SuppressWarnings("unused")
	private PathCounts() {}
}

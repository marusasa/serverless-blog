package ssg.serverlessblog.analytics;

import java.util.HashMap;
import java.util.Set;

/**
 * Class used by analytics functionality.  
 */
public class Visitor {
	public long visitorId = 0L;
	public HashMap<String, Long> pathAndDuration = new HashMap<>();
	public HashMap<String, Long> pathVisibleTime = new HashMap<>();
	
	public Visitor(long visitorId) {
		this.visitorId = visitorId;
	}
	
	public boolean isRealPerson() {		
		final Set<String> keys = pathAndDuration.keySet();
		for(String key:keys) {
			if(key.equals("/")) {
				if(pathAndDuration.get(key) > 3000) {
					//if stayed longer than 3 sec in '/'
					return true;
				}
			}else if(pathAndDuration.get(key) > 10000){
				//if stayed longer than 10 sec
				return true;
			}
		}
		
		//if no match.
		return false;
	}
	
}

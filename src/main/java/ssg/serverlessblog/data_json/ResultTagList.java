package ssg.serverlessblog.data_json;

import java.util.ArrayList;
import java.util.List;

public class ResultTagList extends ResultBase{
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	public List<Tag> tags = new ArrayList<>();
}

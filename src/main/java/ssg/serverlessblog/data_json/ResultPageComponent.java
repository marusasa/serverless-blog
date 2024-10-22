package ssg.serverlessblog.data_json;

public class ResultPageComponent extends ResultBase{
	
	/**
	 * For service result, always instantiate an object to prevent null situation.
	 */
	public PageComponent component = new PageComponent("","",0,false,"");
}

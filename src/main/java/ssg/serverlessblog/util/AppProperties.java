package ssg.serverlessblog.util;

/**
 * Class that holds environment specific properties.
 * Make sure to review this before every deployment to cloud PaaS.
 */
public class AppProperties {
	/*-------------------------------------
	 * Test Environment
	 -------------------------------------*/
//	public static boolean isTest = true;
//	public static boolean isSingleTenant = true;

	/*-------------------------------------
	 * Prod Environment
	 -------------------------------------*/
	public static boolean isTest = false;
	public static boolean isSingleTenant = true;
}

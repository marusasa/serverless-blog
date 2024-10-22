package ssg.serverlessblog.interfaces;

/**
 * DAO interface for System.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface SystemDaoInt {
	public int getAccountsSize() throws Exception;
	public String getSingleTenantAccoundId() throws Exception;
	public String createInitialSystemData() throws Exception;	
	
}

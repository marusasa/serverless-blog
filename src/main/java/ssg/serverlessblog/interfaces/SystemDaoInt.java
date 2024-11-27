package ssg.serverlessblog.interfaces;

/**
 * DAO interface for System.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface SystemDaoInt {
	public String createInitialSystemData() throws Exception;	
	
}

package ssg.serverlessblog.interfaces;

import java.util.Map;
import java.util.Optional;

import ssg.serverlessblog.util.CloudDocument;

/**
 * DAO interface for Users.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface UserDaoInt {
//	public boolean login(String username, String password) throws Exception;
	public Optional<CloudDocument> getUser(String username) throws Exception;
	public void createUser(String userName,Map<String, Object> data) throws Exception;
}

package ssg.serverlessblog.interfaces;

import java.util.Optional;

/**
 * DAO interface for Users.
 * 
 * Implementation is needed for each supported Cloud serverless environments (PaaS).
 * (At the moment there is only one implementation.
 */
public interface UserDaoInt {
	public Optional<String> login(String username, String password) throws Exception;
}

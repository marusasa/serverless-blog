package ssg.serverlessblog.daobase;

import java.util.Optional;

import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;
import ssg.serverlessblog.util.PasswordUtil;

public class UserLogic {
		
	public static boolean login(String username, String password) throws Exception {
		boolean result = false;
		Optional<CloudDocument> opt = Env.userDao.getUser(username);
		if (opt.isPresent()) {
			CloudDocument doc = opt.get();
			// user found.
			final String passInDataStore = doc.getString(UserDoc.field_password);
			// hash the provided password using a 'salt' value retrieved from data store.
			final String passHash = PasswordUtil.hashPassword(password, doc.getString(UserDoc.field_salt)).get();
			// compare the hashed value.
			if (passInDataStore.equals(passHash)) {
				// username and password match.
				result = true;
			}
		} else {
			System.out.println("No matching documents found.");
		}

		return result;
	}
	
	public static Optional<CloudDocument> getUser(String username) throws Exception{
		return Env.userDao.getUser(username);
	}
}

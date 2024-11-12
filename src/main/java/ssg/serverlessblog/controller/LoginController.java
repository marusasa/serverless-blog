package ssg.serverlessblog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import ssg.serverlessblog.data_json.ResultCheckLogin;
import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.util.AppConst;

/**
 * Controller for handling Login REST services for management.
 */
public class LoginController {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class.getName());
	
	/**
	 * Checks the user is currently logged in.
	 * It looks at a session data.
	 * This method is called when the user first accesses the management view.
	 * 
	 * @param ctx
	 */
	public static void checkLogin(Context ctx) {
		final ResultCheckLogin result = new ResultCheckLogin();
		final String userName = ctx.sessionAttribute(UserDoc.id_ref_name);
		if(userName != null && !userName.isBlank()) {
			result.setResult(AppConst.RESULT_SUCCESS);
			result.setUserName(userName);
		}else {
			result.getMessages().add("Not logged in.");
		}

		ctx.json(result);		
    }
}

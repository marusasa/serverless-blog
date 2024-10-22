package ssg.serverlessblog.handler;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import ssg.serverlessblog.data_json.LoginForm;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.documentref.AccountDoc;
import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;

/**
 * REST service for login.
 */
public class LoginHandler implements Handler {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class.getName());

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		ResultBase result = new ResultBase();
		try{
			LoginForm login = ctx.bodyAsClass(LoginForm.class);			
			Optional<String> opOfAccountId = Env.userDao.login(login.username(), login.password());
			
			if(opOfAccountId.isPresent()){
				//put user name in the session.
				ctx.sessionAttribute(UserDoc.id_ref_name,login.username());
				//put account id in the session.
				ctx.sessionAttribute(AccountDoc.id_ref_name, opOfAccountId.get());
				result.setResult(AppConst.RESULT_SUCCESS);	
			}else {
				result.getMessages().add("Login failed.");
			}
		}catch(Exception e) {
			logger.error("Error processing Login.",e);
			result.getMessages().add("Error processing login.");
		}
		ctx.json(result);
	}

}

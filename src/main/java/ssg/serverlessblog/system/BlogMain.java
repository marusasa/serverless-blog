package ssg.serverlessblog.system;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.delete;

import org.eclipse.jetty.server.session.NullSessionCache;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.staticfiles.Location;
import ssg.serverlessblog.controller.ArticleController;
import ssg.serverlessblog.controller.LoginController;
import ssg.serverlessblog.controller.PageComponentController;
import ssg.serverlessblog.controller.SettingController;
import ssg.serverlessblog.data_json.ResultBase;
import ssg.serverlessblog.documentref.UserDoc;
import ssg.serverlessblog.handler.ArticleGet;
import ssg.serverlessblog.handler.ArticleGetList;
import ssg.serverlessblog.handler.BasicInfoHandler;
import ssg.serverlessblog.handler.LoginHandler;
import ssg.serverlessblog.handler.PageComponentHandler;
import ssg.serverlessblog.util.AppProperties;

/**
 * The main class that starts the server.
 * This starts the Javalin server.
 */
public class BlogMain {

	static Logger logger = LoggerFactory.getLogger(BlogMain.class);	
	
	public static void main(String[] args) {
		var app = Javalin.create(config -> {
			config.staticFiles.add(staticFiles -> {
				staticFiles.hostedPath = "/"; // change to host files on a subpath, like '/assets'
				staticFiles.directory = "public"; // the directory where your files are located
				staticFiles.location = Location.CLASSPATH; // Location.CLASSPATH (jar) or Location.EXTERNAL (file
															// system)
				staticFiles.precompress = false; // if the files should be pre-compressed and cached in memory
													// (optimization)
				staticFiles.aliasCheck = null; // you can configure this to enable symlinks (=
												// ContextHandler.ApproveAliases())
				// staticFiles.headers = Map.of(...); // headers that will be set for the files
				staticFiles.skipFileFunction = req -> false; // you can use this to skip certain files in the dir, based
																// on the HttpServletRequest
			});
			config.jetty.modifyServletContextHandler(jettyContext ->{
				//jettyContext.setMaxFormContentSize(2000000);	//2 Megabyte to give some buffer
				//Session Handling
				jettyContext.setSessionHandler(noSqlSessionHandler());
				jettyContext.getSessionHandler().getSessionCookieConfig().setPath("/mng");
				jettyContext.getSessionHandler().getSessionCookieConfig().setMaxAge(31_536_000);	//365 days * 24h * 60min * 60 sec = 31,536,000
			});
						
			if(AppProperties.getBoolean("env.is-test")) {
				config.bundledPlugins.enableCors(cors -> {
			        cors.addRule(it -> {
			        	 it.anyHost();
			        });
			    });
			}else {
				//automatically redirect to HTTPS
				config.bundledPlugins.enableSslRedirects();
			}
			
			//
			/*----------------------------------------------------
			 * Management services. Login is required to access these
			 * services. Once login, a session cookie will be created 
			 * so the user doesn't have to login every time they want to
			 * manage the site.
			 ---------------------------------------------------*/
			config.router.apiBuilder(() -> {
				path("/mng",() ->{
					path("/articles", () ->{
						post(ArticleController::createArticle);
						get(ArticleController::listArticleForManage);
						patch(ArticleController::updateArticle);
						path("/{articleId}",() -> {
							get(ArticleController::getArticle);
							delete(ArticleController::deleteArticle);
							path("ai-summary",() ->{
								get(ArticleController::getAiSummary);
							});
						});
					});
					path("setting", () ->{
						get(SettingController::get);
						patch(SettingController::update);
					});
					path("/check-login", () -> {
						post(LoginController::checkLogin);
					});
					path("/components", () ->{
						get(PageComponentController::getList);
						post(PageComponentController::createNewDefault);
						path("/{pageComponentId}",() -> {
							get(PageComponentController::getItem);
							delete(PageComponentController::deleteItem);
							path("/profile-pic",() -> {
								patch(PageComponentController::updateProfilePic);
							});
							path("/link-list",() -> {
								patch(PageComponentController::updateLinkList);
							});
							path("/text-box",() -> {
								patch(PageComponentController::updateTextBox);
							});
						});
					});					
				});
			});
		});
		
		
		/*----------------------------------------------------
		 * These are for public requests. No login required.
		 ---------------------------------------------------*/
		app.get("/basic-info", new BasicInfoHandler());		
		app.get("/articles", new ArticleGetList());
		app.get("/articles/{articleId}", new ArticleGet());
		app.post("/login", new LoginHandler());
		app.get("/components", new PageComponentHandler());
		
		/*
		 * Redirect some path to index.
		 */
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("public/index.html");		
		try(InputStreamReader isr = new InputStreamReader(is);){
			String indexHtml = new BufferedReader(isr)
					   .lines().collect(Collectors.joining("\n"));
			app.get("/post/*", ctx -> {ctx.html(indexHtml);});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		//--------------------------------------------------------------
		//Make sure user is logged in for management REST
		//service calls.
		//If not logged in, it will throw an exception, 
		//which is handled below.
		app.before("/mng/*",ctx ->{
			String userName = ctx.sessionAttribute(UserDoc.id_ref_name);
			if(userName == null || userName.isBlank()) {
				throw new UnauthorizedResponse("Invalid Session. You are not logged in.");
			}			
		});
		app.exception(UnauthorizedResponse.class, (e, ctx) -> {
			ResultBase result = new ResultBase();
			result.getMessages().add(e.getMessage());
			ctx.json(result);
		});
		//End making sure user is logged in.
		//--------------------------------------------------------------
		
		//If requested resource is not found, always redirect to the 
		//main page.
		//This can happen when a user hits 'Refresh' while on a url
		//generated by React Router.
		app.exception(NotFoundResponse.class, (e, ctx) -> {
			ctx.redirect("/");
		});
		
		//Make sure datastore is ready.
		if(AppProperties.getBoolean("env.run-first-run")) {
			firstRun();		
		}
		
		app.start(8080);
		
	}
	
	public static SessionHandler noSqlSessionHandler() {
	    SessionHandler sessionHandler = new SessionHandler();
	    SessionCache sessionCache = new NullSessionCache(sessionHandler);//no caching
	    sessionCache.setSessionDataStore(
	    	Env.noSqlSessionDataStore
	    );
	    sessionHandler.setSessionCache(sessionCache);
	    sessionHandler.setHttpOnly(true);
	    // make additional changes to your SessionHandler here
	    return sessionHandler;
	}
	
	/**
	 * This method gets invoked every time the server starts.
	 * It checks certain datastore existence, if it  doesn't exists,
	 * then it will create some initial records needed for the plataform to 
	 * run.
	 */
	private static void firstRun() {
		try {
			//check if account record exists
			int accountsCount = Env.systemDao.getAccountsSize();
			if(accountsCount == 0) {
				//Account data doesn't exists. Create the default account.
				Env.systemDao.createInitialSystemData();
				logger.info("***************************************************************");
				logger.info("* Initial Data Preparation Completed.");
				logger.info("***************************************************************");
			}else if(accountsCount == 1) {
				var message = "No action.";
				logger.info("***************************************************************");
				logger.info("* Initial Data Checked. %s ".formatted(message));
				logger.info("***************************************************************");
			}else {
				//size is >= 2. 
				if(AppProperties.getBoolean("env.is-single-tenant")) {
					logger.error("***************************************************************");
					logger.error("* Initial Data Check Result: ");
					logger.error("* Detected multiple account record for single tenant system.");
					logger.error("* This is an error.");
					logger.error("***************************************************************");					
				}else {
					logger.info("***************************************************************");
					logger.info("* Initial Data Checked. No action.");
					logger.info("***************************************************************");
				}
			}
		}catch(Exception e) {
			logger.error("Error while initial data preparation.",e);
			logger.info("***************************************************************");
			logger.info("* Initial Data Preparation - Error occurred. Check Datastore. ");
			logger.info("***************************************************************");
		}			
	}
	
}

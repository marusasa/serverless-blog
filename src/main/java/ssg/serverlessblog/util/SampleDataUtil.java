package ssg.serverlessblog.util;

import java.util.ArrayList;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.LinkItem;
import ssg.serverlessblog.data_json.PCLinkList;
import ssg.serverlessblog.data_json.PCProfilePic;
import ssg.serverlessblog.data_json.PCTextBox;

/**
 * A class used for generating the initial sample data.
 */
public class SampleDataUtil {

	public static Article getSampleArticleWelcome(String password) {
		final var a = new Article.Builder()
				.title("Login information")
				.body(
					"""
					Congratulations!
					
					Your blog is up and running.
					
					Login to the management console by clicking "Manage" from the side-bar:
					
					Username: admin
					Password: %s
					""".formatted(password))
				.status(AppConst.ART_STATUS_PUBLISH)
				.build();
		return a;
	}
	
	public static Article getSampleArticle(String additional) {
		final var a = new Article.Builder()
				.title("Sample post " + additional)
				.body(
					"""
					Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vel varius nibh. Suspendisse potenti. Suspendisse ultrices rhoncus enim, quis volutpat leo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi convallis elit odio, a interdum eros semper sed. Curabitur ac ex est. Morbi blandit ut lectus ac vulputate. Praesent sapien tellus, imperdiet nec iaculis at, tincidunt at dui. Nam fringilla enim sit amet velit blandit malesuada.

					Nullam et tellus nec turpis ornare dictum nec id neque. Vestibulum erat massa, interdum non lacus vel, facilisis tincidunt urna. Aliquam erat arcu, lobortis nec justo ut, pretium cursus ipsum. Ut condimentum nisi in ante malesuada, ut auctor lectus congue. Sed porttitor nibh dui, at lobortis orci vulputate a. Curabitur tempus, neque ut mollis tristique, ex odio posuere tellus, eu auctor tellus est et eros. Fusce a euismod dui. Aenean et sapien condimentum, tincidunt neque id, laoreet quam. Fusce mauris purus, ullamcorper eu sapien sit amet, cursus eleifend leo. Sed arcu justo, lobortis id nisi ut, suscipit vestibulum orci. Nam quis nunc quis risus vestibulum fringilla sit amet at eros. Nunc sed tempus nisi. Mauris elementum porta mauris vel dapibus. In finibus quis nulla vel vulputate.
					""")
				.status(AppConst.ART_STATUS_PUBLISH)
				.build();
		return a;
	}
	
	public static PCProfilePic getSampleProfilePic() {	
		final var pp = new PCProfilePic(
				"https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp",
				6);
		return pp;
	}
	public static PCTextBox getSampleTextBoxAboutMe() {	
		final var pp = new PCTextBox(
				"About Me",
				"""
				About me.
						
				About me.
				"""
				);
		return pp;
	}
	
	//Link List
	public static PCLinkList getSampleLinkList() {
		final var ll = new PCLinkList("My Links", new ArrayList<LinkItem>());
		ll.items().add(new LinkItem("My Github Projects","https://github.com/marusasa?tab=repositories",
				"My open source projects."));
		ll.items().add(new LinkItem("Serverless Blog Project","https://github.com/marusasa/serverless-blog",
				"Blog platform that powers this site."));
		return ll;
	}
	
	//Text Box
	public static PCTextBox getSampleTextBox() {
		return new PCTextBox("Textbox",
				"""
				Hello.
				
				More text here.
				"""
				);
	}
	
}

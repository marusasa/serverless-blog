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

	public static Article getSampleArticle(String password) {
		final var a = new Article.Builder()
				.title("Your blog is up and running!")
				.body(
					"""
					Congratulations!
					
					Your blog is up and running.
					
					Login to the management console using:
					
					Username: admin
					Password: %s
					""".formatted(password))
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

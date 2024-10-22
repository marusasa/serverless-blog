package ssg.serverlessblog.util;

import java.util.ArrayList;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.data_json.LinkItem;
import ssg.serverlessblog.data_json.PCLinkList;
import ssg.serverlessblog.data_json.PCProfilePic;

/**
 * A class used for generating the initial sample data.
 */
public class SampleDataUtil {

	public static Article getSampleArticle() {
		var a = new Article(
				"Sample Article",
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nOn the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.",
				AppConst.ART_STATUS_PUBLISH,
				"","","");
		return a;
	}
	
	public static PCProfilePic getSampleProfilePic() {	
		var pp = new PCProfilePic(
				"https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp",
				6);
		return pp;
	}
	
	//Link List
	public static PCLinkList getSampleLinkList() {
		var ll = new PCLinkList("My Links", new ArrayList<LinkItem>());
		ll.items().add(new LinkItem("My Github Projects","https://github.com/marusasa?tab=repositories",
				"My open source projects."));
		ll.items().add(new LinkItem("Serverless Blog Project","tbd",
				"Blog platform that powers this blog you are seeing."));
		return ll;
	}
	
}

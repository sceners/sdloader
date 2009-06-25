package sdloader.main;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderAppStart extends TestCase {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		sdloader.setUseNoCacheMode(true);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/testwebapp", "webapps/test");

		sdloader.addWebAppContext(webapp);

		sdloader.start();
		try {
			MiscUtils.openBrowser("http://localhost:" + sdloader.getPort()
					+ "/testwebapp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

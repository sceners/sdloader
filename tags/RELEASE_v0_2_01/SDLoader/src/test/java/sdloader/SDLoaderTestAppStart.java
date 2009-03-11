package sdloader;

import junit.framework.TestCase;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderTestAppStart extends TestCase {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		sdloader.setUseNoCacheMode(true);

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

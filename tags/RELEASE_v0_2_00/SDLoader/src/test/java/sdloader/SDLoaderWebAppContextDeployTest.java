package sdloader;

import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class SDLoaderWebAppContextDeployTest {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setUseOutSidePort(true);
		WebAppContext webapp = new WebAppContext("/testwebapp", "testwebapp");
		sdloader.addWebAppContext(webapp);
		sdloader.start();
		Browser.open("http://localhost:" + sdloader.getPort());
	}
}

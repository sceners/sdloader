package sdloader.main;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class SDLoaderWebAppContextDeploy {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setUseOutSidePort(true);
		WebAppContext webapp = new WebAppContext("/testwebapp", "webapps/test");
		sdloader.addWebAppContext(webapp);
		sdloader.start();
		Browser.open("http://localhost:" + sdloader.getPort());
	}
}

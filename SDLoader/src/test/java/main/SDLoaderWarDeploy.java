package main;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Browser;

public class SDLoaderWarDeploy {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setAutoPortDetect(true);

		WebAppContext webapp = new WebAppContext("/t2",
				"webapps/t2-samples.war");
		sdloader.addWebAppContext(webapp);

		sdloader.start();

		Browser.open("http://localhost:" + sdloader.getPort());
	}
}

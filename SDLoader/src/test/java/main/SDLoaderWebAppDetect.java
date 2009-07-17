package main;

import sdloader.SDLoader;
import sdloader.util.Browser;

public class SDLoaderWebAppDetect {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setAutoPortDetect(true);
		sdloader.setUseOutSidePort(true);

		sdloader.start();
		Browser.open("http://localhost:" + sdloader.getPort());
	}
}

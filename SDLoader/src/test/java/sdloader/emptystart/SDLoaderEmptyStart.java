package sdloader.emptystart;

import sdloader.SDLoader;

public class SDLoaderEmptyStart {

	public static void main(String[] args) {

		SDLoader sdloader = new SDLoader(8080);
		sdloader.setWebAppsDir("src/test/java/sdloader/emptystart/webapps");
		sdloader.start();
	}
}

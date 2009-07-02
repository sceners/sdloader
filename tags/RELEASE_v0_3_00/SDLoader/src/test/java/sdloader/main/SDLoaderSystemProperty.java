package sdloader.main;

import sdloader.SDLoader;

public class SDLoaderSystemProperty {

	public static void main(String[] args) {
		SDLoader sdLoader = new SDLoader();
		System.setProperty(SDLoader.KEY_SDLOADER_WEBAPPS_DIR,
				"src/test/resources/sdloader");
		System.setProperty("sdloaderwebapp", "webapps");
		sdLoader.start();
	}
}

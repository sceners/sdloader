package sdloader;

import junit.framework.TestCase;

public class SDLoaderSystemPropertyTest extends TestCase {

	public static void main(String[] args){
		SDLoader sdLoader = new SDLoader();
		System.setProperty(SDLoader.KEY_SDLOADER_WEBAPP_PATH,"src/test/resources/sdloader");
		System.setProperty("sdloaderwebapp","webapps");
		sdLoader.start();
	}
}

package sdloader;

import sdloader.util.Browser;

public class SDLoaderSSLTest {

	public static void main(String[] args) {

		SDLoader sdloader = new SDLoader(8080);
		sdloader.setAutoPortDetect(true);
		sdloader.setSSLEnable(true);
		sdloader.setUseOutSidePort(true);

		sdloader.start();
		Browser.open("https://localhost:" + sdloader.getPort());
	}
}

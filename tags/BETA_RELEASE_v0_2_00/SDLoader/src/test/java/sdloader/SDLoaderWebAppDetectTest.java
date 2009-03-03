package sdloader;

import sdloader.util.MiscUtils;

public class SDLoaderWebAppDetectTest {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setUseOutSidePort(true);

		sdloader.start();
		try {
			MiscUtils.openBrowser("http://localhost:" + sdloader.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

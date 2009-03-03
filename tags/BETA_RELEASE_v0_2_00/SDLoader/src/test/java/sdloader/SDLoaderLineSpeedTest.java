package sdloader;

import sdloader.constants.LineSpeed;
import sdloader.util.MiscUtils;

public class SDLoaderLineSpeedTest {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		sdloader.setLineSpeed(LineSpeed.ISDN_64K_BPS);

		sdloader.start();
		try {
			MiscUtils.openBrowser("http://localhost:" + sdloader.getPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package sdloader;

import sdloader.constants.LineSpeed;
import sdloader.util.Browser;

public class SDLoaderLineSpeedTest {

	public static void main(String[] args) {

		SDLoader sdloader = new SDLoader(8080);
		sdloader.setAutoPortDetect(true);
		sdloader.setLineSpeed(LineSpeed.ISDN_64K_BPS);

		sdloader.start();
		Browser.open("http://localhost:" + sdloader.getPort());
	}
}

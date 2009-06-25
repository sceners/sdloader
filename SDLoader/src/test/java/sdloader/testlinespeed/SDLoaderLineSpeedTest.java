package sdloader.testlinespeed;

import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.constants.LineSpeed;
import sdloader.javaee.WebAppContext;

public class SDLoaderLineSpeedTest extends TestCase {

	public void testLineSpeed() throws Exception {

		SDLoader sdloader = new SDLoader(8080);
		try {
			sdloader.setLineSpeed(LineSpeed.ISDN_64K_BPS);
			WebAppContext webapp = new WebAppContext("/test",
					"src/test/java/sdloader/testlinespeed");
			sdloader.addWebAppContext(webapp);

			sdloader.start();
			long time = System.currentTimeMillis();

			URL url = new URL("http://localhost:8080/test/linespeed");

			InputStream is = url.openConnection().getInputStream();
			byte[] buf = new byte[1024];
			while (is.read(buf) != -1) {
			}
			assertTrue((System.currentTimeMillis() - time) > 5000);
		} finally {
			sdloader.stop();
		}

	}
}

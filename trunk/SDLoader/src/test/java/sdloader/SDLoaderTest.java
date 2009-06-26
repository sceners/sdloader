package sdloader;

import java.lang.reflect.Field;

import junit.framework.TestCase;

public class SDLoaderTest extends TestCase {

	public void testConfigKeys() throws Exception {

		Field[] fields = SDLoader.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().startsWith("KEY")) {
				assertTrue(SDLoader.CONFIG_KEYS.contains(fields[i].get(null)));
			}
		}
	}

	public void testForceStop() {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setWebAppsDir("src/test/java/sdloader/main/webapps");
		sdloader.start();

		sdloader = new SDLoader(8080);
		sdloader.setWebAppsDir("src/test/java/sdloader/main/webapps");
		sdloader.start();

		sdloader.stop();
	}

	public void testWaitForStop() {
		final SDLoader sdloader = new SDLoader(8080);
		sdloader.setWebAppsDir("src/test/java/sdloader/main/webapps");
		sdloader.start();

		long time = System.currentTimeMillis();
		new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
					sdloader.stop();
				} catch (Exception e) {
					fail();
				}
			}
		}.start();
		sdloader.waitForStop();
		assertTrue((System.currentTimeMillis() - time) >= 1000);
	}
}

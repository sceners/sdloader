package test.requesturl;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Util;

public class RequestURLTest extends TestCase {

	public void testRequestURL() throws Exception {

		SDLoader sdloader = new SDLoader(8190);
		try {
			WebAppContext webapp = new WebAppContext("/requesturl",
					"src/test/java/test/requesturl");
			sdloader.addWebAppContext(webapp);

			sdloader.start();

			Util.getRequestContent("http://localhost:" + sdloader.getPort()
					+ "/requesturl/hoge.do");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sdloader.stop();
		}

	}

}

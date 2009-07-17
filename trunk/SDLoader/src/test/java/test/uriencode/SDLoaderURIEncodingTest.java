package test.uriencode;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Util;

public class SDLoaderURIEncodingTest extends TestCase {

	public void testEncode() throws Exception {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/encode",
				"src/test/java/test/uriencode");
		sdloader.addWebAppContext(webapp);

		sdloader.start();

		String result = Util.getRequestContent("http://localhost:"
				+ sdloader.getPort()
				+ "/encode/%E5%86%86%E9%AB%98%E5%BA%A6.jsp");

		assertTrue(result.indexOf("円高度") != -1);
	}
}

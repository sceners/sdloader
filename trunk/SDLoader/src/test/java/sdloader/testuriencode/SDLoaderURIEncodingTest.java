package sdloader.testuriencode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;

public class SDLoaderURIEncodingTest extends TestCase {

	public void testEncode() throws Exception {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/encode",
				"src/test/java/sdloader/testuriencode");
		sdloader.addWebAppContext(webapp);

		sdloader.start();

		String result = getRequestContent("http://localhost:"
				+ sdloader.getPort()
				+ "/encode/%E5%86%86%E9%AB%98%E5%BA%A6.jsp");

		assertTrue(result.indexOf("円高度") != -1);
	}

	protected String getRequestContent(String urlText) throws Exception {
		URL url = new URL(urlText);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setUseCaches(false);
		urlcon.setDoOutput(true);
		PrintStream ps = new PrintStream(urlcon.getOutputStream());
		ps.close();
		urlcon.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlcon
				.getInputStream(), "UTF-8"));
		String body = "";
		String line = null;
		while ((line = reader.readLine()) != null) {
			body += line;
		}
		reader.close();
		urlcon.disconnect();
		return body;
	}
}

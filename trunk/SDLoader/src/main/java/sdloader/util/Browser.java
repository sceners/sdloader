package sdloader.util;

import java.io.IOException;

public class Browser {

	public static void open(String url) throws IOException {
		MiscUtils.openBrowser(url);
	}
}

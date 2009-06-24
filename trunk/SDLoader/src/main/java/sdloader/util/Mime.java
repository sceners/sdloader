package sdloader.util;

import java.util.Properties;

public class Mime {

	private static Properties mimeMap;

	public static String getMime(String ext) {
		if (mimeMap == null) {
			init();
		}
		return mimeMap.getProperty(ext);
	}

	private synchronized static void init() {
		mimeMap = ResourceUtil.loadProperties(
				"/sdloader/resource/mime.properties", Mime.class);
	}
}

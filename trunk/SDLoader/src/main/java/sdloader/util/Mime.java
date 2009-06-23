package sdloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import sdloader.exception.IORuntimeException;

public class Mime {

	private static Properties mimeMap;

	public static String getMime(String ext) {
		if (mimeMap == null) {
			init();
		}
		return mimeMap.getProperty(ext);
	}

	private synchronized static void init() {
		InputStream is = ResourceUtil.getResourceAsStream(
				"/sdloader/resource/mime.properties", Mime.class);
		if (is == null) {
			throw new ExceptionInInitializerError("mime.properties not found.");
		}
		mimeMap = new Properties();
		try {
			mimeMap.load(is);
		} catch (IOException ioe) {
			throw new IORuntimeException(ioe);
		}
	}
}

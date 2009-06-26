package sdloader.desktopswt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Point;

import sdloader.desktopswt.util.BooleanUtil;
import sdloader.util.ResourceUtil;

public class AppConfig {

	public static final String CONFIG_TITLE = "title";
	public static final String CONFIG_WINDOW_WIDTH = "windowWidth";
	public static final String CONFIG_WINDOW_HEIGHT = "windowHeight";
	public static final String CONFIG_RESIZABLE = "resizable";

	private Properties appProperties;

	public void init() {
		appProperties = new Properties();
		InputStream app = ResourceUtil.getResourceAsStream(
				"application.properties", DesktopSWTMain.class);
		if (app == null) {
			return;
		}
		try {
			appProperties.load(app);
		} catch (IOException ioe) {
			throw new RuntimeException("application.propertiesのロードに失敗しました。");
		}
	}

	public String getTitleName() {
		return appProperties.getProperty(CONFIG_TITLE, "SDLoaderDesktopSWT");
	}

	public String getConfig(String key) {
		return appProperties.getProperty(key);
	}

	public String getConfig(String key, String defaultValue) {
		String value = appProperties.getProperty(key);
		return value == null ? defaultValue : value;
	}

	public Set<Entry<Object, Object>> getEntryList() {
		return appProperties.entrySet();
	}

	public boolean isResizable() {
		String value = getConfig(CONFIG_RESIZABLE, "true");
		return BooleanUtil.toBoolean(value);
	}

	public Point getWindowSize() {
		String w = appProperties.getProperty(CONFIG_WINDOW_WIDTH);
		String h = appProperties.getProperty(CONFIG_WINDOW_HEIGHT);
		if (w != null && w != null) {
			return new Point(Integer.valueOf(w), Integer.valueOf(h));
		} else {
			return null;
		}
	}
}

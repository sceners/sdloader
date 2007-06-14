package sdloader.util;

import java.util.Properties;

/**
 * @author shot
 */
public class PropertiesUtil {

	public static String getValueFromSystem(final String key) {
        final Properties properties = System.getProperties();
        return (String) properties.get(key);
	}
}

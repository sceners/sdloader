package sdloader.util;

/**
 * @author shot
 */
public class ClassUtil {

	public static boolean hasClass(final String className) {
		if(className == null) {
			return false;
		}
		try {
			final Class c = Class.forName(className);
			return c != null;
		} catch(Throwable t) {
			return false;
		}
	}
}

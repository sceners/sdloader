package sdloader.util;

/**
 * @author shot
 */
public class AssertUtil {

	public static <T> T assertNotNull(T target) {
		return assertNotNull(target, null);
	}
	
	public static <T> T assertNotNull(T target, String message) {
		if (target == null) {
			throw new NullPointerException(message);
		}
		return target;
	}

}

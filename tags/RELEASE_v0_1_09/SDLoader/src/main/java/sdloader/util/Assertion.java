package sdloader.util;

/**
 * @author shot
 */
public class Assertion {

	public static <T> T notNull(T target) {
		return notNull(target, null);
	}
	
	public static <T> T notNull(T target, String message) {
		if (target == null) {
			throw new NullPointerException(message);
		}
		return target;
	}

	public static <T> T[] notNull(T... args) {
		for (int i = 0; i < args.length; i++) {
			notNull(args[i]);
		}
		return args;
	}
	
}

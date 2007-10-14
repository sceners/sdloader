package lucy.util;

public class AssertionUtil {

	//TODO
	public static void assertNotNull(Object target) {
		if (target == null) {
			throw new RuntimeException();
		}
	}
}

package sdloader.util;

public class StringUtil {

	public static String emptyIfNull(String value) {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}
}

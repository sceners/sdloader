package sdloader.util;

import java.util.regex.Pattern;

public class BooleanUtil {

	public static final Pattern YES_PATTERN = Pattern.compile("(yes|true|y)",
			Pattern.CASE_INSENSITIVE);

	public static boolean toBoolean(String s) {
		if (s == null || "".equals(s)) {
			return false;
		}
		return YES_PATTERN.matcher(s).matches();
	}
}

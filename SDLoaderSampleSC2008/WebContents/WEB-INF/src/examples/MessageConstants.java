package examples;

import commons.util.StringLoader;

public class MessageConstants {

	public static String ADD_ERROR_MESSAGE;

	public static String ADD_ERROR_REQUIRED_MESSAGE;

	static {
		StringLoader.load(MessageConstants.class);
	}
}

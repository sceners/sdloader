package sdloader.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandFactory {

	private static final Map<String, Command> commands = new HashMap<String, Command>();
	static {
		commands.put(Command.STOP.toString(), Command.STOP);
		commands.put(Command.RESTART.toString(), Command.RESTART);
	}

	private static Pattern httpPattern = Pattern.compile("(GET|POST) /(.*) HTTP/.*");
	
	public static void addCommand(Command command) {
		if (command != null) {
			commands.put(command.toString(), command);
		}
	}

	public static Command getCommand(String key) {
		key = stripHttp(key);
		return commands.get(key);
	}

	private static String stripHttp(final String key){
		Matcher match = httpPattern.matcher(key);
		if(match.find()){
			return match.group(2);
		}else{
			return key;
		}
	}
}

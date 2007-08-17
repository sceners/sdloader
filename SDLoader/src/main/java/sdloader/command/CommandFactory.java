package sdloader.command;

import java.util.HashMap;
import java.util.Map;

public final class CommandFactory {

	private static final Map<String, Command> commands = new HashMap<String, Command>();
	static {
		commands.put(Command.STOP.toString(), Command.STOP);
	}

	public static void addCommand(Command command) {
		if (command != null) {
			commands.put(command.toString(), command);
		}
	}

	public static Command getCommand(final String key) {
		return commands.get(key);
	}
}

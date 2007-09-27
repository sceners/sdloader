/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sdloader.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author shot
 * @author c9katayama
 */
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

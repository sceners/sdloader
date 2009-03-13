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
package sdloader;

import java.util.Properties;

/**
 * SDLoaderをオープンし、デプロイしたアプリの一覧をブラウザに表示します。
 * 
 * @author c9katayama
 */
public class CommandLineOpen {

	public static void main(String[] args) {

		Properties p = createProperties(args);
		SDLoader sdloader = new SDLoader(p);
		sdloader.start();
		sdloader.waitForStop();
	}

	private static Properties createProperties(String[] args) {
		Properties p = new Properties();
		if (args == null) {
			return p;
		}
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("--")) {
				String command = arg.substring(2);
				String[] keyvalue = command.split("=");
				if (keyvalue.length != 2) {
					throw new IllegalArgumentException("invalide argument ["
							+ arg + "]");
				}
				String key = SDLoader.CONFIG_KEY_PREFIX + keyvalue[0];
				if (!SDLoader.CONFIG_KEYS.contains(key)) {
					throw new IllegalArgumentException("invalide argument ["
							+ arg + "]");
				}
				String value = keyvalue[1].trim();
				p.setProperty(key, value);
			}else{
				throw new IllegalArgumentException("invalide argument ["
						+ arg + "]");				
			}
		}
		return p;
	}
}

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

import java.io.File;
import java.util.List;
import java.util.Properties;

import sdloader.javaee.WebAppContext;
import sdloader.util.CollectionsUtil;

/**
 * SDLoaderをオープンし、デプロイしたアプリの一覧をブラウザに表示します。
 * 
 * @author c9katayama
 */
public class CommandLineOpen {

	public static final String OPTION_WEBAPP = "webApp";

	public static void main(String[] args) {
		new CommandLineOpen().execute(args);
	}

	public void execute(String[] args) {
		if (args.length >= 1 && args[0].equals("--help")) {
			printUsage();
			return;
		}
		try {
			SDLoader sdloader = initSDLoader(args);
			sdloader.start();
			sdloader.waitForStop();
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.out.println("オプションは --help で参照できます。");
		}
		System.exit(0);
	}

	protected SDLoader initSDLoader(String[] args) {
		SDLoader loader = new SDLoader();
		if (args == null) {
			return loader;
		}
		Properties p = new Properties();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("--")) {
				String command = arg.substring(2);
				String[] keyvalue = command.split("=");
				if (keyvalue.length != 2) {
					throw new IllegalArgumentException("invalide argument ["
							+ arg + "]");
				}
				String key = keyvalue[0];
				String value = keyvalue[1].trim();
				if (OPTION_WEBAPP.equals(key)) {
					initWebApps(loader, value);
					continue;
				}
				key = SDLoader.CONFIG_KEY_PREFIX + key;
				if (!SDLoader.CONFIG_KEYS.contains(key)) {
					throw new IllegalArgumentException("invalide argument ["
							+ arg + "]");
				}
				p.setProperty(key, value);
			} else {
				throw new IllegalArgumentException("invalide argument [" + arg
						+ "]");
			}
		}
		loader.getSDLoaderConfig().addAll(p);
		return loader;
	}

	protected void initWebApps(SDLoader loader, String warFilePaths) {
		String[] paths = warFilePaths.split(";");
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			File warFile = new File(path);
			if (warFile.exists() == false) {
				throw new IllegalArgumentException("War file not found. path="
						+ path);
			}
			String contextPath = "/" + warFile.getName();
			WebAppContext app = new WebAppContext(contextPath, warFile);
			loader.addWebAppContext(app);
		}
	}

	protected void printUsage() {
		List<Option> optionList = CollectionsUtil.newArrayList();

		optionList.add(new Option("Usage:", ""));
		optionList.add(new Option("--port", "Listenするポート番号",
				"指定しない場合30000を使用します。", "例）--port=8080"));
		optionList.add(new Option("--" + OPTION_WEBAPP,
				"デプロイするアプリケーションのwarファイル、もしくはディレクトリを指定します。",
				"「;」で区切ると、複数のアプリケーションをデプロイできます。",
				"コンテキストルートには、warファイル名がもしくはディレクトリ名が使用されます。",
				"例）--webApp=/path/to/app.war"));
		optionList.add(new Option("--home",
				"SDLoaderホームディレクトリを指定します。",
				"このディレクトリ下の「"+OPTION_WEBAPP+"」もしくは--webAppsDirオプションで指定したディレクトリにアプリケーションを配置します。",
				"指定しない場合、Java実行ディレクトリを使用します。",
				"例）--home=/path/to/sdloader"));
		optionList.add(new Option("--webAppsDir",
				"アプリケーションの入ったディレクトリを指定します。",
				"相対パスの場合、ホームディレクトリからの相対パスになります。",
				"指定しない場合、「webapps」が使用されます。",
				"--webappオプションを指定した場合、",
				"例）--home=/path/to/sdloader"));
		

		int maxCommandSize = 0;
		for (Option option : optionList) {
			maxCommandSize = Math.max(option.command.length(), maxCommandSize);
		}
		int commandSize = maxCommandSize + 2;
		for (Option option : optionList) {
			String[] detail = option.detail;
			for (int i = 0; i < detail.length; i++) {
				String command = (i == 0) ? option.command : "";
				for (int c = command.length(); c <= commandSize; c++) {
					command += " ";
				}
				System.out.println(command + detail[i]);
			}
		}
	}

	private static class Option {
		public Option(String command, String... detail) {
			this.command = command;
			this.detail = detail;
		}

		private String command;
		private String[] detail;
	}
}

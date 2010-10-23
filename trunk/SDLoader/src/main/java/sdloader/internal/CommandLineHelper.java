/*
 * Copyright 2005-2010 the original author or authors.
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
package sdloader.internal;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.log.SDLoaderLog;
import sdloader.util.CollectionsUtil;

/**
 * コマンドライン用Helper
 *
 * @author AKatayama
 *
 */
public class CommandLineHelper {

	protected String OPTION_WEBAPPS = "webApps";

	protected String[] args;

	public CommandLineHelper(String[] args) {
		this.args = args;
	}

	public boolean hasHelpOption() {
		if (args.length >= 1 && args[0].equals("--help")) {
			return true;
		} else {
			return false;
		}
	}

	protected void initWebApps(SDLoader loader, String warFilePaths) {
		String[] paths = warFilePaths.split(";");
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			File warFile = new File(path);
			if (warFile.exists() == false) {
				throw new IllegalArgumentException(
						"WebApp File not found. path=" + path);
			}
			String contextPath = "/" + warFile.getName();
			WebAppContext app = new WebAppContext(contextPath, warFile);
			loader.addWebAppContext(app);
		}
	}

	public void initSDLoader(SDLoader loader) throws IllegalArgumentException {
		if (args == null) {
			return;
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
				if (OPTION_WEBAPPS.equals(key)) {
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
	}

	public void printHelpOption(SDLoaderLog log) {
		log.info(getHelpOptionText());
	}

	public void printHelpOption() {
		System.out.println(getHelpOptionText());
	}

	public void printUsage(SDLoaderLog log) {
		log.info(getUsageText());
	}

	public void printUsage() {
		System.out.print(getUsageText());
	}

	public String getHelpOptionText() {
		return "オプションは --help で参照できます。";
	}

	public String getUsageText() {
		List<Option> optionList = CollectionsUtil.newArrayList();

		optionList.add(new Option("Usage:",
				"すべてのオプションは、--option=value の形で記述します。"));
		optionList.add(new Option("--port", "Listenするポート番号",
				"指定しない場合30000を使用します。", "例）--port=8080"));
		optionList.add(new Option("--" + OPTION_WEBAPPS,
				"デプロイするアプリケーションのwarファイル、もしくはディレクトリを指定します。",
				"「;」で区切ると、複数のアプリケーションをデプロイできます。",
				"コンテキストルートには、warファイル名がもしくはディレクトリ名が使用されます。",
				"例）--webApps=/path/to/app.war"));
		optionList
				.add(new Option(
						"--home",
						"SDLoaderホームディレクトリを指定します。",
						"このディレクトリ下の「webapps」もしくは--webAppsDirオプションで指定したディレクトリにアプリケーションを配置します。",
						"指定しない場合、Java実行ディレクトリを使用します。",
						"例）--home=/path/to/sdloader"));
		optionList.add(new Option("--webAppsDir", "アプリケーションの入ったディレクトリを指定します。",
				"相対パスの場合、ホームディレクトリからの相対パスになります。", "指定しない場合、「webapps」が使用されます。",
				"例）--webAppsDir=weblibs"));
		optionList.add(new Option("--autoPortDetect",
				"指定ポートが使用中の場合、空きポートを探すかどうか。", "指定しない場合、falseが使用されます。",
				"例）--autoPortDetect=true"));
		optionList
				.add(new Option("--useOutSidePort",
						"外部からのアクセス可能なポートを使用するかどうか。",
						"指定しない場合、localhostのみのリクエストを受け付けます。",
						"例）--useOutSidePort=true"));
		optionList.add(new Option("--sslEnable", "SSLを使用するかどうか。",
				"指定しない場合、SSLは使用しません。", "例）--sslEnable=true"));
		optionList.add(new Option("--lineSpeed", "擬似的な回線速度を設定します。",
				"指定しない場合、速度制限はありません。", "例）--lineSpeed=64000 (64Kbpsの場合)"));
		optionList.add(new Option("--workDir",
				"JSPおよびwarファイル展開用のworkディレクトリを指定します。",
				"指定しない場合、${java.io.tmp}/.sdloaderを使用します。",
				"例）--workDir=/path/to/dir"));

		int maxCommandSize = 0;
		for (Option option : optionList) {
			maxCommandSize = Math.max(option.command.length(), maxCommandSize);
		}
		StringWriter w = new StringWriter();
		PrintWriter bw = new PrintWriter(w);
		int commandSize = maxCommandSize + 2;
		for (Option option : optionList) {
			String[] detail = option.detail;
			for (int i = 0; i < detail.length; i++) {
				String command = (i == 0) ? option.command : "";
				for (int c = command.length(); c <= commandSize; c++) {
					command += " ";
				}
				bw.println(command + detail[i]);
			}
		}
		bw.close();
		return w.getBuffer().toString();
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

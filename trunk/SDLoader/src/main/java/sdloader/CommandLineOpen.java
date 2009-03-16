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

import java.util.List;
import java.util.Properties;

import sdloader.util.CollectionsUtil;

/**
 * SDLoaderをオープンし、デプロイしたアプリの一覧をブラウザに表示します。
 * 
 * @author c9katayama
 */
public class CommandLineOpen {

	public static void main(String[] args) {
		if(args.length >= 1 && args[0].equals("--help")){
			printUsage();
			return;
		}
		try{
			Properties p = createProperties(args);
			SDLoader sdloader = new SDLoader(p);
			sdloader.start();
			sdloader.waitForStop();
		}catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
			System.out.println("オプションは --help で参照できます。");
		}
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
	private static void printUsage(){
		List<Option> optionList = CollectionsUtil.newArrayList();
		optionList.add(new Option("--port","Listenするポート番号","指定しない場合30000を使用します。","例）--port=8080"));
		optionList.add(new Option("--war","Listenするポート番号","指定しない場合30000を使用します。","例）--port=8080"));

		
		int maxCommandSize = 0;
		for(Option option:optionList){
			maxCommandSize = Math.max(option.command.length(),maxCommandSize);
		}
		int commandSize = maxCommandSize + 2;
		for(Option option:optionList){
			String[] detail = option.detail;
			for(int i = 0;i < detail.length;i++){
				String command = (i==0) ? option.command : "";
				for(int c = command.length();c <= commandSize;c++){
					command += " ";
				}
				System.out.println(command + detail[i]);
			}			
		}
	}
	private static class Option{
		public Option(String command,String... detail) {
			this.command = command;
			this.detail = detail;
		}
		private String command;
		private String[] detail;
	}
}

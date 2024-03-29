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
package main;

import java.net.Inet4Address;

import sdloader.SDLoader;
import sdloader.util.Browser;

/**
 * SDLoaderをオープンし、デプロイしたアプリの一覧をブラウザに表示します。 外向けポートもListenします。
 * 
 * @author c9katayama
 */
public class SDLoaderAutoDetectAndOutSidePort {

	public static void main(String[] args) {

		try {
			SDLoader server = new SDLoader();
			server.setAutoPortDetect(true);
			server.setUseOutSidePort(true);

			server.start();

			int port = server.getPort();
			String url = "http://"
					+ Inet4Address.getLocalHost().getHostAddress() + ":" + port;

			Browser.open(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

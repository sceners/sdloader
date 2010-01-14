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
package sdloader.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

	public static String getRequestContent(String urlText) throws Exception {
		URL url = new URL(urlText);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("POST");
		urlcon.setUseCaches(false);
		urlcon.setDoOutput(true);
		PrintStream ps = new PrintStream(urlcon.getOutputStream());
		ps.close();
		urlcon.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlcon
				.getInputStream(), "UTF-8"));
		String body = "";
		String line = null;
		while ((line = reader.readLine()) != null) {
			body += line;
		}
		reader.close();
		urlcon.disconnect();
		return body;
	}
}

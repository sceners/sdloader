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
package sdloader.util;

import java.io.IOException;

import sdloader.exception.NotImplementedYetException;

public class MiscUtils {

	public static void openBrowser(String url) throws IOException {
		final String os = PropertiesUtil.getValueFromSystem("os.name");
		if (os != null && os.startsWith("Windows")) {
			if(os.indexOf("2000") >= 0){
				//2000の場合、htmlもしくはhtmで終わるとブラウザが開かない為、ダミーの#をつける
				if(url.toLowerCase().endsWith(".html") ||
						url.toLowerCase().endsWith(".htm")){
					url +="#";
				}
			}
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			throw new NotImplementedYetException();
		}
	}

}

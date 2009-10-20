/*
 * Copyright 2005-2009 the original author or authors.
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

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.MiscUtils;

public class SDLoaderAppStart extends TestCase {

	public static void main(String[] args) {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setUseNoCacheMode(true);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/testwebapp", "webapps/test");

		sdloader.addWebAppContext(webapp);

		sdloader.start();
		try {
			MiscUtils.openBrowser("http://localhost:" + sdloader.getPort()
					+ "/testwebapp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

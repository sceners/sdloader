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
package test.uriencode;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Util;

public class SDLoaderURIEncodingTest extends TestCase {

	public void testEncode() throws Exception {
		SDLoader sdloader = new SDLoader(8080);
		sdloader.setURIEncoding("UTF-8");
		WebAppContext webapp = new WebAppContext("/encode",
				"src/test/java/test/uriencode");
		sdloader.addWebAppContext(webapp);

		sdloader.start();

		String result = Util.getRequestContent("http://localhost:"
				+ sdloader.getPort()
				+ "/encode/%E5%86%86%E9%AB%98%E5%BA%A6.jsp");

		assertTrue(result.indexOf("円高度") != -1);
	}
}

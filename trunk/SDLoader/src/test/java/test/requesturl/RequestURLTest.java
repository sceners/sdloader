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
package test.requesturl;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.util.Util;

public class RequestURLTest extends TestCase {

	public void testRequestURL() throws Exception {

		SDLoader sdloader = new SDLoader(8190);
		try {
			WebAppContext webapp = new WebAppContext("/requesturl",
					"src/test/java/test/requesturl");
			sdloader.addWebAppContext(webapp);

			sdloader.start();

			Util.getRequestContent("http://localhost:" + sdloader.getPort()
					+ "/requesturl/hoge.do");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sdloader.stop();
		}

	}

}

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
package test.loadonstartup;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;

public class LoadOnStartUpTest extends TestCase {

	private SDLoader sdloader;

	@Override
	protected void setUp() throws Exception {
		sdloader = new SDLoader();
		sdloader.setAutoPortDetect(true);
		sdloader.setUseNoCacheMode(true);

		WebAppContext webapp = new WebAppContext("/testwebapp",
				"src/test/java/teset/loadonstartup");

		sdloader.addWebAppContext(webapp);

		sdloader.start();
	}

	@Override
	protected void tearDown() throws Exception {
		sdloader.stop();
	}

	public void testLoadOnStartUp() {
		/*
		 * assertEquals(5, StartUpList.startUpList.size());
		 * assertEquals("Servlet2", StartUpList.startUpList.get(0));
		 * assertEquals("Servlet5", StartUpList.startUpList.get(1));
		 * assertEquals("Servlet3", StartUpList.startUpList.get(2));
		 * assertEquals("Servlet1", StartUpList.startUpList.get(3));
		 * assertEquals("Servlet4", StartUpList.startUpList.get(4));
		 */
	}
}

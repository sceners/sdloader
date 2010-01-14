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
package sdloader.javaee;

import junit.framework.TestCase;
import sdloader.SDLoader;

public class WebAppManagerTest extends TestCase {

	public void testFindWebApp() {

		SDLoader loader = new SDLoader(8080);
		try {
			loader.addWebAppContext(new WebAppContext("/hoge", "test"));
			loader.addWebAppContext(new WebAppContext("/hogehoge", "test"));
			loader.addWebAppContext(new WebAppContext("/hogehoge/foo", "test"));
			loader.start();

			WebAppManager manager = loader.getWebAppManager();

			assertEquals("/hoge", manager.findWebApp("/hoge/test")
					.getContextPath());
			assertEquals("/", manager.findWebApp("/hoge2").getContextPath());
			assertEquals("/", manager.findWebApp("/hog").getContextPath());
			assertEquals("/hogehoge", manager.findWebApp("/hogehoge")
					.getContextPath());
			assertEquals("/hogehoge", manager.findWebApp("/hogehoge/")
					.getContextPath());
			assertEquals("/hogehoge/foo", manager.findWebApp("/hogehoge/foo")
					.getContextPath());
			assertEquals("/hogehoge/foo", manager.findWebApp(
					"/hogehoge/foo/test").getContextPath());
		} finally {
			loader.stop();
		}
	}
}

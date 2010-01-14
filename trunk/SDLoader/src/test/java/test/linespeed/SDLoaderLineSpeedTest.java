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
package test.linespeed;

import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.constants.LineSpeed;
import sdloader.javaee.WebAppContext;

public class SDLoaderLineSpeedTest extends TestCase {

	public void testLineSpeed() throws Exception {

		SDLoader sdloader = new SDLoader(8080);
		try {
			sdloader.setLineSpeed(LineSpeed.ISDN_64K_BPS);
			WebAppContext webapp = new WebAppContext("/test",
					"src/test/java/test/linespeed");
			sdloader.addWebAppContext(webapp);

			sdloader.start();
			long time = System.currentTimeMillis();

			URL url = new URL("http://localhost:8080/test/linespeed");

			InputStream is = url.openConnection().getInputStream();
			byte[] buf = new byte[1024];
			int totalSize = 0;
			int size = 0;
			while ((size = is.read(buf)) != -1) {
				totalSize += size;
			}
			assertEquals(40000, totalSize);
			long pasttime = System.currentTimeMillis() - time;
			System.out.println("SIZE=" + totalSize + " PASTTIME=" + pasttime
					+ " BPS=" + ((totalSize * 8) / (pasttime / 1000d)));
			assertTrue(pasttime >= 5000);
		} finally {
			sdloader.stop();
		}

	}
}

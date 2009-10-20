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
package test;

import java.lang.reflect.Field;
import java.net.ServerSocket;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.util.IOUtil;

public class SDLoaderTest extends TestCase {

	public void testConfigKeys() throws Exception {

		Field[] fields = SDLoader.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().startsWith("KEY")) {
				assertTrue(SDLoader.CONFIG_KEYS.contains(fields[i].get(null)));
			}
		}
	}

	public void testForceStop() {
		SDLoader sdloader = new SDLoader(8080);
		try {
			sdloader.setWebAppsDir("src/test/java/main/webapps");
			sdloader.start();

			sdloader = new SDLoader(8080);
			sdloader.setWebAppsDir("src/test/java/main/webapps");
			sdloader.start();
		} finally {
			sdloader.stop();
		}
	}

	public void testForceStopFail() throws Exception {
		ServerSocket sc = IOUtil.createServerSocket(8099, false, false);
		try {
			SDLoader sdloader = new SDLoader(8099);
			sdloader.setWebAppsDir("src/test/java/main/webapps");
			sdloader.start();
			fail();
		} catch (Exception e) {

		}
		IOUtil.closeServerSocketNoException(sc);
	}

	public void testAutoPortDetect() throws Exception {
		ServerSocket sc = IOUtil.createServerSocket(8090, false, false);
		try {
			SDLoader sdloader = new SDLoader(8090);
			try {
				sdloader.setAutoPortDetect(true);
				sdloader.setWebAppsDir("src/test/java/main/webapps");
				sdloader.start();
				assertTrue(sdloader.getPort() != 8090);
			} finally {
				sdloader.stop();
			}
		} catch (Exception e) {
			fail();
		}
		IOUtil.closeServerSocketNoException(sc);
	}

	public void testWaitForStop() {
		final SDLoader sdloader = new SDLoader(8080);
		try {
			sdloader.setWebAppsDir("src/test/java/main/webapps");
			sdloader.start();

			long time = System.currentTimeMillis();
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(1000);
						sdloader.stop();
					} catch (Exception e) {
						fail();
					}
				}
			}.start();
			sdloader.waitForStop();
			assertTrue((System.currentTimeMillis() - time) >= 1000);
		} finally {
			sdloader.stop();
		}
	}
}

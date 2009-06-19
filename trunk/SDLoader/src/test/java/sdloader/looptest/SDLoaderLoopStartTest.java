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
package sdloader.looptest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import junit.framework.Assert;
import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;

/**
 * SDLoaderの開始と停止を繰り返します。
 * 
 * @author c9katayama
 */
public class SDLoaderLoopStartTest extends TestCase {

	public void testLoop() throws InterruptedException {
		List<Thread> tlist = new ArrayList<Thread>(100);
		for (int i = 0; i < 10; i++) {
			Thread t = new Thread() {
				@Override
				public void run() {
					startstop();
				}
			};
			t.start();
			tlist.add(t);
		}
		for (Thread t : tlist) {
			t.join();
		}
		System.out.println("end");
	}

	protected void startstop() {
		final SDLoader server = new SDLoader();
		server.setAutoPortDetect(true);
		try {

			server.setAutoPortDetect(true);

			final WebAppContext ap = new WebAppContext("/loop",
					"src/test/java/sdloader/looptest");
			server.addWebAppContext(ap);
			LogManager.getLogManager().reset();
			server.start();

			List<Thread> tlist = new ArrayList<Thread>(100);
			for (int i = 0; i < 50; i++) {
				Thread t = new Thread() {
					@Override
					public void run() {
						long time = System.currentTimeMillis();
						String url = "http://localhost:" + server.getPort()
								+ ap.getContextPath() + "/loop?loop=" + time
								+ "&filter=" + time;
						try {
							String result = SDLoaderLoopStartTest
									.getRequestContent(url, "GET");
							Assert.assertEquals(time + "" + time, result);
						} catch (Exception e) {
							e.printStackTrace();
							Assert.fail();
						}
					}
				};
				tlist.add(t);
				t.start();
			}
			for (Thread t : tlist) {
				t.join();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			server.stop();
		}
	}

	public static String getRequestContent(String urlText, String method)
			throws Exception {
		URL url = new URL(urlText);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod(method);
		BufferedReader reader = null;
		String line = null;
		try {
			urlcon.connect();
			reader = new BufferedReader(new InputStreamReader(urlcon
					.getInputStream()));
			line = reader.readLine();
		} finally {
			reader.close();
			urlcon.disconnect();
		}
		return line;
	}
}

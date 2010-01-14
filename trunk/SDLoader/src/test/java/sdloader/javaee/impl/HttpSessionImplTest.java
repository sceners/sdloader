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
package sdloader.javaee.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import junit.framework.TestCase;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.WebXml;

public class HttpSessionImplTest extends TestCase {

	public void testListener() {
		{
			WebXml webxml = new WebXml();
			webxml
					.getWebApp()
					.addListener(
							new ListenerTag()
									.setListenerClass("sdloader.javaee.impl.HttpSessionImplTest$Listener"));
			WebAppContext context = new WebAppContext("/test", "test");

			context.setWebXml(webxml);

			WebAppManager manager = new WebAppManager();
			manager.addWebAppContext(context);

			InternalWebApplication webApp = new InternalWebApplication(context,
					getClass().getClassLoader(), manager);

			HttpSessionImpl session = new HttpSessionImpl(webApp, "123456");

			session.setAttribute("test", "set");
			session.setAttribute("test", "set2");
			session.removeAttribute("test");

			session.invalidate();
		}
		{
			WebXml webxml = new WebXml();
			WebAppContext context = new WebAppContext("/test", "test");

			context.setWebXml(webxml);

			WebAppManager manager = new WebAppManager();
			manager.addWebAppContext(context);

			InternalWebApplication webApp = new InternalWebApplication(context,
					getClass().getClassLoader(), manager);

			HttpSessionImpl session = new HttpSessionImpl(webApp, "123456");

			Listener2 l2 = new Listener2();
			session.setAttribute("Li", l2);
			session.removeAttribute("Li");
		}

		String[] expect = { "sessionCreated", "attributeAdded",
				"attributeReplaced", "attributeRemoved", "sessionDestroyed",
				"valueBound", "valueUnbound" };
		int index = 0;
		for (String success : successList) {
			assertEquals(expect[index++], success);
		}
		assertEquals(expect.length, successList.size());
	}

	private static List<String> successList = new ArrayList<String>();

	public static class Listener implements HttpSessionAttributeListener,
			HttpSessionListener {
		public void attributeAdded(HttpSessionBindingEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
			successList.add("attributeAdded");
		}

		public void attributeRemoved(HttpSessionBindingEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set2", e.getValue().toString());
			successList.add("attributeRemoved");
		}

		public void attributeReplaced(HttpSessionBindingEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
			successList.add("attributeReplaced");
		}

		public void sessionCreated(HttpSessionEvent e) {
			assertEquals("123456", e.getSession().getId());
			successList.add("sessionCreated");
		}

		public void sessionDestroyed(HttpSessionEvent e) {
			assertEquals("123456", e.getSession().getId());
			successList.add("sessionDestroyed");
		}
	}

	public static class Listener2 implements HttpSessionBindingListener {
		public void valueBound(HttpSessionBindingEvent e) {
			assertEquals("Li", e.getName());
			assertEquals("123456", e.getSession().getId());
			successList.add("valueBound");
		}

		public void valueUnbound(HttpSessionBindingEvent e) {
			assertEquals("Li", e.getName());
			assertEquals("123456", e.getSession().getId());
			successList.add("valueUnbound");
		}
	}
}

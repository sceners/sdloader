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
package sdloader.javaee.impl;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import junit.framework.TestCase;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.WebXml;

public class ServletContextImplTest extends TestCase {

	public void testServletContextAttributeListener() {

		WebXml webxml = new WebXml();
		webxml
				.getWebApp()
				.addListener(
						new ListenerTag()
								.setListenerClass("sdloader.javaee.impl.ServletContextImplTest$Listener"));
		WebAppContext context = new WebAppContext("/test", "test");
		context.setWebXml(webxml);

		WebAppManager manager = new WebAppManager();
		manager.addWebAppContext(context);

		InternalWebApplication webApp = new InternalWebApplication(context,
				getClass().getClassLoader(), manager);
		ServletContextImpl impl = new ServletContextImpl(webApp);

		impl.setAttribute("test", "set");
		impl.setAttribute("test", "set2");
		impl.removeAttribute("test");
	}

	public static class Listener implements ServletContextListener,
			ServletContextAttributeListener {
		public void attributeAdded(ServletContextAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
		}

		public void attributeRemoved(ServletContextAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set2", e.getValue().toString());
		}

		public void attributeReplaced(ServletContextAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
		}

		public void contextDestroyed(ServletContextEvent e) {
			assertNotNull(((ServletContextImpl) e.getServletContext())
					.getContextPath(), "/test");
		}

		public void contextInitialized(ServletContextEvent e) {
			assertNotNull(((ServletContextImpl) e.getServletContext())
					.getContextPath(), "/test");
		}
	}
}

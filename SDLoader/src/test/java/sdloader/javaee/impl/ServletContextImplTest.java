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

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.ServletTag;
import sdloader.javaee.webxml.WebXml;
import sdloader.util.Util;

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

	public void testGetResourcePaths() throws Exception {
		WebXml webxml = new WebXml();
		webxml.getWebApp().addServlet(
				new ServletTag().setServletName("test").setServletClass(
						ResourceTestServlet.class)).addServletMapping(
				new ServletMappingTag().setServletName("test").setUrlPattern(
						"*.do"));

		WebAppContext context = new WebAppContext("/test",
				"src/test/java/sdloader/javaee/impl/docbase1",
				"src/test/java/sdloader/javaee/impl/docbase2");
		context.setWebXml(webxml);

		SDLoader sdloader = new SDLoader();
		try {

			sdloader.addWebAppContext(context);

			sdloader.start();

			Util.getRequestContent("http://localhost:" + sdloader.getPort()
					+ "/test/test.do");
		} finally {
			sdloader.stop();
		}
	}

	public static class ResourceTestServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			@SuppressWarnings("unchecked")
			Set<String> paths = getServletContext().getResourcePaths("/");

			Assert.assertTrue(paths.contains("/hoge/"));
			Assert.assertTrue(paths.contains("/foo.txt"));
			Assert.assertTrue(paths.contains("/bar.txt"));
			Assert.assertTrue(paths.contains("/foo2.txt"));
			Assert.assertTrue(paths.contains("/.svn/"));
			Assert.assertEquals(5, paths.size());
		}
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

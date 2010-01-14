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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import junit.framework.TestCase;
import sdloader.http.HttpRequest;
import sdloader.http.HttpRequestReader;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.WebXml;

public class ServletRequestImplTest extends TestCase {

	public void testServletRequestAttributeListener() throws IOException {

		WebXml webxml = new WebXml();
		webxml
				.getWebApp()
				.addListener(
						new ListenerTag()
								.setListenerClass("sdloader.javaee.impl.ServletRequestImplTest$Listener"));
		WebAppContext context = new WebAppContext("/test", "test");
		context.setWebXml(webxml);

		WebAppManager manager = new WebAppManager();
		manager.addWebAppContext(context);

		InternalWebApplication webApp = new InternalWebApplication(context,
				getClass().getClassLoader(), manager);

		String body = "GET /favicon.ico HTTP/1.1\r\n\r\n";

		HttpRequestReader reader = new HttpRequestReader(
				new ByteArrayInputStream(body.getBytes()));
		HttpRequest request = new HttpRequest(reader);
		request.readRequest();
		HttpServletRequestImpl impl = new HttpServletRequestImpl(request,
				webApp, null);

		impl.intoScope();
		impl.setAttribute("test", "set");
		impl.setAttribute("test", "set2");
		impl.removeAttribute("test");
		impl.dispose();

	}

	public static class Listener implements ServletRequestListener,
			ServletRequestAttributeListener {
		public void attributeAdded(ServletRequestAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
		}

		public void attributeRemoved(ServletRequestAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set2", e.getValue().toString());
		}

		public void attributeReplaced(ServletRequestAttributeEvent e) {
			assertEquals("test", e.getName());
			assertEquals("set", e.getValue().toString());
		}

		public void requestInitialized(ServletRequestEvent e) {
			assertNotNull(((ServletContextImpl) e.getServletContext())
					.getContextPath(), "/test");
		}

		public void requestDestroyed(ServletRequestEvent e) {
			assertNotNull(((ServletContextImpl) e.getServletContext())
					.getContextPath(), "/test");
		}
	}
}

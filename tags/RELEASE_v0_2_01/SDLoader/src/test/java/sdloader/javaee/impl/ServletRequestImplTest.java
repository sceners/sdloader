package sdloader.javaee.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import junit.framework.TestCase;
import sdloader.constants.LineSpeed;
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
		
		HttpRequestReader reader = new HttpRequestReader(new ByteArrayInputStream(body.getBytes()),LineSpeed.NO_LIMIT);
		HttpRequest request = new HttpRequest(reader);
		request.readRequest();
		HttpServletRequestImpl impl = new HttpServletRequestImpl(request,webApp,null);
		
		impl.intoScope();
		impl.setAttribute("test", "set");
		impl.setAttribute("test", "set2");
		impl.removeAttribute("test");
		impl.destroy();
		
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
			assertNotNull(((ServletContextImpl)e.getServletContext()).getContextPath(), "/test");
		}
		public void requestDestroyed(ServletRequestEvent e) {
			assertNotNull(((ServletContextImpl)e.getServletContext()).getContextPath(), "/test");
		}
	}
}

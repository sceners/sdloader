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
			assertNotNull(((ServletContextImpl)e.getServletContext()).getContextPath(), "/test");
		}

		public void contextInitialized(ServletContextEvent e) {
			assertNotNull(((ServletContextImpl)e.getServletContext()).getContextPath(), "/test");
		}
	}
}

package sdloader.javaee.impl;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import junit.framework.TestCase;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.WebXml;

public class ServletRequestImplTest extends TestCase {

	public void testServletRequestAttributeListener() {

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
		
		HttpServletRequestImpl impl = new HttpServletRequestImpl(null,webApp,null);
		
		impl.setAttribute("test", "set");
		impl.setAttribute("test", "set2");
		impl.removeAttribute("test");
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

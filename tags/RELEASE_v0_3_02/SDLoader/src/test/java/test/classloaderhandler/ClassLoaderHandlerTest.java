package test.classloaderhandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import sdloader.SDLoader;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.classloader.ClassLoaderHandler;
import sdloader.util.Util;

public class ClassLoaderHandlerTest extends TestCase {

	public void testLineSpeed() throws Exception {

		SDLoader sdloader = new SDLoader(8080);
		try {
			WebAppContext webapp = new WebAppContext("/test",
					"src/test/java/test/classloaderhandler");
			webapp.addClassPath("bin");

			webapp.setClassLoaderHandler(new ClassLoaderHandler() {
				public Class<?> handleLoadClass(String className,
						boolean resolve) throws ClassNotFoundException {
					if (className.startsWith("test.classloaderhandler") == false) {
						return null;
					}
					if (className
							.equals("test.classloaderhandler.servlet.ClassA")) {
						throw new ClassNotFoundException("NOT FOUND");
					}
					// nullを返すと通常のクラスロードを続行
					return null;
				}

				public List<URL> handleResources(String resounceName)
						throws IOException {
					if (resounceName.endsWith("resourceA.txt")) {
						List<URL> urls = new ArrayList<URL>();
						urls
								.add(new File(
										"src/test/java/test/classloaderhandler/servlet/resourceB.txt")
										.toURI().toURL());
						return urls;
					}
					return null;
				}
			});

			sdloader.addWebAppContext(webapp);

			sdloader.start();

			String value = Util
					.getRequestContent("http://localhost:8080/test/classloaderhandler");

			assertEquals("B", value);
		} finally {
			sdloader.stop();
		}

	}
}

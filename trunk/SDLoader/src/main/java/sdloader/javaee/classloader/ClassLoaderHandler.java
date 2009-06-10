package sdloader.javaee.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ClassLoaderHandler {

	Class<?> handleLoadClass(String name, boolean resolve)
			throws ClassNotFoundException;

	List<URL> handleResources(String name) throws IOException;
}

package sdloader.util;

import java.io.InputStream;

public class ResourceUtil {

	public static InputStream getResourceByClassPath(String path,Class caller) {

		String resource = path;
		if (resource.startsWith("/")){
			resource = resource.substring(1);
		}
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);	
		if (is == null)
			is = caller.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.class.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.getSystemResourceAsStream(resource);

		return is;
	}
}

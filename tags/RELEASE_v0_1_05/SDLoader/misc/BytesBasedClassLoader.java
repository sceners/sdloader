package sdloader;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author shot
 */
public class BytesBasedClassLoader extends ClassLoader {

	private static final Method findLoadedClassMethod = getFindLoadedClassMethod();

	private Map<String, Resource> resources;

	public BytesBasedClassLoader(ClassLoader parent,
			Map<String, Resource> resources) {
		super(parent);
		this.resources = resources;
	}

	public synchronized Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class c = findLoadedClass(name);
		if (c != null) {
			return c;
		}
		c = findLoadedClass(getParent(), name);
		if (c != null) {
			return c;
		}
		c = defineClass(name, resolve);
		if (c != null) {
			return c;
		}
		return super.loadClass(name, resolve);
	}

	private Class defineClass(String name, boolean resolve) {
		for (Iterator itr = resources.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, Resource> entry = (Entry<String, Resource>) itr
					.next();
			final String key = entry.getKey();
			final Resource resource = entry.getValue();
			if (ClassTypeResource.class.isInstance(resource)
					&& name.equals(key)) {
				byte[] bytes;
				bytes = resource.getResourceAsBytes();
				Class c = defineClass(name, bytes, 0, bytes.length);
				if (resolve) {
					resolveClass(c);
				}
				if (c != null) {
					return c;
				}
			}
		}
		return null;
	}

	private static Class findLoadedClass(final ClassLoader classLoader,
			final String className) {
		for (ClassLoader loader = classLoader; loader != null; loader = loader
				.getParent()) {
			Class clazz = null;
			try {
				clazz = (Class) findLoadedClassMethod.invoke(loader,
						new Object[] { className });
			} catch (Exception ignore) {
			}
			if (clazz != null) {
				return clazz;
			}
		}
		return null;
	}

	private static Method getFindLoadedClassMethod() {
		Method method = null;
		try {
			method = ClassLoader.class.getDeclaredMethod("findLoadedClass",
					new Class[] { String.class });
		} catch (Exception e) {
			return null;
		}
		method.setAccessible(true);
		return method;
	}

}

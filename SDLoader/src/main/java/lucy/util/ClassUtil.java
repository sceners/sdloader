package lucy.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//TODO exception
public class ClassUtil {

	public static <T> Class<T> forName(final String className) {
		return forName(className, Thread.currentThread()
				.getContextClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(final String className,
			final ClassLoader loader) {
		try {
			return (Class<T>) Class.forName(className, true, loader);
		} catch (final ClassNotFoundException e) {
			// throw new ClassNotFoundRuntimeException(e);
			throw new RuntimeException(e);
		}
	}

	public static <T> Class<T> forNameNoException(final String className) {
		return forNameNoException(className, Thread.currentThread()
				.getContextClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forNameNoException(final String className,
			final ClassLoader loader) {
		try {
			return (Class<T>) Class.forName(className, true, loader);
		} catch (final Throwable ignore) {
			return null;
		}
	}

	public static <T> T newInstance(final Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (final InstantiationException e) {
			// throw new InstantiationRuntimeException(clazz, e);
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			// throw new IllegalAccessRuntimeException(clazz, e);
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstance(final Constructor<T> constructor,
			final Object... args) {
		try {
			return constructor.newInstance(args);
		} catch (final InstantiationException e) {
			// throw new InstantiationRuntimeException(constructor
			// .getDeclaringClass(), e);
			throw new RuntimeException(e);

		} catch (final IllegalAccessException e) {
			// throw new IllegalAccessRuntimeException(constructor
			// .getDeclaringClass(), e);
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			// throw new InvocationTargetRuntimeException(constructor
			// .getDeclaringClass(), e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean isAssignableFrom(Class from, Class to) {
		final boolean fromPrimitive = from.isPrimitive();
		if (to == Object.class && !fromPrimitive) {
			return true;
		}
		final boolean toPrimitive = to.isPrimitive();
		if (!fromPrimitive && toPrimitive || fromPrimitive && !toPrimitive) {
			return false;
		}
		return to.isAssignableFrom(from);
	}
}

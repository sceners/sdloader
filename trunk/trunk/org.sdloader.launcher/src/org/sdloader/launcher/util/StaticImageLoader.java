package org.sdloader.launcher.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.plaf.synth.Region;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

public class StaticImageLoader {

	public static void loadResources(Class c) {
		loadResources(c, c.getName());
	}

	public static void loadResources(Class c, String name) {
		loadResources(JFaceResources.getImageRegistry(), c, name);
	}

	private static void loadResources(ImageRegistry imageRegistry, Class c,
			String name) {
		ResourceBundle bundle = getBundleNoException(name, c.getClassLoader());
		if (bundle == null) {
			return;
		}
		Map<String, String> bundleMap = toMap(bundle);
		for (Field f : c.getFields()) {
			final String key = f.getName();
			if (isNotPublicStaticField(f)) {
				continue;
			}
			if (!bundleMap.containsKey(key)) {
				log(key + "not found in " + name);
				continue;
			}
			ImageDescriptor descriptor = imageRegistry.getDescriptor(key);
			if (descriptor == null) {
				descriptor = ImageDescriptor.createFromFile(c, bundleMap
						.get(key));
				imageRegistry.put(key, descriptor);
			} else {
				log(key + "is already registered  " + c);
			}

			if (ImageDescriptor.class.isAssignableFrom(f.getType())) {
				try {
					f.set(null, descriptor);
				} catch (Exception e) {
					LogUtil.log(ResourcesPlugin.getPlugin(), e);
				}
			} else if (Image.class.isAssignableFrom(f.getType())) {
				try {
					f.set(null, imageRegistry.get(key));
				} catch (Exception e) {
					LogUtil.log(ResourcesPlugin.getPlugin(), e);
				}
			}
		}
	}

	private static void log(String string) {
		LogUtil.log(ResourcesPlugin.getPlugin(), string);
	}

	private static Map<String, String> toMap(ResourceBundle bundle) {
		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = bundle.getString(key);
			map.put(key, value);
		}
		return map;
	}

	private static boolean isNotPublicStaticField(Field f) {
		final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		return (f.getModifiers() & MOD_MASK) != MOD_EXPECTED;
	}

	private static ResourceBundle getBundleNoException(String name,
			ClassLoader classLoader) {
		try {
			return ResourceBundle.getBundle(name, Locale.getDefault(),
					classLoader);
		} catch (MissingResourceException ignore) {
			return null;
		}
	}
}

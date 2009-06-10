/*
 * Copyright 2005-2007 the original author or authors.
 *
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
package sdloader.javaee.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;

/**
 * WebApp用クラスローダー
 * 
 * @author c9katayama
 */
public class WebAppClassLoader extends URLClassLoader {

	protected SDLoaderLog log = SDLoaderLogFactory
			.getLog(WebAppClassLoader.class);

	// WEB-INF以下からはロードしないPrefix
	protected String[] ignoreLoadFromWebInfPackagePrefix = { "java.",
			"com.sun.", "sun." };

	// 先に親ローダーでロードするパッケージPrefix
	protected String[] parentLoadFirstPackagePrefix = { "java.", "javax.",
			"org.apache.el.", "org.apache.jasper.", "org.apache.juli.",
			"com.sun.", "sun.", "org.w3c.", "org.xml.sax.", "org.omg.",
			"org.ietf.jgss" };

	protected ClassLoaderHandler classLoaderHandler = new ClassLoaderHandler() {
		public java.lang.Class<?> handleLoadClass(String name, boolean resolve)
				throws ClassNotFoundException {
			return null;// no op
		};

		public java.util.List<URL> handleResources(String name)
				throws IOException {
			return null;// no op
		};
	};

	public WebAppClassLoader(URL[] webInfUrls, ClassLoader parent) {
		super(webInfUrls, parent);
	}

	public WebAppClassLoader(URL[] webInfUrls, ClassLoader parent,
			ClassLoaderHandler classLoaderHandler) {
		super(webInfUrls, parent);
		setClassLoaderHandler(classLoaderHandler);
	}

	public void setClassLoaderHandler(ClassLoaderHandler classLoaderHandler) {
		if (classLoaderHandler != null) {
			this.classLoaderHandler = classLoaderHandler;
		}
	}

	/**
	 * クラスをロードします.
	 * 
	 * <pre>
	 * 1,ロード済みかどうかチェック
	 * 2,SystemClassLoaderからロード
	 * 3,親が先に読むパッケージの場合、親-&gt;子の順にロード
	 * 4,それ以外は子-&gt;親の順にロード
	 * 5,ただし特定のパッケージは子からロードしない。
	 * </pre>
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if (c != null) {
			return doResolve(c, resolve);
		}
		c = classLoaderHandler.handleLoadClass(name, resolve);
		if (c != null) {
			log.debug("Class load by class loader handler. class=[" + name
					+ "]");
			return doResolve(c, resolve);
		}
		final boolean parentFirst = isParentFirst(name);
		if (parentFirst) {
			try {
				c = findSystemClass(name);
				if (c != null) {
					log.debug("Class load by system. class=[" + name + "]");
				}
				return doResolve(c, resolve);
			} catch (ClassNotFoundException e) {
			}
			try {
				c = getParent().loadClass(name);
				if (c != null) {
					log.debug("Class load by parent. class=[" + name + "]");
				}
				return doResolve(c, resolve);
			} catch (ClassNotFoundException e) {
			}
			if (isIgnoreLoadFromWebInfPackagePrefix(name) == false) {
				c = findClass(name);
				if (c != null) {
					log.debug("Class load by app[" + this.hashCode()
							+ "]. class=[" + name + "]");
				}
				return doResolve(c, resolve);
			}
		} else {
			try {
				if (isIgnoreLoadFromWebInfPackagePrefix(name) == false) {
					c = findClass(name);
					if (c != null) {
						log.debug("Class load by app[" + this.hashCode()
								+ "]. class=[" + name + "]");
					}
				}
				return doResolve(c, resolve);
			} catch (ClassNotFoundException e) {
			}
			try {
				c = findSystemClass(name);
				if (c != null) {
					log.debug("Class load by system. class=[" + name + "]");
				}
				return doResolve(c, resolve);
			} catch (ClassNotFoundException e) {
			}
			c = getParent().loadClass(name);
			if (c != null) {
				log.debug("Class load by parent. class=[" + name + "]");
			}
			return doResolve(c, resolve);
		}
		throw new ClassNotFoundException("Class not found.class=[" + name + "]");
	}

	protected Class<?> doResolve(Class<?> c, boolean resolve) {
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	protected boolean isParentFirst(String name) {
		if (parentLoadFirstPackagePrefix != null) {
			for (int i = 0; i < parentLoadFirstPackagePrefix.length; i++) {
				if (name.startsWith(parentLoadFirstPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean isIgnoreLoadFromWebInfPackagePrefix(String name) {
		if (ignoreLoadFromWebInfPackagePrefix != null) {
			for (int i = 0; i < ignoreLoadFromWebInfPackagePrefix.length; i++) {
				if (name.startsWith(ignoreLoadFromWebInfPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		List<URL> handleResourecs = classLoaderHandler.handleResources(name);
		if (handleResourecs != null) {
			Vector<URL> handleUrlList = new Vector<URL>();
			handleUrlList.addAll(handleResourecs);
			return handleUrlList.elements();
		}
		final Enumeration<URL> resources = super.getResources(name);
		// duplicate check
		Vector<URL> margeUrlList = new Vector<URL>();
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			if (!margeUrlList.contains(url)) {
				margeUrlList.add(url);
			}
		}
		return margeUrlList.elements();
	}
}
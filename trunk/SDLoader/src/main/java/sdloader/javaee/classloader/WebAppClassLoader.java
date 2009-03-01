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

import java.net.URL;
import java.net.URLClassLoader;

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
	protected String[] ignoreLoadFromWebInfPackagePrefix = { "java.","javax.servlet.","com.sun.","sun." };
	
	// 先に親ローダーでロードするパッケージPrefix
	protected String[] parentLoadFirstPackagePrefix = { "java.","javax.","org.w3c.","com.sun.","sun." };

	public WebAppClassLoader(URL[] webInfUrls, ClassLoader parent) {
		super(webInfUrls, parent);
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
		if (c == null) {
			try {
				c = findSystemClass(name);
				if (c != null) {
					log.debug("Class load by system.name=" + name);
				}
			} catch (ClassNotFoundException e) {
				// ignone
			}
		}
		if (c == null) {
			boolean parentFirst = isParentFirst(name);
			boolean ignoreLoadFromWebInf = isIgnoreLoadFromWebInfPackagePrefix(name);

			if (parentFirst) {
				try {
					c = getParent().loadClass(name);
					if (c != null) {
						log.debug("Class load by parent.name=" + name);
					}
				} catch (ClassNotFoundException e) {
				}
				if (c == null && ignoreLoadFromWebInf == false) {
					c = findClass(name);
					if (c != null) {
						log.debug("Class load by self.name=" + name);
					}
				}
			} else {
				try {
					if (ignoreLoadFromWebInf == false) {
						c = findClass(name);
						if (c != null) {
							log.debug("Class load by self.name=" + name);
						}
					}
				} catch (ClassNotFoundException e) {
				}
				if (c == null) {
					c = getParent().loadClass(name);
					if (c != null) {
						log.debug("Class load by parent.name=" + name);
					}
				}
			}
		}
		if (c == null) {
			throw new ClassNotFoundException("Class not found.classname="
					+ name);
		}
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
}
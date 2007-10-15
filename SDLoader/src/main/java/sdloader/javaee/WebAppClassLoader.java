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
package sdloader.javaee;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * WebApp用クラスローダー WEB-INF/classesとWEB-INF/lib内のjarとzipがロード対象となります。
 * 
 * @author c9katayama
 */
public class WebAppClassLoader extends URLClassLoader {
	
	protected ClassLoader parentClassLoader;

	protected String[] selfLoadPackagePrefix = {};

	protected String[] parentLoadPackagePrefix = { "java.", "javax.servlet",
			"javax.xml", "org.w3c.dom", "org.xml.sax", "sun.", "com.sun." };
	
	public WebAppClassLoader(URL[] urls) {
		super(urls);
	}

	public WebAppClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		this.parentClassLoader = parent;
	}

	public WebAppClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
		this.parentClassLoader = parent;
	}

	public void setParentClassLoader(ClassLoader loader) {
		this.parentClassLoader = loader;
	}

	public void setSelfLoadPackagePrefix(String[] selfLoadPackagePrefix) {
		this.selfLoadPackagePrefix = selfLoadPackagePrefix;
	}

	public void setParentLoadPackagePrefix(String[] parentLoadPackagePrefix) {
		this.parentLoadPackagePrefix = parentLoadPackagePrefix;
	}

	/**
	 * クラスをロードします。 ロード済みクラスがあるかどうか 事前読み込み（親クラスローダーから） 自前読み込み（自前クラスローダーから）
	 * 事前読み込み（親クラスローダーから） の順で読み込みを行い、クラスが見つかった場合はそのクラスを返します。
	 * 自前クラスローダーは、WEB-INF/classesとWEB-INF/libがロード対象になります。
	 */
	protected synchronized Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// ロード済みクラスをチェック
		Class c = findLoadedClass(name);
		if (c != null) {
			if (resolve)
				resolveClass(c);
			return c;
		}

		boolean selfLoad = isSelfLoad(name);
		boolean parentLoad = isParentLoad(name);
		// 自前ロードでなく、かつ親ロードの場合、親で先にロード
		boolean preParentLoad = (!selfLoad && parentLoad);

		if (preParentLoad) {
			try {
				c = parentClassLoader.loadClass(name);
				if (c != null) {
					if (resolve)
						resolveClass(c);
					return c;
				}
			} catch (Throwable e) {
			}
		}

		try {
			// 前ロード
			c = findClass(name);
			if (c != null) {
				if (resolve)
					resolveClass(c);
				return c;
			}
		} catch (Throwable e) {
		}

		if (!preParentLoad) {
			// ない場合は委譲
			c = parentClassLoader.loadClass(name);
			if (c != null) {
				if (resolve)
					resolveClass(c);
				return c;
			}
		}
		throw new ClassNotFoundException("Class not found.classname=" + name);
	}

	/**
	 * 親ロードの前に自前ロードするかどうか
	 */
	private boolean isSelfLoad(String name) {
		if (selfLoadPackagePrefix != null) {
			for (int i = 0; i < selfLoadPackagePrefix.length; i++) {
				if (name.startsWith(selfLoadPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 自前ロードの前に親でロードするかどうか
	 * 
	 * @param name
	 * @return
	 */
	private boolean isParentLoad(String name) {
		if (parentLoadPackagePrefix != null) {
			for (int i = 0; i < parentLoadPackagePrefix.length; i++) {
				if (name.startsWith(parentLoadPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
}

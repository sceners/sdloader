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

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import sdloader.internal.resource.ArchiveTypeResource;
import sdloader.internal.resource.BranchTypeResource;
import sdloader.internal.resource.Resource;

/**
 * @author shot
 * @author c9katayama
 */
public class WebAppBytesBasedClassLoader extends URLClassLoader {

	private static final Method findLoadedClassMethod = getFindLoadedClassMethod();

	private Map<URL, Resource> resources;
	
	private URL[] classPathURLs;

	public WebAppBytesBasedClassLoader(ClassLoader parent,
			Map<URL, Resource> resources,
			URL[] classPathURLs) {
		super(classPathURLs,parent);
		this.resources = resources;
		this.classPathURLs = classPathURLs;
	}

	public synchronized Class<?> loadClass(String name, boolean resolve)
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
		return getParent().loadClass(name);
	}
    protected Class<?> findClass(final String name)
	 throws ClassNotFoundException{
    	//
    	return null;
    }
	private Class defineClass(String name, boolean resolve) {
		//メモリ上のリソースからロード
		String classResourceName = name.replace(".","/")+".class";
		Resource classResource = findClassPathResource(classResourceName);
		if(classResource != null){
			byte[] bytes = classResource.getResourceAsBytes();
			Class c = defineClass(name, bytes, 0, bytes.length);
			if (resolve) {
				resolveClass(c);
			}
			if (c != null) {
				return c;
			}
			return null;
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
	
	protected Resource findClassPathResource(String name){
		for(int i = 0;i < classPathURLs.length;i++){
			final URL classPathBase = classPathURLs[i];
			final Resource classPathBaseResource = resources.get(classPathBase);
			if(classPathBaseResource instanceof BranchTypeResource){
				try{
					URL resourceUrl = 
						new URL(classPathBase.toExternalForm()+name);
					final Resource classPathResource = 
						resources.get(resourceUrl);
					if(classPathResource != null)
						return classPathResource;
				}catch(Exception e){
				}
			}else if(classPathBaseResource instanceof ArchiveTypeResource){
				final Resource classPathResource = 
					((ArchiveTypeResource)classPathBaseResource).getArchiveResource(name);
				if(classPathResource != null)
					return classPathResource;
			}
		}
		return null;
	}	
	@Override
	public URL getResource(String name) {
		Resource res = findClassPathResource(name);
		//handle custom URLStreamHandler
		return (res != null) ? res.getURL() :  null;
	}
}

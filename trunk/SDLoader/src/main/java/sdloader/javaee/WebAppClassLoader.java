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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * WebApp用クラスローダー WEB-INF/classesとWEB-INF/lib内のjarとzipがロード対象となります。
 * 
 * @author c9katayama
 */
public class WebAppClassLoader extends URLClassLoader {

	protected ClassLoader webinfClassLoader;
	
	protected String[] webInfLoadFirstPackagePrefix={"org.h2","org.apache.commons.logging"};

	public WebAppClassLoader(ClassLoader parent,ClassLoader webinfClassLoader) {
		super(new URL[]{}, parent);
		this.webinfClassLoader = webinfClassLoader;
	}
	/**
	 * クラスをロードします。 
	 * まずロード済みクラスがあるかどうかをチェックします。
	 * 次にWEB-INFを先読みする対象パターンのクラスを、WEB-INF以下から読み込みます。
	 * （SDLoaderが読み込んでいるけどもWebアプリごとにロードした方が良いものが対象）
	 * 次に親クラスローダーで読み込み、なけばWEb-INFから読み込みます。
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		//check loaded Class
		Class<?> c = findLoadedClass(name);
		if (c != null) {
			if (resolve)
				resolveClass(c);
			return c;
		}
		
		boolean webInfLoadFirst = isWebInfLoadFirst(name);
		
		if(webInfLoadFirst){
			try{
				c = webinfClassLoader.loadClass(name);
				return c;
			}catch(ClassNotFoundException e){
				//ignore
			}
		}
		try{
			c = super.loadClass(name,resolve);
			if (c != null) {
				return c;
			}
		}catch(ClassNotFoundException e){
			return webinfClassLoader.loadClass(name);			
		}
		throw new ClassNotFoundException("Class not found.classname=" + name);
	}
	@Override
	public URL[] getURLs() {
		if(webinfClassLoader instanceof URLClassLoader){
			return ((URLClassLoader)webinfClassLoader).getURLs();
		}else{
			return super.getURLs();
		}
	}
	@Override
	public URL findResource(String name) {
		URL resource = super.findResource(name);
		if(resource == null){
			resource = webinfClassLoader.getResource(name);
		}
		return resource;
	}
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		final Enumeration<URL> resources = super.findResources(name);
		final Enumeration<URL> selfResources = webinfClassLoader.getResources(name);		
		return new Enumeration<URL>(){
			public boolean hasMoreElements() {
				return (resources.hasMoreElements() || selfResources.hasMoreElements());
			}
			public URL nextElement() {
				if(resources.hasMoreElements()){
					return resources.nextElement();
				}
				if(selfResources.hasMoreElements()){
					return selfResources.nextElement();
				}
				 throw new NoSuchElementException();
			}
		};
	}

	/**
	 * 親ロードの前にweb-inf内をロードするかどうか
	 */
	private boolean isWebInfLoadFirst(String name) {
		if (webInfLoadFirstPackagePrefix != null) {
			for (int i = 0; i < webInfLoadFirstPackagePrefix.length; i++) {
				if (name.startsWith(webInfLoadFirstPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}
}

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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import sdloader.util.ClassUtil;

/**
 * WebApp用クラスローダー 
 * 実際のロード処理はApplicationLoaderに委譲します。
 * @author c9katayama
 */
public class WebAppClassLoader extends URLClassLoader {

	protected ApplicationLoader applicationLoader;
	//クラスパス上にあって、アプリケーションに入る可能性のあるクラスの場合、アプリケーションを優先。
	protected String[] webInfLoadFirstPackagePrefix = {"org.h2","org.apache.commons.logging"};
	
	public WebAppClassLoader(ClassLoader parent,ClassLoader webinfClassLoader) {
		super(new URL[]{}, parent);
		this.applicationLoader = new ApplicationLoader(webinfClassLoader);
		
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
		Class<?> c = findAndResolveLoadedClass(name, resolve);
		if(c != null){
			return c;
		}
		
		boolean webInfLoadFirst = isWebInfLoadFirst(name);
		
		//if first load class,use webinfclassloader
		if(webInfLoadFirst){
			try{
				c = applicationLoader.findClass(name);
				if (c != null) {
					if (resolve)
						applicationLoader.resolveClass(c);
					return c;
				}
			}catch(Exception e){//ignore
			}
		}
		
		//use parent classloader.
		try{
			c = super.loadClass(name,resolve);
			if (c != null) {
				return c;
			}
		}catch(ClassNotFoundException e){//ignore			
		}
		
		//parent cannot load,use webinfclassloader
		if(c==null && !webInfLoadFirst){
			c = applicationLoader.findClass(name);
			if (c != null) {
				if (resolve)
					applicationLoader.resolveClass(c);
				return c;
			}
		}
		throw new ClassNotFoundException("Class not found.classname=" + name);
	}
	@Override
	public URL[] getURLs() {
		return applicationLoader.getURLs();
	}
	@Override
	public URL findResource(String name) {
		URL resource = super.findResource(name);
		if(resource == null){
			resource = applicationLoader.classLoader.getResource(name);
		}
		return resource;
	}
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		final Enumeration<URL> resources = super.findResources(name);
		final Enumeration<URL> selfResources = applicationLoader.classLoader.getResources(name);		
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
	public ClassLoader getWebInfClassLoader() {
		return applicationLoader.classLoader;
	}
	protected final Class<?> findAndResolveLoadedClass(String name,boolean resolve) {
		Class<?> c = findLoadedClass(name);
		if (c != null) {
			if (resolve)
				resolveClass(c);
			return c;
		}
		// check webinf
		c = applicationLoader.findLoadedClass(name);
		if (c != null) {
			if (resolve)
				applicationLoader.resolveClass(c);
			return c;
		}
		return null;
	}
	protected boolean isWebInfLoadFirst(String name) {
		if (webInfLoadFirstPackagePrefix != null) {
			for (int i = 0; i < webInfLoadFirstPackagePrefix.length; i++) {
				if (name.startsWith(webInfLoadFirstPackagePrefix[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
	class ApplicationLoader{
		private ClassLoader classLoader;
		protected Method findClass;
		protected Method resolveClass;
		protected Method findLoadedClass;
		protected Method getURLs;
		ApplicationLoader(ClassLoader cl){
			classLoader = cl;
			Class<?> clClass = cl.getClass();
			findClass = ClassUtil.getMethod(clClass,"findClass",new Class[]{String.class});
			resolveClass = ClassUtil.getMethod(clClass,"resolveClass",new Class[]{Class.class});
			findLoadedClass = ClassUtil.getMethod(clClass,"findLoadedClass",new Class[]{String.class});
			getURLs = ClassUtil.getMethod(clClass,"getURLs",null);
		}
		Class<?> findClass(String name) throws ClassNotFoundException{
			try{
				return (Class<?>)ClassUtil.invoke(classLoader,findClass,new Object[]{name});
			}catch(RuntimeException e){				
				throw new ClassNotFoundException(name);
			}
		}
		void resolveClass(Class<?> c) {
			ClassUtil.invoke(classLoader,resolveClass,new Object[]{c});
		}
		Class<?> findLoadedClass(String name){
			return (Class<?>)ClassUtil.invoke(classLoader,findLoadedClass,new Object[]{name});
		}
		URL[] getURLs(){
			return (URL[])ClassUtil.invoke(classLoader,getURLs,null);
		}
	}
}

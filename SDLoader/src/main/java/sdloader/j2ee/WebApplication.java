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
package sdloader.j2ee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import sdloader.j2ee.imp.FilterConfigImp;
import sdloader.j2ee.imp.ServletConfigImp;
import sdloader.j2ee.imp.ServletContextImp;
import sdloader.j2ee.webxml.ContextParamTag;
import sdloader.j2ee.webxml.FilterMappingTag;
import sdloader.j2ee.webxml.FilterTag;
import sdloader.j2ee.webxml.InitParamTag;
import sdloader.j2ee.webxml.ListenerTag;
import sdloader.j2ee.webxml.ServletMappingTag;
import sdloader.j2ee.webxml.ServletTag;
import sdloader.j2ee.webxml.WebXml;
import sdloader.util.WebUtils;

/**
 * WebAppクラス
 * Webアプリケーションに属するサーブレットコンテキスト、 サーブレット、フィルター、webxml、クラスローダーが
 * 集約されています。
 * WebAppManagerにより管理され、コンテキストパスをキーにして取り出します。
 * 
 * @author c9katayama
 */
public class WebApplication {

	/** web.xml定義 */
	private WebXml webXml;

	/** ドキュメントルート */
	private String docBase;

	/** /から始まるコンテキストパス */
	private String contextPath;

	/** WebApplicationクラスローダー */
	private WebAppClassLoader webAppClassLoader;
	
	/** WebAppManager */
	private WebAppManager manager;
	
	/** ServletContext */
	private ServletContextImp servletContext;

	/** listener */
	private List listenerList;

	/** Servlet Map */
	private Map servletMap;

	/** Filter Map */
	private Map filterMap;

	/**
	 * WebAppクラス
	 */
	WebApplication(WebXml webXml, String docBase, String contextPath,
			WebAppClassLoader webAppClassLoader,WebAppManager manager)
			 {
		this.webXml = webXml;
		this.docBase = docBase;
		this.contextPath = contextPath;
		this.webAppClassLoader = webAppClassLoader;
		this.manager = manager;
		init();
	}
	public WebAppManager getManager() {
		return manager;
	}
	
	public String getDocBase() {
		return docBase;
	}

	public String getContextPath() {
		return contextPath;
	}

	public WebAppClassLoader getWebAppClassLoader() {
		return webAppClassLoader;
	}

	public WebXml getWebXml() {
		return webXml;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	private void init(){
		initServletContext();
		initListener();
		initServlet();
		initFilter();
	}

	private void initServletContext() {
		servletContext = new ServletContextImp(this);
		servletContext.setDocBase(docBase);
		servletContext.setServletContextName(contextPath);
		for (Iterator itr = webXml.getWebApp().getContextParam().iterator(); itr
				.hasNext();) {
			ContextParamTag param = (ContextParamTag) itr.next();
			servletContext.addInitParameter(param.getParamName(), param
					.getParamValue());
		}
	}

	private void initListener(){
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			ServletContextEvent contextEvent = new ServletContextEvent(
					this.servletContext);
			for (Iterator itr = webXml.getWebApp().getListener().iterator(); itr
					.hasNext();) {
				if (listenerList == null)
					listenerList = new ArrayList();

				ListenerTag listenerTag = (ListenerTag) itr.next();
				ServletContextListener listenerImp = (ServletContextListener) createInstance(
						webAppClassLoader, listenerTag.getListenerClass());
				listenerImp.contextInitialized(contextEvent);
				listenerList.add(listenerImp);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	private void initServlet(){
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			List servletList = webXml.getWebApp().getServlet();
			for (Iterator servletItr = servletList.iterator(); servletItr
					.hasNext();) {
				if (servletMap == null)
					servletMap = new HashMap();

				ServletTag servletTag = (ServletTag) servletItr.next();
				Servlet servletInstance = (Servlet) createInstance(
						webAppClassLoader, servletTag.getServletClass());
				ServletConfig config = createServletConfig(servletTag);
				try {
					servletInstance.init(config);
				} catch (ServletException e) {
					throw new RuntimeException(e);
				}
				servletMap.put(servletTag.getServletName(), servletInstance);
			}
			servletContext.setServletMap(servletMap);
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	private void initFilter(){
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			List filterList = webXml.getWebApp().getFilter();
			for (Iterator filterItr = filterList.iterator(); filterItr
					.hasNext();) {
				if (filterMap == null)
					filterMap = new HashMap();

				FilterTag filterTag = (FilterTag) filterItr.next();

				Filter filterInstance = (Filter) createInstance(
						webAppClassLoader, filterTag.getFilterClass());
				FilterConfig config = createFilterConfig(filterTag);
				try {
					filterInstance.init(config);
				} catch (ServletException e) {
					throw new RuntimeException(e);
				}
				filterMap.put(filterTag.getFilterName(), filterInstance);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	private Object createInstance(ClassLoader webAppClassLoader,
			String className){
		try {
			Class clazz = webAppClassLoader.loadClass(className);
			Object instance = clazz.newInstance();
			return instance;
		} catch (Exception e){
			throw new RuntimeException("create instance fail.className="+className,e);
		}
	}

	private ServletConfig createServletConfig(ServletTag servletTag) {
		ServletConfigImp config = new ServletConfigImp();
		config.setServletContext(servletContext);
		List initParamList = servletTag.getInitParamList();
		for (Iterator paramItr = initParamList.iterator(); paramItr.hasNext();) {
			InitParamTag initParam = (InitParamTag)paramItr.next();
			config.addInitParameter(initParam.getParamName(),initParam.getParamValue());
		}
		config.setServletName(servletTag.getServletName());
		return config;
	}

	private FilterConfig createFilterConfig(FilterTag filterTag) {
		FilterConfigImp config = new FilterConfigImp();
		config.setServletContext(servletContext);
		List initParamList = filterTag.getInitParamList();
		for (Iterator paramItr = initParamList.iterator(); paramItr.hasNext();) {
			InitParamTag initParam = (InitParamTag)paramItr.next();
			config.addInitParameter(initParam.getParamName(),initParam.getParamValue());
		}
		config.setFilterName(filterTag.getFilterName());
		return config;
	}

	/**
	 * リクエストを処理するフィルターのリストを返します。
	 * 
	 * @param resourcePath
	 * @param servletName
	 * @return List フィルターのリスト フィルターがない場合、空のリストを返します。
	 * @throws ServletException
	 */
	public List findFilters(String resourcePath, String servletName)
			throws ServletException {
		List filterList = new ArrayList();
		if (resourcePath != null && filterMap != null) {
			List mappingList = webXml.getWebApp().getFilterMapping();

			for (Iterator mappingItr = mappingList.iterator(); mappingItr.hasNext();) {
				FilterMappingTag mapping = (FilterMappingTag) mappingItr.next();
				String patternText = mapping.getUrlPattern();
				if(patternText != null){
					if (WebUtils.matchPattern(patternText, resourcePath) != WebUtils.PATTERN_NOMATCH) {
						String filterName = mapping.getFilterName();
						Filter filter = (Filter) filterMap.get(filterName);
						filterList.add(filter);
					}
				}				
				String nameTest = mapping.getServletName();
				if(nameTest != null && nameTest.equals(servletName)) {
					String filterName = mapping.getFilterName();
					Filter filter = (Filter) filterMap.get(filterName);
					filterList.add(filter);
				}				
			}
		}
		return filterList;
	}
	
	/**
	 * リクエストを処理するサーブレットマッピング情報を返します。
	 * 
	 * @param uri
	 * @return servletMapping 見つからなかった場合、nullを返します。
	 * @throws ServletException
	 */
	public ServletMapping findServletMapping(String uri){
		if (uri != null && servletMap != null) {
			uri = WebUtils.stripQueryPart(uri);
			ServletMapping targetServletMapping = null;
			int currentMatchType = WebUtils.PATTERN_NOMATCH;
			int currentPathMatchLength = 0;
			
			List mappingList = webXml.getWebApp().getServletMapping();
			for (Iterator mappingItr = mappingList.iterator(); mappingItr.hasNext();) {
				ServletMappingTag mapping = (ServletMappingTag) mappingItr.next();
				String patternText = mapping.getUrlPattern();
				int matchType = WebUtils.matchPattern(patternText, uri);

				if(matchType != WebUtils.PATTERN_NOMATCH && matchType >= currentMatchType){
					if(matchType==WebUtils.PATTERN_EXACT_MATCH){
						ServletMapping servletMapping = new ServletMapping(mapping.getServletName(),patternText);
						return servletMapping;
					}
					if(matchType == WebUtils.PATTERN_PATH_MATCH && currentMatchType == WebUtils.PATTERN_PATH_MATCH){
						if(patternText.length() <= currentPathMatchLength){
							continue;
						}
					}
					currentMatchType = matchType;
					currentPathMatchLength = patternText.length();
					targetServletMapping = new ServletMapping(mapping.getServletName(),patternText);
				}
			}
			return targetServletMapping;
		}
		return null;
	}
	/**
	 * サーブレット名に該当するサーブレットを返します。
	 * 
	 * @param servletName
	 * @return servlet 見つからなかった場合、nullを返します。
	 */
	public Servlet findServlet(String servletName){
		return (Servlet)servletMap.get(servletName);
	}
	
	public List getServletList() {
		if (servletMap != null)
			return new ArrayList(servletMap.values());
		else
			return null;
	}

	public List getListenerList() {
		return listenerList;
	}

	public List getFilterList() {
		if (filterMap != null)
			return new ArrayList(filterMap.values());
		else
			return null;
	}

}

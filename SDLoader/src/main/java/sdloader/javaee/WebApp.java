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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import sdloader.javaee.constants.JavaEEConstants;
import sdloader.javaee.impl.FilterConfigImpl;
import sdloader.javaee.impl.ServletConfigImpl;
import sdloader.javaee.impl.ServletContextImpl;
import sdloader.javaee.webxml.ContextParamTag;
import sdloader.javaee.webxml.FilterMappingTag;
import sdloader.javaee.webxml.FilterTag;
import sdloader.javaee.webxml.InitParamTag;
import sdloader.javaee.webxml.ListenerTag;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.ServletTag;
import sdloader.javaee.webxml.WebXml;
import sdloader.util.CollectionsUtil;
import sdloader.util.WebUtils;

/**
 * WebAppクラス Webアプリケーションに属するサーブレットコンテキスト、 サーブレット、フィルター、webxml、クラスローダーが 集約されています。
 * WebAppManagerにより管理され、コンテキストパスをキーにして取り出します。
 * 
 * @author c9katayama
 */
public class WebApp {

	/** web.xml定義 */
	private WebXml webXml;

	private WebAppContext webAppContext;

	/** WebApplicationクラスローダー */
	private ClassLoader webAppClassLoader;

	/** WebAppManager */
	private WebAppManager manager;

	/** ServletContext */
	private ServletContextImpl servletContext;

	/** Servlet Map */
	private Map<String, Servlet> servletMap;

	/** Filter Map */
	private Map<String, Filter> filterMap;

	/** ListenerEventDispatcher */
	private ListenerEventDispatcher listenerEventDispatcher;

	/**
	 * WebAppクラス
	 */
	WebApp(WebXml webXml, WebAppContext webAppContext,
			ClassLoader webAppClassLoader, WebAppManager manager) {
		this.webXml = webXml;
		this.webAppContext = webAppContext;
		this.webAppClassLoader = webAppClassLoader;
		this.manager = manager;
		init();
	}

	public WebAppManager getWebApplicationManager() {
		return manager;
	}

	public WebAppContext getWebAppContext() {
		return webAppContext;
	}

	public URL[] getDocBase() {
		return getWebAppContext().getDocBase();
	}

	public String getContextPath() {
		return getWebAppContext().getContextPath();
	}

	public ClassLoader getWebAppClassLoader() {
		return webAppClassLoader;
	}

	public WebXml getWebXml() {
		return webXml;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	private void init() {
		initServletContext();
		initListener();
		initServlet();
		initFilter();
	}

	private void initServletContext() {
		servletContext = new ServletContextImpl(this);
		servletContext.setDocBase(getDocBase());
		servletContext.setServletContextName(getContextPath());
		for (Iterator<ContextParamTag> itr = webXml.getWebApp()
				.getContextParam().iterator(); itr.hasNext();) {
			ContextParamTag param = (ContextParamTag) itr.next();
			servletContext.addInitParameter(param.getParamName(), param
					.getParamValue());
		}
	}

	private void initListener() {
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			listenerEventDispatcher = new ListenerEventDispatcher();

			for (Iterator<ListenerTag> itr = webXml.getWebApp().getListener()
					.iterator(); itr.hasNext();) {
				ListenerTag listenerTag = itr.next();
				Object listenerImp = createInstance(webAppClassLoader,
						listenerTag.getListenerClass());
				listenerEventDispatcher.addListener(listenerImp);
			}
			// dispatch servletcontext event
			ServletContextEvent contextEvent = new ServletContextEvent(
					this.servletContext);
			listenerEventDispatcher
					.dispatchServletContextListener_contextInitialized(contextEvent);

		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	private void initServlet() {
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			List<ServletTag> servletList = webXml.getWebApp().getServlet();
			for (Iterator<ServletTag> servletItr = servletList.iterator(); servletItr
					.hasNext();) {
				if (servletMap == null) {
					servletMap = CollectionsUtil.newHashMap();
				}
				ServletTag servletTag = servletItr.next();
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

	private void initFilter() {
		ClassLoader oldClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webAppClassLoader);
		try {
			List<FilterTag> filterList = webXml.getWebApp().getFilter();
			for (Iterator<FilterTag> filterItr = filterList.iterator(); filterItr
					.hasNext();) {
				if (filterMap == null) {
					filterMap = CollectionsUtil.newHashMap();
				}

				FilterTag filterTag = filterItr.next();
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
			String className) {
		try {
			Class<?> clazz = webAppClassLoader.loadClass(className);
			Object instance = clazz.newInstance();
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("create instance fail.className="
					+ className, e);
		}
	}

	private ServletConfig createServletConfig(ServletTag servletTag) {
		ServletConfigImpl config = new ServletConfigImpl();
		config.setServletContext(servletContext);
		List<InitParamTag> initParamList = servletTag.getInitParamList();
		for (Iterator<InitParamTag> paramItr = initParamList.iterator(); paramItr
				.hasNext();) {
			InitParamTag initParam = paramItr.next();
			config.addInitParameter(initParam.getParamName(), initParam
					.getParamValue());
		}
		config.setServletName(servletTag.getServletName());
		return config;
	}

	private FilterConfig createFilterConfig(FilterTag filterTag) {
		FilterConfigImpl config = new FilterConfigImpl();
		config.setServletContext(servletContext);
		List<InitParamTag> initParamList = filterTag.getInitParamList();
		for (Iterator<InitParamTag> paramItr = initParamList.iterator(); paramItr
				.hasNext();) {
			InitParamTag initParam = paramItr.next();
			config.addInitParameter(initParam.getParamName(), initParam
					.getParamValue());
		}
		config.setFilterName(filterTag.getFilterName());
		return config;
	}

	/**
	 * リクエストを処理するフィルターのリストを返します。
	 * 
	 * @param resourcePath
	 * @param servletName
	 * @param dispatcherType
	 *            REQUEST,FORWARD,INCLUDE,ERROR
	 * @return List フィルターのリスト フィルターがない場合、空のリストを返します。
	 * @throws ServletException
	 */
	public List<Filter> findFilters(String resourcePath, String servletName,
			String dispatcherType) {
		List<Filter> filterList = CollectionsUtil.newArrayList();
		if (resourcePath != null && filterMap != null) {
			List<FilterMappingTag> mappingList = webXml.getWebApp()
					.getFilterMapping();
			search: for (Iterator<FilterMappingTag> mappingItr = mappingList
					.iterator(); mappingItr.hasNext();) {
				FilterMappingTag mapping = mappingItr.next();
				Set<String> dispatchers = mapping.getDispatchers();
				// dispatcherがない場合はREQUESTのみが対象 ある場合はdispatcherTypeが含まれているかチェック
				if (dispatchers.isEmpty()) {
					if (!dispatcherType
							.equals(JavaEEConstants.DISPATCHER_TYPE_REQUEST)) {
						continue search;
					}
				} else {
					if (!dispatchers.contains(dispatcherType)) {
						continue search;
					}
				}
				String patternText = mapping.getUrlPattern();
				if (patternText != null) {
					if (WebUtils.matchPattern(patternText, resourcePath) != WebUtils.PATTERN_NOMATCH) {
						String filterName = mapping.getFilterName();
						Filter filter = (Filter) filterMap.get(filterName);
						if (filter == null) {
							throw new RuntimeException(
									"Filter not found. filterName="
											+ filterName);
						}
						filterList.add(filter);
						continue search;
					}
				}
				String nameTest = mapping.getServletName();
				if (nameTest != null && nameTest.equals(servletName)) {
					String filterName = mapping.getFilterName();
					Filter filter = (Filter) filterMap.get(filterName);
					if (filter == null) {
						throw new RuntimeException(
								"Filter not found. filterName=" + filterName);
					}
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
	public ServletMapping findServletMapping(String uri) {
		if (uri != null && servletMap != null) {
			uri = WebUtils.stripQueryPart(uri);
			ServletMapping targetServletMapping = null;
			int currentMatchType = WebUtils.PATTERN_NOMATCH;
			int currentPathMatchLength = 0;

			List<ServletMappingTag> mappingList = webXml.getWebApp()
					.getServletMapping();
			// servlet
			for (Iterator<ServletMappingTag> mappingItr = mappingList
					.iterator(); mappingItr.hasNext();) {
				ServletMappingTag mapping = mappingItr.next();
				String patternText = mapping.getUrlPattern();
				int matchType = WebUtils.matchPattern(patternText, uri);
				if (matchType != WebUtils.PATTERN_NOMATCH
						&& matchType >= currentMatchType) {
					if (matchType == WebUtils.PATTERN_EXACT_MATCH) {
						ServletMapping servletMapping = new ServletMapping(
								mapping.getServletName(), patternText);
						return servletMapping;
					}
					if (matchType == WebUtils.PATTERN_PATH_MATCH
							&& currentMatchType == WebUtils.PATTERN_PATH_MATCH) {
						if (patternText.length() <= currentPathMatchLength) {
							continue;
						}
					}
					if(matchType==WebUtils.PATTERN_DEFAULT_MATCH
						&& currentMatchType == WebUtils.PATTERN_DEFAULT_MATCH){
						continue;
					}
					currentMatchType = matchType;
					currentPathMatchLength = patternText.length();
					targetServletMapping = new ServletMapping(mapping
							.getServletName(), patternText);
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
	 * @return servlet 見つからなかった場合、RuntimeExceptionをスローします。
	 */
	public Servlet findServlet(String servletName) {
		Servlet servlet = servletMap.get(servletName);
		if (servlet == null) {
			throw new RuntimeException("Servlet not found. servletName="
					+ servletName);
		}
		return servlet;
	}

	public List<Servlet> getServletList() {
		if (servletMap != null)
			return CollectionsUtil.newArrayList(servletMap.values());
		else
			return null;
	}

	public ListenerEventDispatcher getListenerEventDispatcher() {
		return listenerEventDispatcher;
	}

	public List<Filter> getFilterList() {
		if (filterMap != null)
			return CollectionsUtil.newArrayList(filterMap.values());
		else
			return null;
	}
}

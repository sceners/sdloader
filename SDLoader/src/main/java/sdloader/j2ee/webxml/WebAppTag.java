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
package sdloader.j2ee.webxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * web-appタグ
 * 
 * @author c9katayama
 */
public class WebAppTag {

	private List contextParam = new ArrayList();

	private List listener = new ArrayList();

	private List filter = new ArrayList();

	private List filterMapping = new ArrayList();

	private List servlet = new ArrayList();

	private List servletMapping = new ArrayList();

	public WebAppTag() {
		super();
	}

	public void addContextParam(ContextParamTag tag) {
		this.contextParam.add(tag);
	}

	public List getContextParam() {
		return contextParam;
	}

	public void addListener(ListenerTag tag) {
		this.listener.add(tag);
	}

	public List getListener() {
		return listener;
	}

	public void addFilter(FilterTag filter) {
		this.filter.add(filter);
	}

	public void addFilterMapping(FilterMappingTag mapping) {
		this.filterMapping.add(mapping);
	}

	public List getFilter() {
		return filter;
	}

	public List getFilterMapping() {
		return filterMapping;
	}

	public FilterTag findFilter(FilterTag filterName) {
		for (Iterator itr = filter.iterator(); itr.hasNext();) {
			FilterTag filter = (FilterTag) itr.next();
			if (filter.getFilterName().equals(filterName))
				return filter;
		}
		return null;
	}

	public FilterMappingTag findFiterMapping(String filterName) {
		for (Iterator itr = filterMapping.iterator(); itr.hasNext();) {
			FilterMappingTag filterMapping = (FilterMappingTag) itr.next();
			if (filterMapping.getFilterName().equals(filterName))
				return filterMapping;
		}
		return null;
	}

	public void addServlet(ServletTag servlet) {
		this.servlet.add(servlet);
	}

	public void addServletMapping(ServletMappingTag mapping) {
		this.servletMapping.add(mapping);
	}

	public List getServlet() {
		return servlet;
	}

	public List getServletMapping() {
		return servletMapping;
	}

	public ServletTag findServlet(String servletName) {
		for (Iterator itr = servlet.iterator(); itr.hasNext();) {
			ServletTag servet = (ServletTag) itr.next();
			if (servet.getServletName().equals(servletName))
				return servet;
		}
		return null;
	}

	public ServletMappingTag findServletMapping(String servletName) {
		for (Iterator itr = servletMapping.iterator(); itr.hasNext();) {
			ServletMappingTag servetMapping = (ServletMappingTag) itr.next();
			if (servetMapping.getServletName().equals(servletName))
				return servetMapping;
		}
		return null;
	}
}

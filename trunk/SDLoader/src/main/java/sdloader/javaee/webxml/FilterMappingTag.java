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
package sdloader.javaee.webxml;

import java.util.HashSet;
import java.util.Set;

import sdloader.exception.NotImplementedYetException;
import sdloader.javaee.JavaEEConstants;
import sdloader.util.CollectionsUtil;

/**
 * filter-mappingタグ
 * 
 * @author c9katayama
 * @author shot
 */
public class FilterMappingTag implements WebXmlTagElement {

	private static final Set<String> SUPPORT_DISPATCHERS = new HashSet<String>();
	static {
		SUPPORT_DISPATCHERS.add(JavaEEConstants.DISPATCHER_TYPE_REQUEST);
		SUPPORT_DISPATCHERS.add(JavaEEConstants.DISPATCHER_TYPE_FORWARD);
		SUPPORT_DISPATCHERS.add(JavaEEConstants.DISPATCHER_TYPE_INCLUDE);
		SUPPORT_DISPATCHERS.add(JavaEEConstants.DISPATCHER_TYPE_ERROR);
	}

	private String filterName;

	private String urlPattern;

	private String servletName;

	private Set<String> dispatchers = CollectionsUtil.newHashSet();

	public FilterMappingTag() {
		super();
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public Set<String> getDispatchers() {
		return dispatchers;
	}

	public void addDispatcher(String dispatcher) {
		if (!SUPPORT_DISPATCHERS.contains(dispatcher)) {
			throw new NotImplementedYetException("dispatcher value ["
					+ dispatcher + "] not support.");
		}
		this.dispatchers.add(dispatcher);
	}
}

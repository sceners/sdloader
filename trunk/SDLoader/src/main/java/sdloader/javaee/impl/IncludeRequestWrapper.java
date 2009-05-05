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
package sdloader.javaee.impl;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import sdloader.util.CollectionsUtil;
import sdloader.util.IteratorEnumeration;

/**
 * RequestDispatcher#include利用時のリクエストラッパー
 * 
 * @author c9katayama
 * @author shot
 */
@SuppressWarnings("unchecked")
public class IncludeRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> includeParameterMap = CollectionsUtil
			.newHashMap();

	public IncludeRequestWrapper(HttpServletRequest req) {
		super(req);
	}
	void addIncludeParameter(String key, String value) {
		if (value == null) {
			value = "";
		}
		String[] params = includeParameterMap.get(key);
		if (params == null) {
			params = new String[] { value };
		} else {
			String[] newParams = new String[params.length + 1];
			System.arraycopy(params, 0, newParams, 0, params.length);
			newParams[newParams.length - 1] = value;
			params = newParams;
		}
		includeParameterMap.put(key, params);
	}

	@Override
	public String getParameter(String name) {
		String param = null;
		String[] includeParam = includeParameterMap.get(name);
		if (includeParam != null) {
			param = includeParam[0];
		}
		if (param == null) {
			param = super.getParameter(name);
		}
		return param;
	}

	@Override
	public String[] getParameterValues(String name) {
		List<String> paramValues = CollectionsUtil.newArrayList();
		String[] includeValues = includeParameterMap.get(name);
		if (includeValues != null) {
			for (int i = 0; i < includeValues.length; i++) {
				paramValues.add(includeValues[i]);
			}
		}
		String[] values = super.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				paramValues.add(values[i]);
			}
		}
		return paramValues.size() == 0 ? null : paramValues
				.toArray(new String[] {});
	}

	@Override
	public Enumeration getParameterNames() {
		Enumeration<String> names = super.getParameterNames();
		Set<String> includeNames = new HashSet<String>(includeParameterMap
				.keySet());
		if (names != null) {
			while (names.hasMoreElements()) {
				includeNames.add(names.nextElement());
			}
		}
		return new IteratorEnumeration(includeNames.iterator());
	}

	@Override
	public Map getParameterMap() {
		Map paramMap = CollectionsUtil.newHashMap();
		Enumeration names = getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			paramMap.put(name, getParameterValues(name));
		}
		return paramMap;
	}
}

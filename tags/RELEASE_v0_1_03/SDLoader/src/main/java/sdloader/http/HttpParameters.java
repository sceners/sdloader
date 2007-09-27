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
package sdloader.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * HTTPパラメータ
 * 
 * @author c9katayama
 */
public class HttpParameters {
	public static final String DEFAULT_CHAR_ENCODE = "ISO-8859-1";

	private Map paramMap;

	private List paramNameList;

	private String characterEncoding = DEFAULT_CHAR_ENCODE;

	private HttpRequestBody body;

	public HttpParameters(HttpRequestBody body) {
		this.body = body;
	}

	private void init() {
		if (paramMap == null) {
			paramMap = new HashMap();
			paramNameList = new LinkedList();
			body.initParameters();
		}
	}

	public String getParamter(String key) {
		init();

		List paramList = (List) paramMap.get(key);
		if (paramList == null)
			return null;

		String param = (String) paramList.get(0);
		return param;
	}

	public String[] getParamterValues(String key) {
		init();

		List paramList = (List) paramMap.get(key);
		if (paramList == null)
			return null;
		String[] params = (String[]) paramList.toArray(new String[] {});
		return params;
	}

	public Iterator getParameterNames() {
		init();

		return paramNameList.iterator();
	}

	public Map getParamterMap() {
		init();

		Map newMap = new HashMap();
		newMap.putAll(paramMap);
		return newMap;
	}

	public void setCharacterEncoding(String encoding) {
		this.characterEncoding = encoding;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	void addParameter(String key, String value) {
		List paramList = (List) paramMap.get(key);
		if (paramList == null)
			paramList = new ArrayList();
		paramList.add(value);
		paramMap.put(key, paramList);
		paramNameList.add(key);
	}

}

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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sdloader.util.CollectionsUtil;

/**
 * HTTPパラメータ
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpParameters {
	public static final String DEFAULT_CHAR_ENCODE = "ISO-8859-1";

	private Map<String, String[]> paramMap;

	private List<String> paramNameList;

	private String characterEncoding = DEFAULT_CHAR_ENCODE;

	private HttpRequestBody body;

	public HttpParameters(HttpRequestBody body) {
		this.body = body;
	}

	protected void initIfNeed() {
		if (paramMap == null) {
			paramMap = CollectionsUtil.newHashMap();
			paramNameList = CollectionsUtil.newLinkedList();
			body.initParameters();
		}
	}

	public String getParamter(String key) {
		initIfNeed();
		String[] paramList = paramMap.get(key);
		if (paramList == null) {
			return null;
		}
		String param = paramList[0];
		return param;
	}

	public String[] getParamterValues(String key) {
		initIfNeed();
		String[] params = paramMap.get(key);
		if (params == null) {
			return null;
		}		
		return params;
	}

	public Iterator<String> getParameterNames() {
		initIfNeed();
		return paramNameList.iterator();
	}

	public Map<String, String[]> getParamterMap() {
		initIfNeed();		
		Map<String, String[]> newMap = CollectionsUtil.newHashMap();
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
		if(value==null){
			return;
		}
		String[] params = paramMap.get(key);
		if (params == null) {
			params = new String[]{value};
			paramMap.put(key,params);
			paramNameList.add(key);
		}else{
			String[] newParams = new String[params.length+1];
			System.arraycopy(params,0,newParams,0,params.length);
			newParams[newParams.length-1] = value;
			paramMap.put(key,newParams);
		}
	}

}

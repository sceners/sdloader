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
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import sdloader.util.CollectionsUtil;

/**
 * HTTPリクエストのヘッダー部分
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpRequestHeader {

	private String method;

	private String version;

	private String requestURI;

	private String queryString;

	private Map<String, String> headerFieldMap = CollectionsUtil.newHashMap();

	private List<String> headerFieldNameList = CollectionsUtil.newLinkedList();

	private Map<String, Cookie> cookieMap = CollectionsUtil.newHashMap();

	private List<String> cookieNameList = CollectionsUtil.newLinkedList();

	private String header;

	public HttpRequestHeader(String httpHeader) {
		if (httpHeader == null)
			throw new IllegalArgumentException("Http header is null.");
		this.header = httpHeader;
		parseHttpRequest(httpHeader);
	}

	private void parseHttpRequest(String httpRequest) {
		StringTokenizer token = new StringTokenizer(httpRequest,
				HttpConst.CRLF_STRING, false);
		if (token.hasMoreTokens()) {
			String requestLine = token.nextToken();
			parseRequestPathLine(requestLine);
		} else
			throw new IllegalArgumentException("Invalid http request.");

		while (token.hasMoreTokens()) {
			String line = token.nextToken();
			if (line.trim().length() <= 0)
				break;
			int nameEnd = line.indexOf(HttpConst.COLON_STRING);
			String name = line.substring(0, nameEnd);
			String value = null;
			int valueStart = nameEnd + HttpConst.COLON_STRING.length();
			if (line.length() > valueStart)
				value = line.substring(valueStart, line.length());

			if (name.equals(HttpConst.COOKIE))
				parseCookie(value);
			else
				addHeader(name, value);
		}
	}

	private void parseRequestPathLine(String requestLine) {
		StringTokenizer token = new StringTokenizer(requestLine, " ", false);

		if (token.hasMoreTokens())
			method = token.nextToken().trim();
		else
			throw new IllegalArgumentException(
					"Invalid http request. method not found.");
		//parse query part
		if (token.hasMoreTokens()) {
			String request = token.nextToken().trim();
			int paramDelim = request.indexOf("?");
			if (paramDelim > 0) {
				requestURI = request.substring(0, paramDelim);
				queryString = request.substring(paramDelim + 1, request
						.length());
			} else
				requestURI = request;
		} else
			throw new IllegalArgumentException(
					"Invalid http request. requestURI not found.");

		if (token.hasMoreTokens())
			version = token.nextToken().trim();
		else
			throw new IllegalArgumentException(
					"Invalid http request. version not found.");
	}

	private void parseCookie(String cookieValue) {
		if (cookieValue == null || cookieValue.length() <= 0)
			return;

		StringTokenizer token = new StringTokenizer(cookieValue,
				HttpConst.SEMI_COLON_STRING, false);
		while (token.hasMoreTokens()) {
			String keyValue = token.nextToken();

			int delimIndex = keyValue.indexOf("=");
			String key = keyValue.substring(0, delimIndex);
			String value = keyValue
					.substring(delimIndex + 1, keyValue.length());
			addCookie(key, value);
		}
	}

	public void addCookie(String key, String value) {
		if (!cookieMap.containsKey(key)) {
			this.cookieNameList.add(key);
		}
		Cookie cookie = new Cookie(key, value);
		this.cookieMap.put(key, cookie);
	}

	public List<String> getHeaderName() {
		return headerFieldNameList;
	}

	public List<String> getHeaders() {
		List<String> list = CollectionsUtil.newArrayList();
		for (Iterator<String> itr = headerFieldNameList.iterator(); itr
				.hasNext();)
			list.add(headerFieldMap.get(itr.next()));
		return list;
	}

	public void addHeader(String paramName, String paramValue) {
		if (!headerFieldMap.containsKey(paramName)) {
			headerFieldNameList.add(paramName);
		}
		headerFieldMap.put(paramName, paramValue);
	}

	public void removeHeader(String paramName) {
		headerFieldMap.remove(paramName);
		headerFieldNameList.remove(paramName);
	}

	public String getHeader(String paramName) {
		return (String) headerFieldMap.get(paramName);
	}

	public String getMethod() {
		return method;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getVersion() {
		return version;
	}

	public Cookie getCookie(String cookieName) {
		return (Cookie) cookieMap.get(cookieName);
	}

	public List<Cookie> getCookieList() {
		return CollectionsUtil.newArrayList(cookieMap.values());
	}

	public boolean isKeepAlive() {
		String keepAliveHeader = getHeader(HttpConst.KEEPALIVE);
		if (keepAliveHeader != null
				&& keepAliveHeader.equalsIgnoreCase(HttpConst.CLOSE))
			return false;

		if (version.endsWith("1.1"))
			return true;

		String connection = getHeader(HttpConst.CONNECTION);
		if (connection.equals(HttpConst.KEEPALIVE))
			return true;
		return false;
	}

	public String getHeader() {
		return header;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("method=" + method);
		buf.append(",version=" + version);
		buf.append(",requestURI=" + requestURI);
		buf.append(",queryString=" + queryString + "\n");
		buf.append("header[");
		for (Iterator<String> itr = headerFieldNameList.iterator(); itr
				.hasNext();) {
			String name = itr.next();
			String value = headerFieldMap.get(name);
			buf.append(name + "=" + value + ",");
		}
		buf.append("]\n");
		buf.append("cookie[");
		for (Iterator<String> itr = cookieNameList.iterator(); itr.hasNext();) {
			String name = itr.next();
			String value = cookieMap.get(name).getValue();
			buf.append(name + "=" + value + ",");
		}
		buf.append("]");
		return buf.toString();
	}
}

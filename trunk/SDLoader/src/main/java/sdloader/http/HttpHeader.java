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

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import sdloader.util.CollectionsUtil;
import sdloader.util.WebUtil;

/**
 * HTTPリクエストのヘッダー部分
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpHeader {

	// for request
	private String method;

	private String requestURI;

	private String queryString;

	// for response
	private int statusCode = HttpConst.SC_OK;

	private String status = HttpConst.findStatus(HttpConst.SC_OK);

	private String version = HttpConst.HTTP_1_1;

	private List<HeaderData> headerList = CollectionsUtil.newArrayList();

	private List<Cookie> cookieList = CollectionsUtil.newArrayList();

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public HttpHeader() {		
	}
	public HttpHeader(String httpHeader) {
		if (httpHeader == null) {
			throw new IllegalArgumentException("Http header is null.");
		}
		parseHttpRequest(httpHeader);
	}

	private void parseHttpRequest(String httpRequest) {
		StringTokenizer token = new StringTokenizer(httpRequest,
				HttpConst.CRLF_STRING, false);
		if (token.hasMoreTokens()) {
			String requestLine = token.nextToken();
			parseRequestPathLine(requestLine);
		} else {
			throw new IllegalArgumentException("Invalid http request.");
		}

		while (token.hasMoreTokens()) {
			String line = token.nextToken();
			if (line.trim().length() <= 0) {
				break;
			}
			int nameEnd = line.indexOf(HttpConst.COLON_STRING);
			String name = line.substring(0, nameEnd);
			String value = null;
			int valueStart = nameEnd + HttpConst.COLON_STRING.length();
			if (line.length() > valueStart) {
				value = line.substring(valueStart, line.length());
			}

			if (name.equalsIgnoreCase(HttpConst.COOKIE)) {
				parseCookie(value);
			} else {
				addHeader(name, value);
			}
		}
	}

	private void parseRequestPathLine(String requestLine) {
		StringTokenizer token = new StringTokenizer(requestLine, " ", false);

		if (token.hasMoreTokens()) {
			method = token.nextToken().trim();
		} else {
			throw new IllegalArgumentException(
					"Invalid http request. method not found.");
		}
		// parse query part
		if (token.hasMoreTokens()) {
			String request = token.nextToken().trim();
			int paramDelim = request.indexOf("?");
			if (paramDelim > 0) {
				requestURI = request.substring(0, paramDelim);
				queryString = request.substring(paramDelim + 1, request
						.length());
			} else {
				requestURI = request;
			}
		} else {
			throw new IllegalArgumentException(
					"Invalid http request. requestURI not found.");
		}

		if (token.hasMoreTokens()) {
			version = token.nextToken().trim();
		} else {
			throw new IllegalArgumentException(
					"Invalid http request. version not found.");
		}
	}

	private void parseCookie(String cookieValue) {
		if (cookieValue == null || cookieValue.length() <= 0) {
			return;
		}
		StringTokenizer token = new StringTokenizer(cookieValue,
				HttpConst.SEMI_COLON_STRING, false);
		while (token.hasMoreTokens()) {
			String keyValue = token.nextToken();

			int delimIndex = keyValue.indexOf("=");
			String key = keyValue.substring(0, delimIndex);
			String value = keyValue
					.substring(delimIndex + 1, keyValue.length());
			Cookie cookie = new Cookie(key, value);
			cookieList.add(cookie);
		}
	}
	
	public void addHeader(String name,String value){
		headerList.add(new HeaderData(name, value));
	}

	public void setHeader(String name,String value){
		boolean exist = false;
		for(HeaderData data:headerList){
			if(data.getName().equalsIgnoreCase(name)){
				data.value = value;
				exist = true;
			}
		}
		if(!exist){
			addHeader(name, value);
		}
	}

	public List<String> getHeaderNameList() {
		List<String> headerFieldNameList = CollectionsUtil.newArrayList();
		for (HeaderData data : headerList) {
			headerFieldNameList.add(data.getName());
		}
		return headerFieldNameList;
	}

	public String getHeaderValue(String headerName) {
		for (HeaderData data : headerList) {
			if (data.getName().equalsIgnoreCase(headerName)) {
				return data.getValue();
			}
		}
		return null;
	}

	public List<String> getHeaderValueList(String headerName) {
		List<String> headerValueList = CollectionsUtil.newArrayList();
		for (HeaderData data : headerList) {
			if (data.getName().equalsIgnoreCase(headerName)) {
				headerValueList.add(data.getValue());
			}
		}
		return headerValueList;
	}
	public void addCookie(Cookie newCookie) {
		cookieList.add(newCookie);
	}
	public Cookie getCookie(String name) {
		for (Cookie cookie : cookieList) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}

	public List<Cookie> getCookieList() {
		return cookieList;
	}

	public boolean isKeepAlive() {
		String keepAliveHeader = getHeaderValue(HttpConst.KEEPALIVE);
		if (keepAliveHeader != null
				&& keepAliveHeader.equalsIgnoreCase(HttpConst.CLOSE)) {
			return false;
		}
		if (version.equals(HttpConst.HTTP_1_1)) {
			return true;
		}
		String connection = getHeaderValue(HttpConst.CONNECTION);
		if (connection.equalsIgnoreCase(HttpConst.KEEPALIVE)) {
			return true;
		}
		return false;
	}

	public String buildHeader() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.version + " " + statusCode + " " + status
				+ HttpConst.CRLF_STRING);
		for (HeaderData headerData : headerList) {
			String headerName = headerData.getName();
			String headerValue = headerData.getValue();
			buf.append(headerName + HttpConst.COLON_STRING + headerValue
					+ HttpConst.CRLF_STRING);
		}
		for (Cookie cookie : cookieList) {
			if (cookie.getValue() != null) {
				buf.append(HttpConst.SETCOOKIE + HttpConst.COLON_STRING);
				buf.append(cookie.getName() + "=" + cookie.getValue());

				if (cookie.getMaxAge() > 0) {
					Calendar now = Calendar.getInstance();
					now.add(Calendar.SECOND, cookie.getMaxAge());
					buf.append("; expires="
							+ WebUtil.formatCookieDate(now.getTime()));
				}
				if (cookie.getPath() != null) {
					buf.append("; path=" + cookie.getPath());
				}
				if (cookie.getDomain() != null) {
					buf.append("; domain=" + cookie.getDomain());
				}
				if (cookie.getSecure()) {
					buf.append("; secure");
				}
				buf.append(HttpConst.CRLF_STRING);
			}
		}
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("method=" + method);
		buf.append(",version=" + version);
		buf.append(",requestURI=" + requestURI);
		buf.append(",queryString=" + queryString + "\n");
		buf.append("header[");
		boolean first = false;
		for (HeaderData headerData : headerList) {
			if (first) {
				first = false;
			} else {
				buf.append(",");
			}
			buf.append(headerData.getName() + "=" + headerData.getValue());
		}
		buf.append("]\n");
		buf.append("cookie[");
		first = false;
		for (Cookie cookie : cookieList) {
			if (first) {
				first = false;
			} else {
				buf.append(",");
			}
			buf.append(cookie.getName() + "=" + cookie.getValue());
		}
		buf.append("]");
		return buf.toString();
	}

	protected static class HeaderData {
		private String name;
		private String value;

		public HeaderData(String aName, String aValue) {
			name = aName;
			value = aValue;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
}

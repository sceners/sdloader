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

import javax.servlet.http.Cookie;

import sdloader.util.CollectionsUtil;

/**
 * HTTPレスポンスのヘッダー部分
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpResponseHeader {

	private String version = HttpConst.HTTP_1_1;

	private int statusCode = HttpConst.SC_OK;

	private String status = HttpConst.findStatus(HttpConst.SC_OK);

	private Map<String, String> headerFieldMap = CollectionsUtil.newHashMap();

	private List<String> headerFieldNameList = CollectionsUtil.newLinkedList();

	private Map<String, Cookie> cookieMap = CollectionsUtil.newHashMap();

	private List<String> cookieNameList = CollectionsUtil.newLinkedList();

	public HttpResponseHeader() {
		super();
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addCookie(Cookie cookie) {
		if (!cookieMap.containsKey(cookie.getName())) {
			cookieNameList.add(cookie.getName());
		}
		cookieMap.put(cookie.getName(), cookie);
	}

	public String getHeader(String paramName) {
		return (String) headerFieldMap.get(paramName);
	}

	public void addHeader(String name, String value) {
		if (!headerFieldMap.containsKey(name)) {
			headerFieldNameList.add(name);
		}
		headerFieldMap.put(name, value);
	}

	public List<String> getHeaderName() {
		return headerFieldNameList;
	}

	public List<String> getHeaders() {
		return CollectionsUtil.newArrayList(headerFieldMap.values());
	}

	public void removeHeader(String paramName) {
		headerFieldMap.remove(paramName);
		headerFieldNameList.remove(paramName);
	}

	public boolean containsHeader(String name) {
		if (headerFieldMap.get(name) == null)
			return false;
		else
			return true;
	}

	public String getHeaderString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.version + " " + statusCode + " " + status
				+ HttpConst.CRLF_STRING);
		for (Iterator<String> itr = headerFieldNameList.iterator(); itr
				.hasNext();) {
			String headerName = itr.next();
			String headerValue = headerFieldMap.get(headerName);
			buf.append(headerName + HttpConst.COLON_STRING + headerValue
					+ HttpConst.CRLF_STRING);
		}
		for (Iterator<String> itr = cookieNameList.iterator(); itr.hasNext();) {
			String cookieName = itr.next();
			Cookie cookie = cookieMap.get(cookieName);
			if (cookie.getValue() != null) {
				buf.append(HttpConst.SETCOOKIE + HttpConst.COLON_STRING);
				buf.append(cookie.getName() + "=" + cookie.getValue()
						+ HttpConst.SEMI_COLON_STRING);
				// TODO cookie implementation
				buf.append(HttpConst.CRLF_STRING);
			}
		}
		return buf.toString();
	}
}

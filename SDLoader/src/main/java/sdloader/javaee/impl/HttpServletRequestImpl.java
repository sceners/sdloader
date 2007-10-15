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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import sdloader.http.HttpConst;
import sdloader.http.HttpRequestBody;
import sdloader.http.HttpRequestHeader;
import sdloader.javaee.SessionManager;
import sdloader.util.CollectionsUtil;
import sdloader.util.IteratorEnumeration;
import sdloader.util.WebUtils;

/**
 * HttpServletRequest実装クラス
 * 
 * @author c9katayama
 * @author shot
 */
public class HttpServletRequestImpl implements HttpServletRequest {
	private StringBuffer requestURL;

	private String servletPath;

	private String pathInfo;

	private String protocol = "HTTP/1.1";

	private String scheme = "http";

	private int serverPort;

	private int localPort;

	private int remotePort;

	private String remoteAddr;

	private String localAddr;

	private String remoteHost;

	private String localName;

	private Locale locale = Locale.getDefault();

	private Map<String, Object> attribute = CollectionsUtil.newHashMap();

	private HttpRequestHeader header;

	private HttpRequestBody body;

	private ServletContext servletContext;

	// TODO
	private String characterEncoding = "UTF-8";

	public HttpServletRequestImpl() {
		super();
	}

	public String getHeader(String paramName) {
		return header.getHeader(paramName);
	}

	public Enumeration getHeaders(String arg0) {
		return new IteratorEnumeration(header.getHeaders().iterator());
	}

	public Enumeration getHeaderNames() {
		return new IteratorEnumeration(header.getHeaderName().iterator());
	}

	public int getIntHeader(String paramName) {
		String val = header.getHeader(paramName);
		if (val != null)
			return Integer.parseInt(val);
		return -1;
	}

	public String getMethod() {
		return header.getMethod();
	}

	public String getQueryString() {
		return header.getQueryString();
	}

	public Object getAttribute(String key) {
		return attribute.get(key);
	}

	public Enumeration getAttributeNames() {
		return new IteratorEnumeration(attribute.keySet().iterator());
	}

	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	public void setCharacterEncoding(String encoding)
			throws UnsupportedEncodingException {
		// check
		URLDecoder.decode("", encoding);
		this.characterEncoding = encoding;
	}

	public int getContentLength() {
		String length = getHeader(HttpConst.CONTENTLENGTH);
		if (length == null)
			return 0;
		else
			return Integer.parseInt(length);
	}

	public String getContentType() {
		return header.getHeader(HttpConst.CONTENTTYPE);
	}

	public ServletInputStream getInputStream() throws IOException {
		byte[] data = body.getBodyData();
		if (data == null)
			data = new byte[] {};
		final byte[] isData = data;
		ServletInputStream sIs = new ServletInputStream() {
			private InputStream is = new ByteArrayInputStream(isData);

			public int read() throws IOException {
				return is.read();
			}
		};
		return sIs;
	}

	public String getParameter(String key) {
		return body.getParameters().getParamter(key);
	}

	public Enumeration getParameterNames() {
		Iterator paramNameItr = body.getParameters().getParameterNames();
		return new IteratorEnumeration(paramNameItr);
	}

	public String[] getParameterValues(String key) {
		return body.getParameters().getParamterValues(key);
	}

	public Map getParameterMap() {
		return body.getParameters().getParamterMap();
	}

	public String getProtocol() {
		return protocol;
	}

	public BufferedReader getReader() throws IOException {
		byte[] data = body.getBodyData();
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		BufferedReader reader = new BufferedReader(new InputStreamReader(bin));
		return reader;
	}

	public void setAttribute(String key, Object value) {
		this.attribute.put(key, value);
	}

	public void removeAttribute(String key) {
		this.attribute.remove(key);
	}

	public String getContextPath() {
		return servletContext.getServletContextName();
	}

	public StringBuffer getRequestURL() {
		if (requestURL == null) {
			requestURL = WebUtils.buildRequestURL(getScheme(), getLocalName(),
					getServerPort(), getRequestURI());
		}
		return requestURL;
	}

	public Cookie[] getCookies() {
		List<Cookie> cookieList = header.getCookieList();
		Cookie[] cookies = cookieList.toArray(new Cookie[] {});
		return cookies;
	}

	/**
	 * セッションの取得 新規作成時には、HttpHeaderのセッションIDを書き換えます。
	 */
	public HttpSession getSession(boolean create) {

		String sessionId = getRequestedSessionId();
		HttpSession session = SessionManager.getInstance().getSession(
				sessionId, create, servletContext);
		if (session == null) {
			header.addCookie(HttpConst.SESSIONID_KEY, null);
			return null;
		} else {
			header.addCookie(HttpConst.SESSIONID_KEY, session.getId());
			return session;
		}
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public String getRequestedSessionId() {
		Cookie cookie = header.getCookie(HttpConst.SESSIONID_KEY);
		if (cookie != null)
			return cookie.getValue();
		else
			return null;
	}

	public String getRequestURI() {
		return header.getRequestURI();
	}

	public String getServletPath() {
		return servletPath;
	}

	public String getScheme() {
		return scheme;
	}

	public String getServerName() {
		String host = header.getHeader(HttpConst.HOST);
		if (host.indexOf(":") > 0)
			return host.substring(0, host.indexOf(":"));
		else
			return host;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public Locale getLocale() {
		return locale;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public String getLocalName() {
		return localName;
	}

	public Enumeration getLocales() {
		Vector<Locale> vec = CollectionsUtil.newVector();
		Locale[] locales = Locale.getAvailableLocales();
		if (locales != null) {
			for (int i = 0; i < locales.length; i++)
				vec.add(locales[i]);
		}
		return vec.elements();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return servletContext.getRequestDispatcher(path);
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public String getPathInfo() {
		return pathInfo;
	}

	public boolean isSecure() {
		return false;
	}

	public String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

	public String getLocalAddr() {
		return localAddr;
	}

	public String getAuthType() {
		return null;
	}

	public long getDateHeader(String name) {
		String value = getHeader(name);
		if (value == null)
			return -1;

		try {
			return WebUtils.parseHeaderDate(value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("date format fail.header name="
					+ name + " value=" + value);
		}
	}

	public String getPathTranslated() {
		return null;
	}

	public String getRemoteUser() {
		return null;
	}

	public boolean isUserInRole(String arg0) {
		return false;
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public boolean isRequestedSessionIdValid() {
		return false;
	}

	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	// /non interfacemethod
	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

	public void setHeader(HttpRequestHeader header) {
		this.header = header;
	}

	public HttpRequestHeader getHeader() {
		return this.header;
	}

	public void setBody(HttpRequestBody body) {
		this.body = body;
	}

	public HttpRequestBody getBody() {
		return this.body;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}

	public void setLocalPort(int port) {
		this.localPort = port;
	}

	public void setRemotePort(int port) {
		this.remotePort = port;
	}

	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}
}
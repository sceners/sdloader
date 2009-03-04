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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import sdloader.SDLoader;
import sdloader.constants.LineSpeed;
import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.ServletMapping;
import sdloader.javaee.constants.JavaEEConstants;
import sdloader.javaee.impl.FilterChainImpl;
import sdloader.javaee.impl.HttpServletRequestImpl;
import sdloader.javaee.impl.HttpServletResponseImpl;
import sdloader.javaee.impl.ServletContextImpl;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.IOUtil;
import sdloader.util.SocketUtil;
import sdloader.util.WebUtils;

/**
 * ソケット接続に対して、処理を行います. リクエスト解析＞サーブレット呼び出し＞レスポンスの順に 処理を行います。
 * 
 * @author c9katayama
 */
public class HttpProcessor extends Thread {
	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(HttpProcessor.class);

	private int socketTimeout = 60 * 1000;

	private int keepAliveTimeout = 3 * 1000;// Apache 15

	private int keppAliveMaxRequests = 100;// Apache 5

	private Socket socket;

	private SDLoader sdLoader;

	private int lineSpeed;

	private boolean stop;

	public HttpProcessor(String name) {
		super(name);
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	public void setKeppAliveMaxRequests(int keppAliveMaxRequests) {
		this.keppAliveMaxRequests = keppAliveMaxRequests;
	}

	public void process(Socket socket, SDLoader loader) {
		synchronized (this) {
			this.socket = socket;
			this.sdLoader = loader;
			lineSpeed = sdLoader.getSDLoaderConfig().getConfigInteger(
					SDLoader.KEY_SDLOADER_LINE_SPEED, LineSpeed.NO_LIMIT);
			notify();
		}
	}

	void stopProcessor() {
		synchronized (this) {
			stop = true;
			notifyAll();
		}
	}

	public void run() {
		while (!stop) {
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				log.warn("SocketProcessor interrupetd", e);
				return;
			}
			if (stop) {
				return;
			}
			processSocket();
		}
	}

	protected void processSocket() {
		InputStream is = null;
		OutputStream os = null;
		try {
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(socketTimeout);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			int requestCount = 1;
			boolean keepAlive = true;
			while (keepAlive) {
				RequestScopeContext.init();
				RequestScopeContext.getContext().setAttribute(SDLoader.class,
						sdLoader);
				keepAlive = processServlet(is, os, requestCount);
				RequestScopeContext.destroy();
				requestCount++;
			}
		} catch (SocketTimeoutException e) {
			log.debug("socket timeout.");
		} catch (SocketException e) {
			log.debug("socket close.");
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		} finally {
			IOUtil.flushNoException(os);
			IOUtil.closeNoException(is);
			IOUtil.closeNoException(os);
			SocketUtil.closeSocketNoException(socket);
			RequestScopeContext.destroy();
		}
		is = null;
		os = null;
		socket = null;
		SDLoader localLoader = this.sdLoader;
		sdLoader = null;
		localLoader.returnProcessor(this);
	}

	protected boolean processServlet(InputStream is, OutputStream os,
			int requestCount) throws Throwable {
		HttpRequest httpRequest;
		try {
			if (requestCount != 1) {
				socket.setSoTimeout(keepAliveTimeout);
			}
			httpRequest = new HttpRequest(new HttpRequestReader(is, lineSpeed));
			if (log.isDebugEnabled()) {
				log.debug("<REQUEST_HEADER>\n" + httpRequest.getHeader());
			}
		} finally {
			if (socket.isClosed()) {
				return false;
			} else {
				socket.setSoTimeout(socketTimeout);
			}
		}

		HttpHeader header = httpRequest.getHeader();
		String requestURI = header.getRequestURI();
		InternalWebApplication webapp = sdLoader.getWebAppManager().findWebApp(
				requestURI);
		HttpServletRequestImpl request = createServletRequestImp(httpRequest);
		HttpServletResponseImpl response = new HttpServletResponseImpl();
		// デフォルトもなければ404
		if (webapp == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(null, request, response, requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}
		request.setInternalWebApplication(webapp);
		ServletContextImpl servletContextImpl = webapp.getServletContext();

		String contextPath = webapp.getContextPath();
		String resourcePath = WebUtils.getResourcePath(contextPath, requestURI);
		// contextpathだけのパターン (/testのようなパターン）の場合、contextpathに/をつけてリダイレクト
		if (!requestURI.equals("/") && resourcePath == null) {
			response.setStatus(HttpConst.SC_MOVED_TEMPORARILY);
			resourcePath = requestURI + "/";
			String host = request.getHeader(HttpConst.HOST);
			if (host == null) {
				host = request.getLocalName() + ":" + request.getLocalPort();
			}

			response.addHeader(HttpConst.LOCATION, WebUtils.buildRequestURL(
					request.getScheme(), host, resourcePath).toString());
			setDefaultResponseHeader(servletContextImpl, request, response,
					requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}

		ServletMapping mapping = webapp.findServletMapping(resourcePath);
		Servlet servlet = null;
		if (mapping == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(servletContextImpl, request, response,
					requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		} else {
			servlet = webapp.findServlet(mapping.getServletName());
		}
		request.setServletPath(WebUtils.getServletPath(mapping.getUrlPattern(),
				resourcePath));
		request.setPathInfo(WebUtils.getPathInfo(mapping.getUrlPattern(),
				resourcePath));

		// class loader
		ClassLoader webClassLoader = webapp.getWebAppClassLoader();
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webClassLoader);

		RequestScopeContext requestScopeContext = RequestScopeContext
				.getContext();
		requestScopeContext.setRequest(request);
		requestScopeContext.setResponse(response);

		// service
		try {
			String servletName = mapping.getServletName();
			List<Filter> filterList = webapp.findFilters(resourcePath,
					servletName, JavaEEConstants.DISPATCHER_TYPE_REQUEST);
			if (filterList.size() > 0) {
				Filter[] filters = (Filter[]) filterList
						.toArray(new Filter[] {});
				FilterChainImpl filterChain = new FilterChainImpl(filters,
						servlet);
				filterChain.doFilter(request, response);
			} else {
				servlet.service(request, response);
			}
		} catch (ServletException se) {
			log.error(se.getMessage(), se);
			response.setStatus(HttpConst.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			response.setStatus(HttpConst.SC_INTERNAL_SERVER_ERROR);
		} finally {
			RequestScopeContext.destroy();
			Thread.currentThread().setContextClassLoader(oldLoader);
		}
		setDefaultResponseHeader(servletContextImpl, request, response,
				requestCount);
		processDataOutput(response, os);
		return header.isKeepAlive();
	}

	private HttpServletRequestImpl createServletRequestImp(
			HttpRequest httpRequest) {
		HttpServletRequestImpl request = new HttpServletRequestImpl(
				httpRequest, sdLoader.getSessionManager());

		request.setServerPort(sdLoader.getPort());
		request.setLocalPort(socket.getLocalPort());
		request.setLocalAddr(socket.getLocalAddress().getHostAddress());
		request.setLocalName(socket.getLocalAddress().getHostName());

		request.setRemotePort(socket.getPort());
		request.setRemoteAddr(socket.getInetAddress().getHostAddress());
		request.setRemoteHost(socket.getInetAddress().getHostName());

		request.setScheme("http");

		String uriEncoding = sdLoader.getSDLoaderConfig()
				.getConfigStringIgnoreExist(
						HttpRequest.KEY_REQUEST_URI_ENCODING);
		if (uriEncoding != null) {
			request.setUriEncoding(uriEncoding);
		}
		return request;
	}

	protected void setDefaultResponseHeader(
			ServletContextImpl servletContextImpl,
			HttpServletRequestImpl request, HttpServletResponseImpl response,
			int requestCount) throws IOException {
		response.setHeader(HttpConst.DATE, WebUtils.formatHeaderDate(Calendar
				.getInstance().getTime()));
		response.setHeader(HttpConst.SERVER, sdLoader.getServerName());

		// session
		HttpSession session = request.getSession(false);
		if (session != null && servletContextImpl != null) {
			Cookie sessionCookie = new Cookie(HttpConst.SESSIONID_KEY, session
					.getId());
			sessionCookie.setPath(servletContextImpl.getContextPath());
			response.addCookie(sessionCookie);
		}
		// Keep-Alive
		if (request.getHeader().isKeepAlive()
				&& requestCount < keppAliveMaxRequests) {
			response.addHeader(HttpConst.KEEPALIVE, "timeout="
					+ (int) keepAliveTimeout / 1000 + ", max="
					+ keppAliveMaxRequests);
			response.addHeader(HttpConst.CONNECTION, HttpConst.KEEPALIVE);
		} else {
			response.addHeader(HttpConst.CONNECTION, HttpConst.CLOSE);
		}
		// Cache Control
		if (sdLoader.getSDLoaderConfig().getConfigBoolean(
				HttpResponse.KEY_RESPONSE_USE_NOCACHE_MODE, false)) {
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 1L);
			response.setHeader("Cache-Control", "no-cache");
			response.addHeader("Cache-Control", "no-store");
		}

		// Content-Length
		// Chunked以外はセット
		HttpHeader resHeader = response.getResponseHeader();
		String transferEncoding = resHeader
				.getHeaderValue(HttpConst.TRANSFERENCODING);
		if (transferEncoding == null
				|| !transferEncoding.equalsIgnoreCase(HttpConst.CHUNKED)) {
			response.setHeader(HttpConst.CONTENTLENGTH, String.valueOf(response
					.getBodyData().length));
		}
	}

	private void processDataOutput(HttpServletResponseImpl response,
			OutputStream os) throws IOException {
		HttpHeader resHeader = response.getResponseHeader();

		byte[] headerData = resHeader.buildHeader().getBytes();
		IOUtil.write(lineSpeed, headerData, os);
		if (log.isDebugEnabled()) {
			log.debug("<RESPONSE_HEADER>\n" + new String(headerData));
		}
		os.write(HttpConst.CRLF_STRING.getBytes());// Separator
		byte[] bodyData = response.getBodyData();
		if (bodyData != null) {
			IOUtil.write(lineSpeed, bodyData, os);
		}
		os.flush();
	}
}

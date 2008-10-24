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

import sdloader.SDLoader;
import sdloader.javaee.ServletMapping;
import sdloader.javaee.WebApplication;
import sdloader.javaee.constants.JavaEEConstants;
import sdloader.javaee.impl.FilterChainImpl;
import sdloader.javaee.impl.HttpServletRequestImpl;
import sdloader.javaee.impl.HttpServletResponseImpl;
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
public class HttpRequestProcessor extends Thread {
	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(HttpRequestProcessor.class);

	private int socketTimeout = 60 * 1000;

	private int keepAliveTimeout = 3 * 1000;// Apache 15

	private int keppAliveMaxRequests = 5;// Apache 5

	private Socket socket;

	private SDLoader sdLoader;

	private boolean stop;

	public HttpRequestProcessor(String name) {
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
			notify();
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
					RequestScopeContext.getContext().setAttribute(
							SDLoader.class, sdLoader);
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
				IOUtil.closeNoException(is);
				IOUtil.flushNoException(os);
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
			localLoader = null;
		}
	}

	protected boolean processServlet(InputStream is, OutputStream os,
			int requestCount) throws Throwable {
		HttpRequest httpRequest;
		try {
			if (requestCount != 1) {
				socket.setSoTimeout(keepAliveTimeout);
			}
			HttpInput input = new HttpInput(is);
			httpRequest = new HttpRequest(input);

			if (httpRequest.getHeader() == null)
				return false;// empty request;

			if (log.isDebugEnabled()) {
				log.debug("<REQUEST_HEADER>\n" + httpRequest.getHeader());
			}
		} catch (SocketException e) {
			throw new SocketTimeoutException(e.getMessage());
		} finally {
			if (socket.isClosed()) {
				return false;
			} else {
				socket.setSoTimeout(socketTimeout);
			}
		}
		HttpRequestHeader header = httpRequest.getHeader();
		// request
		HttpServletRequestImpl request = createServletRequestImp(httpRequest);
		// response
		HttpServletResponseImpl response = new HttpServletResponseImpl();

		String requestURI = header.getRequestURI();
		WebApplication webapp = sdLoader.getWebAppManager().findWebApp(requestURI);
		// デフォルトもなければ404
		if (webapp == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(request, response, requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}		

		String contextPath = webapp.getContextPath();
		String resourcePath = WebUtils.getResourcePath(contextPath,requestURI);
		// contextpathだけのパターン (/testのようなパターン）の場合、contextpathに/をつけてリダイレクト
		if (!requestURI.equals("/") && resourcePath == null) {
			response.setStatus(HttpConst.SC_MOVED_TEMPORARILY);
			resourcePath = requestURI + "/";
			String host = request.getHeader(HttpConst.HOST);
			if (host == null){
				host = request.getLocalName() + ":"
						+ request.getLocalPort();
			}
			String scheme = request.getScheme();
			response
					.addHeader(HttpConst.LOCATION, WebUtils
							.buildRequestURL(scheme, host, resourcePath)
							.toString());
			processDataOutput(response, os);
			return header.isKeepAlive();
		}

		ServletMapping mapping = webapp.findServletMapping(resourcePath);
		Servlet servlet = null;
		if (mapping == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(request, response, requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		} else {
			servlet = webapp.findServlet(mapping.getServletName());
		}
		request.setServletPath(WebUtils.getServletPath(mapping.getUrlPattern(),
				resourcePath));
		request.setPathInfo(WebUtils.getPathInfo(mapping.getUrlPattern(),
				resourcePath));
		request.setServletContext(webapp.getServletContext());

		// class loader
		ClassLoader webClassLoader = webapp.getWebAppClassLoader();
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(webClassLoader);

		RequestScopeContext requestScopeContext = RequestScopeContext.getContext();
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
			} else{
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
		setDefaultResponseHeader(request, response, requestCount);
		processDataOutput(response, os);
		return header.isKeepAlive();
	}

	private HttpServletRequestImpl createServletRequestImp(
			HttpRequest httpRequest) {
		HttpServletRequestImpl request = new HttpServletRequestImpl(httpRequest);

		request.setServerPort(sdLoader.getPort());
		request.setLocalPort(socket.getLocalPort());
		request.setLocalAddr(socket.getLocalAddress().getHostAddress());
		request.setLocalName(socket.getLocalAddress().getHostName());

		request.setRemotePort(socket.getPort());
		request.setRemoteAddr(socket.getInetAddress().getHostAddress());
		request.setRemoteHost(socket.getInetAddress().getHostName());

		request.setScheme("http");
		
		request.setUriEncoding(sdLoader.getConfig(HttpRequest.KEY_REQUEST_URI_ENCODING));
		
		return request;
	}

	private void processDataOutput(HttpServletResponseImpl response,
			OutputStream os) throws IOException {
		HttpResponseHeader resHeader = response.getResponseHeader();
		byte[] headerData = resHeader.getHeaderString().getBytes();
		byte[] bodyData = response.getBodyData();
		if (headerData != null) {
			os.write(headerData);
			if (log.isDebugEnabled()) {
				log.debug("<RESPONSE_HEADER>\n" + new String(headerData));
			}
		}
		os.write(HttpConst.CRLF_STRING.getBytes());// Separator
		if (bodyData != null) {
			os.write(bodyData);
		}
		os.flush();
	}

	private void setDefaultResponseHeader(HttpServletRequestImpl request,
			HttpServletResponseImpl response, int requestCount)
			throws IOException {
		response.setHeader(HttpConst.DATE, WebUtils.formatHeaderDate(Calendar
				.getInstance().getTime()));
		response.setHeader(HttpConst.SERVER, sdLoader.getServerName());
		String sessionId = request.getRequestedSessionId();
		if (sessionId != null) {
			Cookie sessionCookie = new Cookie(HttpConst.SESSIONID_KEY,
					sessionId);
			response.addCookie(sessionCookie);
		}

		if (request.getHeader().isKeepAlive()
				&& requestCount < keppAliveMaxRequests) {
			response.addHeader(HttpConst.KEEPALIVE, "timeout="
					+ (int) keepAliveTimeout / 1000 + ", max="
					+ keppAliveMaxRequests);
			response.addHeader(HttpConst.CONNECTION, HttpConst.KEEPALIVE);
		} else {
			response.addHeader(HttpConst.CONNECTION, HttpConst.CLOSE);
		}

		// Content-Length
		// Chunked以外はセット
		HttpResponseHeader resHeader = response.getResponseHeader();
		String transferEncoding = resHeader
				.getHeader(HttpConst.TRANSFERENCODING);
		if (transferEncoding == null
				|| !transferEncoding.equalsIgnoreCase(HttpConst.CHUNKED)) {
			response.setHeader(HttpConst.CONTENTLENGTH, String.valueOf(response
					.getBodyData().length));
		}
	}

	void stopProcessor() {
		synchronized (this) {
			stop = true;
			notifyAll();
		}
	}
}

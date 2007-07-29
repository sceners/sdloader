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
import sdloader.j2ee.ServletMapping;
import sdloader.j2ee.WebAppClassLoader;
import sdloader.j2ee.WebApplication;
import sdloader.j2ee.impl.FilterChainImpl;
import sdloader.j2ee.impl.HttpServletRequestImpl;
import sdloader.j2ee.impl.HttpServletResponseImpl;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.WebUtils;

/**
 * ソケット接続に対して、処理を行います。
 * リクエスト解析＞サーブレット呼び出し＞レスポンスの順に 処理を行います。
 * 
 * @author c9katayama
 */
public class HttpRequestProcessor extends Thread {
	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(HttpRequestProcessor.class);

	private int socketTimeout = 60 * 1000;

	private int keepAliveTimeout = 3 * 1000;//Apache 15
	
	private int keppAliveMaxRequests = 5;//Apache 5
	
	private Socket socket;

	private SDLoader loader;

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
			this.loader = loader;
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
			if (stop)
				return;

			InputStream is = null;
			OutputStream os = null;			
			try {
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(socketTimeout);
				is = socket.getInputStream();
				os = socket.getOutputStream();
				int requestCount = 1;
				boolean keepAlive = true;
				while(keepAlive){
					keepAlive = processServlet(is, os, requestCount);
					requestCount++;
				}				
			} catch (SocketTimeoutException e) {
				log.debug("socket timeout.");
			} catch (SocketException e) {
				log.debug("socket close.");
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			} finally {
				try{is.close();}catch(IOException ioe){}
				try{os.flush();os.close();}catch(IOException ioe){}
				try{socket.close();}catch(IOException ioe){}
			}
			is = null;
			os = null;
			socket = null;
			SDLoader localLoader = this.loader;
			loader=null;
			localLoader.returnProcessor(this);
			localLoader = null;			
		}
	}

	protected boolean processServlet(InputStream is, OutputStream os,int requestCount) throws Throwable {
		HttpRequestHeader header = null;
		HttpRequestBody body = null;
		try {
			if (requestCount!=1)
				socket.setSoTimeout(keepAliveTimeout);

			HttpInput input = new HttpInput(is);
			HttpRequest request = new HttpRequest(input);
			
			header = request.getHeader();
			if (header == null)
				return false;// empty request;

			if (log.isDebugEnabled())
				log.debug("<REQUEST_HEADER>\n" + header.getHeader());
			
			body = request.getBody();
		} catch (SocketException e) {
			throw new SocketTimeoutException();
		} finally {
			if (socket.isClosed())
				return false;
			else
				socket.setSoTimeout(socketTimeout);
		}

		// request
		HttpServletRequestImpl request =createServletRequestImp(header, body);		
		// response
		HttpServletResponseImpl response = new HttpServletResponseImpl();
		
		String requestURI = header.getRequestURI();
		String resourcePath = WebUtils.getResourcePath(requestURI);
		if(!requestURI.equals("/") && resourcePath == null){//contextpathだけのパターン /をつけてリダイレクト
			response.setStatus(HttpConst.SC_MOVED_TEMPORARILY);
			resourcePath = requestURI+"/";
			response.addHeader(HttpConst.LOCATION,request.getScheme()+"://"+request.getLocalName()+":"+request.getLocalPort()+resourcePath);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}
		
		String contextPath = WebUtils.getContextPath(requestURI);
		WebApplication webapp = loader.getWebAppManager().findWebApp(contextPath);
		if (webapp == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(request, response,requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}
		ServletMapping mapping = webapp.findServletMapping(resourcePath);
		Servlet servlet = null;
		if(mapping != null)
			servlet = webapp.findServlet(mapping.getServletName());
		if (mapping == null || servlet == null) {
			response.setStatus(HttpConst.SC_NOT_FOUND);
			setDefaultResponseHeader(request, response,requestCount);
			processDataOutput(response, os);
			return header.isKeepAlive();
		}
		request.setServletPath(WebUtils.getServletPath(mapping.getUrlPattern(),resourcePath));
		request.setPathInfo(WebUtils.getPathInfo(mapping.getUrlPattern(),resourcePath));
		request.setServletContext(webapp.getServletContext());

		// class loader
		WebAppClassLoader webClassLoader = webapp.getWebAppClassLoader();
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		webClassLoader.setParentClassLoader(oldLoader);
		Thread.currentThread().setContextClassLoader(webClassLoader);

		// service
		try {
			String servletName = mapping.getServletName();
			List filterList = webapp.findFilters(resourcePath, servletName);
			if (filterList.size() > 0) {
				Filter[] filters = (Filter[]) filterList
						.toArray(new Filter[] {});
				FilterChainImpl filterChain = new FilterChainImpl(filters,
						servlet);
				filterChain.doFilter(request, response);
			} else
				servlet.service(request, response);
		} catch (ServletException se) {
			log.error(se.getMessage(), se);
			response.setStatus(HttpConst.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			response.setStatus(HttpConst.SC_INTERNAL_SERVER_ERROR);
		} finally {
			Thread.currentThread().setContextClassLoader(oldLoader);
		}
		setDefaultResponseHeader(request, response,requestCount);
		processDataOutput(response, os);
		return header.isKeepAlive();
	}
	private HttpServletRequestImpl createServletRequestImp(HttpRequestHeader header,HttpRequestBody body){
		HttpServletRequestImpl request = new HttpServletRequestImpl();
		request.setHeader(header);
		request.setBody(body);
		
		request.setServerPort(loader.getPort());		
		request.setLocalPort(socket.getLocalPort());
		request.setLocalAddr(socket.getLocalAddress().getHostAddress());
		request.setLocalName(socket.getLocalAddress().getHostName());

		request.setRemotePort(socket.getPort());
		request.setRemoteAddr(socket.getInetAddress().getHostAddress());
		request.setRemoteHost(socket.getInetAddress().getHostName());
		
		request.setScheme("http");
		return request;
	}
	private void processDataOutput(HttpServletResponseImpl response,
			OutputStream os) throws IOException {
		HttpResponseHeader resHeader = response.getResponseHeader();
		byte[] headerData = resHeader.getHeaderString().getBytes();
		byte[] bodyData = response.getBodyData();
		if(headerData != null){
			os.write(headerData);
			if(log.isDebugEnabled()){
				log.debug("<RESPONSE_HEADER>\n" + new String(headerData));
			}
		}
		os.write(HttpConst.CRLF_STRING.getBytes());// Separator
		if(bodyData != null){			
			os.write(bodyData);
		}
		os.flush();
	}
	private void setDefaultResponseHeader(HttpServletRequestImpl request,HttpServletResponseImpl response,int requestCount) throws IOException {
		response.setHeader(HttpConst.DATE, WebUtils.formatHeaderDate(Calendar.getInstance().getTime()));
		response.setHeader(HttpConst.SERVER, loader.getServerName());
		String sessionId = request.getRequestedSessionId();
		if (sessionId != null) {
			Cookie sessionCookie = new Cookie(HttpConst.SESSIONID_KEY,sessionId);
			response.addCookie(sessionCookie);
		}

		if (request.getHeader().isKeepAlive() && requestCount<keppAliveMaxRequests) {
			response.addHeader(HttpConst.KEEPALIVE, "timeout="
					+ (int) keepAliveTimeout / 1000 + ", max="+keppAliveMaxRequests);
			response.addHeader(HttpConst.CONNECTION,HttpConst.KEEPALIVE);
		} else{
			response.addHeader(HttpConst.CONNECTION,HttpConst.CLOSE);
		}

		// Content-Length
		HttpResponseHeader resHeader = response.getResponseHeader();
		if (resHeader.getHeader(HttpConst.TRANSFERENCODING) == null
				&& resHeader.getHeader(HttpConst.CONTENTLENGTH) == null){
			response.setHeader(HttpConst.CONTENTLENGTH, String
					.valueOf(response.getBodyData().length));
		}
	}
	
	void stopProcessor() {
		synchronized (this) {
			stop = true;
			notifyAll();
		}
	}
}

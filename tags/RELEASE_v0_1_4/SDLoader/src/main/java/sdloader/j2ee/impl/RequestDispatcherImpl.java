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
package sdloader.j2ee.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import sdloader.j2ee.ServletMapping;
import sdloader.util.WebUtils;

/**
 * RequestDispatcherの実装クラス
 * 
 * @author c9katayama
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    public static final String JAVAX_INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String JAVAX_INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    public static final String JAVAX_INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String JAVAX_INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String JAVAX_INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";

    public static final String JAVAX_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    public static final String JAVAX_FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
    public static final String JAVAX_FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
    public static final String JAVAX_FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
    public static final String JAVAX_FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";

    public static final String JAVAX_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    public static final String JAVAX_ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String JAVAX_ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String JAVAX_ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String JAVAX_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String JAVAX_ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    
    private ServletContext dispatchServletContext;
	private Servlet dispatchServlet;
	private ServletMapping dispatchServletMapping;
	/** dispatch先のURI(コンテキストパス、クエリー込みのURI）*/
	private String requestURI;
	private String queryString;

	RequestDispatcherImpl(ServletMapping dispatchServletMapping,Servlet dispatchServlet, ServletContext dispatchServletContext,String dispatchURI) {
		this.dispatchServletContext = dispatchServletContext;
		this.dispatchServletMapping = dispatchServletMapping;
		this.dispatchServlet = dispatchServlet;
		this.requestURI = WebUtils.stripQueryPart(dispatchURI);
		this.queryString = WebUtils.getQueryPart(dispatchURI);
	}
	
	private HttpServletRequestImpl stripRequestWrapper(ServletRequest request){
		while(request instanceof HttpServletRequestWrapper){
			request = ((HttpServletRequestWrapper)request).getRequest();
		}
		return (HttpServletRequestImpl)request;
	}
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {

		HttpServletRequestImpl firstRequest = stripRequestWrapper(request);
		if(request.getAttribute(JAVAX_FORWARD_SERVLET_PATH) == null){
			//初回はパスを保存
			firstRequest.setAttribute(JAVAX_FORWARD_REQUEST_URI,firstRequest.getRequestURI());
			firstRequest.setAttribute(JAVAX_FORWARD_CONTEXT_PATH,firstRequest.getContextPath());
			firstRequest.setAttribute(JAVAX_FORWARD_SERVLET_PATH,firstRequest.getServletPath());
			firstRequest.setAttribute(JAVAX_FORWARD_PATH_INFO,firstRequest.getPathInfo());
			firstRequest.setAttribute(JAVAX_FORWARD_QUERY_STRING,firstRequest.getQueryString());
		}
		String contextPath = WebUtils.getContextPath(requestURI);
		String resourcePath = WebUtils.getResourcePath(requestURI);
		String servletPath = WebUtils.getServletPath(dispatchServletMapping.getUrlPattern(),resourcePath);
		String pathInfo = WebUtils.getPathInfo(dispatchServletMapping.getUrlPattern(),resourcePath);
		
		ForwardRequestWrapper requestWrapper = new ForwardRequestWrapper((HttpServletRequest)request);
		requestWrapper.setServletContext(dispatchServletContext);
		requestWrapper.setRequestURI(requestURI);
		requestWrapper.setServletPath(servletPath);
		requestWrapper.setPathInfo(pathInfo);
		requestWrapper.setContextPath(contextPath);
		
		response.resetBuffer();
		
		dispatchServlet.service(requestWrapper, response);
	}

	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		IncludeRequestWrapper requestWrapper = new IncludeRequestWrapper((HttpServletRequest)request);
		
		String contextPath = WebUtils.getContextPath(requestURI);
		String resourcePath = WebUtils.getResourcePath(requestURI);
		String servletPath = WebUtils.getServletPath(dispatchServletMapping.getUrlPattern(),resourcePath);
		String pathInfo = WebUtils.getPathInfo(dispatchServletMapping.getUrlPattern(),resourcePath);

		requestWrapper.setAttribute(JAVAX_INCLUDE_REQUEST_URI,requestURI);
		requestWrapper.setAttribute(JAVAX_INCLUDE_CONTEXT_PATH,contextPath);
		requestWrapper.setAttribute(JAVAX_INCLUDE_SERVLET_PATH,servletPath);
		requestWrapper.setAttribute(JAVAX_INCLUDE_PATH_INFO,pathInfo);
		requestWrapper.setAttribute(JAVAX_INCLUDE_QUERY_STRING,queryString);
		//TODO include時のquery
		//TODO response(closeとか出来なくする）
		
		dispatchServlet.service(requestWrapper,response);
	}

}

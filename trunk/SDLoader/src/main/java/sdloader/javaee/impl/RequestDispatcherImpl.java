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

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import sdloader.javaee.ServletMapping;
import sdloader.javaee.constants.JavaEEConstants;
import sdloader.util.WebUtil;

/**
 * RequestDispatcherの実装クラス
 * 
 * @author c9katayama
 */
public class RequestDispatcherImpl implements RequestDispatcher {

	private ServletContext dispatchServletContext;
	private Servlet dispatchServlet;
	private List<Filter> forwardFilterList;
	private List<Filter> includeFilterList;
	private ServletMapping dispatchServletMapping;
	private String contextPath;
	/** dispatch先のURI(コンテキストパス、クエリー込みのURI） */
	private String requestURI;
	private String queryString;

	RequestDispatcherImpl(ServletMapping dispatchServletMapping,
			Servlet dispatchServlet, List<Filter> forwardFilterList,
			List<Filter> includeFilterList,
			ServletContext dispatchServletContext, 
			String contextPath,
			String dispatchURI) {
		this.dispatchServletContext = dispatchServletContext;
		this.dispatchServletMapping = dispatchServletMapping;
		this.dispatchServlet = dispatchServlet;
		this.forwardFilterList = forwardFilterList;
		this.includeFilterList = includeFilterList;
		this.contextPath = contextPath;
		this.requestURI = WebUtil.stripQueryPart(dispatchURI);
		this.queryString = WebUtil.getQueryPart(dispatchURI);
	}

	private HttpServletRequestImpl stripRequestWrapper(ServletRequest request) {
		while (request instanceof HttpServletRequestWrapper) {
			request = ((HttpServletRequestWrapper) request).getRequest();
		}
		return (HttpServletRequestImpl) request;
	}

	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {

		HttpServletRequestImpl firstRequest = stripRequestWrapper(request);
		if (request.getAttribute(JavaEEConstants.JAVAX_FORWARD_SERVLET_PATH) == null) {
			// 初回はパスを保存
			firstRequest.setAttribute(
					JavaEEConstants.JAVAX_FORWARD_REQUEST_URI, firstRequest
							.getRequestURI());
			firstRequest.setAttribute(
					JavaEEConstants.JAVAX_FORWARD_CONTEXT_PATH, firstRequest
							.getContextPath());
			firstRequest.setAttribute(
					JavaEEConstants.JAVAX_FORWARD_SERVLET_PATH, firstRequest
							.getServletPath());
			firstRequest.setAttribute(JavaEEConstants.JAVAX_FORWARD_PATH_INFO,
					firstRequest.getPathInfo());
			firstRequest.setAttribute(
					JavaEEConstants.JAVAX_FORWARD_QUERY_STRING, firstRequest
							.getQueryString());
		}
		String resourcePath = WebUtil.getResourcePath(contextPath,requestURI);
		String servletPath = WebUtil.getServletPath(dispatchServletMapping
				.getUrlPattern(), resourcePath);
		String pathInfo = WebUtil.getPathInfo(dispatchServletMapping
				.getUrlPattern(), resourcePath);

		ForwardRequestWrapper requestWrapper = new ForwardRequestWrapper(
				(HttpServletRequest) request);
		requestWrapper.setServletContext(dispatchServletContext);
		requestWrapper.setRequestURI(requestURI);
		requestWrapper.setServletPath(servletPath);
		requestWrapper.setPathInfo(pathInfo);
		requestWrapper.setContextPath(contextPath);

		response.resetBuffer();

		doService(dispatchServlet, forwardFilterList, requestWrapper, response);
	}

	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		
		IncludeRequestWrapper requestWrapper = new IncludeRequestWrapper(
				(HttpServletRequest) request);
		IncludeResponseWrapper responseWrapper = new IncludeResponseWrapper(
				(HttpServletResponse) response);

		String resourcePath = WebUtil.getResourcePath(contextPath,requestURI);
		String servletPath = WebUtil.getServletPath(dispatchServletMapping
				.getUrlPattern(), resourcePath);
		String pathInfo = WebUtil.getPathInfo(dispatchServletMapping
				.getUrlPattern(), resourcePath);

		requestWrapper.setIncludeAttribute(
				JavaEEConstants.JAVAX_INCLUDE_REQUEST_URI, requestURI);
		requestWrapper.setIncludeAttribute(
				JavaEEConstants.JAVAX_INCLUDE_CONTEXT_PATH, contextPath);
		requestWrapper.setIncludeAttribute(
				JavaEEConstants.JAVAX_INCLUDE_SERVLET_PATH, servletPath);
		requestWrapper.setIncludeAttribute(
				JavaEEConstants.JAVAX_INCLUDE_PATH_INFO, pathInfo);
		requestWrapper.setIncludeAttribute(
				JavaEEConstants.JAVAX_INCLUDE_QUERY_STRING, queryString);
		// TODO include時のquery

		doService(dispatchServlet, includeFilterList, requestWrapper,
				responseWrapper);
	}

	private void doService(Servlet servlet, List<Filter> filterList,
			ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		if (filterList.size() > 0) {
			Filter[] filters = (Filter[]) filterList.toArray(new Filter[] {});
			FilterChainImpl filterChain = new FilterChainImpl(filters, servlet);
			filterChain.doFilter(request, response);
		} else {
			servlet.service(request, response);
		}
	}
}

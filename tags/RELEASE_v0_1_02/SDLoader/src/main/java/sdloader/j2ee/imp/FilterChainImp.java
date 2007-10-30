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
package sdloader.j2ee.imp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * FilterChain実装クラス
 * 
 * @author c9katayama
 */
public class FilterChainImp implements FilterChain {

	private Filter[] nextFilters;

	private Servlet servlet;

	private int currentFilterNo = 0;

	public FilterChainImp(Filter[] nextFilter, Servlet servlet) {
		this.nextFilters = nextFilter;
		this.servlet = servlet;
	}

	public void doFilter(ServletRequest req, ServletResponse res)
			throws IOException, ServletException {
		if (currentFilterNo < nextFilters.length)
			nextFilters[currentFilterNo++].doFilter(req, res, this);
		else
			servlet.service(req, res);
	}
}
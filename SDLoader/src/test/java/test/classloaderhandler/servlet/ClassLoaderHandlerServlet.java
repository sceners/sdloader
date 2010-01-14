/*
 * Copyright 2005-2010 the original author or authors.
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
package test.classloaderhandler.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClassLoaderHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Class.forName("test.classloaderhandler.servlet.ClassA");
			throw new RuntimeException("NG");
		} catch (ClassNotFoundException e) {

		}
		try {
			Class.forName("test.classloaderhandler.servlet.ClassB");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		InputStream is = getClass().getResourceAsStream("resourceA.txt");
		resp.getWriter().write(
				new BufferedReader(new InputStreamReader(is)).readLine());
	}
}

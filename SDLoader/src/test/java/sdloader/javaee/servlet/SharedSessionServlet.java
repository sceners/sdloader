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
package sdloader.javaee.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * セッション共有テスト
 * 
 * @author c9katayama
 */
public class SharedSessionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		HttpSession session = arg0.getSession();

		Integer n = (Integer) session.getAttribute("shared");
		if (n == null) {
			n = new Integer(1);
		} else {
			n = new Integer(n.intValue() + 1);
		}
		session.setAttribute("shared", n);
		PrintWriter w = arg1.getWriter();
		w.write("<html><body>" + n + "</body></html>");
		w.flush();
		w.close();
	}
}

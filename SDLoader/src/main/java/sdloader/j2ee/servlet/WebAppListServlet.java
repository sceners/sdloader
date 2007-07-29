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
package sdloader.j2ee.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sdloader.j2ee.WebApplication;
import sdloader.j2ee.impl.ServletContextImpl;


/**
 * デプロイしてあるWebアプリの一覧を表示するサーブレット
 * 
 * @author c9katayama
 * @author shot
 */
public class WebAppListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doIt(req, res);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		doIt(req, res);
	}
	
	protected void doIt(HttpServletRequest req,HttpServletResponse res)
		throws ServletException, IOException {
		
		WebApplication webapp = ((ServletContextImpl)getServletContext()).getWebApplication();
		List contextPathList = webapp.getWebApplicationManager().getContextPathList();
		outputWebAppList(contextPathList, req, res);
	}
	protected void outputWebAppList(List contextPathList,HttpServletRequest req,HttpServletResponse res)
																	throws ServletException, IOException {
		PrintWriter writer = res.getWriter();
		writer.write("<html><head><title>SDLoader -Web Application List-</title></head><body>");
		writer.write("[Web Application List]<br/>");
		writer.write("<a href=\"/\">ROOT</a><br/>");
		if(contextPathList != null){
			for(Iterator itr = contextPathList.iterator();itr.hasNext();){
				String path = (String)itr.next();
				writer.write("<a href=\"" + path + "\">" + path + "</a><br/>");				
			}
		}
		writer.write("</body></html>");
	}
}

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
package sdloader.javaee.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sdloader.util.ResourceUtil;
import sdloader.util.WebUtils;

/**
 * メモリ上のリソース出力サーブレット リクエストパスからファイルを検索し、返します。
 * 
 * @author c9katayama
 */
public class InMemoryFileSavingServlet extends FileSavingServlet {
	
	public InMemoryFileSavingServlet() {
		super();
	}
	protected void doIt(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String uri = req.getPathInfo();

		if (uri == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (uri.startsWith("/WEB-INF/") || uri.endsWith("/WEB-INF")) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if(!uri.endsWith("/")){
			URL file = ResourceUtil.createURL(docRootPath,uri);
			
			if (ResourceUtil.isResourceExist(file)) {
				outputResource(file, req, res);
				return;
			}
			uri += "/";
		}		
		URL dir = ResourceUtil.createURL(docRootPath,uri);
		if (ResourceUtil.isResourceExist(dir)) {
			if (welcomeFileListTag != null) {
				processWelcomeFile(dir, req, res);
				return;
			}
		}
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	protected void outputResource(URL resource,HttpServletRequest req,HttpServletResponse res) throws IOException{
		//TODO check last modified
		InputStream is = resource.openStream();
		ServletOutputStream sout = res.getOutputStream();
		try {
			int size = WebUtils.copyStream(is, sout);
			setContentType(res, resource.toExternalForm());
			res.setContentLength(size);
			res.setStatus(HttpServletResponse.SC_OK);
		} finally {
			is.close();
			sout.flush();
			sout.close();
		}
	} 
}

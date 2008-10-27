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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import sdloader.http.HttpConst;
import sdloader.javaee.WebApp;
import sdloader.javaee.impl.ServletContextImpl;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.WelcomeFileListTag;
import sdloader.util.CollectionsUtil;
import sdloader.util.PathUtils;
import sdloader.util.ResourceUtil;
import sdloader.util.WebUtils;

/**
 * ファイル出力サーブレット リクエストパスからファイルを検索し、返します。
 * 
 * @author c9katayama
 */
public class FileSavingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String PARAM_DOC_ROOT = "docRootPath";

	/**
	 * ドキュメントルート（絶対パス）
	 */
	protected URL[] docRootPath;

	protected Map<String, String> mimeTypeMap = CollectionsUtil.newHashMap();

	protected WelcomeFileListTag welcomeFileListTag;

	protected WebApp webApp;

	public FileSavingServlet() {
		super();

	}

	public void init() throws ServletException {
		String docRootStr = getInitParameter(PARAM_DOC_ROOT);
		if (docRootStr == null)
			throw new ServletException("InitParameter [docRootPath] not found.");
		String[] paths = docRootStr.split(",");
		this.docRootPath = new URL[paths.length];
		for (int i = 0; i < paths.length; i++) {
			docRootPath[i] = ResourceUtil.createURL(paths[i]);
		}
		// load mimetype
		initMime();

		ServletContextImpl servletContext = (ServletContextImpl) getServletContext();
		webApp = servletContext.getWebApplication();
		welcomeFileListTag = webApp.getWebXml().getWebApp()
				.getWelcomeFileList();
	}

	/**
	 * mimeTypeを初期化します。
	 * 
	 * @throws ServletException
	 */
	protected void initMime() throws ServletException {
		InputStream is = ResourceUtil.getResourceAsStream(
				"/sdloader/resource/mime.xml", getClass());
		if (is == null)
			throw new ServletException("mime.xml not found.");

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, new MimeParseHandler(this));
		} catch (Exception se) {
			throw new ServletException("Mime parse fail. " + se.getMessage());
		}
	}

	public void addMimeType(String ext, String type) {
		this.mimeTypeMap.put(ext, type);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doIt(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doIt(req, res);
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
		File fileOrDir = null;
		for (int i = 0; i < docRootPath.length; i++) {
			File testFile = PathUtils.url2File(this.docRootPath[i]
					.toExternalForm()
					+ uri);
			if (testFile.exists()) {
				fileOrDir = testFile;
				break;
			}
		}
		if (fileOrDir == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (fileOrDir.isFile()) {
			File file = fileOrDir;
			outputFile(file, req, res);
			return;
		} else {
			if (welcomeFileListTag != null) {
				File dir = fileOrDir;
				processWelcomeFile(dir.toURI().toURL(), req, res);
				return;
			} else {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
	}

	/**
	 * welcomeファイルの処理 welcomeファイルリストのパスに対して、パターンが完全一致するサーブレット
	 * かファイルを検索し、見つかった場合forwardします。
	 * 
	 * @param dir
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processWelcomeFile(final URL dir,
			final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		String basePath = req.getPathInfo();
		if (!basePath.endsWith("/"))
			basePath += "/";

		ServletContextImpl context = (ServletContextImpl) getServletContext();
		WebApp webapp = context.getWebApplication();
		List<String> welcomeFileList = welcomeFileListTag.getWelcomeFile();
		List<ServletMappingTag> servletMappingList = webapp.getWebXml()
				.getWebApp().getServletMapping();

		for (Iterator<String> itr = welcomeFileList.iterator(); itr.hasNext();) {
			String welcomefileName = itr.next();
			String path = basePath + welcomefileName;
			// find servlet mapping
			for (Iterator<ServletMappingTag> mappingItr = servletMappingList
					.iterator(); mappingItr.hasNext();) {
				ServletMappingTag mappingTag = mappingItr.next();
				int matchType = WebUtils.matchPattern(mappingTag
						.getUrlPattern(), path);
				if (matchType == WebUtils.PATTERN_EXACT_MATCH) {// 完全
					context.getRequestDispatcher(path).forward(req, res);
					return;
				}
			}
			// find file
			URL welcomeFile = ResourceUtil.createURL(dir, welcomefileName);
			if (ResourceUtil.isResourceExist(welcomeFile)) {
				context.getRequestDispatcher(path).forward(req, res);
				return;
			}
		}
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;
	}

	/**
	 * ファイル出力
	 * 
	 * @param file
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	protected void outputFile(File file, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		Date lastModifyDate = new Date(file.lastModified());
		res.setHeader(HttpConst.LASTMODIFIED, WebUtils
				.formatHeaderDate(lastModifyDate));
		String ifModified = req.getHeader(HttpConst.IFMODIFIEDSINCE);
		// 変更したかどうか
		if (!isModifiedSince(lastModifyDate, ifModified)) {
			res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		FileInputStream fin = new FileInputStream(file);
		ServletOutputStream sout = res.getOutputStream();
		try {
			int size = ResourceUtil.copyStream(fin, sout);
			setContentType(res, file.getName());
			res.setContentLength(size);
			res.setStatus(HttpServletResponse.SC_OK);
		} finally {
			fin.close();
			sout.flush();
			sout.close();
		}
	}

	/**
	 * ファイルを変更したかどうか
	 * 
	 * @param fileLastModify
	 * @param ifModifiedSince
	 * @return 変更していた場合true,していない場合false
	 */
	protected boolean isModifiedSince(Date fileLastModify,
			String ifModifiedSince) {
		if (ifModifiedSince == null)
			return true;
		try {
			long ifModifiedTime = WebUtils.parseHeaderDate(ifModifiedSince
					.trim());
			long lastModifyTime = fileLastModify.getTime();
			if (lastModifyTime <= ifModifiedTime)
				return false;
			else
				return true;
		} catch (NumberFormatException ne) {
			return true;
		} catch (ParseException e) {
			return true;
		}
	}

	protected void setContentType(HttpServletResponse res, String path) {
		int dotIndex = path.lastIndexOf(".");
		if (dotIndex >= 0) {
			try {
				String ext = path.substring(dotIndex + 1);
				String type = mimeTypeMap.get(ext);
				if (type == null)
					type = mimeTypeMap.get(ext.toLowerCase());
				if (type == null)
					type = mimeTypeMap.get(ext.toUpperCase());

				if (type != null) {
					res.setContentType(type);
					return;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		// default
		res.setContentType("text/html");
	}
}

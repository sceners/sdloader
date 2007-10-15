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
package sdloader.javaee;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sdloader.SDLoader;
import sdloader.javaee.servlet.FileSavingServlet;
import sdloader.javaee.servlet.WebAppListServlet;
import sdloader.javaee.webxml.InitParamTag;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.ServletTag;
import sdloader.javaee.webxml.WebAppTag;
import sdloader.javaee.webxml.WebXml;
import sdloader.javaee.webxml.WebXmlFactory;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.BooleanUtil;
import sdloader.util.ClassUtil;
import sdloader.util.CollectionsUtil;
import sdloader.util.WebUtils;

/**
 * Webアプリケーションのマネージャークラス webapps以下のWebアプリケーションを読み込み、 初期化します。
 * webアプリは、3つの方法で配置できます。 ・warファイル ・ディレクトリ ・xmlファイル
 * warファイルは、ファイル名と同じディレクトリがない場合はその場で解凍されます。 warファイル名がコンテキストパスとなります。
 * ディレクトリは、ディレクトリ名がコンテキストパスになります。 xmlファイルは、コンテキストパスとドキュメントベースを指定できます。 ファイルに<Context
 * path="/コンテキストパス" docBase="Webアプリのドキュメントベース"/> という内容でファイルを記述すると、そこからロードを行います。
 * コンテキストパスがない場合は、xmlファイル名がコンテキストパスになります
 * docBaseは、.からはじめると相対パス、そうでない場合は絶対パスとして取り扱います。
 * 相対パスの場合は、xmlファイルのある位置からの相対パスを記述します。
 * 
 * @author c9katayama
 * @author shot
 */
public class WebAppManager {
	private static SDLoaderLog log = SDLoaderLogFactory
			.getLog(WebAppManager.class);

	protected SDLoader server;

	protected String webappDirPath;

	protected List<PathPair> pathPairList;

	protected List<String> contextPathList;

	protected List<WebApplication> webAppList = CollectionsUtil.newArrayList();

	protected boolean isInmemoryExtract = false;

	private static final String JASPER_SERVLET_CLASS = "org.apache.jasper.servlet.JspServlet";

	private static final boolean JASPER_SUPPORT;

	static {
		JASPER_SUPPORT = ClassUtil.hasClass(JASPER_SERVLET_CLASS);
	}

	public WebAppManager(SDLoader server) {
		this.server = server;
	}

	public void init() {
		try {
			final String homeDirPath = server
					.getConfig(SDLoader.KEY_SDLOADER_HOME);
			// webappsの絶対パス
			webappDirPath = homeDirPath + "/" + WebConstants.WEBAPP_DIR_NAME;
			File webappDir = new File(webappDirPath);
			if (!webappDir.exists()) {
				throw new RuntimeException("webapps directory not exists.path="
						+ webappDirPath);
			}
			String extractStr = server
					.getConfig(SDLoader.KEY_WAR_INMEMORY_EXTRACT);
			isInmemoryExtract = BooleanUtil.toBoolean(extractStr);
			detectWebApps();
			initWebAppContext();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void detectWebApps() throws Exception {
		File webappDir = new File(webappDirPath);

		File[] dirs = webappDir.listFiles(new DirFileFilter());

		File[] warFiles = webappDir.listFiles(new WarFileFilter());

		if (warFiles != null) {
			for (int i = 0; i < warFiles.length; i++) {
				if (!isExtracted(warFiles[i], dirs))
					extractWar(warFiles[i], webappDir);
			}
		}
		// webapps以下のフォルダ
		pathPairList = CollectionsUtil.newArrayList();
		;
		contextPathList = CollectionsUtil.newArrayList();
		;
		dirs = webappDir.listFiles(new DirFileFilter());
		if (dirs != null) {
			for (int i = 0; i < dirs.length; i++) {
				final String contextPath = "/" + dirs[i].getName();
				final String docBase = webappDirPath + contextPath;
				final PathPair pathPair = createPathPair(new File(docBase)
						.toURL());
				pathPairList.add(pathPair);
				contextPathList.add(contextPath);
				log.info("detect webapp context. contextPath=" + contextPath
						+ " docBase=" + docBase);
			}
		}
		// コンテキストXML
		File[] contextXMLs = webappDir.listFiles(new ContextXMLFileFilter());
		if (contextXMLs != null) {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			for (int i = 0; i < contextXMLs.length; i++) {
				File contextXml = contextXMLs[i];
				final String fileName = contextXml.getName();
				final String contextPathFromFileName = "/"
						+ fileName.substring(0, fileName.length()
								- ".xml".length());
				parser.parse(contextXml, new DefaultHandler() {
					public void startElement(String uri, String localName,
							String qName, Attributes attributes)
							throws SAXException {
						if (qName.equals("Context")) {
							String contextPath = attributes.getValue("path");
							String docBase = attributes.getValue("docBase");
							if (docBase == null) {
								log.error("docBase attribute not found. file="
										+ fileName);
								return;
							} else {
								if (contextPath == null)
									contextPath = contextPathFromFileName;
								docBase = docBase.replace('\\', '/');
								if (docBase.startsWith(".")) {// 相対パスの場合、webappsまでのパスを追加
									docBase = webappDirPath + "/" + docBase;
								}
								if (!new File(docBase).exists()) {
									log.error("docBase not exist.file="
											+ fileName + " contextPath="
											+ contextPath + " docBase="
											+ docBase);
									return;
								} else {
									log
											.info("detect webapp context. contextPath="
													+ contextPath
													+ " docBase="
													+ docBase);
									try {
										final PathPair pathPair = createPathPair(new File(
												docBase).toURL());
										pathPairList.add(pathPair);
									} catch (MalformedURLException ignore) {
										// TODO logging or throw exception.
									}
									contextPathList.add(contextPath);
								}
							}
						}
					}
				});
			}
		}
	}

	protected PathPair createPathPair(final URL url) {
		return new PathPair(url);
	}

	protected void initWebAppContext() throws Exception {
		for (Iterator<PathPair> itr = pathPairList.iterator(); itr.hasNext();) {
			PathPair pathPair = itr.next();
			String docBase = pathPair.docBase;
			String contextPath = pathPair.contextPath;
			File webxmlFile = new File(docBase + "/WEB-INF/web.xml");
			WebXml webxml = null;
			if (webxmlFile.exists()) {
				FileInputStream fin = null;
				try {
					fin = new FileInputStream(webxmlFile);
					webxml = WebXmlFactory.createWebXml(fin);
				} finally {
					fin.close();
					fin = null;
				}
			} else {
				webxml = new WebXml();
				webxml.setWebApp(new WebAppTag());
			}
			setDefaultServlet(webxml, docBase, contextPath);

			// create WebApplication
			WebAppClassLoader webAppClassLoader = createWebAppClassLoader(docBase);
			webAppClassLoader.setParentClassLoader(Thread.currentThread()
					.getContextClassLoader());
			WebApplication webapp = new WebApplication(webxml, docBase,
					contextPath, webAppClassLoader, this);
			log.info("create webapp [" + contextPath + "]");
			this.webAppList.add(webapp);
		}

		this.webAppList.add(getRootWebApplication());
	}

	protected WebApplication getRootWebApplication() {
		// WebApp List
		final String webAppListServletName = "webAppList";
		final ServletTag webAppListServletTag = new ServletTag();
		webAppListServletTag.setLoadOnStartup(0);
		webAppListServletTag.setServletClass(WebAppListServlet.class.getName());
		webAppListServletTag.setServletName(webAppListServletName);

		final ServletMappingTag webAppListMappingTag = new ServletMappingTag();
		webAppListMappingTag.setServletName(webAppListServletName);
		webAppListMappingTag.setUrlPattern("/");

		final WebXml webXmlTag = new WebXml();
		WebAppTag webAppTag = new WebAppTag();
		webAppTag.addServlet(webAppListServletTag);
		webAppTag.addServletMapping(webAppListMappingTag);
		webXmlTag.setWebApp(webAppTag);

		// Default servlet
		final String contextPath = "/";
		String docBase = webappDirPath + "/" + WebConstants.ROOT_DIR_NAME;
		setDefaultServlet(webXmlTag, docBase, contextPath);

		final WebAppClassLoader webAppClassLoader = createWebAppClassLoader("");
		final WebApplication webapp = new WebApplication(webXmlTag, null, "/",
				webAppClassLoader, this);

		return webapp;
	}

	/**
	 * Webアプリ用のクラスローダーを生成します。
	 * 
	 * @param absoluteContextPath
	 * @return
	 * @throws MalformedURLException
	 */
	protected WebAppClassLoader createWebAppClassLoader(String docBase) {
		try {
			List<URL> urlList = CollectionsUtil.newArrayList();
			// classes
			File classesDir = new File(docBase + "/WEB-INF/classes");
			if (classesDir.exists()) {
				String dirPath = classesDir.getAbsolutePath();
				dirPath = WebUtils.replaceFileSeparator(dirPath) + "/";
				urlList.add(new URL("file:///" + dirPath));
			}
			// WEB-INF/lib
			String webinfLibDir = docBase + "/WEB-INF/lib";
			URL[] libs = WebUtils.createClassPaths(webinfLibDir,
					new ArchiveFileFilter());
			if (libs != null) {
				for (int i = 0; i < libs.length; i++)
					urlList.add(libs[i]);
			}
			URL[] urls = (URL[]) urlList.toArray(new URL[] {});
			WebAppClassLoader webAppClassLoader = new WebAppClassLoader(urls);
			webAppClassLoader.setParentClassLoader(Thread.currentThread()
					.getContextClassLoader());
			return webAppClassLoader;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * デフォルトサーブレット（FileSavingServlet)の記述を追加します。
	 * 
	 * @param webxml
	 * @param absoluteContextPath
	 *            ドキュメントルートになるパス
	 * @param contextPath
	 *            コンテキストパス
	 */
	private void setDefaultServlet(WebXml webxml, String docBase,
			String contextPath) {

		if (JASPER_SUPPORT) {
			// jsp compiler
			final String jspServletName = "jsp";
			ServletTag jspServlet = new ServletTag();
			jspServlet.setLoadOnStartup(0);
			jspServlet.setServletClass(JASPER_SERVLET_CLASS);
			jspServlet.setServletName(jspServletName);
			jspServlet.addInitParam(new InitParamTag("logVerbosityLevel",
					"WARNING"));
			jspServlet.addInitParam(new InitParamTag("validating", "false"));
			jspServlet.addInitParam(new InitParamTag("fork", "false"));

			// JSPコンパイルディレクトリの作成
			jspServlet.addInitParam(new InitParamTag("scratchdir", "false"));
			String jspWorkDirPath = System.getProperty("java.io.tmpdir")
					+ "/sdloaderjsp" + contextPath;
			File jspWorkDir = new File(jspWorkDirPath);
			jspWorkDir.mkdirs();
			jspServlet.addInitParam(new InitParamTag("scratchdir",
					jspWorkDirPath));
			String jspClassPath = System
					.getProperty(SDLoader.SDLOADER_JSP_LIBPATH);
			if (jspClassPath != null)
				jspServlet.addInitParam(new InitParamTag("classpath",
						jspClassPath));

			ServletMappingTag jspMapping = new ServletMappingTag();
			jspMapping.setServletName(jspServletName);
			jspMapping.setUrlPattern("*.jsp");

			webxml.getWebApp().addServlet(jspServlet);
			webxml.getWebApp().addServletMapping(jspMapping);
		}

		// file saving
		final String fileSavingServletName = "file";
		ServletTag fileServletTag = new ServletTag();
		fileServletTag.setLoadOnStartup(0);
		fileServletTag.setServletClass(FileSavingServlet.class.getName());
		fileServletTag.setServletName(fileSavingServletName);
		fileServletTag.addInitParam(new InitParamTag(
				FileSavingServlet.PARAM_DOC_ROOT, docBase));
		ServletMappingTag mappingTag = new ServletMappingTag();
		mappingTag.setServletName(fileSavingServletName);
		mappingTag.setUrlPattern("/*");

		webxml.getWebApp().addServlet(fileServletTag);
		webxml.getWebApp().addServletMapping(mappingTag);
	}

	private static boolean isExtracted(File warFile, File[] dirs) {
		boolean extracted = false;
		if (dirs != null) {
			String archiveName = getArchiveName(warFile.getName());
			for (int i = 0; i < dirs.length; i++) {
				if (archiveName.equals(dirs[i].getName()))
					extracted = true;
			}
		}
		return extracted;
	}

	private static String getArchiveName(String warFileName) {
		return warFileName.substring(0, warFileName.length() - ".war".length());
	}

	/**
	 * WARファイルを解凍します。
	 * 
	 * @param warFile
	 * @param directory
	 * @throws IOException
	 */
	private static void extractWar(File warFile, File directory)
			throws IOException {
		log.info("war extract start. warfile=" + warFile.getName());

		try {
			JarInputStream jin = new JarInputStream(
					new FileInputStream(warFile));
			JarEntry entry = null;

			File webAppDir = new File(directory, getArchiveName(warFile
					.getName()));
			webAppDir.mkdirs();

			while ((entry = jin.getNextJarEntry()) != null) {
				File file = new File(webAppDir, entry.getName());

				if (entry.isDirectory()) {
					if (!file.exists())
						file.mkdirs();
				} else {
					File dir = new File(file.getParent());
					if (!dir.exists())
						dir.mkdirs();

					FileOutputStream fout = null;
					try {
						fout = new FileOutputStream(file);
						WebUtils.copyStream(jin, fout);
					} finally {
						fout.flush();
						fout.close();
						fout = null;
					}

					if (entry.getTime() >= 0)
						file.setLastModified(entry.getTime());
				}
			}
			log.info("war extract success.");
		} catch (IOException ioe) {
			log.info("war extract fail.");
			throw ioe;
		}
	}

	/**
	 * webapps以下のフォルダをコンテキストパスとして認識し、/をつけて返します。
	 * 
	 * @return
	 */
	public List<String> getContextPathList() {
		return contextPathList;
	}

	/**
	 * WebAppのリストを返します。
	 * 
	 * @return
	 */
	public List<WebApplication> getWebAppList() {
		return webAppList;
	}

	/**
	 * コンテキストパスに対するWebAppを検索します。
	 * 
	 * @param contextPath
	 * @return 該当するWebAppがない場合、null
	 */
	public WebApplication findWebApp(String contextPath) {
		for (Iterator<WebApplication> itr = getWebAppList().iterator(); itr
				.hasNext();) {
			WebApplication webapp = itr.next();
			if (webapp.getContextPath().equals(contextPath))
				return webapp;
		}
		return null;
	}

	public void close() {
		List<WebApplication> webAppList = getWebAppList();
		for (Iterator<WebApplication> itr = webAppList.iterator(); itr
				.hasNext();) {
			WebApplication webapp = itr.next();
			List<Servlet> servletList = webapp.getServletList();
			{
				if (servletList != null) {
					for (Iterator<Servlet> servletItr = servletList.iterator(); servletItr
							.hasNext();) {
						Servlet servlet = servletItr.next();
						try {
							servlet.destroy();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
			List<Filter> filterList = webapp.getFilterList();
			{
				if (filterList != null) {
					for (Iterator<Filter> filterItr = filterList.iterator(); filterItr
							.hasNext();) {
						Filter filter = filterItr.next();
						try {
							filter.destroy();
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
			ServletContextEvent contextEvent = new ServletContextEvent(webapp
					.getServletContext());
			List<ServletContextListener> listenerList = webapp.getListenerList();
			{
				if (listenerList != null) {
					for (Iterator<ServletContextListener> listenerItr = listenerList.iterator(); listenerItr
							.hasNext();) {
						ServletContextListener listener = (ServletContextListener) listenerItr
								.next();
						try {
							listener.contextDestroyed(contextEvent);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	private class PathPair {
		String docBase;// アプリケーションのドキュメントルート

		String contextPath;// アプリケーションのコンテキストパス

		URL url;// docBaseのURL形式またはWARへのURL

		PathPair(final URL url) {
			this.url = url;
			if (!isInmemoryExtract) {
				final String urlStr = url.toExternalForm();
				this.docBase = urlStr.replace("file:/", "");
				String s = this.docBase.replace(webappDirPath, "");
				this.contextPath = (s.endsWith("/")) ? s.substring(0, s
						.length() - 1) : s;
			} else {
				// TODO
			}
		}
	}

	private class DirFileFilter implements FileFilter {
		public boolean accept(File file) {
			return file.isDirectory() && !file.getName().equals("CVS")
					&& !file.getName().startsWith(".");
		}
	}

	private class WarFileFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith(".war");
		}
	}

	private class ContextXMLFileFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith(".xml");
		}
	}

	private class ArchiveFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (pathname.getName().endsWith(".jar")
					|| pathname.getName().endsWith(".zip"))
				return true;
			else
				return false;
		}
	}

}

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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import sdloader.SDLoader;
import sdloader.internal.resource.ArchiveTypeResource;
import sdloader.internal.resource.BranchTypeResource;
import sdloader.internal.resource.LeafTypeResource;
import sdloader.internal.resource.Resource;
import sdloader.internal.resource.ResourceBuilder;
import sdloader.internal.resource.ResourceBuilderImpl;
import sdloader.internal.resource.ResourceURLConnection;
import sdloader.javaee.jasper.InMemoryEmbeddedServletOptions;
import sdloader.javaee.servlet.FileSavingServlet;
import sdloader.javaee.servlet.InMemoryFileSavingServlet;
import sdloader.javaee.servlet.WebAppListServlet;
import sdloader.javaee.webxml.InitParamTag;
import sdloader.javaee.webxml.ServletMappingTag;
import sdloader.javaee.webxml.ServletTag;
import sdloader.javaee.webxml.WebAppTag;
import sdloader.javaee.webxml.WebXml;
import sdloader.javaee.webxml.WebXmlBuilder;
import sdloader.javaee.webxml.WebXmlFactory;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.Assertion;
import sdloader.util.BooleanUtil;
import sdloader.util.ClassUtil;
import sdloader.util.CollectionsUtil;
import sdloader.util.PathUtils;
import sdloader.util.ResourceUtil;
import sdloader.util.WarUtil;
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

	protected List<WebAppContext> webAppContextList = CollectionsUtil.newArrayList();

	protected List<WebApplication> webAppList = CollectionsUtil.newArrayList();

	protected boolean isInmemoryExtract = false;

	protected Map<URL, Map<URL, Resource>> warInmemoryMap = CollectionsUtil
			.newHashMap();

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
		this.webappDirPath = server.getConfig(SDLoader.KEY_SDLOADER_WEBAPP_PATH);
		File webappDir = new File(webappDirPath);
		if (!webappDir.exists()) {
			throw new RuntimeException("webapps directory not exists.path="
					+ webappDirPath);
		}
		
		File[] dirs = webappDir.listFiles(new DirFileFilter());
		File[] warFiles = webappDir.listFiles(new WarFileFilter());

		if (!isInmemoryExtract) {
			if (warFiles != null) {
				for (int i = 0; i < warFiles.length; i++) {
					if (!isExtracted(warFiles[i], dirs))
						WarUtil.extractWar(warFiles[i], webappDir);
				}
			}
		}
		// webapps以下のフォルダ
		dirs = webappDir.listFiles(new DirFileFilter());
		if (!isInmemoryExtract) {
			if (dirs != null) {
				for (int i = 0; i < dirs.length; i++) {
					final String contextPath = "/" + dirs[i].getName();
					final String docBase = webappDirPath + contextPath;
					final WebAppContext context 
						= new WebAppContext(contextPath,PathUtils.file2URL(docBase));
					webAppContextList.add(context);
					log.info("detect webapp context. contextPath="
							+ contextPath + " docBase=" + docBase);
				}
			}
		} else {
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				public URLStreamHandler createURLStreamHandler(
						final String protocol) {
					if (protocol.equals(WarURLStreamHandler.PROTOCOL)) {
						return new WarURLStreamHandler();
					} else if (protocol.equals(InnerJarURLStreamHandler.PROTOCOL)) {
						return new InnerJarURLStreamHandler();
					}
					return null;
				}
			});
			ResourceBuilder builder = new ResourceBuilderImpl();
			for (File warfile : warFiles) {
				final URL warDocRoot = 
					ResourceUtil.createURL(WarURLStreamHandler.PROTOCOL+":" 
							+ warfile.toURI().toURL().toExternalForm());
				final String contextPath = "/" + ResourceUtil.stripExtension(warfile.getName());
				Map<URL, Resource> map = builder.build(warfile.getPath());
				WebAppContext context = new WebAppContext(contextPath, warDocRoot);
				webAppContextList.add(context);
				warInmemoryMap.put(context.getDocBase(), map);
			}
		}
		// コンテキストXML
		File[] contextXMLs = webappDir.listFiles(new ContextXMLFileFilter());
		parseContextXMLs(contextXMLs);
	}

	protected void initWebAppContext() throws Exception {
		for (WebAppContext context : webAppContextList) {
			final URL docBase = context.getDocBase();
			final String contextPath = context.getContextPath();
			WebXml webxml = buildWebXml(docBase);

			setDefaultServlet(webxml, docBase, contextPath, isInmemoryExtract);
			// create WebApplication
			ClassLoader webAppClassLoader = !isInmemoryExtract ? createWebAppClassLoader(docBase)
					: createInMemoryWebAppClassLoader(docBase);
			WebApplication webapp = new WebApplication(webxml,context, webAppClassLoader, this);
			this.webAppList.add(webapp);

			log.info("create webapp [" + contextPath + "]");
		}

		this.webAppList.add(getRootWebApplication());
	}

	protected WebXml buildWebXml(URL url) throws SAXException,
			ParserConfigurationException, MalformedURLException {
		URL webXmlUrl = ResourceUtil.createURL(url, "WEB-INF/web.xml");
		return new WebXmlBuilder() {

			public WebXml build(final URL webXmlUrl) throws SAXException,
					ParserConfigurationException {
				try {
					InputStream is = webXmlUrl.openStream();
					return WebXmlFactory.createWebXml(is);
				} catch (Exception ignore) {
					WebXml webxml = new WebXml();
					webxml.setWebApp(new WebAppTag());
					return webxml;
				}
			}

		}.build(webXmlUrl);
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
		URL docBase = PathUtils.file2URL(webappDirPath+ "/"+ WebConstants.ROOT_DIR_NAME);
		if(ResourceUtil.isResourceExist(docBase)){
			setDefaultServlet(webXmlTag, docBase, contextPath, false);
		}
		final ClassLoader webAppClassLoader = createWebAppClassLoader(docBase);
		WebAppContext context = new WebAppContext(contextPath,docBase);
		final WebApplication webapp = new WebApplication(webXmlTag,context,webAppClassLoader, this);

		return webapp;
	}

	/**
	 * Webアプリ用のクラスローダーを生成します。
	 * 
	 * @param absoluteContextPath
	 * @return
	 * @throws MalformedURLException
	 */
	protected ClassLoader createWebAppClassLoader(URL docBase) {
		List<URL> urlList = CollectionsUtil.newArrayList();
		// classes
		File docBaseDir = PathUtils.url2File(docBase);
		File classesDir = new File(docBaseDir,"/WEB-INF/classes/");
		if (classesDir.exists()) {
			urlList.add(PathUtils.file2URL(classesDir));
		}
		// WEB-INF/lib
		File webinfLibDir = new File(docBaseDir,"/WEB-INF/lib");
		URL[] libs = WebUtils.createClassPaths(webinfLibDir,new ArchiveFileFilter(), false);
		if (libs != null) {
			for (int i = 0; i < libs.length; i++){
				urlList.add(libs[i]);
			}
		}
		URL[] urls = (URL[]) urlList.toArray(new URL[] {});
		ClassLoader webinfClassLoader = new URLClassLoader(urls,
				ClassLoader.getSystemClassLoader());
		ClassLoader parentClassLoader = Thread.currentThread()
				.getContextClassLoader();
		WebAppClassLoader webAppClassLoader = new WebAppClassLoader(
				parentClassLoader, webinfClassLoader);
		return webAppClassLoader;
	}

	/**
	 * inMemory動作のWebアプリ用のクラスローダーを生成します。
	 * メモリ上のwarファイルから、クラスパスになるリソースを読み込み、ClassLoaderに 設定します。
	 * 
	 * @param absoluteContextPath
	 * @return
	 * @throws MalformedURLException
	 */
	protected ClassLoader createInMemoryWebAppClassLoader(URL docBase) {
		List<URL> urlList = CollectionsUtil.newArrayList();
		Map<URL, Resource> resourceMap = warInmemoryMap.get(docBase);
		// classes
		URL classesDir = ResourceUtil.createURL(docBase, "WEB-INF/classes/");
		if (resourceMap.get(classesDir) != null) {
			urlList.add(classesDir);
		}
		// WEB-INF/lib
		URL webinfLibDir = ResourceUtil.createURL(docBase, "WEB-INF/lib/");
		Resource libDirResource = resourceMap.get(webinfLibDir);
		if (libDirResource != null) {
			BranchTypeResource dirResource = (BranchTypeResource) libDirResource;
			List<Resource> libs = dirResource.getResources();
			if (libs != null) {
				for (Resource lib : libs) {
					String path = lib.getPath();
					if (lib instanceof LeafTypeResource
							&& (path.endsWith(".jar") || path.endsWith(".zip"))) {
						urlList.add(lib.getURL());
					}
				}
			}
		}
		URL[] urls = (URL[]) urlList.toArray(new URL[] {});
		ClassLoader webinfClassLoader = new BytesBasedClassLoader(ClassLoader
				.getSystemClassLoader(), resourceMap, urls);
		ClassLoader parentClassLoader = Thread.currentThread()
				.getContextClassLoader();
		WebAppClassLoader webAppClassLoader = new WebAppClassLoader(
				parentClassLoader, webinfClassLoader);

		return webAppClassLoader;
	}

	/**
	 * デフォルトサーブレット（FileSavingServlet)の記述を追加します。
	 * 
	 * @param webxml
	 * @param absoluteContextPath
	 *            ドキュメントルートになるパス
	 * @param contextPath
	 *            コンテキストパス
	 * @param warInMemory
	 */
	private void setDefaultServlet(WebXml webxml, URL docBase,
			String contextPath, boolean warInMemory) {

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

			//inmemory用TldLocationCache利用の為
			if(isInmemoryExtract){
				jspServlet.addInitParam(new InitParamTag("engineOptionsClass",
						InMemoryEmbeddedServletOptions.class.getName()));
			}
			
			// JSPコンパイルディレクトリの作成
			jspServlet.addInitParam(new InitParamTag("scratchdir", "false"));
			String jspWorkDirPath = System.getProperty("java.io.tmpdir")
					+ "sdloaderjsp" + contextPath;
			File jspWorkDir = new File(jspWorkDirPath);
			jspWorkDir.mkdirs();
			jspServlet.addInitParam(new InitParamTag("scratchdir",
					jspWorkDirPath));
			String jspLibPath = server.getConfig(SDLoader.KEY_SDLOADER_JSP_LIBPATH);
			if (jspLibPath != null){
				jspLibPath = PathUtils.replaceFileSeparator(jspLibPath);
				jspServlet.addInitParam(new InitParamTag("classpath",
						jspLibPath));
			}

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
		// TODO 統合したい
		if (warInMemory) {
			fileServletTag.setServletClass(InMemoryFileSavingServlet.class
					.getName());
		} else {
			fileServletTag.setServletClass(FileSavingServlet.class.getName());
		}
		fileServletTag.setServletName(fileSavingServletName);
		fileServletTag.addInitParam(new InitParamTag(
				FileSavingServlet.PARAM_DOC_ROOT, docBase.toExternalForm()));
		ServletMappingTag mappingTag = new ServletMappingTag();
		mappingTag.setServletName(fileSavingServletName);
		mappingTag.setUrlPattern("/*");

		webxml.getWebApp().addServlet(fileServletTag);
		webxml.getWebApp().addServletMapping(mappingTag);
	}

	private static boolean isExtracted(File warFile, File[] dirs) {
		boolean extracted = false;
		if (dirs != null) {
			String archiveName = WarUtil.getArchiveName(warFile.getName());
			for (int i = 0; i < dirs.length; i++) {
				if (archiveName.equals(dirs[i].getName()))
					extracted = true;
			}
		}
		return extracted;
	}
	protected void parseContextXMLs(File[] contextXMLs) throws Exception {
		if (contextXMLs != null) {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();			
			for (int i = 0; i < contextXMLs.length; i++) {
				final File contextXml = contextXMLs[i];
				final String fileName = contextXml.getName();
				WebAppContextXmlParserHandler contextXmlParserHandler 
					= new WebAppContextXmlParserHandler(fileName,webappDirPath);
				parser.parse(contextXml, contextXmlParserHandler);
				webAppContextList.add(Assertion.notNull(contextXmlParserHandler.getWebAppContext()));
			}
		}
	}
		
	/**
	 * /から始まるコンテキストパスを返します。
	 * 
	 * @return
	 */
	public List<String> getContextPathList() {
		List<String> contextPathList = CollectionsUtil.newArrayList();
		for(WebAppContext context:webAppContextList){
			contextPathList.add(context.getContextPath());
		}
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
			if (servletList != null) {
				for (Servlet servlet:servletList){
					try {
						servlet.destroy();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
			List<Filter> filterList = webapp.getFilterList();
			if (filterList != null) {
				for (Filter filter:filterList){
					try {
						filter.destroy();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
			ServletContextEvent contextEvent = new ServletContextEvent(webapp
					.getServletContext());
			webapp.getListenerEventDispatcher()
				.dispatchServletContextListener_contextDestroyed(contextEvent);
		}
	}

	private final class WarURLStreamHandler extends URLStreamHandler {
		public static final String PROTOCOL = "war";
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			String warPath = u.toExternalForm();
			warPath = warPath.substring(0, warPath.indexOf("!"));
			Map<URL, Resource> resources = null;
			for (URL key : warInmemoryMap.keySet()) {
				if (key.toExternalForm().equals(warPath)) {
					resources = warInmemoryMap.get(key);
					break;
				}
			}
			final Resource resource = (resources != null) ? resources.get(u)
					: null;
			return new ResourceURLConnection(resource);
		}
	}

	private final class InnerJarURLStreamHandler extends URLStreamHandler {
		public static final String PROTOCOL = "innerjar";
		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			String warPath = u.getPath();
			warPath = warPath.substring(0, warPath.indexOf("!"));
			Map<URL, Resource> resources = null;
			for (URL key : warInmemoryMap.keySet()) {
				if (key.toExternalForm().equals(warPath)) {
					resources = warInmemoryMap.get(key);
					break;
				}
			}
			if (resources == null) {
				return new ResourceURLConnection(null);
			}
			String jarPath = u.toExternalForm();
			String innerJar = jarPath.substring(0, jarPath.lastIndexOf("!"));
			String resourceName = jarPath.substring(
					jarPath.lastIndexOf("!/") + 2, jarPath.length());
			Resource jarResource = resources.get(ResourceUtil
					.createURL(innerJar));
			if (jarResource != null) {
				ArchiveTypeResource jar = (ArchiveTypeResource) jarResource;
				final Resource innerJarResource = jar
						.getArchiveResource(resourceName);
				if (innerJarResource != null) {
					return new ResourceURLConnection(innerJarResource);
				}
			}
			return new ResourceURLConnection(null);
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

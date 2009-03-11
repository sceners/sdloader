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
package sdloader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.Properties;

import sdloader.event.EventDispatcher;
import sdloader.http.HttpProcessor;
import sdloader.http.HttpProcessorPool;
import sdloader.http.HttpRequest;
import sdloader.http.HttpResponse;
import sdloader.internal.SDLoaderConfig;
import sdloader.javaee.SessionManager;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.constants.JavaEEConstants;
import sdloader.javaee.constants.WebConstants;
import sdloader.lifecycle.Lifecycle;
import sdloader.lifecycle.LifecycleEvent;
import sdloader.lifecycle.LifecycleListener;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.ClassUtil;
import sdloader.util.DisposableUtil;
import sdloader.util.IOUtil;
import sdloader.util.PathUtil;
import sdloader.util.ResourceUtil;

/**
 * SDLoader ローカル動作のアプリケーションサーバー
 * 
 * @author c9katayama
 */
public class SDLoader implements Lifecycle {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(SDLoader.class);

	public static final String CONFIG_KEY_PREFIX = "sdloader.";
	/**
	 * JSPコンパイル用のライブラリパス
	 * 
	 * <pre>
	 * SDLoader呼び出し前にセットすると、このパスのライブラリを利用します。
	 * </pre>
	 */
	public static final String KEY_SDLOADER_JSP_LIBPATH = CONFIG_KEY_PREFIX
			+ "jsp.libpath";
	/**
	 * SDLoaderのベースディレクトリパス
	 */
	public static final String KEY_SDLOADER_HOME = CONFIG_KEY_PREFIX + "home";

	public static final String KEY_SDLOADER_WEBAPP_PATH = CONFIG_KEY_PREFIX
			+ "webAppPath";

	public static final String KEY_WAR_INMEMORY_EXTRACT = CONFIG_KEY_PREFIX
			+ "warInMemoryExtract";

	public static final String KEY_SDLOADER_USE_OUTSIDE_PORT = CONFIG_KEY_PREFIX
			+ "useOutSidePort";

	public static final String KEY_SDLOADER_MAX_THREAD_POOL_NUM = CONFIG_KEY_PREFIX
			+ "maxThreadPoolNum";

	public static final String KEY_SDLOADER_AUTO_PORT_DETECT = CONFIG_KEY_PREFIX
			+ "autoPortDetect";

	public static final String KEY_SDLOADER_SERVER_NAME = CONFIG_KEY_PREFIX
			+ "serverName";

	public static final String KEY_SDLOADER_SESSION_MANAGER = CONFIG_KEY_PREFIX
			+ "sessionManager";

	public static final String KEY_SDLOADER_LINE_SPEED = CONFIG_KEY_PREFIX
			+ "lineSpeed";

	public static final String KEY_SDLOADER_PORT = CONFIG_KEY_PREFIX + "port";

	private String sdloaderConfigPath = "sdloader.properties";

	protected WebAppManager webAppManager = new WebAppManager();

	protected SDLoaderConfig config = new SDLoaderConfig();

	protected EventDispatcher<LifecycleListener, LifecycleEvent<SDLoader>> dispatcher = new EventDispatcher<LifecycleListener, LifecycleEvent<SDLoader>>(
			LifecycleListener.class);

	public boolean running = false;

	private HttpProcessorPool socketProcessorPool;

	private SessionManager sessionManager;

	private SDLoaderThread sdLoaderThread;

	/**
	 * ポート30000でSDLoaderを構築します。
	 * 
	 * @param port
	 */
	public SDLoader() {
		loadDefaultConfig();
	}

	/**
	 * 指定のポートでSDLoaderを構築します。
	 * 
	 * @param port
	 */
	public SDLoader(int port) {
		loadDefaultConfig();
		setPort(port);
	}

	public SDLoader(boolean autoPortDetect) {
		loadDefaultConfig();
		setAutoPortDetect(autoPortDetect);
	}

	public SDLoader(String defaultConfigPath) {
		sdloaderConfigPath = defaultConfigPath;
		loadDefaultConfig();
	}

	protected void loadDefaultConfig() {
		Properties initSetting = ResourceUtil.loadProperties(
				sdloaderConfigPath, SDLoader.class);
		config.addAllIfNotExist(initSetting);
	}

	public void setConfig(String key, String value) {
		getSDLoaderConfig().setConfig(key, value);
	}

	public SDLoaderConfig getSDLoaderConfig() {
		return config;
	}

	/**
	 * ポートが使用中の場合、使用できるポートを探すかどうかをセットします.
	 * <p>
	 * trueの場合、セットしたポートが使用中の場合、別のポートを探します。
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	public void setAutoPortDetect(boolean autoPortDetect) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_AUTO_PORT_DETECT, autoPortDetect);
	}

	/**
	 * ポートを外部からの接続に応答するようにするかどうかをセットします。
	 * 
	 * @param useOutSizePort
	 */
	public void setUseOutSidePort(boolean useOutSizePort) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_USE_OUTSIDE_PORT, Boolean
				.toString(useOutSizePort));
	}

	/**
	 * 最大スレッドプール数を設定します。 open前にセットしてください。
	 * 
	 * @param maxThreadPoolNum
	 */
	public void setMaxThreadPoolNum(int maxThreadPoolNum) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_MAX_THREAD_POOL_NUM, maxThreadPoolNum);
	}

	/**
	 * URIのエンコードをセットします。
	 * 
	 * @param encoding
	 */
	public void setURIEncoding(String uriEncoding) {
		checkNotRunning();
		config.setConfig(HttpRequest.KEY_REQUEST_URI_ENCODING, uriEncoding);
	}

	/**
	 * trueの場合、Body部分のエンコードをGETパラメータにも適用します。
	 * 
	 * @param encoding
	 */
	public void setUseBodyEncodingURI(boolean useBodyEncodingForURI) {
		checkNotRunning();
		config.setConfig(HttpRequest.KEY_REQUEST_USE_BODY_ENCODEING_FOR_URI,
				useBodyEncodingForURI);
	}

	/**
	 * trueの場合、すべてのレスポンスにNoCacheヘッダーをつけます。
	 * 
	 * @param useNoCacheMode
	 */
	public void setUseNoCacheMode(boolean useNoCacheMode) {
		checkNotRunning();
		config.setConfig(HttpResponse.KEY_RESPONSE_USE_NOCACHE_MODE,
				useNoCacheMode);
	}

	/**
	 * Listenするポート番号を設定します。 open前にセットしてください。
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_PORT, port);
	}

	/**
	 * サーバ名を設定します。レスポンスのServerヘッダーに この名前が入ります。
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_SERVER_NAME, serverName);
	}

	/**
	 * 回線速度をセットします。
	 * 
	 * @param bps
	 */
	public void setLineSpeed(int bps) {
		checkNotRunning();
		config.setConfig(KEY_SDLOADER_LINE_SPEED, bps);
	}

	/**
	 * Webアプリケーションコンテキストを追加します。
	 * 
	 * @param context
	 */
	public void addWebAppContext(WebAppContext context) {
		checkNotRunning();
		webAppManager.addWebAppContext(context);
	}

	/**
	 * Listenするポート番号を返します。
	 * 
	 * @return
	 */
	public int getPort() {
		return config.getConfigInteger(KEY_SDLOADER_PORT);
	}

	/**
	 * サーバ名を返します。
	 * 
	 * @return
	 */
	public String getServerName() {
		return config.getConfigString(KEY_SDLOADER_SERVER_NAME);
	}

	/**
	 * SessionManagerを返します。
	 * 
	 * @return
	 */
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	/**
	 * イベントリスナーを追加します。
	 * 
	 * @param type
	 * @param listener
	 */
	public void addEventListener(String type, LifecycleListener listener) {
		dispatcher.addEventListener(type, listener);
	}

	public boolean isRunning() {
		return running;
	}

	protected boolean checkNotRunning() {
		if (running) {
			throw new IllegalStateException("SDLoader is already running.");
		}
		return false;
	}

	/**
	 * ソケットをオープンし、サーバを開始します。
	 */
	public void start() {
		checkNotRunning();
		running = true;

		log.info("Detect ServletAPI["
				+ JavaEEConstants.SERVLETAPI_MAJOR_VERSION + "."
				+ JavaEEConstants.SERVLETAPI_MINOR_VERSION + "] JSP["
				+ JavaEEConstants.JSP_MAJOR_VERSION + "."
				+ JavaEEConstants.JSP_MINOR_VERSION + "]");

		long t = System.currentTimeMillis();

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.BEFORE_START, this));

		initConfig();

		initSessionManager();

		ServerSocket initSocket = initServerSocket();

		initWebApp();
		initSocketProcessor();
		initShutdownHook();

		sdLoaderThread = new SDLoaderThread(initSocket);
		sdLoaderThread.start();

		log.info("SDLoader[port:" + getPort() + "] startup in "
				+ (System.currentTimeMillis() - t) + " ms.");

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.AFTER_START, this));
	}

	protected void initConfig() {
		// init home
		String homeDir = config.getConfigStringIgnoreExist(KEY_SDLOADER_HOME);
		if (homeDir == null) {
			homeDir = System.getProperty(KEY_SDLOADER_HOME);
			if (homeDir == null) {
				homeDir = System.getProperty("user.dir");
			}
		}
		homeDir = PathUtil.replaceFileSeparator(homeDir);
		homeDir = PathUtil.removeEndSlashIfNeed(homeDir);

		config.setConfig(KEY_SDLOADER_HOME, homeDir);
		log.info("SDLOADER_HOME=" + homeDir);

		// init webappDir
		String webappPath = config
				.getConfigStringIgnoreExist(KEY_SDLOADER_WEBAPP_PATH);
		if (webappPath == null) {
			webappPath = System.getProperty(KEY_SDLOADER_WEBAPP_PATH);
			if (webappPath == null) {
				webappPath = WebConstants.WEBAPP_DIR_NAME;
			}
		}
		webappPath = PathUtil.replaceFileSeparator(webappPath);
		if (!PathUtil.isAbsolutePath(webappPath)) {
			// homeからの絶対パスに変換
			webappPath = homeDir + "/" + webappPath;
		}
		config.setConfig(KEY_SDLOADER_WEBAPP_PATH, webappPath);
		log.info(KEY_SDLOADER_WEBAPP_PATH + "=" + webappPath);

		// init jsp lib path
		config.setConfigFromSystemIfNotExit(KEY_SDLOADER_JSP_LIBPATH);
	}

	protected void initSessionManager() {
		String sessionManagerClassName = config
				.getConfigString(KEY_SDLOADER_SESSION_MANAGER);
		this.sessionManager = ClassUtil.newInstance(sessionManagerClassName);
	}

	protected void initWebApp() {
		try {
			log.info("InternalWebApplication initialize start.");
			webAppManager.init(config);
			log.info("InternalWebApplication initialize success.");
		} catch (RuntimeException e) {
			log.error("InternalWebApplication initialize fail.", e);
			throw e;
		}
	}

	protected ServerSocket initServerSocket() {
		boolean useOutSidePort = config
				.getConfigBoolean(KEY_SDLOADER_USE_OUTSIDE_PORT);
		boolean autoPortDetect = config
				.getConfigBoolean(KEY_SDLOADER_AUTO_PORT_DETECT);
		int port = getPort();
		String portMessage = autoPortDetect ? "AutoDetect" : String
				.valueOf(port);
		log.info("Bind start. Port=" + portMessage + " useOutSidePort="
				+ useOutSidePort);

		ServerSocket servetSocket = null;
		try {
			try {
				int bindPort = getPort();
				servetSocket = IOUtil.createServerSocket(bindPort,
						useOutSidePort);
			} catch (IOException ioe) {
				if (autoPortDetect) {
					servetSocket = IOUtil.createServerSocket(0, useOutSidePort);
				} else {
					throw ioe;
				}
			}
			int bindSuccessPort = servetSocket.getLocalPort();
			config.setConfig(KEY_SDLOADER_PORT, bindSuccessPort);
			log.info("Bind success. Port=" + getPort());
		} catch (IOException ioe) {
			log.error("Bind fail! Port=" + portMessage, ioe);
			throw new RuntimeException(ioe);
		}
		return servetSocket;
	}

	protected void initSocketProcessor() {
		int maxThreadPoolNum = config
				.getConfigInteger(KEY_SDLOADER_MAX_THREAD_POOL_NUM);
		socketProcessorPool = new HttpProcessorPool(maxThreadPoolNum);
	}

	protected void initShutdownHook() {
		final SDLoader sdloader = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					sdloader.stop();
				} catch (Throwable e) {
					log.error("SDLoader close fail.", e);
				} finally {
					DisposableUtil.dispose();
				}
			};
		});
	}

	public WebAppManager getWebAppManager() {
		return webAppManager;
	}

	public void returnProcessor(HttpProcessor processor) {
		socketProcessorPool.returnProcessor(processor);
	}

	class SDLoaderThread extends Thread {
		private boolean shutdown = false;

		private ServerSocket serverSocket;

		SDLoaderThread(ServerSocket serverSocket) {
			super("SDLoaderThread");
			this.serverSocket = serverSocket;
			setDaemon(false);
		}

		public void run() {
			while (!shutdown) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
					log.debug("Accept socket connection.");
				} catch (AccessControlException ace) {
					log.warn("Socket accept security exception "
							+ ace.getMessage(), ace);
					continue;
				} catch (IOException ioe) {
					if (shutdown) {
						break;
					}
					log.error("Socket accept error.", ioe);
					continue;
				}
				HttpProcessor con = socketProcessorPool.borrowProcessor();
				con.process(socket, SDLoader.this);
			}
		}

		public synchronized void close(){
			if (serverSocket != null) {
				IOUtil.closeServerSocketNoException(serverSocket);
				serverSocket = null;
			}
			shutdown = true;
		}
	}

	/**
	 * SDLoaderが終了するまで待機します。
	 * 
	 */
	public void waitForStop() {
		if (running) {
			try {
				sdLoaderThread.join();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * ソケットを閉じ、サーバを終了します。
	 */
	public synchronized void stop() {
		if (!running) {
			return;
		}
		log.info("SDLoader[port:" + getPort() + "] shutdown start.");
		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.BEFORE_STOP, this));

		// destroy webapps
		webAppManager.close();
		
		socketProcessorPool.stop();		
		
		sdLoaderThread.close();

		running = false;

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.AFTER_STOP, this));

		log.info("SDLoader[port:" + getPort() + "] stop.");
	}
}

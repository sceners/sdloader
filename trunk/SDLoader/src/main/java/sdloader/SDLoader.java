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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import sdloader.event.EventDispatcher;
import sdloader.http.HttpRequest;
import sdloader.http.HttpRequestProcessor;
import sdloader.http.HttpRequestProcessorPool;
import sdloader.javaee.WebAppContext;
import sdloader.javaee.WebAppManager;
import sdloader.javaee.constants.WebConstants;
import sdloader.lifecycle.Lifecycle;
import sdloader.lifecycle.LifecycleEvent;
import sdloader.lifecycle.LifecycleListener;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.BooleanUtil;
import sdloader.util.DisposableUtil;
import sdloader.util.PathUtils;
import sdloader.util.SocketUtil;

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
	 * JSPコンパイル用のライブラリパス SDLoader呼び出し前にSystem.setPropertyでセットすると、このパスの
	 * ライブラリを利用します。
	 */
	public static final String KEY_SDLOADER_JSP_LIBPATH = CONFIG_KEY_PREFIX
			+ "jsp.libpath";
	/**
	 * SDLoaderのベースディレクトリパス
	 */
	public static final String KEY_SDLOADER_HOME = CONFIG_KEY_PREFIX + "home";

	public static final String KEY_WAR_INMEMORY_EXTRACT = CONFIG_KEY_PREFIX
			+ "warInMemoryExtract";

	public static final String KEY_SDLOADER_WEBAPP_PATH = CONFIG_KEY_PREFIX
			+ "webAppPath";

	public static final String KEY_SDLOADER_USE_OUTSIDE_PORT = CONFIG_KEY_PREFIX
			+ "useOutSidePort";

	private int maxThreadPoolNum = 4;

	private int backLogNum = -1;// -1でデフォルト値を使用する

	private int port = 30000;

	public static boolean isRunnnig = false;

	/**
	 * ポートが使用中の場合、使用できるポートを探すかどうか
	 */
	private boolean autoPortDetect = false;

	private String serverName = "SDLoader";

	private HttpRequestProcessorPool socketProcessorPool;

	private SDLoaderThread sdLoaderThread;

	protected WebAppManager webAppManager = new WebAppManager(this);

	private Map<String, String> config = new HashMap<String, String>();

	private EventDispatcher<LifecycleListener, LifecycleEvent<SDLoader>> dispatcher = new EventDispatcher<LifecycleListener, LifecycleEvent<SDLoader>>(
			LifecycleListener.class);

	/**
	 * ポート30000でSDLoaderを構築します。
	 * 
	 * @param port
	 */
	public SDLoader() {
	}

	/**
	 * 指定のポートでSDLoaderを構築します。
	 * 
	 * @param port
	 */
	public SDLoader(int port) {
		this.port = port;
	}

	public SDLoader(boolean autoPortDetect) {
		this.autoPortDetect = autoPortDetect;
	}
	
	//----Configuration

	public String getConfig(String key) {
		return (String) config.get(key);
	}

	public String getConfig(String key, String defaultValue) {
		String value = (String) config.get(key);
		return (value == null) ? defaultValue : value;
	}

	public void setConfig(String key, String value) {
		config.put(key, value);
	}

	public void setConfig(String key, String value, String defaultValue) {
		if (value != null) {
			config.put(key, value);
		} else {
			config.put(key, defaultValue);
		}
	}
	/**
	 * ポートが使用中の場合、使用できるポートを探すかどうかをセットします。
	 * 
	 * @param key
	 * @return
	 */
	public void setAutoPortDetect(boolean autoPortDetect) {
		this.autoPortDetect = autoPortDetect;
	}
	
	/**
	 * ポートを外部からの接続に応答するようにするかどうかをセットします。
	 * @param useOutSizePort
	 */
	public void setUseOutSidePort(boolean useOutSizePort){
		setConfig(KEY_SDLOADER_USE_OUTSIDE_PORT,Boolean.toString(useOutSizePort));
	}
	/**
	 * 最大スレッドプール数を設定します。 open前にセットしてください。
	 * 
	 * @param maxThreadPoolNum
	 */
	public void setMaxThreadPoolNum(int maxThreadPoolNum) {
		this.maxThreadPoolNum = maxThreadPoolNum;
	}
	/**
	 * URIのエンコードをセットします。
	 * @param encoding
	 */
	public void setURIEncoding(String uriEncoding){
		setConfig(HttpRequest.KEY_REQUEST_URI_ENCODING,uriEncoding);
	}
	/**
	 * trueの場合、Body部分のエンコードをGETパラメータにも適用します。
	 * @param encoding
	 */
	public void setUseBodyEncodingURI(boolean useBodyEncodingForURI){
		setConfig(HttpRequest.KEY_REQUEST_USE_BODY_ENCODEING_FOR_URI,Boolean.toString(useBodyEncodingForURI));
	}	
	
	/**
	 * Listenするポート番号を設定します。 open前にセットしてください。
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}	
	/**
	 * サーバ名を設定します。レスポンスのServerヘッダーに この名前が入ります。
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	
	
	/**
	 * ポートが使用中の場合、使用できるポートを探すかどうかを返します。
	 * 
	 * @param key
	 * @return
	 */
	public boolean isAutoPortDetect() {
		return autoPortDetect;
	}
	
	/**
	 * Webアプリケーションコンテキストを追加します。
	 * 
	 * @param context
	 */
	public void addWebAppContext(WebAppContext context) {
		if (webAppManager.isInitialized()) {
			throw new RuntimeException("WebAppManager already initialized.");
		}
		webAppManager.addWebAppContext(context);
	}
	/**
	 * 最大プールスレッド数を返します。
	 * 
	 * @return
	 */
	public int getMaxThreadPoolNum() {
		return maxThreadPoolNum;
	}
	/**
	 * Listenするポート番号を返します。
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}
	/**
	 * サーバ名を返します。
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
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
		return isRunnnig;
	}

	/**
	 * ソケットをオープンし、サーバを開始します。
	 */
	public void start() {
		if (isRunnnig) {
			return;
		}

		initConfig();

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.BEFORE_START, this));

		long t = System.currentTimeMillis();

		ServerSocket initSocket = initServerSocket();

		initWebApp();
		initSocketProcessor();
		initShutdown();

		this.sdLoaderThread = new SDLoaderThread(initSocket);

		sdLoaderThread.start();

		log.info("SDLoader startup in " + (System.currentTimeMillis() - t)
				+ " ms.");

		isRunnnig = true;

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.AFTER_START, this));
	}

	protected void initConfig() {
		// init home
		String homeDir = getConfig(KEY_SDLOADER_HOME);
		if (homeDir == null) {
			homeDir = System.getProperty(KEY_SDLOADER_HOME);
			if (homeDir == null) {
				homeDir = System.getProperty("user.dir");
			}
		}
		homeDir = PathUtils.replaceFileSeparator(homeDir);
		homeDir = PathUtils.removeEndSlashIfNeed(homeDir);

		setConfig(KEY_SDLOADER_HOME, homeDir);
		log.info("SDLOADER_HOME=" + homeDir);

		// init webappDir
		String webappPath = getConfig(KEY_SDLOADER_WEBAPP_PATH);
		if (webappPath == null) {
			webappPath = System.getProperty(KEY_SDLOADER_WEBAPP_PATH);
			if (webappPath == null) {
				webappPath = WebConstants.WEBAPP_DIR_NAME;
			}
		}
		webappPath = PathUtils.replaceFileSeparator(webappPath);
		if (!PathUtils.isAbsolutePath(webappPath)) {
			// homeからの絶対パスに変換
			webappPath = homeDir + "/" + webappPath;
		}
		setConfig(KEY_SDLOADER_WEBAPP_PATH, webappPath);
		log.info(KEY_SDLOADER_WEBAPP_PATH + "=" + webappPath);

		// init jsp lib path
		setSystemConfigIfNoConfig(KEY_SDLOADER_JSP_LIBPATH);
		// init inMemoryWar
		setSystemConfigIfNoConfig(KEY_WAR_INMEMORY_EXTRACT);
		// init outside port
		setSystemConfigIfNoConfig(KEY_SDLOADER_USE_OUTSIDE_PORT);
	}

	protected void setSystemConfigIfNoConfig(String key) {
		String value = getConfig(key);
		if (value == null) {
			value = System.getProperty(key);
			if (value != null)
				setConfig(key, value);
		}
	}

	protected void initWebApp() {
		try {
			log.info("WebApplication initialize start.");
			webAppManager.init();
			log.info("WebApplication initialize success.");
		} catch (RuntimeException e) {
			log.error("WebApplication initialize fail.", e);
			throw e;
		}
	}

	protected ServerSocket initServerSocket() {
		ServerSocket initSocket = null;

		boolean useOutSidePort = BooleanUtil
				.toBoolean(getConfig(KEY_SDLOADER_USE_OUTSIDE_PORT));
		String portMessage = autoPortDetect ? "AutoDetect" : String
				.valueOf(port);
		log.info("Bind start. Port=" + portMessage + " useOutSidePort="
				+ useOutSidePort);
		try {
			int bindPort = autoPortDetect ? 0 : getPort();
			if (useOutSidePort) {
				initSocket = new ServerSocket(bindPort);
			} else {
				initSocket = new ServerSocket(bindPort, backLogNum, InetAddress
						.getByName("localhost"));
			}
			setPort(initSocket.getLocalPort());
			log.info("Bind success. Port=" + getPort());
		} catch (IOException ioe) {
			log.error("Bind fail. Port=" + portMessage, ioe);
			throw new RuntimeException(ioe);
		}
		return initSocket;
	}

	protected void initSocketProcessor() {
		socketProcessorPool = new HttpRequestProcessorPool(
				this.maxThreadPoolNum);
	}

	protected void initShutdown() {
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

	public void returnProcessor(HttpRequestProcessor processor) {
		socketProcessorPool.returnProcessor(processor);
	}

	class SDLoaderThread extends Thread {
		private boolean shutdown = false;

		private ServerSocket serverSocket;

		SDLoaderThread(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

		public void run() {
			while (!shutdown) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
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
				HttpRequestProcessor con = socketProcessorPool
						.borrowProcessor();
				con.process(socket, SDLoader.this);
			}
		}

		public synchronized void close() throws IOException {
			if (serverSocket != null) {
				SocketUtil.closeServerSocketNoException(serverSocket);
				serverSocket = null;
			}
			shutdown = true;
		}
	}

	/**
	 * ソケットを閉じ、サーバを終了します。
	 */
	public void stop() {
		if (!isRunnnig) {
			return;
		}

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.BEFORE_STOP, this));

		// destroy webapps
		webAppManager.close();
		// close socket
		try {
			sdLoaderThread.close();
		} catch (IOException ioe) {
			log.error(ioe);
		}
		isRunnnig = false;

		dispatcher.dispatchEvent(new LifecycleEvent<SDLoader>(
				LifecycleEvent.AFTER_STOP, this));
	}
}

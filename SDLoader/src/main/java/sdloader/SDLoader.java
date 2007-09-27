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

import sdloader.exception.IORuntimeException;
import sdloader.http.HttpRequestProcessor;
import sdloader.http.HttpRequestProcessorPool;
import sdloader.j2ee.WebAppManager;
import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.SocketUtil;
import sdloader.util.WebUtils;

/**
 * SDLoader ローカル動作のアプリケーションサーバー
 * 
 * @author c9katayama
 */
public class SDLoader {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(SDLoader.class);

	/**
	 * JSPコンパイル用のライブラリパス SDLoader呼び出し前にSystem.setPropertyでセットすると、このパスの
	 * ライブラリを利用します。
	 */
	public static final String SDLOADER_JSP_LIBPATH = "sdloader.jsp.libpath";

	/**
	 * SDLoaderのベースディレクトリパス このパス以下のwebappsディレクトに対して動作します。
	 */
	public static final String KEY_SDLOADER_HOME = "SDLOADER_HOME";

	private int maxThreadPoolNum = 5;

	private int backLogNum = -1;// -1でデフォルト値を使用する

	private int port = 30000;

	/**
	 * ポートが使用中の場合、使用できるポートを探すかどうか
	 */
	private boolean autoPortDetect = false;

	private String serverName = "SDLoader";

	private HttpRequestProcessorPool socketProcessorPool;

	private SDLoaderThread sdLoaderThread;

	private WebAppManager webAppManager;

	private Map config = new HashMap();

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

	public String getConfig(String key) {
		return (String) config.get(key);
	}

	public void setConfig(String key, String value) {
		config.put(key, value);
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
	 * ポートが使用中の場合、使用できるポートを探すかどうかをセットします。
	 * 
	 * @param key
	 * @return
	 */
	public void setAutoPortDetect(boolean autoPortDetect) {
		this.autoPortDetect = autoPortDetect;
	}

	public WebAppManager getWebAppManager() {
		return webAppManager;
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
	 * 最大スレッドプール数を設定します。 open前にセットしてください。
	 * 
	 * @param maxThreadPoolNum
	 */
	public void setMaxThreadPoolNum(int maxThreadPoolNum) {
		this.maxThreadPoolNum = maxThreadPoolNum;
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
	 * Listenするポート番号を設定します。 open前にセットしてください。
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
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
	 * サーバ名を設定します。レスポンスのServerヘッダーに この名前が入ります。
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * ソケットをオープンし、サーバを開始します。
	 */
	public void open() {
		ServerSocket initSocket = null;
		long t = System.currentTimeMillis();

		log.info("Bind start.Port=" + port);
		bindToFreePort();
		try {
			initSocket = new ServerSocket(getPort(), backLogNum, InetAddress
					.getByName("127.0.0.1"));
			log.info("Bind success.port=" + port);
		} catch (IOException ioe) {
			log.error("Bind fail.Port=" + port, ioe);
			throw new RuntimeException(ioe);
		}

		init();

		this.sdLoaderThread = new SDLoaderThread(initSocket);

		sdLoaderThread.start();

		log.info("SDLoader startup in " + (System.currentTimeMillis() - t)
				+ " ms.");
	}

	protected void bindToFreePort() {
		if (!isAutoPortDetect()) {
			return;
		}
		while (true) {
			try {
				port = SocketUtil.findFreePort();
				if (port < Constants.MAX_PORT_ADDRESS) {
					break;
				}
			} catch (IORuntimeException expected) {
				log.info(expected.getMessage());
			}
		}
	}

	/**
	 * ソケットを閉じ、サーバを終了します。
	 */
	public void close() {
		// destroy webapps
		webAppManager.close();
		// close socket
		try {
			sdLoaderThread.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void init() {
		initConfig();
		initWebApp();
		initSocketProcessor();
		initShutdown();
	}

	protected void initConfig() {
		String homeDir = System.getProperty(SDLoader.KEY_SDLOADER_HOME);
		if (homeDir == null)
			homeDir = System.getProperty("user.dir");// +"/sdloader";
		homeDir = WebUtils.replaceFileSeparator(homeDir);
		if (homeDir.endsWith("/"))
			homeDir = homeDir.substring(0, homeDir.length() - 1);
		log.info("SDLOADER_HOME=" + homeDir);

		setConfig(SDLoader.KEY_SDLOADER_HOME, homeDir);
	}

	protected void initWebApp() {
		try {
			log.info("WebApplication initialize start.");
			webAppManager = new WebAppManager(this);
			webAppManager.init();
			log.info("WebApplication initialize success.");
		} catch (RuntimeException e) {
			log.error("WebApplication initialize fail.", e);
			throw e;
		}
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
					sdloader.close();
				} catch (Throwable e) {
					log.error("SDLoader close fail.", e);
				}
			};
		});
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
					if (shutdown)
						break;

					log.error("Socket accept error.", ioe);
					continue;
				}
				HttpRequestProcessor con = socketProcessorPool
						.borrowProcessor();
				con.process(socket, SDLoader.this);
			}
		}

		public void close() throws IOException {
			shutdown = true;
			serverSocket.close();
			serverSocket = null;
		}
	}
}

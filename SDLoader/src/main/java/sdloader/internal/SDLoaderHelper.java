package sdloader.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

import sdloader.SDLoader;
import sdloader.javaee.constants.JavaEEConstants;
import sdloader.log.SDLoaderLog;
import sdloader.util.IOUtil;

public class SDLoaderHelper {

	private static final String SSL_KEY_STORE_PATH = "sdloader/resource/ssl/SDLoader.keystore";
	private static final String SSL_KEY_STORE_PASSWORD = "SDLoader";

	@SuppressWarnings("unused")
	private SDLoader sdLoader;

	public SDLoaderHelper(SDLoader loader) {
		this.sdLoader = loader;
	}

	public void printInitMessage(SDLoaderLog log) {
		String message = "Detect ServletAPI["
				+ JavaEEConstants.SERVLETAPI_MAJOR_VERSION + "."
				+ JavaEEConstants.SERVLETAPI_MINOR_VERSION + "]";
		if (JavaEEConstants.JSP_MAJOR_VERSION != null) {
			message += " JSP[" + JavaEEConstants.JSP_MAJOR_VERSION + "."
					+ JavaEEConstants.JSP_MINOR_VERSION + "]";
		} else {
			message += " JSP[NOT SUPPORT]";
		}
		log.info(message);
	}

	public ServerSocket createServerSocket(int bindPort, boolean sslEnable,
			boolean useOutSidePort) throws IOException {
		if (sslEnable) {
			return IOUtil.createSSLServerSocket(bindPort, useOutSidePort,
					SSL_KEY_STORE_PATH, SSL_KEY_STORE_PASSWORD);
		} else {
			return IOUtil.createServerSocket(bindPort, useOutSidePort);
		}
	}

	public void sendStopCommand(int port) {
		try {
			URL stopUrl = new URL("http://localhost:" + port
					+ "/sdloader-command/stop");
			HttpURLConnection urlcon = (HttpURLConnection) stopUrl
					.openConnection();
			urlcon.setRequestMethod("POST");
			urlcon.setUseCaches(false);
			urlcon.setConnectTimeout(100);
			urlcon.connect();
			urlcon.getInputStream();
			Thread.sleep(100);
			return;
		} catch (Exception ioe) {
		}
	}
}

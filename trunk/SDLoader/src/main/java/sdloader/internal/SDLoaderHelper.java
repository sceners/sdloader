package sdloader.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

import sdloader.SDLoader;
import sdloader.constants.JavaEEConstants;
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
			boolean useOutSidePort, boolean reuse) throws IOException {
		if (sslEnable) {
			return IOUtil.createSSLServerSocket(bindPort, useOutSidePort,
					SSL_KEY_STORE_PATH, SSL_KEY_STORE_PASSWORD, reuse);
		} else {
			return IOUtil.createServerSocket(bindPort, useOutSidePort, reuse);
		}
	}

	public boolean tryConnectAndSendStopCommand(int port) {
		InputStream is = null;
		HttpURLConnection urlcon = null;
		try {
			try {
				URL stopUrl = new URL("http://localhost:" + port
						+ "/sdloader-command/stop");
				urlcon = (HttpURLConnection) stopUrl.openConnection();
				urlcon.setRequestMethod("POST");
				urlcon.setUseCaches(false);
				urlcon.setConnectTimeout(1000);
				urlcon.setReadTimeout(1000);
				urlcon.setDoInput(true);
				urlcon.setDoOutput(true);
			} catch (Exception e) {
				return false;
			}
			try {
				urlcon.connect();
				is = urlcon.getInputStream();
				return true;
			} catch (Throwable ioe) {
				return false;
			}
		} finally {
			IOUtil.closeNoException(is);
			IOUtil.closeHttpUrlConnectionNoException(urlcon);
		}
	}
}

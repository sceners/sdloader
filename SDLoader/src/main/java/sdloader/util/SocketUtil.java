package sdloader.util;

import java.io.IOException;
import java.net.ServerSocket;

import sdloader.exception.IORuntimeException;

/**
 * @author shot
 */
public class SocketUtil {

	public static int findFreePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(0);
            return ss.getLocalPort();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeServerSocketNoException(ss);
        }
    }

    static void closeServerSocketNoException(final ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}

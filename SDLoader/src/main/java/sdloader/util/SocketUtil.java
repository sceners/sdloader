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
package sdloader.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import sdloader.exception.IORuntimeException;

/**
 * @author shot
 */
public class SocketUtil {

	public static int findFreePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(0,1,InetAddress.getByName("127.0.0.1"));
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

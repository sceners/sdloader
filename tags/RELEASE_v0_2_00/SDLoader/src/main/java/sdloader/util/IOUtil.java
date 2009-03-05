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

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author c9katayama
 */
public class IOUtil {

	public static boolean forceRemoveDirectory(File directory) {
		if (!directory.exists()) {
			return true;
		}

		File[] files = directory.listFiles();
		if (files == null) {
			return directory.delete();
		}
		boolean result = true;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				if (forceRemoveDirectory(file) == false) {
					result = false;
				}
			} else {
				if (file.delete() == false) {
					result = false;
				}
			}
		}
		if (directory.delete() == false) {
			result = false;
		}
		return result;
	}

	public static void flushNoException(Flushable flushable) {
		if (flushable != null) {
			try {
				flushable.flush();
			} catch (IOException ioe) {
				return;
			}
		}
	}

	public static void closeNoException(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ioe) {
				return;
			}
		}
	}

	public static void read(int bps, byte[] data, InputStream is)
			throws IOException {
		if (bps <= 0) {
			is.read(data, 0, data.length);
		} else {
			int bitPerSec = (int) (bps / 8.0 / 4.0);
			bitPerSec = Math.max(1, bitPerSec);
			while (true) {
				int offset = 0;
				int size = data.length;
				while (true) {
					if (offset + bitPerSec >= size) {
						is.read(data, offset, size - offset);
						return;
					} else {
						is.read(data, offset, bitPerSec);
						offset += bitPerSec;
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {

					}
				}
			}
		}
	}

	public static void write(int bps, byte[] data, OutputStream os)
			throws IOException {
		if (data == null || data.length == 0) {
			return;
		}
		if (bps <= 0) {
			os.write(data);
		} else {
			int bitPerSec = (int) (bps / 8.0 / 4.0);
			bitPerSec = Math.max(1, bitPerSec);
			while (true) {
				int offset = 0;
				int size = data.length;
				while (true) {
					if (offset + bitPerSec >= size) {
						os.write(data, offset, size - offset);
						return;
					} else {
						os.write(data, offset, bitPerSec);
						offset += bitPerSec;
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {

					}
				}
			}
		}
	}

	public static void closeServerSocketNoException(
			final ServerSocket serverSocket) {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}
	}

	public static void closeSocketNoException(final Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public static ServerSocket createServerSocket(int bindPort,
			boolean useOutSidePort) throws IOException {
		ServerSocket socket = new ServerSocket();
		try {
			if (useOutSidePort) {
				socket.bind(new InetSocketAddress(bindPort));
			} else {
				socket.bind(new InetSocketAddress(InetAddress
						.getByName("localhost"), bindPort));
			}
			return socket;
		} catch (IOException ioe) {
			closeServerSocketNoException(socket);
			throw ioe;
		}
	}

	public static FileFilter IGNORE_DIR_FILEFILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() && !file.getName().equals("CVS")
					&& !file.getName().startsWith(".");
		}
	};
	public static FileFilter WAR_FILEFILETR = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".war");
		}
	};
	public static FileFilter XML_FILEFILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".xml");
		}
	};
	public static FileFilter JAR_ZIP_FILEFILTER = new FileFilter() {
		public boolean accept(File pathname) {
			if (pathname.getName().endsWith(".jar")
					|| pathname.getName().endsWith(".zip"))
				return true;
			else
				return false;
		}
	};
}

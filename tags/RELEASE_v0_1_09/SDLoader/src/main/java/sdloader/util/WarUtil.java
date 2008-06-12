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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
/**
 * WarUtil
 * @author c9katayama
 */
public class WarUtil {

	private static SDLoaderLog log = SDLoaderLogFactory.getLog(WarUtil.class);
	/**
	 * Warの名前を返します。
	 * @param warFileName
	 * @return
	 */
	public static String getArchiveName(final String warFileName) {
		return warFileName.substring(0, warFileName.length() - ".war".length());
	}
	/**
	 * WARファイルを解凍します。
	 * 
	 * @param warFile
	 * @param directory
	 * @throws IOException
	 */
	public static void extractWar(File warFile, File directory)
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
}

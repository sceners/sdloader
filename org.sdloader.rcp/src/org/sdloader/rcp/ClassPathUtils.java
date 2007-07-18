package org.sdloader.rcp;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassPathUtils {

	public static String createArchiveClassPathString(String targetDir)
			throws MalformedURLException {
		URL[] urls = createArchiveClassPaths(targetDir);
		if (urls == null)
			return null;
		else {
			StringBuffer path = new StringBuffer();
			for (int i = 0; i < urls.length; i++) {
				String libPath = urls[i].toString();
				if (libPath.startsWith("file:/"))
					libPath = libPath.substring("file:/".length());

				if (i == 0)
					path.append(libPath);
				else
					path.append(";" + libPath);
			}
			return path.toString();
		}
	}

	public static URL[] createArchiveClassPaths(String targetDir)
			throws MalformedURLException {
		File libDir = new File(targetDir);
		if (!libDir.exists())
			return null;

		List urlList = new ArrayList();

		File[] libs = libDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".jar")
						|| pathname.getName().endsWith(".zip"))
					return true;
				else
					return false;
			}
		});
		if (libs != null) {
			for (int i = 0; i < libs.length; i++) {
				String libPath = libs[i].getAbsolutePath();
				libPath = libPath.replace('\\', '/');
				urlList.add(new URL("file:///" + libPath));
			}
		}

		File[] dirs = libDir.listFiles();
		if (dirs != null) {
			for (int i = 0; i < dirs.length; i++) {
				if (dirs[i].isDirectory()) {
					URL[] urls = createArchiveClassPaths(dirs[i]
							.getAbsolutePath());
					if (urls != null) {
						for (int j = 0; j < urls.length; j++)
							urlList.add(urls[j]);
					}
				}
			}
		}

		return (URL[]) urlList.toArray(new URL[] {});
	}
}

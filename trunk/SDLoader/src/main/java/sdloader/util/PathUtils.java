package sdloader.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class PathUtils {
	/**
	 * ベースパスに対して相対パスを解決します。
	 * @param basePath
	 * @param path
	 * @return
	 */
	public static String computeRelativePath(String basePath,String path) {
		basePath = basePath.substring(0,basePath.lastIndexOf("/"));
		return jointPathWithSlash(basePath,path);
	}
	/**
	 * 2つのパスを"/"で連結します。
	 */
	public static String jointPathWithSlash(String path1, String path2) {
		path1 = removeEndSlashIfNeed(path1);
		path2 = removeStartSlashIfNeed(path2);
		return path1 + "/" + path2;
	}

	public static String appendStartSlashIfNeed(final String path) {
		if (path != null && !startsWithSlash(path)) {
			return "/" + path;
		}
		return path;
	}

	public static String appendEndSlashIfNeed(final String path) {
		if (path != null && !endsWithSlash(path)) {
			return path + "/";
		}
		return path;
	}

	public static String removeStartSlashIfNeed(final String path) {
		if (path != null && startsWithSlash(path)) {
			return path.substring(1, path.length());
		}
		return path;
	}

	public static String removeEndSlashIfNeed(final String path) {
		if (path != null && path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static boolean startsWithSlash(final String path) {
		if (isEmpty(path)) {
			return false;
		}
		return path.indexOf("/") == 0;
	}

	public static boolean endsWithSlash(final String path) {
		if (isEmpty(path)) {
			return false;
		}
		return path.lastIndexOf("/") == path.length() - 1;
	}

	private static boolean isEmpty(String value) {
		return (value == null || value.trim().length() == 0);
	}

	/**
	 * パス中の\\を/に置き換えます。
	 * 
	 * @param filepath
	 * @return
	 */
	public static final String replaceFileSeparator(String path) {
		return path.replace('\\', '/');
	}

	/**
	 * 絶対パスかどうか
	 * 
	 * @param path
	 * @return
	 */
	public static final boolean isAbsolutePath(String path) {
		String testPath = replaceFileSeparator(path);
		if (testPath.startsWith("/") || testPath.indexOf(":") != -1) {
			return true;
		} else {
			return false;
		}
	}

	public static URL file2URL(String filePath) {
		return file2URL(new File(filePath));
	}

	public static URL file2URL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static File url2File(String urlPath) {
		try {
			return new File(new URI(urlPath));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static File url2File(URL url) {
		try {
			return new File(new URI(url.toExternalForm()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}

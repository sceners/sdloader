package sdloader.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class PathUtils {

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
	 * @param path
	 * @return
	 */
	public static final boolean isAbsolutePath(String path){
		String testPath = replaceFileSeparator(path);
		if(testPath.startsWith("/") || testPath.indexOf(":") != -1){
			return true;
		}else{
			return false;
		}
	}
	public static URL file2URL(String filePath){
		return file2URL(new File(filePath));
	}
	public static URL file2URL(File file){
		try{
			return file.toURI().toURL();
		}catch(MalformedURLException e){
			throw new RuntimeException(e);
		}
	}
	public static File url2File(String urlPath){
		try{
			return new File(new URI(urlPath));
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
	}
	public static File url2File(URL url){
		try{
			return new File(new URI(url.toExternalForm()));
		}catch(URISyntaxException e){
			throw new RuntimeException(e);
		}
	}
}

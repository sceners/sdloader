package sdloader.util;

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
}

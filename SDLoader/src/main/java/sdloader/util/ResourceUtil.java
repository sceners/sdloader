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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * リソース用Util
 * @author c9katayama
 */
public class ResourceUtil {
	
	public static String stripFirstProtocolPart(String path){
		return path.substring(path.indexOf(":")+1,path.length());
	}
	public static URL createURL(final String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	public static URL createURL(final URL baseURL,String relativeURL) {
		try {
			String protocol = baseURL.getProtocol();
			relativeURL = relativeURL.replace("\\","/");
			//TODO
			if(protocol.startsWith("file")){
				if(relativeURL.startsWith("/")){
					relativeURL = relativeURL.substring(1,relativeURL.length());
				}
				return new URL(baseURL,relativeURL);				
			}else{
				//TODO
				String baseArchivePath = baseURL.toExternalForm();
				if(baseArchivePath.endsWith("/")){
					baseArchivePath = baseArchivePath.substring(0,baseArchivePath.length()-1);
				}
				if(baseArchivePath.endsWith("!")){
					baseArchivePath = baseArchivePath.substring(0,baseArchivePath.length()-1);
				}				
				if(!relativeURL.startsWith("/")){
					relativeURL = "/" + relativeURL;
				}
				return new URL(baseArchivePath+"!"+relativeURL);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	public static boolean isAbsoluteURL(String url){
		return (url.indexOf(":/") != -1); 
	}
	public static boolean isResourceExist(URL resource){
		InputStream is = null;
		try{
			is = resource.openStream();	
			return (is!=null);
		}catch(Exception ioe){
			return false;
		}finally{
			if(is != null){
				try{
					is.close();
					is = null;
				}catch(IOException ignore){					
				}
			}
		}
	}
	public static boolean isFileResource(URL resource){
		return !resource.toExternalForm().endsWith("/");
	}
	
	public static boolean isDirectoryResource(URL resource){
		return resource.toExternalForm().endsWith("/");
	}
	/**
	 * リソースのストリームを取得します。
	 * 
	 * @param path
	 * @param caller
	 * @return
	 */
	public static InputStream getResourceAsStream(String path, Class<?> caller) {

		String resource = path;
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}

		InputStream is = null;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}
		}
		if (is == null)
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(resource);
		if (is == null)
			is = caller.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.class.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.getSystemResourceAsStream(resource);

		return is;
	}
}

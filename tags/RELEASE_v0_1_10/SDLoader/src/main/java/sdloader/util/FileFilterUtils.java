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
import java.io.FileFilter;
/**
 * FileFilterUtils
 *
 * @author c9katayama
 */
public class FileFilterUtils {

	public static FileFilter IGNORE_DIR_FILEFILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() && !file.getName().equals("CVS")
					&& !file.getName().startsWith(".");
		}
	};
	
	public static FileFilter WAR_FILEFILETR =new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".war");
		}
	};
	
	public static FileFilter XML_FILEFILTER = new FileFilter(){
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

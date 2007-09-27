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

import java.io.InputStream;

public class ResourceUtil {

	public static InputStream getResourceByClassPath(String path,Class caller) {

		String resource = path;
		if (resource.startsWith("/")){
			resource = resource.substring(1);
		}
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);	
		if (is == null)
			is = caller.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.class.getResourceAsStream(path);
		if (is == null)
			is = ClassLoader.getSystemResourceAsStream(resource);

		return is;
	}
}

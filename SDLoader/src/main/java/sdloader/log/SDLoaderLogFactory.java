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
package sdloader.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import sdloader.util.ResourceUtil;

/**
 * ログファクトリークラス
 * 
 * @author c9katayama
 */
public class SDLoaderLogFactory {

	static {
		String configPath = System.getProperty("java.util.logging.config.file");
		if (configPath == null || configPath.length() == 0) {
			InputStream is = ResourceUtil.getResourceAsStream(
					"sdloader-logging.properties", SDLoaderLogFactory.class);
			if (is == null) {
				is = ResourceUtil.getResourceAsStream("sdloader.properties",
						SDLoaderLogFactory.class);
			}
			if (is != null) {
				try {
					LogManager.getLogManager().readConfiguration(is);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	private SDLoaderLogFactory() {
	}

	public static SDLoaderLog getLog(Class<?> c) {
		return new SDLoaderLogJDKLoggerImpl(c);
	}
}

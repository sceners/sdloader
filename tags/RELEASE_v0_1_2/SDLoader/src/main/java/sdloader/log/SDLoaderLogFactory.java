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

/**
 * ログファクトリークラス
 * 
 * @author c9katayama
 */
public class SDLoaderLogFactory {
	
	private static boolean isCommonsSupport;
	
	static{
		try{
			Class.forName("org.apache.commons.logging.LogFactory");
			isCommonsSupport = true;
		}catch(Throwable e){
			isCommonsSupport = false;
		}
	}
	private SDLoaderLogFactory() {
	}

	public static SDLoaderLog getLog(Class c) {
		if(isCommonsSupport)
			return new SDLoaderLogCommonsLoggingImp(c);
		else
			return new SDLoaderLogSystemOutImp(c);
	}
}

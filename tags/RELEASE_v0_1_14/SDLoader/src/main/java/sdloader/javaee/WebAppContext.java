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
package sdloader.javaee;
/**
 * Webアプリケーションのコンテキスト設定データ
 * c9katayama
 */
import java.io.File;
import java.net.URL;

import sdloader.util.Assertion;
import sdloader.util.PathUtils;

public class WebAppContext {
	
	private URL[] docBase;// アプリケーションのドキュメントルート
	private String contextPath;// アプリケーションのコンテキストパス(/から始まるパス）

	public WebAppContext(final String contextPath,final String... dirPath) {
		docBase = new URL[dirPath.length];
		for(int i = 0;i < dirPath.length;i++){
			docBase[i] = PathUtils.file2URL(dirPath[i]);
		}
		this.contextPath = PathUtils.appendStartSlashIfNeed(contextPath);
	}
	public WebAppContext(final String contextPath,final File... dir) {
		docBase = new URL[dir.length];
		for(int i = 0;i < dir.length;i++){
			docBase[i] = PathUtils.file2URL(dir[i]);
		}
		this.contextPath = PathUtils.appendStartSlashIfNeed(contextPath);
	}	
	public WebAppContext(final String contextPath,final URL... docBase) {		
		this.docBase = Assertion.notNull(docBase);
		this.contextPath = PathUtils.appendStartSlashIfNeed(contextPath);
	}

	public String getContextPath() {
		return contextPath;
	}
	public URL[] getDocBase() {
		return docBase;
	}
}
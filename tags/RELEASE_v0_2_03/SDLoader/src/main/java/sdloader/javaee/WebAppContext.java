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
 * Webアプリケーションのコンテキスト設定データ c9katayama
 */
import java.io.File;
import java.net.URL;

import sdloader.javaee.webxml.WebXml;
import sdloader.util.Assertion;
import sdloader.util.PathUtil;

public class WebAppContext {

	/**
	 * アプリケーションのドキュメントルート
	 * 
	 * <pre>
	 * このパス上から、リソースやクラスをロードします。
	 * web.xmlに関しては、このパス上で最初に見つかった/WEB-INF/web.xmlを使用します。
	 * ただし、WebXmlがセットされている場合は、それを利用します。
	 * </pre>
	 */
	private URL[] docBase;
	/**
	 * アプリケーションのコンテキストパス(/から始まるパス）
	 */
	private String contextPath;
	/**
	 * これがセットされると、パス上のweb.xmlが読み込まれず、アプリケーションの動作にこのwebXmlが使用されます。
	 */
	private WebXml webXml;

	public WebAppContext(final String contextPath,
			final String... docBaseDirOrWarFile) {
		setContextPath(contextPath);
		setDocBase(docBaseDirOrWarFile);
	}

	public WebAppContext(final String contextPath,
			final File... docBaseDirOrWarFile) {
		setContextPath(contextPath);
		setDocBase(docBaseDirOrWarFile);
	}

	public WebAppContext(final String contextPath,
			final URL... docBaseDirOrWarFile) {
		setContextPath(contextPath);
		setDocBase(docBaseDirOrWarFile);
	}

	/**
	 * コンテキストパスを返します。
	 * 
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * コンテキストパスをセットします。
	 * 
	 * @return
	 */
	public void setContextPath(String contextPath) {
		Assertion.notNull(contextPath);
		this.contextPath = PathUtil.appendStartSlashIfNeed(contextPath);
	}

	/**
	 * ドキュメントルートをセットします。
	 * 
	 * <pre>
	 * ドキュメントルートとなるディレクトリ、もしくはwarファイルを指定します。 
	 * warファイルは作業用ディレクトリに展開されます。
	 * </pre>
	 * 
	 * @param docBaseDirOrWarFile
	 */
	public void setDocBase(String... docBaseDirOrWarFile) {
		Assertion.notNull(docBaseDirOrWarFile);
		docBase = new URL[docBaseDirOrWarFile.length];
		for (int i = 0; i < docBaseDirOrWarFile.length; i++) {
			docBase[i] = PathUtil.file2URL(docBaseDirOrWarFile[i]);
		}
	}

	/**
	 * ドキュメントルートをセットします。
	 * 
	 * <pre>
	 * ドキュメントルートとなるディレクトリ、もしくはwarファイルを指定します。 
	 * warファイルは作業用ディレクトリに展開されます。
	 * </pre>
	 * 
	 * @param docBaseDirOrWarFile
	 */
	public void setDocBase(File... docBaseDirOrWarFile) {
		Assertion.notNull(docBaseDirOrWarFile);
		docBase = new URL[docBaseDirOrWarFile.length];
		for (int i = 0; i < docBaseDirOrWarFile.length; i++) {
			docBase[i] = PathUtil.file2URL(docBaseDirOrWarFile[i]);
		}
	}

	/**
	 * ドキュメントルートをセットします。
	 * 
	 * <pre>
	 * ドキュメントルートとなるディレクトリ、もしくはwarファイルを指定します。 
	 * warファイルは作業用ディレクトリに展開されます。
	 * </pre>
	 * 
	 * @param docBaseDirOrWarFile
	 */
	public void setDocBase(URL... docBaseDirOrWarFile) {
		this.docBase = Assertion.notNull(docBaseDirOrWarFile);
	}

	/**
	 * ドキュメントルートを返します。
	 * 
	 * @return
	 */
	public URL[] getDocBase() {
		return docBase;
	}

	/**
	 * WebXmlをセットします。
	 * 
	 * @param webXml
	 */
	public void setWebXml(WebXml webXml) {
		this.webXml = webXml;
	}

	/**
	 * WebXmlを返します。
	 * 
	 * @return
	 */
	public WebXml getWebXml() {
		return webXml;
	}
}
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
package sdloader.javaee.impl;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import sdloader.javaee.SessionManager;
import sdloader.util.CollectionsUtil;
import sdloader.util.DisposableUtil;
import sdloader.util.DisposableUtil.Disposable;

/**
 * セッション管理の実装クラス セッションID単位でセッションを管理します。 通常のJ2EEのセッション管理方式です。
 * 
 * @author c9katayama
 */
public class SessionManagerImpl extends SessionManager {

	protected Map<String, HttpSessionImpl> sessionMap = CollectionsUtil
			.newHashMap();

	public SessionManagerImpl() {
		DisposableUtil.add(new Disposable() {
			public void dispose() {
				sessionMap.clear();
			}

		});
	}

	public HttpSession getSession(String sessionId, boolean createNew,
			ServletContext servletContext) {
		HttpSessionImpl session = (HttpSessionImpl) sessionMap.get(sessionId);
		if (session != null) {
			if (!session.isInvalidate()) {
				return session;
			} else {
				sessionMap.remove(sessionId);
				if (createNew) {
					return createNewSession(servletContext);
				} else {
					return null;
				}
			}
		} else {
			if (createNew) {
				return createNewSession(servletContext);
			} else {
				return null;
			}
		}
	}

	private HttpSession createNewSession(ServletContext servletContext) {
		String sessionId = createNewSessionId();
		HttpSessionImpl ses = new HttpSessionImpl(sessionId);
		ses.setServletContext(servletContext);
		sessionMap.put(sessionId, ses);
		return ses;
	}
}

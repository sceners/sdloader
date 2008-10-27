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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import sdloader.javaee.SessionManager;
import sdloader.util.DisposableUtil;
import sdloader.util.DisposableUtil.Disposable;

/**
 * セッションIDに係わらず、すべてのリクエストに対して 同一のセッションを返します。 プロセスの異なるIEなどでセッション共有できます。
 * 
 * @author c9katayama
 * @author shot
 */
public class SessionManagerSharedSessionImpl extends SessionManager {

	private static HttpSessionImpl session;

	public SessionManagerSharedSessionImpl() {
		DisposableUtil.add(new Disposable() {
			public void dispose() {
				session.dispose();
				session = null;
			}

		});
	}

	public HttpSession getSession(String sessionId, boolean createNew,
			ServletContext servletContext) {
		if (session != null) {
			if (!session.isInvalidate()) {
				return session;
			} else {
				session = null;
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
		session = ses;
		return ses;
	}
}

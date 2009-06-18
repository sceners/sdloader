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

import javax.servlet.http.HttpSession;

import sdloader.javaee.InternalWebApplication;
import sdloader.javaee.SessionManager;
import sdloader.util.CollectionsUtil;
import sdloader.util.DisposableUtil;
import sdloader.util.DisposableUtil.Disposable;

/**
 * セッション管理の実装クラス セッションID単位でセッションを管理します。 通常のJ2EEのセッション管理方式です。
 * 
 * @author c9katayama
 */
public class SessionManagerImpl extends SessionManager implements Disposable {

	protected Map<String, HttpSessionImpl> sessionMap = CollectionsUtil
			.newConcurrentHashMap();

	public SessionManagerImpl() {
		DisposableUtil.add(this);
	}

	@Override
	public synchronized HttpSession getSession(String sessionId,
			boolean createNew, InternalWebApplication webApplication) {
		if (sessionId == null) {
			return createNewSession(webApplication);
		}
		HttpSessionImpl session = (HttpSessionImpl) sessionMap.get(sessionId);
		if (session != null) {
			if (!session.isInvalidate()) {
				return session;
			} else {
				session.invalidate();
				sessionMap.remove(sessionId);
				if (createNew) {
					return createNewSession(webApplication);
				} else {
					return null;
				}
			}
		} else {
			if (createNew) {
				return createNewSession(webApplication);
			} else {
				return null;
			}
		}
	}

	protected HttpSession createNewSession(InternalWebApplication webApplication) {
		String sessionId = createNewSessionId();
		HttpSessionImpl ses = new HttpSessionImpl(webApplication, sessionId);
		sessionMap.put(sessionId, ses);
		return ses;
	}

	public void dispose() {
		for (HttpSessionImpl session : sessionMap.values()) {
			session.invalidate();
		}
		sessionMap.clear();
	}
}

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
package sdloader.j2ee.imp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import sdloader.j2ee.SessionManager;
/**
 * セッション管理の実装クラス
 * セッションID単位でセッションを管理します。
 * 通常のJ2EEのセッション管理方式です。
 * @author c9katayama
 */
public class SessionManagerImp extends SessionManager{

	private Map sessionMap = new HashMap();

	public HttpSession getSession(String sessionId, boolean createNew,
			ServletContext servletContext) {
		HttpSessionImp session = (HttpSessionImp) sessionMap.get(sessionId);
		if (session != null) {
			if (!session.isInvalidate())
				return session;
			else {
				if (createNew)
					return createNewSession(servletContext);
				else
					return null;
			}
		} else {
			if (createNew)
				return createNewSession(servletContext);
			else
				return null;
		}
	}

	private HttpSession createNewSession(ServletContext servletContext) {
		String sessionId = createNewSessionId();
		HttpSessionImp ses = new HttpSessionImp(sessionId);
		ses.setServletContext(servletContext);
		sessionMap.put(sessionId, ses);

		return ses;
	}
}

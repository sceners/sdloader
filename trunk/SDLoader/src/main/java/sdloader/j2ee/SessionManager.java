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
package sdloader.j2ee;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import sdloader.j2ee.imp.SessionManagerSharedSessionImp;
import sdloader.org.apache.commons.codec.binary.Base64;

/**
 * セッション管理クラス
 * @author c9katayama
 */
public abstract class SessionManager {

	//TODO 
	private static SessionManager manager = new SessionManagerSharedSessionImp(); 
	
	private static long sessionIdSeed = System.currentTimeMillis()	+ (int) Math.random() * 1000;

	private static final MessageDigest digest;
	
	static {
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	protected SessionManager() {
		super();
	}

	public static SessionManager getInstance() {
		return manager;
	}

	public abstract HttpSession getSession(String sessionId, boolean createNew,
			ServletContext servletContext) ;

	protected String createNewSessionId(){
		String sessionId = null;
		synchronized (SessionManager.class) {
			long sesIdSeed = ++sessionIdSeed;
			byte[] digestId = digest.digest(Long.toString(sesIdSeed).getBytes());
			sessionId = new String(Base64.encodeBase64(digestId));
		}
		return sessionId;
	}
}
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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import sdloader.internal.SDLoaderInitializer;
import sdloader.org.apache.commons.codec.binary.Base64;
import sdloader.util.MessageDigestUtil;

/**
 * セッション管理クラス
 * 
 * @author c9katayama
 * @author shot
 */
public abstract class SessionManager {

	private static SessionManager manager = SDLoaderInitializer
			.createSessionManager();

	private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();

	private static long sessionIdSeed = System.currentTimeMillis()
			+ (int) RANDOM_GENERATOR.nextInt() * 1000;

	private static final MessageDigest digest;

	protected static final Lock lock = new ReentrantLock();

	static {
		digest = MessageDigestUtil.createMessageDigest();
	}

	protected SessionManager() {
		super();
	}

	public static SessionManager getInstance() {
		return manager;
	}

	public abstract HttpSession getSession(String sessionId, boolean createNew,
			ServletContext servletContext);

	protected String createNewSessionId() {
		String sessionId = null;
		long sesIdSeed = ++sessionIdSeed;
		byte[] digestId = digest.digest(Long.toString(sesIdSeed).getBytes());
		sessionId = new String(Base64.encodeBase64(digestId));
		return sessionId;
	}

}
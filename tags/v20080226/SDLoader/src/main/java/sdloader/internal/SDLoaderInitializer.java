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
package sdloader.internal;

import sdloader.javaee.SessionManager;
import sdloader.javaee.impl.SessionManagerImpl;

/**
 * @author shot
 */
public abstract class SDLoaderInitializer {

	private static SessionManager sessionManager;
	
	//TODO SDLoaderの初期化処理の整理・分離
	
	//TODO もっと汎用的なロードの仕組みを作る
	public static SessionManager createSessionManager() {
		if(sessionManager != null) {
			return sessionManager;
		}
		return new SessionManagerImpl();
	}
	
	public static void setSessionManager(SessionManager sessionManager) {
		SDLoaderInitializer.sessionManager = sessionManager;
	}
}

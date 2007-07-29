package sdloader.internal;

import sdloader.j2ee.SessionManager;
import sdloader.j2ee.imp.SessionManagerSharedSessionImpl;

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
		return new SessionManagerSharedSessionImpl();
	}
	
	public static void setSessionManager(SessionManager sessionManager) {
		SDLoaderInitializer.sessionManager = sessionManager;
	}
}

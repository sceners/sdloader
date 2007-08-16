package sdloader.util;

import java.util.LinkedList;

/**
 * @author shot
 */
public final class DisposableUtil {

    protected static final LinkedList disposables = new LinkedList();

    public static synchronized void add(final Disposable disposable) {
        disposables.add(disposable);
    }

    public static synchronized void remove(final Disposable disposable) {
        disposables.remove(disposable);
    }

    public static synchronized void dispose() {
        while (!disposables.isEmpty()) {
            final Disposable disposable = (Disposable) disposables.removeLast();
            try {
                disposable.dispose();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
        disposables.clear();
    }

    public static interface Disposable {
    	
    	void dispose();
    }
    
}

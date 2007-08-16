package sdloader;

/**
 * @author shot
 * 
 * General start/stop event interface.
 */
public interface Lifecycle {

	void start();
	
	void stop();
	
	boolean isRunning();
}

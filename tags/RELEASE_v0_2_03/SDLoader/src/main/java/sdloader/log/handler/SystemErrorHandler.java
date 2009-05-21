/**
 * 
 */
package sdloader.log.handler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SystemErrorHandler extends ConsoleHandler {
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			super.publish(record);
		}
	}
}
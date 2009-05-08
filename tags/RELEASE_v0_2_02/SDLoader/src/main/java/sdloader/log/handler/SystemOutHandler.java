/**
 * 
 */
package sdloader.log.handler;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SystemOutHandler extends ConsoleHandler {
	@Override
	protected synchronized void setOutputStream(OutputStream out)
			throws SecurityException {
		super.setOutputStream(System.out);
	}

	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		if (record.getLevel().intValue() < Level.WARNING.intValue()) {
			super.publish(record);
		}
	}
}
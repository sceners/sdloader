package sdloader.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDKのLoggerを使ったLog実装
 * 
 * @author c9katayama
 */
public class SDLoaderLogJDKLoggerImpl implements SDLoaderLog {

	private Logger logger;

	public SDLoaderLogJDKLoggerImpl(Class<?> c) {
		logger = Logger.getLogger(c.getName());
	}

	protected String format(Object log, Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		if (log != null) {
			pw.print(log.toString());
		}
		if (t != null) {
			pw.println();
			t.printStackTrace(pw);
		}
		pw.flush();
		pw.close();
		return stringWriter.toString();
	}

	public void debug(Object log) {
		logger.config(format(log, null));
	}

	public void debug(Object log, Throwable t) {
		logger.config(format(log, t));
	}

	public void info(Object log) {
		logger.info(format(log, null));
	}

	public void info(Object log, Throwable t) {
		logger.info(format(log, t));
	}

	public void warn(Object log) {
		logger.warning(format(log, null));
	}

	public void warn(Object log, Throwable t) {
		logger.warning(format(log, t));
	}

	public void error(Object log) {
		logger.severe(format(log, null));
	}

	public void error(Object log, Throwable t) {
		logger.severe(format(log, t));
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	public void release() {
		logger = null;
	}
}

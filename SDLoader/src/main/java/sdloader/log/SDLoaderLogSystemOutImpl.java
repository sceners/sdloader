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
package sdloader.log;

/**
 * System.outを行うログ
 * 
 * @author c9katayama
 * @author shot
 */
public class SDLoaderLogSystemOutImpl implements SDLoaderLog {

	private String className;

	public SDLoaderLogSystemOutImpl(Class<?> c) {
		className = c.getName();
	}

	public void debug(Object log) {
		debug(log, null);
	}

	public void debug(Object log, Throwable t) {
		if (isDebugEnabled())
			log("DEBUG", log, t);
	}

	public void error(Object log) {
		error(log, null);
	}

	public void error(Object log, Throwable t) {
		log("ERROR", className + " " + log, t);
	}

	public void info(Object log) {
		info(log, null);
	}

	public void info(Object log, Throwable t) {
		if (isInfoEnabled())
			log("INFO", log, null);
	}

	public boolean isDebugEnabled() {
		// TODO DEBUGLEVEL
		return false;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isWarnEnabled() {
		return true;
	}

	public void warn(Object log) {
		warn(log, null);
	}

	public void warn(Object log, Throwable t) {
		if (isWarnEnabled())
			log("WARN", className + " " + log, t);
	}

	private void log(String level, Object log, Throwable t) {
		String logtext = "SDLoader:[" + level + "] ";
		if (log != null) {
			logtext += log.toString() + " ";
		}
		if (t != null) {
			logtext += t.getMessage();
			System.out.println(logtext);
			t.printStackTrace();
		} else {
			System.out.println(logtext);
		}
	}

	// TODO i18n
	public void release() {
		log("WARN", "logger released.", null);
	}

}

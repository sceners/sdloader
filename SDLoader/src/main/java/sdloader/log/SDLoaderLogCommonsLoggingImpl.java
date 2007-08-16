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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ログインターフェース
 * 
 * @author c9katayama
 * @author shot
 */
public class SDLoaderLogCommonsLoggingImpl implements SDLoaderLog {

	private Log logger;

	public SDLoaderLogCommonsLoggingImpl(Class c) {
		this.logger = LogFactory.getLog(c);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public void debug(Object log) {
		logger.debug(log);
	}

	public void debug(Object log, Throwable t) {
		logger.debug(log, t);
	}

	public void info(Object log) {
		logger.info(log);
	}

	public void info(Object log, Throwable t) {
		logger.info(log, t);
	}

	public void warn(Object log) {
		logger.warn(log);
	}

	public void warn(Object log, Throwable t) {
		logger.warn(log, t);
	}

	public void error(Object log) {
		logger.error(log);
	}

	public void error(Object log, Throwable t) {
		logger.error(log, t);
	}

	public void release() {
		LogFactory.releaseAll();
		logger = null;
	}
}

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
 */
public class SDLoaderLogCommonsLoggingImp implements SDLoaderLog{

	private Log logimp;

	public SDLoaderLogCommonsLoggingImp(Class c) {
		this.logimp = LogFactory.getLog(c);
	}

	public boolean isDebugEnabled() {
		return logimp.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logimp.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return logimp.isWarnEnabled();
	}

	public void debug(Object log) {
		logimp.debug(log);
	}

	public void debug(Object log, Throwable t) {
		logimp.debug(log, t);
	}

	public void info(Object log) {
		logimp.info(log);
	}

	public void info(Object log, Throwable t) {
		logimp.info(log, t);
	}

	public void warn(Object log) {
		logimp.warn(log);
	}

	public void warn(Object log, Throwable t) {
		logimp.warn(log, t);
	}

	public void error(Object log) {
		logimp.error(log);
	}

	public void error(Object log, Throwable t) {
		logimp.error(log, t);
	}
}

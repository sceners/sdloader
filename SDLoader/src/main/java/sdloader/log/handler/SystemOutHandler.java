/*
 * Copyright 2005-2010 the original author or authors.
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
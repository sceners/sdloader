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
package sdloader.javaee.impl;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import sdloader.util.FastByteArrayOutputStream;
import sdloader.util.IOUtil;

/**
 * ServletOutputStream実装クラス
 * 
 * @author c9katayama
 */
public class ServletOutputStreamImpl extends ServletOutputStream {
	private FastByteArrayOutputStream bout;

	private boolean close = false;

	public ServletOutputStreamImpl() {
		bout = new FastByteArrayOutputStream();
	}

	public void write(int b) throws IOException {
		bout.write(b);
	}

	public void flush() throws IOException {
		bout.flush();
	}

	public void close() throws IOException {
		bout.close();
		close = true;
	}

	public int getOutputSize() {
		return bout.getSize();
	}

	public FastByteArrayOutputStream getOutputData() {
		IOUtil.flushNoException(bout);
		return bout;
	}

	public boolean isClosed() {
		return close;
	}
}

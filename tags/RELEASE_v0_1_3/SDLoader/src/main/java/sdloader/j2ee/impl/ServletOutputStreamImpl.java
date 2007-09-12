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
package sdloader.j2ee.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * ServletOutputStream実装クラス
 * 
 * @author c9katayama
 */
public class ServletOutputStreamImpl extends ServletOutputStream {
	private ByteArrayOutputStream bout;

	private BufferedOutputStream bufOut;
	
	private boolean close = false;
	
	public ServletOutputStreamImpl() {
		bout = new ByteArrayOutputStream();
		bufOut = new BufferedOutputStream(bout);
	}

	public void write(int b) throws IOException {
		bufOut.write(b);
	}

	public void flush() throws IOException {
		bufOut.flush();
	}

	public void close() throws IOException {
		bufOut.close();
		close = true;
	}

	public byte[] getOutputData() {
		return bout.toByteArray();
	}
	public boolean isClosed(){
		return close;
	}
}

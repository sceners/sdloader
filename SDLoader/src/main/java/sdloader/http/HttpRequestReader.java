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
package sdloader.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

/**
 * HttpRequestReader
 * 
 * @author c9katayama
 */
public class HttpRequestReader {

	private static final char CR = '\r';
	private static final char LF = '\n';

	private InputStream inputStream;
	private boolean skipNextLF = true;

	public HttpRequestReader(InputStream is) {
		super();
		this.inputStream = is;
	}

	public String readHeaderLine() throws IOException {
		StringBuffer line = new StringBuffer();

		for (;;) {
			int readChar = inputStream.read();

			if (readChar < 0) {// eof
				throw new SocketException("EOF");
			}

			char c = (char) readChar;

			if (c == CR) {
				skipNextLF = true;// CRで終わった次のLFを読み込まない
				return line.toString();
			}
			if (c == LF) {				
				if (skipNextLF && line.length() == 0) {
					skipNextLF = false;
					continue;
				}
				// CRなしでLFが来るパターン（例外パターン）
				skipNextLF = false;
				return line.toString();
			}
			line.append(c);
		}
	}

	public void readBody(byte[] body) throws IOException {
		int mark = 0;
		if (skipNextLF) {
			int b = inputStream.read();
			if (((char) b) == LF) {
				// CRで終わったLFなので読み飛ばす
			} else if (body.length > 0) {
				body[0] = (byte) b;
				mark++;
			}
		}
		skipNextLF = false;

		int size = body.length;
		for (; mark < size; mark++) {
			body[mark] = (byte) inputStream.read();
		}
	}
}

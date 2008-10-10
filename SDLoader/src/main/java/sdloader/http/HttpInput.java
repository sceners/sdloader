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

/**
 * HttpInputStreamクラス
 * 
 * @author c9katayama
 */
public class HttpInput {

	private static final char CR = '\r';
	private static final char LF = '\n';

	private InputStream inputStream;
	private boolean skipFirstLF = true;

	public HttpInput(InputStream is) {
		super();
		this.inputStream = is;
	}

	public String readHeaderLine() throws IOException {
		StringBuffer s = new StringBuffer();
		for (;;) {
			int readChar = inputStream.read();

			if (readChar < 0) {// eof
				return s.toString();
			}

			char c = (char) readChar;

			if (c == CR) {
				skipFirstLF = true;// CRで終わった次のLFを読み込まない
				return s.toString();
			}
			if (c == LF) {
				if (skipFirstLF && s.length() == 0) {
					skipFirstLF = false;
					continue;
				} else {
					// CRなしでLFが来るパターン（例外パターン）
					skipFirstLF = false;
					return s.toString();
				}
			}
			s.append(c);
		}
	}

	public void readBody(byte[] body) throws IOException {
		if (body.length == 0) {
			return;
		}

		int b = inputStream.read();
		int mark = 0;
		if (skipFirstLF && ((char) b) == LF) {
			// CRで終わったLFなので読み飛ばす
		} else {
			body[0] = (byte) b;
			mark++;
		}
		skipFirstLF = false;

		int maxLength = body.length;
		for (; mark < maxLength; mark++) {
			body[mark] = (byte) inputStream.read();
		}
	}
}

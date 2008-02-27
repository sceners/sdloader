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
/**
 * HTTPリクエスト
 * ヘッダーパートとボディーパートを生成します。
 * @author c9katayama
 */
public class HttpRequest {
	
	private HttpInput input;
	
	private HttpRequestHeader header;
	private HttpRequestBody body;
	private HttpParameters parameters;
	
	public HttpRequest(HttpInput input) throws IOException{
		this.input = input;
		createHttpRequestHeader();
		if(header != null)
			creaheHttpRequestBody();
		parameters = new HttpParameters(header,body);
	}
	public HttpParameters getParameters(){
		return parameters;
	}
	public HttpRequestHeader getHeader(){
		return header;
	}
	public HttpRequestBody getBody() {
		return body;
	}
	private void createHttpRequestHeader() throws IOException {
		StringBuffer httpHeaderBuf = null;
		String line = null;
		
		while ((line = input.readHeaderLine()) != null) {
			line = line.trim();
			// 空白行で終了
			if (line.length() <= 0)
				break;

			if (httpHeaderBuf == null)
				httpHeaderBuf = new StringBuffer();

			httpHeaderBuf.append(line);
			httpHeaderBuf.append(HttpConst.CRLF_STRING);
		}
		if (httpHeaderBuf == null)
			return;

		header = new HttpRequestHeader(new String(httpHeaderBuf));
	}
	private void creaheHttpRequestBody() throws IOException {
		String contentLengthHeader = header
				.getHeader(HttpConst.CONTENTLENGTH);
		byte[] b = null;
		if (contentLengthHeader != null) {
			int contentLength = Integer.parseInt(contentLengthHeader);
			long trueContentLength = Long.parseLong(contentLengthHeader);
			if (trueContentLength > contentLength)
				throw new IllegalArgumentException("ContentLenght too long.Max size = "+ Integer.MAX_VALUE);

			b = new byte[contentLength];
			input.readBody(b);
		}
		body = new HttpRequestBody(b);
	}
}

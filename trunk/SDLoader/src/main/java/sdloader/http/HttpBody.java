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

import sdloader.exception.IORuntimeException;
import sdloader.util.ResourceUtil;
import sdloader.util.databuffer.ByteDataBuffer;
import sdloader.util.databuffer.DataBuffer;
import sdloader.util.databuffer.TempFileDataBuffer;

/**
 * HTTPのボディー部分
 * 
 * <pre>
 * バッファがBYTE_BUFFER_LIMIT以上になると、TempFileを使用するバッファに切り替えます。
 * </pre>
 * 
 * @author c9katayama
 */
public class HttpBody {

	public static final int NO_LIMIT = -1;
	public static final int BYTE_BUFFER_LIMIT = 3 * 1024 * 1024;

	private long dataBufferLimit;

	private DataBuffer bodyData;

	public HttpBody(long initCapacity) {
		dataBufferLimit = BYTE_BUFFER_LIMIT;
		checkBufferLimit(initCapacity);
	}

	public long getSize() {
		return bodyData.getSize();
	}

	public void write(int data) throws IOException {
		checkBufferLimit(getSize() + 1);
		bodyData.write(data);
	}

	public void write(byte[] buf, int offset, int length) throws IOException {
		checkBufferLimit(getSize() + length);
		bodyData.write(buf, offset, length);
	}

	public InputStream getInputStream() throws IOException {
		return bodyData.getInputStream();
	}

	public void dispose() {
		bodyData.dispose();
	}

	protected void checkBufferLimit(long size) {
		if (dataBufferLimit == NO_LIMIT) {
			return;
		}
		if (size >= BYTE_BUFFER_LIMIT) {
			DataBuffer oldData = bodyData;
			bodyData = new TempFileDataBuffer();
			if (oldData != null) {
				try {
					ResourceUtil.copyStream(oldData.getInputStream(),bodyData.getOutputStream());
				} catch (IOException ioe) {
					throw new IORuntimeException(ioe);
				}
				oldData.dispose();
			}
			dataBufferLimit = NO_LIMIT;
		} else {
			if (bodyData == null) {
				bodyData = new ByteDataBuffer((int) size);
			}
		}
	}
}

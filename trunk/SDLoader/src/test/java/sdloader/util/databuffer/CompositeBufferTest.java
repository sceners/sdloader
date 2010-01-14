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
package sdloader.util.databuffer;

import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Assert;

public class CompositeBufferTest extends TestCase {

	public void test1() throws Exception {

		CompositeDataBuffer buffer = new CompositeDataBuffer(14) {
			@Override
			protected DataBuffer createFirstDataBuffer() {
				return new ByteDataBuffer(10);
			}

			@Override
			protected DataBuffer createNextDataBuffer() {
				return new TempFileDataBuffer();
			}
		};

		for (int i = 0; i < 20; i++) {
			buffer.write(i);
		}
		InputStream is = buffer.getInputStream();
		for (int i = 0; i < 22; i++) {
			if (i < 20) {
				assertEquals(i, is.read());
			} else {
				assertEquals(-1, is.read());
			}
		}
	}

	public void test2() throws Exception {

		CompositeDataBuffer buffer = new CompositeDataBuffer(14) {
			@Override
			protected DataBuffer createFirstDataBuffer() {
				return new ByteDataBuffer(10);
			}

			@Override
			protected DataBuffer createNextDataBuffer() {
				return new TempFileDataBuffer();
			}
		};

		byte[] data = new byte[30];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
		buffer.write(data, 0, 10);
		buffer.write(data, 10, 10);
		buffer.write(data, 20, 10);

		InputStream is = buffer.getInputStream();
		byte[] result = new byte[30];
		byte[] buf = new byte[10];
		int len = -1;
		int pos = 0;
		while ((len = is.read(buf)) != -1) {
			System.arraycopy(buf, 0, result, pos, len);
			pos += len;
		}
		Assert.assertArrayEquals(data, result);
	}
}

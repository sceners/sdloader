/*
 * Copyright 2005-2009 the original author or authors.
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Assert;

import sdloader.util.ResourceUtil;

public class ByteDataBufferTest extends TestCase {

	public void test() throws Exception {

		ByteDataBuffer buffer = new ByteDataBuffer(10000);
		buffer.write(new byte[] { 1, 2, 3 }, 0, 3);
		buffer.write(new byte[] { 4, 5, 6 }, 1, 2);

		for (int i = 0; i < 10; i++) {
			InputStream is = buffer.getInputStream();
			assertEquals(1, is.read());
			assertEquals(2, is.read());
			assertEquals(3, is.read());
			assertEquals(5, is.read());
			assertEquals(6, is.read());
		}
		assertEquals(5, buffer.getSize());
		buffer.dispose();
	}

	public void test2() throws Exception {

		ByteDataBuffer buffer = new ByteDataBuffer(3);

		buffer.write(new byte[] { 9, 8 }, 0, 2);
		buffer.write(new byte[] { 1, 2, 3 }, 0, 3);
		buffer.write(new byte[] { 4, 5, 6 }, 1, 2);

		for (int i = 0; i < 10; i++) {
			InputStream is = buffer.getInputStream();
			assertEquals(9, is.read());
			assertEquals(8, is.read());
			assertEquals(1, is.read());
			assertEquals(2, is.read());
			assertEquals(3, is.read());
			assertEquals(5, is.read());
			assertEquals(6, is.read());
			assertEquals(-1, is.read());
		}
		assertEquals(7, buffer.getSize());
		buffer.dispose();
	}

	public void test3() throws Exception {

		ByteDataBuffer buffer = new ByteDataBuffer(5);

		buffer.write(new byte[] { 1, 2, 3, 4 }, 0, 4);

		InputStream is = buffer.getInputStream();
		byte[] b1 = new byte[3];
		byte[] b2 = new byte[2];
		assertEquals(3, is.read(b1));
		assertEquals(1, is.read(b2));

		buffer.write(5);

		assertEquals(1, is.read(b2));
		assertEquals(5, b2[0]);

		is = buffer.getInputStream();
		byte[] buf = new byte[10];
		assertEquals(5, is.read(buf));
		for (int i = 01; i < 5; i++) {
			assertEquals(i + 1, buf[i]);
		}

		buffer.dispose();
	}

	public void test4() throws Exception {

		byte[] data = new byte[5 * 1024 * 1024];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
		ByteDataBuffer buffer = new ByteDataBuffer();
		buffer.write(data, 0, data.length);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		long totalSize = ResourceUtil.copyStream(buffer.getInputStream(), bout);
		assertEquals(totalSize, data.length);
		byte[] result = bout.toByteArray();
		for (int i = 0; i < totalSize; i++) {
			assertEquals(result[i], data[i]);
		}
	}

	public void testToByteArray() throws Exception {
		ByteDataBuffer buffer = new ByteDataBuffer(100);
		byte[] data = new byte[150];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
			buffer.write(data[i]);
		}
		byte[] result = buffer.toByteArray();
		Assert.assertArrayEquals(data, result);
	}
}

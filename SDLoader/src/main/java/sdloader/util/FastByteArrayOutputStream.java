/*
 * $Id: FastByteArrayOutputStream.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package sdloader.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * A speedy implementation of ByteArrayOutputStream. It's not synchronized, and
 * it does not copy buffers when it's expanded. There's also no copying of the
 * internal buffer if it's contents is extracted with the writeTo(stream)
 * method.
 * 
 */
public class FastByteArrayOutputStream extends OutputStream {
	private static final int DEFAULT_BLOCK_SIZE = 8192;

	private LinkedList<byte[]> buffers;
	private byte buffer[];
	private int index;
	private int size;
	private int blockSize;
	private boolean closed;

	public FastByteArrayOutputStream() {
		this(DEFAULT_BLOCK_SIZE);
	}

	public FastByteArrayOutputStream(int blockSize) {
		buffer = new byte[this.blockSize = blockSize];
	}

	public void writeTo(OutputStream out) throws IOException {
		if (buffers != null) {
			for (byte[] bytes : buffers) {
				out.write(bytes, 0, blockSize);
			}
		}
		out.write(buffer, 0, index);
	}

	public int getSize() {
		return size + index;
	}

	public byte[] toByteArray() {
		ByteBuffer data = ByteBuffer.allocate(getSize());
		if (buffers != null) {
			for (byte[] bytes : buffers) {
				data.put(bytes);
			}
		}
		data.put(buffer, 0, index);
		return data.array();
	}

	protected void addBuffer() {
		if (buffers == null) {
			buffers = new LinkedList<byte[]>();
		}
		buffers.addLast(buffer);
		buffer = new byte[blockSize];
		size += index;
		index = 0;
	}

	public void write(int datum) throws IOException {
		if (closed) {
			throw new IOException("Stream closed");
		}
		if (index == blockSize) {
			addBuffer();
		}
		buffer[index++] = (byte) datum;
	}

	public void write(byte data[], int offset, int length) throws IOException {
		if (data == null) {
			throw new NullPointerException();
		}
		if (offset < 0 || offset + length > data.length || length < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (closed) {
			throw new IOException("Stream closed");
		}
		if (index + length > blockSize) {
			do {
				if (index == blockSize) {
					addBuffer();
				}
				int copyLength = blockSize - index;
				if (length < copyLength) {
					copyLength = length;
				}
				System.arraycopy(data, offset, buffer, index, copyLength);
				offset += copyLength;
				index += copyLength;
				length -= copyLength;
			} while (length > 0);
		} else {
			System.arraycopy(data, offset, buffer, index, length);
			index += length;
		}
	}

	public void reset() {
		closed = false;
		buffers = null;
		buffer = new byte[blockSize];
		index = 0;
		size = 0;
	}

	public void close() {
		closed = true;
	}
}
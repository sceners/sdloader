package sdloader.util.databuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import sdloader.exception.IORuntimeException;

public class ByteDataBuffer implements DataBuffer {

	private static final int DEFAULT_BLOCK_SIZE = 8192;
	private LinkedList<ByteBuffer> bufferList = new LinkedList<ByteBuffer>();
	private ByteBuffer buffer;
	private int position;
	private long size;
	private int blockSize;

	public ByteDataBuffer() {
		this(DEFAULT_BLOCK_SIZE);
	}

	public ByteDataBuffer(int blockSize) {
		this.blockSize = blockSize;
		buffer = ByteBuffer.allocate(blockSize);
		bufferList.addLast(buffer);
	}

	public long getSize() {
		return size + position;
	}

	public void dispose() {
		bufferList = null;
	}

	public void copyDataBuffer(DataBuffer src) throws IOException {
		long srcSize = src.getSize();
		if (srcSize > Integer.MAX_VALUE) {
			throw new IORuntimeException("Buffer size too long.size=" + srcSize);
		}
		copyDataBuffer(src, (int) src.getSize());
	}

	protected void copyDataBuffer(DataBuffer src, int srcSize)
			throws IOException {
		buffer = ByteBuffer.allocate(srcSize);
		InputStream is = src.getInputStream();
		byte[] buf = new byte[4096];
		int size = -1;
		while ((size = is.read(buf)) != -1) {
			buffer.put(buf, 0, size);
		}
		buf = null;
	}

	public InputStream getInputStream() {
		return new ByteDataBufferInputStream(this);
	}

	public void write(int data) throws IOException {
		if (position + 1 > blockSize) {
			addBuffer();
		}
		buffer.put((byte) data);
		position++;
	}

	public void write(byte[] buf, int offset, int length) throws IOException {
		if (position + length > blockSize) {
			do {
				if (position == blockSize) {
					addBuffer();
				}
				int copyLength = blockSize - position;
				if (length < copyLength) {
					copyLength = length;
				}
				buffer.put(buf, offset, copyLength);
				offset += copyLength;
				position += copyLength;
				length -= copyLength;
			} while (length > 0);
		} else {
			buffer.put(buf, offset, length);
			position += length;
		}
	}

	protected void addBuffer() {
		buffer = ByteBuffer.allocate(blockSize);
		bufferList.addLast(buffer);
		size += position;
		position = 0;
	}

	private static class ByteDataBufferInputStream extends InputStream {

		private ByteDataBuffer target;

		private ByteBuffer buffer;
		private int position;
		private int listIndex;
		private long totalReadSize;

		private ByteDataBufferInputStream(ByteDataBuffer target) {
			this.target = target;
			buffer = target.bufferList.get(0);
		}
		@Override
		public int read() throws IOException {
			if (target.getSize() <= totalReadSize) {
				return -1;
			}
			if (target.blockSize == position) {
				listIndex++;
				buffer = target.bufferList.get(listIndex);
				position = 0;
			}
			totalReadSize++;
			return buffer.get(position++) & 0xff;
		}
		@Override
		public int read(byte[] b, int off, int length) throws IOException {
			long size = target.getSize();
			if (size <= totalReadSize) {
				return -1;
			}
			length = (int) Math.min(size - totalReadSize, length);
			final int actualReadSize = length;
			int blockSize = target.blockSize;
			if (position + length > blockSize) {
				do {
					if (blockSize == position) {
						listIndex++;
						buffer = target.bufferList.get(listIndex);
						position = 0;
					}
					int readLength = blockSize - position;
					if (length < readLength) {
						readLength = length;
					}
					// getするとpositionがずれるので戻す
					int oldPosition = buffer.position();
					buffer.position(position);
					buffer.get(b, off, readLength);
					buffer.position(oldPosition);

					off += readLength;
					position += readLength;
					length -= readLength;
				} while (length > 0);
			} else {
				int oldPosition = buffer.position();
				buffer.position(position);
				buffer.get(b, off, length);
				buffer.position(oldPosition);

				position += length;
			}
			totalReadSize += actualReadSize;
			return actualReadSize;
		}
	}
}

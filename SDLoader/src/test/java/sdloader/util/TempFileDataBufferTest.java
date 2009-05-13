package sdloader.util;

import java.io.InputStream;

import junit.framework.TestCase;
import sdloader.util.databuffer.TempFileDataBuffer;

public class TempFileDataBufferTest extends TestCase {

	public void test() throws Exception {

		TempFileDataBuffer buffer = new TempFileDataBuffer();
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

		TempFileDataBuffer buffer = new TempFileDataBuffer();

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
}

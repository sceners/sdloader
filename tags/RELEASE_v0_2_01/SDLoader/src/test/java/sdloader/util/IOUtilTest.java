package sdloader.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import sdloader.constants.LineSpeed;

public class IOUtilTest extends TestCase {

	public void testWrite() throws Exception {
		execute(0);
		execute(64);
		execute(64 * 1000 / 8 - 1);
		execute(64 * 1000 / 8);
		execute(64 * 1000 / 8 + 1);
		execute((64 * 1000 / 8) * 2);
		execute((64 * 1000 / 8) * 3 + 100);
	}

	private void execute(int byteSize) throws IOException {
		byte[] b = new byte[byteSize];
		for (int n = 0; n < b.length; n++) {
			b[n] = (byte) (Math.random() * 128);
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtil.write(LineSpeed.ISDN_64K_BPS, b, bout);
		byte[] result = bout.toByteArray();
		assertEquals(b.length, result.length);
		for (int n = 0; n < b.length; n++) {
			assertEquals(b[n], result[n]);
		}
	}
}

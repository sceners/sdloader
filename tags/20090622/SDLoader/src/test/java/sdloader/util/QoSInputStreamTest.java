package sdloader.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import sdloader.constants.LineSpeed;

public class QoSInputStreamTest extends TestCase {

	public void testRead() throws Exception {
		execute(0, LineSpeed.ISDN_64K_BPS);
		execute(64, LineSpeed.ISDN_64K_BPS);
		execute(64 * 1000 / 8 - 1, LineSpeed.ISDN_64K_BPS);
		execute(64 * 1000 / 8, LineSpeed.ISDN_64K_BPS);
		execute(64 * 1000 / 8 + 1, LineSpeed.ISDN_64K_BPS);
		execute((64 * 1000 / 8) * 2, LineSpeed.ISDN_64K_BPS);
		execute((64 * 1000 / 8) * 3 + 100, LineSpeed.ISDN_64K_BPS);

		System.out.println("64k");
		execute(8000 * 10, LineSpeed.ISDN_64K_BPS);// 80k
		execute(16000 * 10, LineSpeed.ISDN_64K_BPS);// 160k
		execute(8000 * 100, LineSpeed.ISDN_64K_BPS);// 800k
		System.out.println("128k");
		execute(8000 * 10, LineSpeed.ISDN_128K_BPS);// 80k
		execute(16000 * 10, LineSpeed.ISDN_128K_BPS);// 160k
		execute(8000 * 100, LineSpeed.ISDN_128K_BPS);// 800k

	}

	private void execute(int byteSize, int bps) throws IOException {
		byte[] b = new byte[byteSize];
		for (int n = 0; n < b.length; n++) {
			b[n] = (byte) (Math.random() * 128);
		}
		long now = System.currentTimeMillis();
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		InputStream in = new QoSInputStream(bin, bps);
		byte[] result = new byte[byteSize];
		in.read(result);
		long time = System.currentTimeMillis() - now;
		if (time != 0) {
			long bit = byteSize * 8L;
			double sec = time / 1000D;
			System.out.println("bit = " + bit + " sec=" + sec);
			System.out.println("bps=" + bit / sec);
		}

		assertEquals(b.length, result.length);
		for (int n = 0; n < b.length; n++) {
			assertEquals(b[n], result[n]);
		}
	}
}

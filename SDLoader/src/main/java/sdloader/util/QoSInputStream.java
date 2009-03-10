package sdloader.util;

import java.io.IOException;
import java.io.InputStream;

import sdloader.constants.LineSpeed;

public class QoSInputStream extends InputStream {

	private static final int SEC_SLICE_NUM = 8;
	private static final int BIT_PER_BYTE = 8;
	private static final int SLEEP_MILLI_SEC = 1000 / SEC_SLICE_NUM;

	private InputStream in;
	private int bytePerSliceSec;
	private boolean qos;

	private int readBytes;
	private long lastSleepTime;

	public QoSInputStream(InputStream in, int bps) {
		this.in = in;
		if (bps <= LineSpeed.NO_LIMIT) {
			qos = false;
		} else {
			bytePerSliceSec = Math.max(100,
					(int) (bps / BIT_PER_BYTE / SEC_SLICE_NUM));
			qos = true;
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public int read() throws IOException {
		int b = in.read();
		if (qos) {
			readBytes++;
			if (lastSleepTime == 0) {
				lastSleepTime = System.currentTimeMillis();
			}
			if (readBytes == bytePerSliceSec) {
				long now = System.currentTimeMillis();
				long sleepTime = SLEEP_MILLI_SEC - (now - lastSleepTime);
				if (sleepTime > 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}
				lastSleepTime = System.currentTimeMillis();
				readBytes = 0;
			}
		}
		return b;
	}

}

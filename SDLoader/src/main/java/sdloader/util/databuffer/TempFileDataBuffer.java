package sdloader.util.databuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import sdloader.exception.IORuntimeException;
import sdloader.util.IOUtil;

public class TempFileDataBuffer implements DataBuffer {

	private File tempFile;
	private OutputStream fileOutputStream;
	private List<InputStream> inputStreamList;
	private long size;

	public TempFileDataBuffer() {
		try {
			tempFile = File.createTempFile("sdl", ".tmp");
			tempFile.deleteOnExit();
			fileOutputStream = new FileOutputStream(tempFile);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	public long getSize() {
		return size;
	}

	public void copyDataBuffer(DataBuffer src) throws IOException {
		IOUtil.closeNoException(fileOutputStream);
		try {
			fileOutputStream = new FileOutputStream(tempFile);
			size = 0;
			InputStream is = src.getInputStream();
			byte[] buf = new byte[8192];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, len);
				this.size += len;
			}
		} finally {
			IOUtil.flushNoException(fileOutputStream);
		}
	}

	public void dispose() {
		IOUtil.closeNoException(fileOutputStream);
		if (inputStreamList != null) {
			IOUtil.closeNoException(inputStreamList
					.toArray(new InputStream[] {}));
		}
		tempFile.delete();
		tempFile = null;
		fileOutputStream = null;
	}

	public InputStream getInputStream() throws IOException {
		if (inputStreamList == null) {
			inputStreamList = new ArrayList<InputStream>();
		}
		IOUtil.flushNoException(fileOutputStream);
		FileInputStream is = new FileInputStream(tempFile) {
			@Override
			public void close() throws IOException {
				super.close();
				inputStreamList.remove(this);
			}
		};
		inputStreamList.add(is);
		return is;
	}

	public void write(int data) throws IOException {
		fileOutputStream.write(data);
		size++;
	}

	public void write(byte[] buf, int offset, int length) throws IOException {
		fileOutputStream.write(buf, offset, length);
		size += length;
	}
}

package sdloader.util.databuffer;

import java.io.IOException;
import java.io.InputStream;

public interface DataBuffer {

	long getSize();

	void write(int data) throws IOException;
	
	void write(byte[] buf, int offset, int length)throws IOException;

	void copyDataBuffer(DataBuffer buffer)throws IOException;

	InputStream getInputStream()throws IOException;

	void dispose();
}

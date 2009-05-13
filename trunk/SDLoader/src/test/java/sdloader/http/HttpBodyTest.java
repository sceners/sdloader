package sdloader.http;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

public class HttpBodyTest extends TestCase {

	public void testSize1() throws IOException{

		HttpBody body = new HttpBody(1001);
		
		byte[] buf = new byte[1000];
		buf[0] = 2;
		buf[buf.length-1] = 3;
		body.write(buf, 0,buf.length);
		
		InputStream is = body.getInputStream();
		
		assertEquals(2,is.read());
		for(int i = 1;i <= 998;i++){
			is.read();
		}
		body.write(new byte[]{4},0,1);
		assertEquals(3,is.read());
		assertEquals(4,is.read());
	}
	public void testAddBuffer() throws IOException{

		HttpBody body = new HttpBody(150);
		
		byte[] buf = new byte[1000];
		buf[0] = 2;
		buf[buf.length-1] = 3;
		body.write(buf, 0,buf.length);
		
		InputStream is = body.getInputStream();
		
		assertEquals(2,is.read());
		for(int i = 1;i <= 998;i++){
			is.read();
		}
		body.write(new byte[]{4},0,1);
		assertEquals(3,is.read());
		assertEquals(4,is.read());
	}
	
	public void testSize2() throws IOException{

		HttpBody body = new HttpBody(150);
		try{
			byte[] buf = new byte[1000];
			buf[0] = 2;
			buf[buf.length-1] = 3;
			body.write(buf, 0,buf.length);
			
			byte[] newBuf = new byte[HttpBody.BYTE_BUFFER_LIMIT];
			body.write(newBuf,0,newBuf.length);
			body.write(new byte[]{9},0,1);
			body.write(new byte[]{8,7,6,5,4,3,2,1},0,8);
			
			body.getInputStream();
			body.getInputStream().close();
			
			InputStream is = body.getInputStream();
			assertEquals(2,is.read());
			for(int i = 1;i <= 998;i++){
				is.read();
			}
			assertEquals(3,is.read());
			int size = newBuf.length;
			is.skip(size);
			assertEquals(9,is.read());
			assertEquals(8,is.read());
			assertEquals(7,is.read());
			assertEquals(6,is.read());
			assertEquals(5,is.read());
			assertEquals(4,is.read());
			assertEquals(3,is.read());
			assertEquals(2,is.read());
			assertEquals(1,is.read());
		}finally{
			body.dispose();
		}
	}
}

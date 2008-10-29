package sdloader.util;

import junit.framework.TestCase;

public class PathUtilsTest extends TestCase{

	public void testGetExtention(){
		String path = "hoge.txt";
		
		assertEquals("txt",PathUtils.getExtension(path));
		
		path = "hoge";
		
		assertEquals(null,PathUtils.getExtension(path));
	}
}

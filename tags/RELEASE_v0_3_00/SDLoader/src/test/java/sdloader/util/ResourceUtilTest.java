package sdloader.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class ResourceUtilTest extends TestCase {

	public void testStripExtension() {
		String value = "test.war";

		assertEquals("test", ResourceUtil.stripExtension(value));

	}

	public void testIsResourceExist() throws IOException {

		File dir = new File("c:/hoge fuga　bar");
		dir.mkdir();
		File file = new File("c:/hoge fuga　bar/テスト.txt");
		file.createNewFile();
		try {
			assertTrue(ResourceUtil.isResourceExist(file.toURI().toURL()));
		} catch (Exception e) {
			fail();
		}
		file.delete();
		dir.delete();
	}
}

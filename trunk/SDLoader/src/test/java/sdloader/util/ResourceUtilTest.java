package sdloader.util;

import junit.framework.TestCase;

public class ResourceUtilTest extends TestCase {

	public void testStripExtension() {
		String value = "test.war";

		assertEquals("test", ResourceUtil.stripExtension(value));

	}
}

package sdloader.util;

import junit.framework.TestCase;

public class MimeTest extends TestCase {

	public void testMime() {
		assertEquals(Mime.getMime("zip"), "application/zip");
		assertEquals(Mime.getMime("air"),
				"application/vnd.adobe.apollo-application-installer-package+zip");
		assertNull(Mime.getMime("katayaman"));
	}
}

package sdloader.util;

import java.security.MessageDigest;

import junit.framework.TestCase;

public class MessageDigestUtilTest extends TestCase {

	public void testCreate() throws Exception {
		MessageDigest d = MessageDigestUtil.createMessageDigest();
		assertNotNull(d);
		// default
		assertEquals("MD5", d.getAlgorithm());

		MessageDigestUtil.setAlgorithm("SHA1");
		d = MessageDigestUtil.createMessageDigest();
		assertNotNull(d);
		assertEquals("SHA1", d.getAlgorithm());
	}
}

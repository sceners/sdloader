package sdloader.util;

import junit.framework.TestCase;

/**
 * @author shot
 */
public class ClassUtilTest extends TestCase {

	public void testHasClass1() throws Exception {
		assertFalse(ClassUtil.hasClass(null));
	}

	public void testHasClass2() throws Exception {
		assertTrue(ClassUtil.hasClass(String.class.getName()));
	}

	public void testHasClass3() throws Exception {
		assertFalse(ClassUtil.hasClass("no_such_class"));
	}

}

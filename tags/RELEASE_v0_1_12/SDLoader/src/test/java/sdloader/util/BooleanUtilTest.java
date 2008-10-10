package sdloader.util;

import junit.framework.TestCase;

public class BooleanUtilTest extends TestCase {

	public void testToBoolean() throws Exception {
		assertTrue(BooleanUtil.toBoolean("true"));
		assertTrue(BooleanUtil.toBoolean("True"));
		assertTrue(BooleanUtil.toBoolean("yes"));
		assertTrue(BooleanUtil.toBoolean("YES"));
		assertTrue(BooleanUtil.toBoolean("y"));
		assertFalse(BooleanUtil.toBoolean("no"));
		assertFalse(BooleanUtil.toBoolean("false"));
		assertFalse(BooleanUtil.toBoolean(null));
		assertFalse(BooleanUtil.toBoolean(""));
	}
}

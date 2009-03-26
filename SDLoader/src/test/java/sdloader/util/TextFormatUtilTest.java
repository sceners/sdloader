package sdloader.util;

import junit.framework.TestCase;

public class TextFormatUtilTest extends TestCase {

	public void testEvalSystemProperties() {

		System.setProperty("TEST", "0000");
		String value = "test${TEST}test";

		value = TextFormatUtil.formatTextBySystemProperties(value);

		assertEquals("test0000test", value);

		System.setProperty("TESTF", "1111");
		System.setProperty("TESTE", "2222");

		value = "${TESTF}test${TEST}test${TESTE}";

		value = TextFormatUtil.formatTextBySystemProperties(value);

		assertEquals("1111test0000test2222", value);

		value = "{TESTF} ${TESTF}${TESTF}test${TEST} ${TEST}test${TESTE} ${TESTE} ${TESTE";

		value = TextFormatUtil.formatTextBySystemProperties(value);

		assertEquals("{TESTF} 11111111test0000 0000test2222 2222 ${TESTE",
				value);
	}

}

/*
 * Copyright 2005-2009 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

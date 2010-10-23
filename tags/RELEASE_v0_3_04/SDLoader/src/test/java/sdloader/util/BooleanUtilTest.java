/*
 * Copyright 2005-2010 the original author or authors.
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

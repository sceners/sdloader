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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class IteratorEnumerationTest extends TestCase {

	public void test1() throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		IteratorEnumeration<String> e = new IteratorEnumeration<String>(list
				.iterator(), true);
		while (e.hasMoreElements()) {
			String nextElement = e.nextElement();
			assertTrue(list.remove(nextElement));
		}
	}
}

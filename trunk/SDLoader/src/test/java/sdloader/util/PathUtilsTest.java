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

public class PathUtilsTest extends TestCase {

	public void testNarrow() {

		String p1 = "c:/hoge/foo/bar";
		assertEquals("c:/hoge/foo/bar", PathUtil.narrow(p1));

		p1 = "c:/hoge//foo//bar";
		assertEquals("c:/hoge/foo/bar", PathUtil.narrow(p1));

		p1 = "c:/hoge/foo/./bar";
		assertEquals("c:/hoge/foo/bar", PathUtil.narrow(p1));

		p1 = "c:/hoge/foo/../bar";
		assertEquals("c:/hoge/bar", PathUtil.narrow(p1));

		p1 = "c:/hoge/foo/../bar/gee/../hoge";
		assertEquals("c:/hoge/bar/hoge", PathUtil.narrow(p1));

		p1 = "c:/hoge/foo/.././bar";
		assertEquals("c:/hoge/bar", PathUtil.narrow(p1));

		p1 = "c:/hoge/foo/../../bar";
		assertEquals("c:/bar", PathUtil.narrow(p1));

		p1 = "/hoge/foo/../../bar";
		assertEquals("/bar", PathUtil.narrow(p1));

		p1 = "/hoge/foo/../../bar/";
		assertEquals("/bar/", PathUtil.narrow(p1));

		p1 = "hoge/foo/../../bar";
		assertEquals("bar", PathUtil.narrow(p1));

		p1 = "hoge/foo/../../../bar";
		assertEquals("bar", PathUtil.narrow(p1));

	}

	public void testGetExtention() {
		String path = "hoge.txt";

		assertEquals("txt", PathUtil.getExtension(path));

		path = "hoge";

		assertEquals(null, PathUtil.getExtension(path));
	}
}

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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class ResourceUtilTest extends TestCase {

	public void testStripExtension() {
		String value = "test.war";

		assertEquals("test", ResourceUtil.stripExtension(value));

	}

	public void testIsResourceExist() throws IOException {

		File dir = new File(System.getProperty("user.dir") + "/hoge fuga　bar");
		dir.mkdir();
		File file = new File(System.getProperty("user.dir")
				+ "/hoge fuga　bar/テスト.txt");
		file.createNewFile();
		try {
			assertTrue(ResourceUtil.isResourceExist(file.toURI().toURL()));
		} catch (Exception e) {
			fail();
		}
		file.delete();
		dir.delete();
	}
}

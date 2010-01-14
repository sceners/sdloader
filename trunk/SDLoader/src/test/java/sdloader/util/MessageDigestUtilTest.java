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

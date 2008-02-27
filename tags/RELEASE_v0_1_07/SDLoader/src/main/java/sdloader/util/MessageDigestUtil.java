/*
 * Copyright 2005-2007 the original author or authors.
 *
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
import java.security.NoSuchAlgorithmException;

/**
 * @author shot
 */
public class MessageDigestUtil {

	protected static String ALGORHTYM = "MD5";
	
	protected MessageDigestUtil() {
	}
	
	public static MessageDigest createMessageDigest() {
		try {
			return MessageDigest.getInstance(ALGORHTYM);
		} catch (NoSuchAlgorithmException e) {
			throw new ExceptionInInitializerError();
		}
	}
	
	public static void setAlgorithm(String algorithm) {
		ALGORHTYM = algorithm;
	}
	
	public static void resetAlgorithm() {
		ALGORHTYM = "MD5";
	}
}

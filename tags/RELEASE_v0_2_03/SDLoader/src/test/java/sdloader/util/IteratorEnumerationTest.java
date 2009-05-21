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

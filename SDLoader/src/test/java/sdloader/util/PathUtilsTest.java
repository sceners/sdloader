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

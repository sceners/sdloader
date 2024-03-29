package examples.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DownloadHelper {

	private final static String CAMMA = ",";

	private final static String CR = "\n";

	private static final List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

	static {
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aaa", "AAA1");
			map.put("bbb", new Integer(1234));
			map.put("ccc", Boolean.TRUE);
			items.add(map);
		}
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aaa", "AAA2");
			map.put("bbb", new Integer(2345));
			map.put("ccc", Boolean.FALSE);
			items.add(map);
		}
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aaa", "AAA3");
			map.put("bbb", new Integer(3456));
			map.put("ccc", Boolean.TRUE);
			items.add(map);
		}
	};

	public File getFile(final String filename) {
		File csv = null;
		try {
			csv = new File(filename);
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv));
			for (Iterator<Map<String, Object>> itr = items.iterator(); itr.hasNext();) {
				Map<String, Object> map = itr.next();
				bw.write(map.get("aaa") + CAMMA);
				bw.write(map.get("bbb") + CAMMA);
				bw.write(map.get("ccc") + CAMMA + CR);
			}
			bw.newLine();
			bw.close();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		return csv;
	}
}

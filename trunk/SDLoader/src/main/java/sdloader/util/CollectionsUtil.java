package sdloader.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CollectionsUtil {

	public static <T> ArrayList<T> newArrayList() {
		return new ArrayList<T>();
	}

	public static <T> LinkedList<T> newLinkedList() {
		return new LinkedList<T>();
	}
	
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static <T> ConcurrentLinkedQueue<T> newConcurrentLinkedQueue() {
		return new ConcurrentLinkedQueue<T>();
	}
}

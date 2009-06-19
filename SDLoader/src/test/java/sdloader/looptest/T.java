package sdloader.looptest;

public class T {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Thread t = new Thread();
		t.start();

		synchronized (t) {
			t.notify();
		}
	}
}

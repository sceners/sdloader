package sdloader;

public class SDLoaderForceStopCommandTest {

	public static void main(String[] args) {

		SDLoader sdloader = new SDLoader(8080);
		sdloader.start();

		sdloader = new SDLoader(8080);
		sdloader.start();

	}
}

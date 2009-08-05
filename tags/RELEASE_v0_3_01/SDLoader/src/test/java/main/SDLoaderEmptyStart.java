package main;

import sdloader.SDLoader;

public class SDLoaderEmptyStart {

	public static void main(String[] args) {

		SDLoader sdloader = new SDLoader(8080);
		sdloader.setWebAppsDir("src/test/java/main/webapps");
		sdloader.start();
	}
}

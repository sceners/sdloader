package org.sdloader.launcher.nls;

import org.eclipse.jface.resource.ImageDescriptor;
import org.sdloader.launcher.util.StaticImageLoader;

public class Images {

	public static ImageDescriptor RUNNING;
	
	public static ImageDescriptor START;
	
	public static ImageDescriptor STOP;
	
	static {
		StaticImageLoader.loadResources(Images.class);
	}
}

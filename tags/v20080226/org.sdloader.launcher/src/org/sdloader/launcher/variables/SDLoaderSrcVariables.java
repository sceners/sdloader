package org.sdloader.launcher.variables;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.sdloader.launcher.Activator;

public class SDLoaderSrcVariables extends AbstractVariable {

	public static final IPath SRC = new Path("SDLOADER_SRC");
	@Override
	protected URL getInstallLocation() {
		Bundle bundle = Activator.getDefault().getBundle();
		return bundle.getEntry("/lib/sdloadersrc.jar");
	}

}

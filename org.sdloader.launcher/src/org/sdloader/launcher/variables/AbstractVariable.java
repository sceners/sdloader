package org.sdloader.launcher.variables;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public abstract class AbstractVariable extends ClasspathVariableInitializer {

	@Override
	public void initialize(String variable) {
		URL installUrl = getInstallLocation();
		URL local = null;
		try {
			local = FileLocator.toFileURL(installUrl);
		} catch (IOException e) {
			JavaCore.removeClasspathVariable(variable, null);
		}
		String fullPath = new File(local.getPath()).getAbsolutePath();
		try {
			JavaCore.setClasspathVariable(variable, Path.fromOSString(fullPath), null);
		} catch (JavaModelException e) {
			JavaCore.removeClasspathVariable(variable, null);
		}
	}

	protected abstract URL getInstallLocation();
}

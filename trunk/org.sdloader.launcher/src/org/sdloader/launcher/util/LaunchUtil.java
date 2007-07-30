package org.sdloader.launcher.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class LaunchUtil {

	public static IProject getProject(ILaunch launch) {
		IProject project = null;
		try {
			final ILaunchConfiguration config = launch.getLaunchConfiguration();
			final String name = config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			if ("".equals(name)) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				project = root.getProject(name);
			}
		} catch (CoreException e) {
			LogUtil.log(ResourcesPlugin.getPlugin(), e);
		}
		return project;
	}

}

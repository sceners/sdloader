package org.sdloader.launcher.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class LogUtil {

	public static void log(Plugin plugin, Throwable throwable) {
		IStatus status = null;
		if(plugin == null) {
			plugin = ResourcesPlugin.getPlugin();
		}
		if(throwable instanceof CoreException) {
			CoreException e = (CoreException)throwable;
			status = e.getStatus();
		} else {
			status = StatusUtil.createError(plugin, throwable);
		}
		plugin.getLog().log(status);
	}
	
	public static void log(Plugin plugin, String message) {
		IStatus status = StatusUtil.createInfo(plugin, Status.INFO, message, null);
		plugin.getLog().log(status);
	}
}

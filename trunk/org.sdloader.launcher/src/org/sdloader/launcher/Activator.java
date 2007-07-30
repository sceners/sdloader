package org.sdloader.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sdloader.launcher.util.LaunchUtil;
import org.sdloader.launcher.util.LogUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.sdloader.launcher";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		debugPlugin.addDebugEventListener(new IDebugEventSetListener() {

			public void handleDebugEvents(DebugEvent[] events) {
				for (int i = 0; i < events.length; i++) {
					DebugEvent debugEvent = events[i];
					if (debugEvent.getKind() == DebugEvent.TERMINATE) {
						Object o = debugEvent.getSource();
						if (o instanceof IProcess) {
							try {
								ILaunch launch = ((IProcess) o).getLaunch();
								String id = launch.getLaunchConfiguration()
										.getType().getIdentifier();
								if(Constants.ID_SDLOADER_LAUNCH_CONFIG.equals(id)) {
									IProject project = LaunchUtil.getProject(launch);
									Activator.setLaunch(project, launch);
								}
							} catch (CoreException e) {
								LogUtil.log(getDefault(), e);
							}
						}
					}
				}
			}

		});
	}

	public static void setLaunch(IProject project, ILaunch launch) {
		if(project != null) {
			try {
				project.setSessionProperty(Constants.KEY_SERVER_STATE, launch);
			} catch (CoreException e) {
				LogUtil.log(getDefault(), e);
			}
		}
	}

	public static ILaunch getLaunch(IProject project) {
		if(project != null) {
			try {
				return (ILaunch) project.getSessionProperty(Constants.KEY_SERVER_STATE);
			} catch (CoreException e) {
				LogUtil.log(getDefault(), e);
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}

package org.sdloader.launcher;

import org.eclipse.core.runtime.QualifiedName;

public interface Constants {

	String ID_PLUGIN = "org.sdloader.launcher";
	
	String ID_SDLOADER_LAUNCH_CONFIG = ID_PLUGIN + ".launchConfigurationType";

    public static final QualifiedName KEY_SERVER_STATE = new QualifiedName(
            ID_PLUGIN, "serverstate");
}

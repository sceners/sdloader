package org.sdloader.launcher.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.sdloader.launcher.decorator.SDLoaderRunningDecorator;

public class SDLoaderLauncherNature implements IProjectNature {

	private IProject project;
	
	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
		SDLoaderRunningDecorator.updateDecorators(project);
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}

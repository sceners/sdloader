package org.sdloader.launcher.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.sdloader.launcher.nls.Images;

import sun.misc.resources.Messages;

public class ToggleServerAction extends ServerAction {

	private Strategy start;
	private Strategy stop;
	private Strategy running;

	public ToggleServerAction() {
		start = new Strategy() {

			public ImageDescriptor getImage() {
				return Images.START;
			}

			public String getText() {
				return Messages.LABEL_START;
			}

			public void run(IAction action, IProject project)
					throws CoreException {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	@Override
	protected boolean checkEnabled() {
		return false;
	}

	public void run(IAction action) {
	}

	private interface Strategy {

		ImageDescriptor getImage();

		String getText();

		void run(IAction action, IProject project) throws CoreException;
	}
}

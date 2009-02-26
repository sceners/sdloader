package org.sdloader.launcher.decorator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.sdloader.launcher.Constants;
import org.sdloader.launcher.nls.Images;

public class SDLoaderRunningDecorator extends LabelProvider implements
		ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		try {
			if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				IProject project = (IProject) adaptable
						.getAdapter(IProject.class);
				Object o = project.getSessionProperty(Constants.KEY_SERVER_STATE);
				//TODO if need preferences, add this
				//Activator.getPreference(project);
				if (o instanceof ITerminate) {
					ITerminate t = (ITerminate) o;
					if(t.isTerminated()) {
						project.setSessionProperty(Constants.KEY_SERVER_STATE, null);
					} else {
						decoration.addOverlay(Images.RUNNING, IDecoration.BOTTOM_RIGHT);
					}
				}
			}
		} catch (CoreException e) {
			//ignore?
		}
	}

	public static void updateDecorators(IProject project) {
		if (project == null) {
			return;
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IDecoratorManager manager = workbench.getDecoratorManager();
		final SDLoaderRunningDecorator decorator = (SDLoaderRunningDecorator) manager
				.getBaseLabelProvider(Constants.ID_DECORATOR);
		final LabelProviderChangedEvent event = new LabelProviderChangedEvent(
				decorator, project);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				decorator.fireLabelProviderChanged(event);
			}

		});
	}

}

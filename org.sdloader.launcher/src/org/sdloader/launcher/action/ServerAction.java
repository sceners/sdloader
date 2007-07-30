package org.sdloader.launcher.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class ServerAction implements IWorkbenchWindowActionDelegate,
		IActionDelegate2, IEditorActionDelegate {

	private IAction delegate;

	public void init(IAction action) {
		this.delegate = action;
		maybeEnabled();
	}

	protected void maybeEnabled() {
		if (delegate == null) {
			return;
		}
		delegate.setEnabled(checkEnabled());
	}

	protected abstract boolean checkEnabled();

	public void dispose() {
		this.delegate = null;
	}

	public void init(IWorkbenchWindow window) {
		window.getActivePage().addPartListener(new IPartListener2() {

			public void partActivated(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partClosed(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partDeactivated(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partHidden(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partInputChanged(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partOpened(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partVisible(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

		});
		window.getWorkbench().addWindowListener(new IWindowListener() {

			public void windowActivated(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowClosed(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowDeactivated(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowOpened(IWorkbenchWindow window) {
				maybeEnabled();
			}

		});
		maybeEnabled();
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		maybeEnabled();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		maybeEnabled();
	}

}

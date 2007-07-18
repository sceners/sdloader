package org.sdloader.rcp;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800,600));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
        configurer.setTitle("SDLoader RCP");
    }    
    @Override
    public void postWindowOpen() {
    	IWorkbench workbench = getWindowConfigurer().getWindow().getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    	
       	try {
			BrowserView view = (BrowserView)page.showView(BrowserView.ID,String.valueOf("BrowserView"), IWorkbenchPage.VIEW_CREATE);
			String url = "http://localhost:"+Activator.getDefault().getSdLoader().getPort()+"/";
			view.setUrl(url);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
}

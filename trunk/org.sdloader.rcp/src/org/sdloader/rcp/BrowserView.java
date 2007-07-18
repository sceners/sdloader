package org.sdloader.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart {

	public static final String ID ="org.sdloader.rcp.browserview";
		
	private Composite parent;
	private Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		browser = new Browser(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {
	}
	
	public void setUrl(String url){
		browser.setUrl(url);
	}
}

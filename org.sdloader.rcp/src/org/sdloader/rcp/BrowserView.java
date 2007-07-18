package org.sdloader.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart {

	public static final String ID ="org.sdloader.rcp.browserview";
		
	private Composite parent;
	private Browser browser;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		parent.setLayout(gridLayout);
		
		Button button = new Button(parent,SWT.BORDER);
		button.setText("<--");
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				browser.back();
			}
		});
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(gridData);
	}

	@Override
	public void setFocus() {
	}
	
	public void setUrl(String url){
		browser.setUrl(url);
	}
}

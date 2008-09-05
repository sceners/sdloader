package examples.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.t2framework.annotation.composite.GET;
import org.t2framework.annotation.composite.POST;
import org.t2framework.annotation.core.ActionParam;
import org.t2framework.annotation.core.Page;
import org.t2framework.navigation.Redirect;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;

@RequestScope
@Page("dialog")
public class DialogPage {

	@GET
	public Navigation index() {
		return Redirect.to("/jsp/dialog.jsp");
	}

	@POST
	@ActionParam
	public Navigation dialog1(final HttpServletRequest req,
			HttpServletResponse res) {
		//SWT呼び出し
		final Display display = new Display();
		final Shell shell = new Shell(display,SWT.ON_TOP|SWT.CLOSE);
		shell.setLayout(new FillLayout());
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Capture!");

		final ImageLoader imageLoader = new ImageLoader();
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				/* Take the screen shot */
				GC gc = new GC(display);
				final Image image = new Image(display, display.getBounds());
				gc.copyArea(image, 0, 0);
				gc.dispose();
				imageLoader.data = new ImageData[] { image.getImageData() };
				shell.dispose();
			}
		});
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		if(imageLoader.data==null){
			return Redirect.to("/jsp/dialog.jsp");
		}
		
		res.setContentType("image/jpeg");
		try {
			imageLoader.save(res.getOutputStream(), SWT.IMAGE_JPEG);
		} catch (Exception e) {
		}
		return null;

	}
	@POST
	@ActionParam
	public Navigation dialog2(){
		try{
			Runtime.getRuntime().exec("notepad.exe");
		}catch(Exception e){			
		}
		return Redirect.to("/jsp/dialog.jsp");
	}
	@POST
	@ActionParam
	public Navigation dialog3(){
		try{
			Runtime.getRuntime().exec("explorer.exe");
		}catch(Exception e){			
		}
		return Redirect.to("/jsp/dialog.jsp");
	}
}

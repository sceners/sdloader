/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sdloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import sdloader.log.SDLoaderLog;
import sdloader.log.SDLoaderLogFactory;
import sdloader.util.Browser;

/**
 * SDLoaderをオープンします。 システムトレーにコンソールを入れます。
 * 
 * @author c9katayama
 */
public class SystemTrayOpen {

	private static final SDLoaderLog log = SDLoaderLogFactory
			.getLog(SystemTrayOpen.class);

	public static void main(String[] args) {
		new SystemTrayOpen().start();
	}

	private SDLoader server;

	public void start() {
		try {
			server = new SDLoader();
			server.setAutoPortDetect(true);
			server.start();

			openBrowser();

			createSystemTray(server);

		} catch (Throwable e) {
			log.error("SDLoader catch error.", e);
		} finally {
			System.exit(0);
		}
	}
	/**
	 * アイコンのInputStreamを返します。
	 * はじめにtrayicon.gifの名前でFileを検索します。
	 * ない場合、クラスパスから/sdloader/resource/trayicon.gifを探します。
	 * @return
	 */
	private InputStream getIconInputStream() {
		try {
			InputStream iconStream = null;
			File icon = new File("trayicon.gif");
			if (icon.exists()) {
				iconStream = new FileInputStream(icon);
			} else {
				iconStream = SystemTrayOpen.class
						.getResourceAsStream("/sdloader/resource/trayicon.gif");
			}
			return iconStream;
		} catch (IOException ioe) {
			return null;
		}
	}
	/**
	 * デフォルトＵＲＬをブラウザをオープンします。
	 */
	private void openBrowser() {
		try {
			int port = server.getPort();
			String url = "http://localhost:" + port;
			Browser.open(url);
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
		}
	}
	/**
	 * システムトレーを作成します。
	 * @param sdLoader
	 */
	private void createSystemTray(final SDLoader sdLoader) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		Image image = new Image(display, getIconInputStream());
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			log.info("System tray not found.");
		} else {
			final TrayItem item = new TrayItem(tray, SWT.NONE);
			item.setImage(image);
			item.setToolTipText("SDLoader port:" + sdLoader.getPort());

			final Menu menu = new Menu(shell, SWT.POP_UP);
			item.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});

			MenuItem shutdownItem = new MenuItem(menu, SWT.PUSH);
			shutdownItem.setText("shutdown");
			shutdownItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					sdLoader.stop();
					display.dispose();
				}
			});

			MenuItem portItem = new MenuItem(menu, SWT.NONE);
			portItem.setText("port:" + sdLoader.getPort());
			portItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					openBrowser();
				}
			});

			MenuItem browserItem = new MenuItem(menu, SWT.PUSH);
			browserItem.setText("open browser");
			browserItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					openBrowser();
				}
			});
			menu.setDefaultItem(browserItem);
		}
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		image.dispose();
		display.dispose();
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 5, 2007
 */
package net.sf.zekr.ui;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
public class SwtBrowserUtils {
	/**
	 * Tricky execution of a script using a deferred thread. This is a workaround for Mozilla issue which
	 * causes scripts from within the context of a callback (<code>TitleListener</code> or
	 * <code>StatusTextListener</code>) not to execute.
	 * 
	 * @param display
	 * @param browser
	 * @param script
	 */
	public static void trickyExecute(final Display display, final Browser browser, final String script) {
		Thread deferThread = new Thread(new Runnable() {
			public void run() {
				display.syncExec(new Runnable() {
					public void run() {
						if (!browser.isDisposed())
							browser.execute(script);
					}
				});
			}
		});
		deferThread.setDaemon(true);
		deferThread.start();
	}
	
	/*
 // a better work around
browser.addStatusTextListener(new StatusTextListener(){
    public void changed(StatusTextEvent event) {
        browser.getDisplay().asyncExec(new Runnable() {
            public void run() {
                if (browser.isDisposed()) return;
                browser.execute(script);
            }
        });
    }
});

 
	 */
}

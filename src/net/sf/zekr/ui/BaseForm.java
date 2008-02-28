/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 7, 2005
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.helper.EventProtocol;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class BaseForm implements EventProtocol {
	final protected Logger logger = Logger.getLogger(this.getClass());
	protected LanguageEngine lang = LanguageEngine.getInstance();
	protected final ResourceManager resource = ResourceManager.getInstance();
	protected Shell shell, parent;
	protected Display display;

	public void show() {
		shell.open();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	public void loopEver() {
		while (!isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}

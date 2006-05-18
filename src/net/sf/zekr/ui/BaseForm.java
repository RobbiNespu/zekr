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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class BaseForm implements EventProtocol {
	protected Shell shell;
	protected String title;
	protected Display display;
	protected LanguageEngine langEngine = LanguageEngine.getInstance();
	protected final ResourceManager resource = ResourceManager.getInstance();

	protected void init() {
	}

	public void show() {
		shell.open();
//		loopEver();
	}

	public void dispose() {
		shell.dispose();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	public void loopEver() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}

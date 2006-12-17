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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class BaseForm implements EventProtocol {
	protected LanguageEngine langEngine = LanguageEngine.getInstance();
	protected final ResourceManager resource = ResourceManager.getInstance();
	protected final Logger logger = Logger.getLogger(this.getClass());

	public void show() {
		getShell().open();
	}

	abstract protected Shell getShell();

	abstract protected Display getDisplay();

//	public void dispose() {
//		getShell().close();
//	}

	public boolean isDisposed() {
		return getShell().isDisposed();
	}

	public void loopEver() {
		while (!getShell().isDisposed()) {
			if (!getDisplay().readAndDispatch()) {
				getDisplay().sleep();
			}
		}
	}
}

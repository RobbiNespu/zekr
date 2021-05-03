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
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskItem;

/**
 * @author Mohsen Saboorian
 */
public abstract class BaseForm extends ZekrForm implements EventProtocol {
	protected final ResourceManager resource = ResourceManager.getInstance();
	protected Shell shell, parent;

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

	public Shell getShell() {
		return shell;
	}

	public Display getDisplay() {
		return display;
	}

	public TaskItem getTaskBarItem() {
		return FormUtils.getTaskBarItem(display, shell);
	}

}

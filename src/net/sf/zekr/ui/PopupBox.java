/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 17, 2006
 */
package net.sf.zekr.ui;

import net.sf.zekr.engine.language.LanguageEngine;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Note that at least one shell should be available when using this widget.
 * 
 * @author Mohsen Saboorian
 */
public class PopupBox {
	private Shell shell;
	private Text textBox;

	public PopupBox(Shell parent, String title, String text, int swtDirection) {
		shell = new Shell(parent, SWT.RESIZE | SWT.TITLE | SWT.ON_TOP | SWT.TOOL
				| LanguageEngine.getInstance().getSWTDirection());
		shell.setText(title);
		shell.setParent(parent);
		shell.setLayout(new FillLayout());
		textBox = new Text(shell, SWT.LEAD | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | swtDirection);
		textBox.setText(text);
	}

	public void open(Point size, Point location) {
		shell.setLocation(location);
		shell.setSize(size);
		shell.open();
		textBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				shell.close();
			}
		});
	}

	public Shell getShell() {
		return shell;
	}
}

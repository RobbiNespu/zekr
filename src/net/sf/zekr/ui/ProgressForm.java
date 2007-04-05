/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.ui;

import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple modal form, to show an SWT.INDETERMINATE progress bar.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ProgressForm extends BaseForm {
	public static final int CALCELED = 0;
	public static final int FINISHED = 1;
	public static final int PROGRESSING = 2;

	private Shell parent;
	private Shell shell;
	private Display display;
	private String title;

	private String message;
	private int state = PROGRESSING;

	public ProgressForm(Shell parent, String title, String message) {
		this.parent = parent;
		this.title = title;
		this.message = message;
		display = parent.getDisplay();
		init();
	}

	public void init() {
		GridLayout gl;
		GridData gd;

		shell = new Shell(display, SWT.TITLE | SWT.BORDER | SWT.SYSTEM_MODAL);
		shell.setData(ProgressForm.class.getName());
		shell.setImages(parent.getImages());
		shell.setText(title);
		shell.setLayout(new FillLayout());

		shell.addListener(EventProtocol.CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event e) {
				if (e.data != null) {
					if (e.data.equals(EventProtocol.END_WAITING)) {
						state = FINISHED;
						shell.close();
					}
				}
			}
		});

		gl = new GridLayout(1, false);
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 15;
		gl.marginHeight = gl.marginBottom = gl.marginLeft = gl.marginRight = 5;
		Composite body = new Composite(shell, SWT.NONE | langEngine.getSWTDirection());
		body.setLayout(gl);

		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.heightHint = 60;
		Label msgLabel = new Label(body, SWT.WRAP);
		msgLabel.setText(message);
		msgLabel.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		ProgressBar progBar = new ProgressBar(body, SWT.SMOOTH | SWT.HORIZONTAL | SWT.INDETERMINATE);
		progBar.setLayoutData(gd);

		gd = new GridData(SWT.END, SWT.CENTER, true, true);
		Button cancelBut = new Button(body, SWT.PUSH);
		cancelBut.setLayoutData(gd);
		cancelBut.setText("   " + langEngine.getMeaning("CANCEL") + "   ");
//		cancelBut.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				shell.close();
//				state = CALCELED;
//			}
//		});
		cancelBut.setEnabled(false);

		shell.pack();
		shell.setSize(400, shell.getSize().y);
		shell.setLocation(FormUtils.getCenter(parent, shell));
	}

	public void show() {
		super.show();
	}

	public Shell getShell() {
		return shell;
	}

	public int getState() {
		return state;
	}

	public Display getDisplay() {
		return display;
	}
}

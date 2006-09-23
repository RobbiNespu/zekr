/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 11, 2005
 */
package net.sf.zekr.ui.error;

import net.sf.zekr.engine.language.LanguageEngineNaming;
import net.sf.zekr.ui.BaseForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ErrorForm extends BaseForm {
	private Label message;
	private Text errorDetail;
	private Throwable error;
	private Shell shell;
	protected Display display;

	public ErrorForm(Display display, Throwable error) {
		this.display = display;
		this.error = error;

		shell = new Shell(display, getShellOptions());
		shell.setText(langEngine.getMeaning("ERROR"));
		shell.setImage(display.getSystemImage(SWT.ICON_ERROR));

		makeFrame(error);
	}

	private void makeFrame(Throwable error) {
		GridData gridData;

		GridLayout errorLayout = new GridLayout(3, false);
		FillLayout fl = new FillLayout();
		fl.marginWidth = 10;
		fl.marginHeight = 10;
		shell.setLayout(fl);
		shell.setFocus();

		Group errorGroup = new Group(shell, langEngine.getSWTDirection());
		errorGroup.setLayout(errorLayout);
		errorGroup.setText(langEngine.getMeaning(LanguageEngineNaming.ERROR_MSG, "GENERAL_ERROR"));

		errorDetail = new Text(errorGroup, SWT.BORDER | SWT.LEFT_TO_RIGHT | SWT.H_SCROLL | SWT.V_SCROLL);
		errorDetail.setEditable(false);

		StringBuffer buf = new StringBuffer(error.toString() + "\n");
		StackTraceElement elements[] = error.getStackTrace();
		for (int i = 0, n = elements.length; i < n; i++) {
			buf.append("\t" + elements[i].toString() + "\n");
		}

		errorDetail.setText(buf.toString());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		errorDetail.setLayoutData(gridData);
		errorDetail.selectAll();

		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = SWT.END;

		Composite buttons = new Composite(errorGroup, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		buttons.setLayout(rl);
		buttons.setLayoutData(gridData);
		RowData rd = new RowData();
		rd.width = 70;

		Button copy = new Button(buttons, SWT.NONE);
		copy.setText(langEngine.getMeaning("COPY"));
		copy.setLayoutData(rd);
		copy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				errorDetail.selectAll();
				errorDetail.copy();
			}
		});

		Button ok = new Button(buttons, SWT.NONE);
		ok.setText(langEngine.getMeaning("OK"));
		ok.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		ok.setLayoutData(rd);

		shell.setDefaultButton(ok);
		shell.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ESC)
					close();
			}
		});
	}

	private void close() {
		shell.close();
	}

	private int getShellOptions() {
		return SWT.SHELL_TRIM | SWT.SYSTEM_MODAL;
	}

	public void show() {
		shell.open();
		shell.pack();
		loopEver();
	}

	protected Shell getShell() {
		return shell;
	}

	protected Display getDisplay() {
		return display;
	}

}

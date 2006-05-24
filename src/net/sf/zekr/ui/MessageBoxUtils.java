/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 12, 2006
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MessageBoxUtils {
	final private static LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();

	public static void showError(String msg) {
		show(msg, dict.getMeaning("ERROR"), SWT.ICON_ERROR | dict.getSWTDirection());
	}

	public static void showMessage(String msg) {
		show(msg, dict.getMeaning("MESSAGE"), SWT.ICON_INFORMATION | dict.getSWTDirection());
	}

	public static void show(String msg, String title, int style) {
		MessageBox mb = new MessageBox(getShell(), style);
		mb.setMessage(msg);
		mb.setText(title);
		mb.open();
	}

	public static boolean yesNoQuestion(String msg, String title) {
		MessageBox mb = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION
				| dict.getSWTDirection());
		mb.setMessage(msg);
		mb.setText(title);
		if (mb.open() == SWT.NO)
			return false;
		return true;
	}

	private static String _ret;

	public static String textBoxPrompt2(String question, String title) {
		Shell parent = getShell();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| dict.getSWTDirection());
		shell.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		shell.setText(title);
		shell.setLayout(new FillLayout());
		
		RowLayout rl = new RowLayout(SWT.VERTICAL);
		Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(rl);

		RowData rd = new RowData();
		
		rd.width = 300;
		Label label = new Label(c, SWT.LEAD);
		label.setText(question);
		label.setLayoutData(rd);
		
		final Text text = new Text(c, SWT.BORDER | SWT.SINGLE | SWT.TRAIL);

		new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		Composite cButtons = new Composite(c, SWT.NONE);
		cButtons.setLayout(new FillLayout());
		
		Button ok = new Button(cButtons, SWT.PUSH);
		ok.setText(dict.getMeaning("OK"));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				_ret = text.getText();
				shell.close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		shell.setDefaultButton(ok);

		Button cancel = new Button(cButtons, SWT.PUSH);
		cancel.setText(dict.getMeaning("CANCEL"));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		_ret = null;
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.pack();
		shell.open();

//		while (!shell.isDisposed()) {
//			if (!shell.getDisplay().readAndDispatch()) {
//				shell.getDisplay().sleep();
//			}
//		}

		return _ret;
	}

	public static String textBoxPrompt(String question, String title) {
		Shell parent = getShell();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| dict.getSWTDirection());
		shell.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		shell.setText(title);
		// shell.setLayout(new RowLayout(SWT.VERTICAL));

		Label label = new Label(shell, SWT.LEAD);
		label.setText(question);
		label.setBounds(10, 10, 300, 20);
		final Text text = new Text(shell, SWT.BORDER | SWT.SINGLE);
		text.setBounds(40, 40, 220, 20);

		new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);

		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.END;

		Button ok = new Button(shell, SWT.PUSH);
		ok.setBounds(150, 70, 55, 22);
		ok.setText(dict.getMeaning("OK"));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				_ret = text.getText();
				shell.close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		shell.setDefaultButton(ok);

		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setBounds(90, 70, 55, 22);
		cancel.setText(dict.getMeaning("CANCEL"));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		_ret = null;
		shell.setSize(300, 135);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();

		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}

		return _ret;
	}

	private static Shell getShell() {
		return Display.getCurrent().getShells()[0];
	}

}

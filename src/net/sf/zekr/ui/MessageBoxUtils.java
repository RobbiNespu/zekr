/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 12, 2006
 */
package net.sf.zekr.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.io.filefilter.WildcardFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MessageBoxUtils {
	final private static LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
	final private static Logger logger = Logger.getLogger(MessageBoxUtils.class);

	public static void showError(String msg) {
		show(msg, lang.getMeaning("ERROR"), SWT.ICON_ERROR | lang.getSWTDirection());
	}

	public static void showMessage(String msg) {
		show(msg, lang.getMeaning("MESSAGE"), SWT.ICON_INFORMATION | lang.getSWTDirection());
	}

	public static void show(String msg, String title, int style) {
		MessageBox mb = new MessageBox(getShell(), style);
		mb.setMessage(msg);
		mb.setText(title);
		mb.open();
	}

	public static boolean showYesNoConfirmation(String msg, String title) {
		MessageBox mb = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION | lang.getSWTDirection());
		mb.setMessage(msg);
		mb.setText(title);
		if (mb.open() == SWT.YES)
			return true;
		return false;
	}

	// TODO: not a good idea!
	private static String _ret;

	public static String textBoxPrompt(String question, String title) {
		Shell parent = getShell();

		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | lang.getSWTDirection());
		shell.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		shell.setText(title);
		shell.setLayout(new FillLayout());
		// shell.setSize(400, 150);

		GridLayout gl = new GridLayout(2, false);
		Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(gl);
		// RowData rd = new RowData();
		// rd.width = 300;
		// c.setLayoutData(rd);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;

		Label label = new Label(c, SWT.NONE);
		label.setText(question);
		label.setLayoutData(gd);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		final Text text = new Text(c, SWT.LEFT_TO_RIGHT | SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.widthHint = 100;

		Button ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(gd);
		ok.setText(lang.getMeaning("OK"));
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

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.widthHint = 100;

		Button cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(gd);
		cancel.setText(lang.getMeaning("CANCEL"));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		_ret = null;
		shell.pack();
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();

		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}

		return _ret;
	}

	private static int __ret;

	/**
	 * @param options
	 *           answer options
	 * @param selectedOption
	 *           option number to be selected by default. This field is 0-base.
	 * @param question
	 *           the string to be placed as a question on the top of the dialog
	 * @param title
	 *           the text to be displayed as a title of this dialog
	 * @return -1 if nothing was selected, or dialog closed/cancelled, or a 0-base selected item number
	 */
	public static int radioQuestionPrompt(String[] options, int selectedOption, String question, String title) {
		Shell parent = getShell();
		Display display = parent.getDisplay();

		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | lang.getSWTDirection());
		shell.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		shell.setText(title);
		shell.setLayout(new FillLayout());

		GridLayout gl = new GridLayout(2, false);
		Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(gl);

		Composite imageComp = new Composite(c, SWT.NONE);
		final Image img = display.getSystemImage(SWT.ICON_QUESTION);
		GridData gd = new GridData(GridData.CENTER);
		int h = img.getBounds().height;
		gd.heightHint = h;
		gd.widthHint = img.getBounds().width;
		gd.verticalIndent = gd.horizontalIndent = 6;
		imageComp.setToolTipText(lang.getMeaning("APP_NAME"));
		imageComp.setLayoutData(gd);
		imageComp.setBounds(img.getBounds());
		imageComp.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(img, 0, 0);
			}
		});

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gd.verticalIndent = gd.horizontalIndent = 15;
		Label l = new Label(c, SWT.NONE);
		l.setText(question);
		l.setLayoutData(gd);

		final Button buts[] = new Button[options.length];
		for (int i = 0; i < options.length; i++) {
			gd = new GridData();
			gd.horizontalIndent = 10;
			if (i == 0)
				gd.verticalIndent = 10;
			else
				gd.verticalIndent = 2;
			gd.horizontalSpan = 2;

			buts[i] = new Button(c, SWT.RADIO);
			buts[i].setText(options[i]);
			buts[i].setLayoutData(gd);
		}

		if (buts.length > selectedOption) {
			buts[selectedOption].setSelection(true);
			buts[selectedOption].forceFocus();
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;
		gd.horizontalSpan = 2;
		gd.verticalIndent = 5;

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(c, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 80;
		Button ok = new Button(butComposite, SWT.NONE);
		ok.setText(FormUtils.addAmpersand( lang.getMeaning("OK")) );
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < buts.length; i++) {
					if (buts[i].getSelection()) {
						__ret = i;
						break;
					}
				}
				shell.close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		ok.setLayoutData(rd);
		
		shell.setDefaultButton(ok);

		rd = new RowData();
		rd.width = 80;
		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand( lang.getMeaning("CANCEL")) );
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				__ret = -1;
				shell.close();
			}
		});
		cancel.setLayoutData(rd);

		__ret = -1;

		shell.pack();
		if (shell.getSize().x < 350)
			shell.setSize(350, shell.getSize().y);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return __ret;
	}

	/**
	 * @param options
	 *           answer options
	 * @param question
	 *           the string to be placed as a question on the top of the dialog
	 * @param title
	 *           the text to be displayed as a title of this dialog
	 * @return -1 if nothing was selected, or dialog closed/cancelled, or a 0-base selected item number
	 */
	public static int radioQuestionPrompt(String[] options, String question, String title) {
		return radioQuestionPrompt(options, 0, question, title);
	}

	public static Shell getShell() {
		// This causes bug on Linux
		// return Display.getCurrent().getActiveShell();
		Shell shells[] = Display.getCurrent().getShells();
		return shells.length > 0 ? shells[0] : null;
	}

	/**
	 * This method opens a file chooser dialog and selects file filtering with the given wildcards.
	 * 
	 * @param filterNames
	 *           names of the filters
	 * @param filterWildcards
	 *           wildcard filters (e.g. *.zip)
	 * @return a 0-item list if action cancelled, no item was selected or selected items did not fit the extension
	 *         criteria. Otherwise, returns a list of selected files (of type <tt>java.io.File</tt>).
	 * @throws IOException
	 *            if any exception occurred during importing.
	 */
	public static List importFileDialog(Shell parentShall, String[] filterNames, String[] filterWildcards)
			throws IOException {
		FileDialog fd = new FileDialog(parentShall, SWT.OPEN | SWT.MULTI);
		fd.setFilterPath(GlobalConfig.getDefaultStartFolder());
		fd.setFilterNames(filterNames);
		fd.setFilterExtensions(filterWildcards); // Windows wild card
		fd.setText(lang.getMeaning("OPEN"));

		FileFilter fileFilter = new WildcardFilter(filterWildcards);
		List fileList = new ArrayList();

		String res = fd.open();
		if (res == null)
			return fileList;

		String fileNames[] = fd.getFileNames();
		for (int i = 0; i < fileNames.length; i++) {
			File srcFile = new File(fd.getFilterPath(), fileNames[i]);
			if (fileFilter.accept(srcFile)) {
				if (!srcFile.exists()) {
					logger.warn("File not found: " + srcFile);
					MessageBoxUtils.showError(lang.getMeaning("FNF") + ":\n" + srcFile);
					return new ArrayList();
				}
				fileList.add(srcFile);
			}
		}
		logger.debug("Files chosen: " + fileList);
		return fileList;
	}

	public static File exportFileDialog(Shell parentShall, String[] filterNames, String[] filterWildcards)
			throws IOException {
		FileDialog fd = new FileDialog(parentShall, SWT.SAVE);
		fd.setFilterPath(GlobalConfig.getDefaultStartFolder());
		fd.setFilterNames(filterNames);
		fd.setFilterExtensions(filterWildcards); // Windows wild card
		fd.setText(lang.getMeaning("SAVE"));

		String res = fd.open();
		if (res == null)
			return null;
		File f = new File(res);
		if (f.exists()) {
			if (!MessageBoxUtils.showYesNoConfirmation(lang.getDynamicMeaning("FILE_ALREADY_EXISTS", new String[] { f
					.getName() }), lang.getMeaning("OVERWRITE")))
				return null;
			if (!f.delete())
				throw new IOException("Can not delete already existing file \"" + f + "\".");
		}
		logger.debug("Save to file: " + f);
		return f;
	}
}

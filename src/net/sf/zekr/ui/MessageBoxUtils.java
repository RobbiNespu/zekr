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

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Mohsen Saboorian
 */
public class MessageBoxUtils {
	public static class YesNoQuestionForm {
		protected String result, defaultValue;

		public YesNoQuestionForm(String question, String title, String defaultValue) {
			this.defaultValue = defaultValue;
			Shell parent = getShell();
			Display display = parent.getDisplay();

			final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | lang.getSWTDirection());
			shell.setImage(display.getSystemImage(SWT.ICON_QUESTION));
			shell.setText(title);
			shell.setLayout(new FillLayout());

			GridLayout gl = new GridLayout(2, false);
			Composite c = new Composite(shell, SWT.NONE);
			c.setLayout(gl);

			Composite imageComp = new Composite(c, SWT.NONE);
			final Image img = display.getSystemImage(SWT.ICON_QUESTION);
			GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
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

			gd = new GridData(SWT.LEAD, SWT.CENTER, true, false);
			gd.verticalIndent = gd.horizontalIndent = 15;
			gd.widthHint = 250;

			Label l = new Label(c, SWT.WRAP);
			l.setText(question);
			l.setLayoutData(gd);

			gd = new GridData(SWT.FILL, SWT.END, false, true);
			gd.horizontalSpan = 2;
			gd.horizontalIndent = imageComp.getSize().x + 28;
			final Text text = new Text(c, SWT.LEFT_TO_RIGHT | SWT.BORDER | SWT.SINGLE);
			text.setLayoutData(gd);
			text.setText(defaultValue);
			text.selectAll();

			gd = new GridData();
			gd = new GridData(SWT.CENTER, SWT.END, true, true);
			gd.horizontalSpan = 2;
			RowLayout rl = new RowLayout(SWT.HORIZONTAL);
			Composite buttonComp = new Composite(c, SWT.NONE);
			buttonComp.setLayout(rl);
			buttonComp.setLayoutData(gd);

			Button ok = new Button(buttonComp, SWT.PUSH);
			ok.setText(lang.getMeaning("OK"));
			ok.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					result = text.getText();
					shell.close();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			shell.setDefaultButton(ok);

			Button cancel = new Button(buttonComp, SWT.PUSH);
			cancel.setText(lang.getMeaning("CANCEL"));
			cancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					result = null;
					shell.close();
				}
			});

			RowData rdOk = new RowData();
			RowData rdCancel = new RowData();
			int buttonLength = FormUtils.buttonLength(GlobalGuiConfig.BUTTON_WIDTH, ok, cancel);
			rdOk.width = buttonLength;
			rdCancel.width = buttonLength;
			ok.setLayoutData(rdOk);
			cancel.setLayoutData(rdCancel);

			shell.pack();
			if (shell.getSize().x < 350)
				shell.setSize(350, shell.getSize().y + 20);

			shell.setLocation(FormUtils.getCenter(parent, shell));

			shell.open();

			while (!shell.isDisposed()) {
				if (!shell.getDisplay().readAndDispatch()) {
					shell.getDisplay().sleep();
				}
			}
		}

		/**
		 * @return the string user entered, or <code>null</code> if cancel button is pressed
		 */
		public String getResult() {
			return result;
		}
	}

	final private static LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
	final private static Logger logger = Logger.getLogger(MessageBoxUtils.class);

	public static void showError(String msg) {
		show(msg, lang.getMeaning("ERROR"), SWT.ICON_ERROR | lang.getSWTDirection());
	}

	public static void showActionFailureError(Exception ex) {
		showError(lang.getMeaning("ACTION_FAILED") + "\n" + ex.getMessage());
	}

	public static void showError(ZekrMessageException zme) {
		showError(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
	}

	public static void showError(String title, String msg) {
		show(msg, title, SWT.ICON_ERROR | lang.getSWTDirection());
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

	public static String textBoxPrompt(String title, String question) {
		return textBoxPrompt(title, question, "");
	}

	public static String textBoxPrompt(String title, String question, String defaultValue) {
		return new YesNoQuestionForm(question, title, defaultValue).getResult();
	}

	private static int __ret;

	/**
	 * @param options answer options
	 * @param selectedOption option number to be selected by default. This field is 0-base.
	 * @param question the string to be placed as a question on the top of the dialog
	 * @param title the text to be displayed as a title of this dialog
	 * @return -1 if nothing was selected, or dialog closed/canceled, or a 0-base selected item number
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

		Button ok = new Button(butComposite, SWT.NONE);
		ok.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		ok.pack();
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

		shell.setDefaultButton(ok);

		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				__ret = -1;
				shell.close();
			}
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		// set the OK and CANCEL buttons to the same length
		int buttonLength = FormUtils.buttonLength(80, ok, cancel);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		ok.setLayoutData(rdOk);
		cancel.setLayoutData(rdCancel);

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
	 * @param options answer options
	 * @param question the string to be placed as a question on the top of the dialog
	 * @param title the text to be displayed as a title of this dialog
	 * @return -1 if nothing was selected, or dialog closed/canceled, or a 0-base selected item number
	 */
	public static int radioQuestionPrompt(String[] options, String question, String title) {
		return radioQuestionPrompt(options, 0, question, title);
	}

	public static Shell getShell() {
		return getShell(Display.getCurrent());
	}

	public static Shell getShell(Display display) {
		Shell shell = display.getActiveShell();
		if (shell != null) {
			return shell;
		} else {
			Shell shells[] = display.getShells();
			return shells.length > 0 ? shells[0] : null;
		}
	}

	public static Shell getFullScreenToolbar(final QuranForm quranForm) {
		ResourceManager res = ResourceManager.getInstance();
		Shell shell = getShell();
		final Display display = shell.getDisplay();
		final Shell floatShell = new Shell(shell, SWT.BORDER | SWT.ON_TOP | SWT.TOOL | SWT.LEFT_TO_RIGHT);
		FillLayout fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 5;
		floatShell.setLayout(fl);
		ToolBar bar = new ToolBar(floatShell, SWT.FLAT);

		final ToolItem item = new ToolItem(bar, SWT.CHECK);
		item.setSelection(true);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				quranForm.setFullScreen(item.getSelection(), false);
			}
		});

		Listener l = new Listener() {
			Point origin;

			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseDown:
					origin = new Point(e.x, e.y);
					break;
				case SWT.MouseUp:
					origin = null;
					break;
				case SWT.MouseMove:
					if (origin != null) {
						Point p = display.map(floatShell, null, e.x, e.y);
						floatShell.setLocation(p.x - origin.x, p.y - origin.y);
					}
					break;
				}
			}
		};
		floatShell.addListener(SWT.MouseDown, l);
		floatShell.addListener(SWT.MouseUp, l);
		floatShell.addListener(SWT.MouseMove, l);

		item.setToolTipText(quranForm.meaning("TOGGLE_FULL_SCREEN"));
		item.setImage(new Image(shell.getDisplay(), res.getString("icon.toolbar.fullScreen")));
		floatShell.pack();
		floatShell.open();

		quranForm.getShell().forceFocus();
		return floatShell;
	}

	/**
	 * This method opens a file chooser dialog and selects file filtering with the given wildcards.
	 * 
	 * @param parentShall
	 * @param filterNames names of the filters
	 * @param filterWildcards wildcard filters (e.g. *.zip)
	 * @return a 0-item list if action canceled, no item was selected or selected items did not fit the
	 *         extension criteria. Otherwise, returns a list of selected files (of type <tt>java.io.File</tt>).
	 * @throws IOException if any exception occurred during importing.
	 */
	public static List<File> importFileDialog(Shell parentShall, String[] filterNames, String[] filterWildcards)
			throws IOException {
		return importFileDialog(parentShall, filterNames, filterWildcards, true);
	}

	/**
	 * This method opens a file chooser dialog and selects file filtering with the given wildcards.
	 * 
	 * @param parentShall
	 * @param filterNames names of the filters
	 * @param filterWildcards wildcard filters (e.g. *.zip)
	 * @param multi indicates whether this open dialog can select multiple or single items
	 * @return a 0-item list if action canceled, no item was selected or selected items did not fit the
	 *         extension criteria. Otherwise, returns a list of selected files (of type <tt>java.io.File</tt>).
	 * @throws IOException if any exception occurred during importing.
	 */
	public static List<File> importFileDialog(Shell parentShall, String[] filterNames, String[] filterWildcards,
			boolean multi) throws IOException {
		FileDialog fd = new FileDialog(parentShall, SWT.OPEN | (multi ? SWT.MULTI : SWT.SINGLE));
		// fd.setFilterPath(GlobalConfig.getDefaultStartFolder()); // this code is a real pain!
		fd.setFilterNames(filterNames);
		fd.setFilterExtensions(filterWildcards); // Windows wild card
		fd.setText(lang.getMeaning("OPEN"));

		
		FileFilter fileFilter = new WildcardFileFilter(filterWildcards[0].split(";"));
		//this needs to be like this due to a mismatch between the WildcardFileFilter concept
		//of multi-extension wild card and the one use in the file dialog.
		
		List<File> fileList = new ArrayList<File>();

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
					return new ArrayList<File>();
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

	public static void showWarning(String msg) {
		show(msg, lang.getMeaning("WARNING"), SWT.ICON_WARNING | lang.getSWTDirection());
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 14, 2010
 */
package net.sf.zekr.ui;

import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 */
public class QuestionPromptForm extends BaseForm {
	public static final String FORM_ID = "QUESTION_PROMPT_FORM";

	private QuestionListener questionListener;
	private boolean hasProgress;
	private String title;
	private String[] options;
	private int selectedOption;
	private String question;
	private ProgressBar progessBar;
	private Button okBut;

	private boolean done = false;

	private Button buts[];

	/**
	 * @param parent parent shell
	 * @param options answer options
	 * @param selectedOption option number to be selected by default. This field is 0-based.
	 * @param question the string to be placed as a question on the top of the dialog
	 * @param title the text to be displayed as a title of this dialog
	 * @param hasProgress specifies whether this dialog should show a progress bar or not
	 * @param listener listener to be called upon pressing OK or Cancel
	 */
	public QuestionPromptForm(Shell parent, String[] options, int selectedOption, String question, String title,
			boolean hasProgress, QuestionListener questionListener) {
		this.parent = parent;
		this.options = options;
		this.selectedOption = selectedOption;
		this.title = title;
		this.question = question;
		this.hasProgress = hasProgress;
		this.questionListener = questionListener;
		init();
	}

	public QuestionPromptForm(Shell parent, String[] options, String question, String title, boolean hasProgress,
			QuestionListener questionListener) {
		this(parent, options, 0, question, title, hasProgress, questionListener);
	}

	public void init() {
		display = parent.getDisplay();
		shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | lang.getSWTDirection());
		shell.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		shell.setText(title);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (!done) {
					canceled();
				}
			}
		});

		if (hasProgress) {
			shell.addListener(EventProtocol.CUSTOM_ZEKR_EVENT, new Listener() {
				public void handleEvent(Event event) {
					if (EventProtocol.IMPORT_PROGRESS.equals(event.data)) {
						int p = Math.max(Math.min(event.detail, 100), 0);
						progessBar.setSelection(p);
					} else if (EventProtocol.IMPORT_PROGRESS_DONE.equals(event.data)
							|| EventProtocol.IMPORT_PROGRESS_FAILED.equals(event.data)) {
						
						if (EventProtocol.IMPORT_PROGRESS_DONE.equals(event.data)) {
							progessBar.setSelection(100);
						}
						if (questionListener != null) {
							questionListener.done();
						}
						done = true;
						shell.close();
					}
				}
			});
		}

		GridLayout gl = new GridLayout(2, false);
		Composite body = new Composite(shell, SWT.NONE);
		body.setLayout(gl);

		Composite imageComp = new Composite(body, SWT.NONE);
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
		Label l = new Label(body, SWT.NONE);
		l.setText(question);
		l.setLayoutData(gd);

		buts = new Button[options.length];
		for (int i = 0; i < options.length; i++) {
			gd = new GridData();
			gd.horizontalIndent = 10;
			if (i == 0) {
				gd.verticalIndent = 10;
			} else {
				gd.verticalIndent = 2;
			}
			gd.horizontalSpan = 2;

			buts[i] = new Button(body, SWT.RADIO);
			buts[i].setText(options[i]);
			buts[i].setLayoutData(gd);
		}

		if (buts.length > selectedOption) {
			buts[selectedOption].setSelection(true);
			buts[selectedOption].forceFocus();
		}

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 5;
		gd.heightHint = 14;
		progessBar = new ProgressBar(body, SWT.SMOOTH | SWT.HORIZONTAL);
		progessBar.setMinimum(0);
		progessBar.setMaximum(100);
		progessBar.setSelection(0);
		progessBar.setVisible(false);
		progessBar.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;
		gd.horizontalSpan = 2;
		gd.verticalIndent = 5;

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(body, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		okBut = new Button(butComposite, SWT.NONE);
		okBut.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		okBut.pack();
		okBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = -1;
				for (int i = 0; i < buts.length; i++) {
					if (buts[i].getSelection()) {
						selection = i;
						break;
					}
				}
				if (hasProgress) {
					progessBar.setVisible(true);
					okBut.setEnabled(false);
					for (Button button : buts) {
						button.setEnabled(false);
					}
					if (questionListener != null) {
						questionListener.start(selection);
					}
				} else {
					if (questionListener != null) {
						questionListener.done();
					}
					shell.close();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});

		shell.setDefaultButton(okBut);

		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		// set the OK and CANCEL buttons to the same length
		int buttonLength = FormUtils.buttonLength(80, okBut, cancel);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		okBut.setLayoutData(rdOk);
		cancel.setLayoutData(rdCancel);
	}

	private void canceled() {
		logger.info("Import process canceled.");
		questionListener.cancel();
	}

	@Override
	public void show() {
		shell.pack();
		if (shell.getSize().x < 350) {
			shell.setSize(350, shell.getSize().y);
		}
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
		// loopEver();
	}

	public String getFormId() {
		return FORM_ID;
	}

	public static void main(String[] args) {
		Display d = Display.getDefault();
		Shell shell = new Shell(d, SWT.SHELL_TRIM);
		shell.open();

		QuestionListener listener = new QuestionListener() {
			public void done() {
			}

			public void start(int result) {
			}

			public void cancel() {
			}
		};
		QuestionPromptForm qpf = new QuestionPromptForm(shell, new String[] { "Option 1 is ...", "Option 2 is ..." },
				"Where do you want to go today?", "Question", true, listener);
		qpf.show();
	}
}

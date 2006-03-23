/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 7, 2005
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.3
 */
public class AboutForm extends BaseForm {
	private final static Logger logger = Logger.getLogger(AboutForm.class);
	Shell parent;

	public AboutForm(Shell parent) {
		this.parent = parent;
		title = langEngine.getMeaning("ABOUT") + " " + langEngine.getMeaning("APP_NAME");
		display = parent.getDisplay();
		init();
	}

	public void init() {
		GridLayout gl;
		GridData gd;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		shell.setText(title);
		gl = new GridLayout(2, false);
		shell.setLayout(gl);
		shell.setSize(460, 155);
		shell.setFocus();

		Composite imageComp = new Composite(shell, SWT.NONE);
		final Image image = new Image(display, resource.getString("image.smallLogo"));
		gd = new GridData(GridData.CENTER);
		gd.heightHint = image.getBounds().height;
		gd.widthHint = image.getBounds().width;
		imageComp.setToolTipText(langEngine.getMeaning("APP_NAME"));
		imageComp.setLayoutData(gd);
		imageComp.setBounds(image.getBounds());
		imageComp.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		Composite detailCom = new Composite(shell, langEngine.getSWTDirection());
		detailCom.setLayout(new FillLayout(SWT.VERTICAL));
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 200;
		detailCom.setLayoutData(gd);
		Link link = new Link(detailCom, SWT.NONE);
		String s = langEngine.getMeaning("APP_FULL_NAME")
				+ ".\n\t<a href=\"http://siahe.com/zekr\">http://siahe.com/zekr</a>\n";

		link.setText(s);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new Thread() {
					public void run() {
						Program.launch(GlobalConfig.HOME_PAGE);
					}
				}.start();
			}
		});

		Text text = new Text(detailCom, SWT.MULTI | SWT.WRAP | SWT.SCROLL_LINE);
		text.setText(langEngine.getMeaning("COPYRIGHT_DISCLAIMER"));
		text.setEditable(false);
		text.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		Composite buttons = new Composite(shell, SWT.NO_FOCUS);
		buttons.setLayoutData(new GridData(GridData.FILL));
		buttons.setLayout(new FillLayout(SWT.HORIZONTAL));

		Button forceGC = new Button(buttons, SWT.PUSH);
		forceGC.setText("Force GC");

		final Label mem = new Label(shell, SWT.NONE);
		mem.setText(getMemText());

		forceGC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.gc();
				mem.setText(getMemText());
			}
		});
	}

	/**
	 * @return <tt>used memory / total heap memory</tt>
	 */
	private String getMemText() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return (total - free) + " / " + total;
	}
}

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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class AboutForm extends BaseForm {
	private final static Logger logger = Logger.getLogger(AboutForm.class);

	public AboutForm(Display display) {
		this.display = display;
		title = langEngine.getMeaning("ABOUT") + " " + langEngine.getMeaning("APP_NAME");
		init();
	}

	public void init() {
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		shell.setText(title);
		shell.setFocus();

		shell.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC)
					shell.close();
			}
		});

		Composite imageComp = new Composite(shell, SWT.BORDER);
		final Image image = new Image(display, resource.getString("image.smallLogo"));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = image.getBounds().height;
		gd.widthHint = image.getBounds().width;
		imageComp.setLayoutData(gd);
		imageComp.setBounds(image.getBounds());
		imageComp.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		Composite detailCom = new Composite(shell, langEngine.getSWTDirection());
		detailCom.setLayout(new RowLayout(SWT.VERTICAL));
		Link link = new Link(detailCom, SWT.NONE);
		String s = langEngine.getMeaning("APP_FULL_NAME")
				+ ".\n\t<a href=\"http://siahe.com/zekr\">http://www.siahe.com/zekr</a>\n";

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
		text.setBackground(shell.getBackground());
		
		shell.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ESC)
					shell.close();
			}
		});

		shell.setLayout(new GridLayout(2, false));
		shell.pack();
	}
}

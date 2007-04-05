/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 7, 2005
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.BrowserShop;
import net.sf.zekr.common.config.GlobalConfig;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class AboutForm extends BaseForm {
	private Shell parent;
	private Shell shell;
	private Display display;
	private String title;
	private Label mem;

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
				new Image(display, resource.getString("icon.form32")), new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")), new Image(display, resource.getString("icon.form256"))});
		shell.setText(title);
		shell.setLayout(new FillLayout());

		gl = new GridLayout(2, false);
		Composite body = new Composite(shell, SWT.NONE | langEngine.getSWTDirection());
		body.setLayout(gl);

		Composite imageComp = new Composite(body, SWT.NONE);
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

		gl = new GridLayout(1, false);
		gl.marginWidth = gl.marginHeight = 0;
		Composite detailCom = new Group(body, langEngine.getSWTDirection());
		detailCom.setLayout(gl);
		gd = new GridData(GridData.FILL_BOTH);
		detailCom.setLayoutData(gd);

		Link link = new Link(detailCom, SWT.NONE);
		String s = langEngine.getMeaning("APP_FULL_NAME")
				+ ".\n\t<a href=\"http://siahe.com/zekr\">http://siahe.com/zekr</a>\n";

		gd = new GridData(GridData.BEGINNING);
		link.setText(s);
		link.setLayoutData(gd);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BrowserShop.openLink(GlobalConfig.HOME_PAGE);
			}
		});

		gd = new GridData(GridData.BEGINNING);
		Label versionLabel = new Label(detailCom, SWT.NONE);
		versionLabel.setText(langEngine.getMeaning("VERSION") + ": " + GlobalConfig.ZEKR_VERSION);
		versionLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 50;
		Text text = new Text(detailCom, SWT.MULTI | SWT.WRAP | SWT.SCROLL_LINE | SWT.READ_ONLY);
		text.setText(langEngine.getMeaning("COPYRIGHT_DISCLAIMER"));
		text.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		text.setLayoutData(gd);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		Label delim = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		delim.setLayoutData(gd);

		Button forceGC = new Button(body, SWT.PUSH);
		forceGC.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		forceGC.setText("&Force GC");

		mem = new Label(body, SWT.NONE);
		mem.setText(getMemText());

		forceGC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.gc();
				updateMemText();
			}
		});

		shell.pack();
		shell.setSize(480, shell.getSize().y);
	}

	/**
	 * @return <tt>used memory / total heap memory</tt>
	 */
	private String getMemText() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return (total - free) + " / " + total;
	}

	protected Shell getShell() {
		return shell;
	}

	protected Display getDisplay() {
		return display;
	}

	private void updateMemText() {
		mem.setText(getMemText());
	}
}

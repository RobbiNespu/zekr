/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 7, 2005
 */
package net.sf.zekr.ui;

import java.text.DecimalFormat;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.util.HyperlinkUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private Label mem;

	public AboutForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();
		init();
	}

	public void init() {
		GridLayout gl;
		GridData gd;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")),
				new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")),
				new Image(display, resource.getString("icon.form256")) });
		shell.setText(meaning("TITLE"));
		shell.setLayout(new FillLayout());

		gl = new GridLayout(2, false);
		Composite body = new Composite(shell, SWT.NONE | lang.getSWTDirection());
		body.setLayout(gl);

		Composite imageComp = new Composite(body, SWT.NONE);
		final Image image = new Image(display, resource.getString("image.smallLogo"));
		gd = new GridData(GridData.CENTER);
		gd.heightHint = image.getBounds().height;
		gd.widthHint = image.getBounds().width;
		imageComp.setToolTipText(lang.getMeaning("APP_NAME"));
		imageComp.setLayoutData(gd);
		imageComp.setBounds(image.getBounds());
		imageComp.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		gl = new GridLayout(1, false);
		gl.marginWidth = gl.marginHeight = 0;
		Composite detailCom = new Group(body, lang.getSWTDirection());
		detailCom.setLayout(gl);
		gd = new GridData(GridData.FILL_BOTH);
		detailCom.setLayoutData(gd);

		Link link = new Link(detailCom, SWT.NONE);
		String s = lang.getMeaning("APP_FULL_NAME") + ".\n\t<a href=\"" + GlobalConfig.HOME_PAGE + "\">"
				+ GlobalConfig.HOME_PAGE + "</a>\n";

		gd = new GridData(GridData.BEGINNING);
		link.setText(s);
		link.setLayoutData(gd);
		link.setData(FormUtils.URL_DATA, GlobalConfig.HOME_PAGE);
		FormUtils.addLinkListener(link);

		gd = new GridData(GridData.BEGINNING);
		Label versionLabel = new Label(detailCom, SWT.NONE);
		versionLabel.setText(lang.getMeaning("VERSION") + ": " + GlobalConfig.ZEKR_VERSION);
		versionLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 55;
		Text text = new Text(detailCom, SWT.MULTI | SWT.WRAP | SWT.SCROLL_LINE | SWT.READ_ONLY);
		text.setText(lang.getMeaning("COPYRIGHT_DISCLAIMER"));
		text.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		text.setLayoutData(gd);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;

		Label delim = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		delim.setLayoutData(gd);

		gd = new GridData(SWT.END, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		Link logLink = new Link(body, SWT.NONE);
		logLink.setText("<a>" + meaning("VIEW_LOG") + "</a>");
		logLink.setLayoutData(gd);
		logLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				logger.debug("Open log file: " + Logger.LOG_FILE_PATH);
				HyperlinkUtils.openEditor(Logger.LOG_FILE_PATH);
			}
		});

		if (GlobalConfig.DEBUG_MODE) {
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
		}

		shell.pack();
		shell.setSize(480, shell.getSize().y);
	}

	private String meaning(String key) {
		return lang.getMeaningById("ABOUT", key);
	}

	/**
	 * @return <tt>used memory / total heap memory</tt>
	 */
	private String getMemText() {
		DecimalFormat df = new DecimalFormat("###,###");
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		String used = df.format((total - free) / 1024);
		String max = df.format(total / 1024);
		return used + " / " + max;
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

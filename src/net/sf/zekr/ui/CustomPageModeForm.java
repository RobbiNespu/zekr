/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.ui;

import java.util.Collection;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.page.IPagingData;
import net.sf.zekr.ui.helper.FormUtils;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CustomPageModeForm extends BaseForm {
	private static final String FORM_ID = "CUSTOM_PAGE_MODE";
	private final ApplicationConfig conf = ApplicationConfig.getInstance();
	private Button editBut;
	private org.eclipse.swt.widgets.List listWidget;
	private String[] listItems;

	public CustomPageModeForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);
		shell.setLayout(new FillLayout());
		shell.setText(lang.getMeaning("PAGE_MODE"));
		//		shell.setImages(new Image[] { new Image(display, resource.getString("icon.options16")),
		//				new Image(display, resource.getString("icon.options32")) });
		makeForm();
		shell.pack();
		shell.setSize(300, 300);
		shell.setLocation(FormUtils.getCenter(parent, shell));
	}

	private void makeForm() {
		Composite body = new Composite(shell, lang.getSWTDirection());
		body.setLayout(new GridLayout(1, false));

		GridData gd = new GridData(GridData.FILL_BOTH);
		listWidget = new org.eclipse.swt.widgets.List(body, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		listWidget.setLayoutData(gd);

		Collection pagings = conf.getQuranPaging().getAllPagings();
		listItems = new String[pagings.size()];
		int i = 0;
		for (Iterator iterator = pagings.iterator(); iterator.hasNext(); i++) {
			IPagingData paging = (IPagingData) iterator.next();
			listItems[i] = paging.toString();
		}

		listWidget.setItems(listItems);

		gd = new GridData();
		gd.horizontalAlignment = SWT.LEAD;

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		Composite manageButComposite = new Composite(body, SWT.NONE);
		manageButComposite.setLayout(rl);
		manageButComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd = new RowData();
		rd.width = 40;

		gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(gd);

		gd = new GridData();
		gd.horizontalAlignment = SWT.TRAIL;

		rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(body, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		Button okBut = new Button(butComposite, SWT.PUSH);
		Button cancelBut = new Button(butComposite, SWT.PUSH);
		okBut.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		okBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				listWidget.getSelectionIndices();
				shell.close();
			};
		});

		shell.setDefaultButton(okBut);

		cancelBut.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancelBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				shell.close();
			};
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		// set the OK and CANCEL buttons to the same length		
		int buttonLength = FormUtils.buttonLength(GlobalGuiConfig.BUTTON_WIDTH, okBut, cancelBut);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		okBut.setLayoutData(rdOk);
		cancelBut.setLayoutData(rdCancel);
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}
}

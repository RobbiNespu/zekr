/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sf.zekr.engine.page.FixedAyaPagingData;
import net.sf.zekr.engine.page.HizbQuarterPagingData;
import net.sf.zekr.engine.page.IPagingData;
import net.sf.zekr.engine.page.JuzPagingData;
import net.sf.zekr.engine.page.PagingException;
import net.sf.zekr.engine.page.QuranPaging;
import net.sf.zekr.engine.page.SuraPagingData;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CustomPageModeForm extends BaseForm {
	private Button editBut;
	private org.eclipse.swt.widgets.List listWidget;
	private String[] listItems;
	private List<String> listModel = new ArrayList<String>();
	private int selectedMode = -1;

	public CustomPageModeForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();
		shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setLayout(new FillLayout());
		shell.setText(meaning("TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.paging")), });
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

		QuranPaging qp = config.getQuranPaging();
		Collection<IPagingData> pagings = config.getQuranPaging().getAllPagings();
		listItems = new String[pagings.size()];
		IPagingData[] builtinPagings = new IPagingData[] { qp.get(FixedAyaPagingData.ID), qp.get(SuraPagingData.ID),
				qp.get(JuzPagingData.ID), qp.get(HizbQuarterPagingData.ID) };

		List<IPagingData> pagingList = Arrays.asList(builtinPagings);
		List<String> itemList = new ArrayList<String>();
		for (IPagingData paging : pagings) {
			if (!pagingList.contains(paging)) {
				try {
					logger.debug("Try to load paging data which are not yet loaded.");
					paging.load();
					itemList.add(paging.toString());
					listModel.add(paging.getId());
				} catch (PagingException e) {
					logger.error(e);
				}
			}
		}

		listWidget.setItems(itemList.toArray(new String[0]));
		listWidget.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (listWidget.getSelectionCount() > 0) {
					doOk();
				}
			}
		});

		int def = listModel.indexOf(config.getQuranPaging().getDefault().getId());
		if (def > -1) {
			listWidget.select(def);
		}

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
				doOk();
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

	public String getPagingMode() {
		if (selectedMode == -1) {
			return null;
		}
		return listModel.get(selectedMode);
	}

	private void doOk() {
		selectedMode = listWidget.getSelectionIndex();
		shell.close();
	}

	public String getFormId() {
		return "PAGING_MODE";
	}
}

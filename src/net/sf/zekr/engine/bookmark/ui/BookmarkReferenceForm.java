/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 29, 2007
 */
package net.sf.zekr.engine.bookmark.ui;

import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Mohsen Saboorian
 */
@SuppressWarnings("unchecked")
public class BookmarkReferenceForm extends BaseForm {
	public static final String FORM_ID = "BOOKMARK_REF";
	private Shell parent;
	private IQuranLocation location;
	private Table table;
	private List referenceList;

	public BookmarkReferenceForm(Shell parent, List refs, IQuranLocation location) {
		try {
			this.parent = parent;
			this.location = location;
			this.referenceList = refs;
			init();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
	}

	private void init() {
		shell = createShell(parent, lang.getSWTDirection() | SWT.TOOL | SWT.SHELL_TRIM);
		shell.setText(meaning("TITLE", location.toString()));
		FillLayout fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 2;
		shell.setLayout(fl);

		Group body = new Group(shell, SWT.NONE);
		// body.setText(meaning("TITLE", location.toString()));
		fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 2;
		body.setLayout(fl);

		// gd = new GridData(GridData.FILL_BOTH);
		table = new Table(body, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		// table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn idCol = new TableColumn(table, SWT.NONE);
		idCol.setText(lang.getMeaning("NAME"));
		idCol.setWidth(120);

		TableColumn nameCol = new TableColumn(table, SWT.NONE);
		nameCol.setText(lang.getMeaning("PATH"));
		nameCol.setWidth(150);

		for (int i = 0; i < referenceList.size(); i++) {
			Object[] entry = (Object[]) referenceList.get(i);
			TableItem ti = new TableItem(table, SWT.NONE);
			String path = CollectionUtils.toString((List) entry[0], "/");
			ti.setText(new String[] { ((BookmarkItem) entry[1]).getName(), path });
			ti.setData(entry[1]);
		}

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				TableItem ti = (TableItem) e.item;
				BookmarkItem bmi = (BookmarkItem) ti.getData();
				int dir = LanguageEngine.getSWTDirection(config.getBookmark().getDirection());
				BookmarkItemForm bif = new BookmarkItemForm(parent, bmi, dir);
				logger.debug("Open read-only bookmark item: " + bmi);
				bif.open(true);
				shell.forceActive();
			}
		});
	}

	public void open() {
		shell.pack();
		if (shell.getSize().y > 300)
			shell.setSize(shell.getSize().x, 300);
		if (shell.getSize().y < 140)
			shell.setSize(shell.getSize().x, 140);

		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}

	public String getFormId() {
		return FORM_ID;
	}
}

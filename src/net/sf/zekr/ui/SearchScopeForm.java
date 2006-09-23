/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 19, 2006
 */
package net.sf.zekr.ui;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SearchScopeForm extends BaseForm {
	private Shell shell;
	protected Display display;
	private Table table;

	public SearchScopeForm(Display display) {
		this.display = display;
		shell = new Shell(this.display, SWT.SHELL_TRIM);
		FillLayout fl = new FillLayout();
		shell.setLayout(fl);
		shell.setText(langEngine.getMeaning("SEARCH_SCOPE"));

		init();
	}

	private void init() {
		Group body = new Group(shell, SWT.NONE);
		body.setLayout(new FillLayout());
		body.setText(langEngine.getMeaning("SEARCH_SCOPE"));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		
		
		
		table = new Table(body, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLayoutData(new FillLayout());
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn fromSuraCol = new TableColumn(table, SWT.NONE);
		fromSuraCol.setText("Sura from");
		fromSuraCol.setWidth(100);

		TableColumn fromAyaCol = new TableColumn(table, SWT.NONE);
		fromAyaCol.setText("Aya from");
		fromAyaCol.setWidth(100);

		TableColumn toSuraCol = new TableColumn(table, SWT.NONE);
		toSuraCol.setText("Sura to");
		toSuraCol.setWidth(100);

		TableColumn toAyaCol = new TableColumn(table, SWT.NONE);
		toAyaCol.setText("Aya to");
		toAyaCol.setWidth(100);


		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
        editor.horizontalAlignment = SWT.LEFT;

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(1));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(1, text.getText());
					}
				});
				newEditor.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						Control oldEditor = editor.getEditor();
						if (oldEditor != null)
							oldEditor.dispose();
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 1);
			}
		});

	}

	public static void main(String[] args) {
		SearchScopeForm ssf = new SearchScopeForm(new Display());
		ssf.show();
		ssf.loopEver();
	}

	protected Shell getShell() {
		return shell;
	}

	protected Display getDisplay() {
		return display;
	}
}

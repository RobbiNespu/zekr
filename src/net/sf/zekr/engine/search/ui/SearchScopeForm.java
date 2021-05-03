/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 19, 2006
 */
package net.sf.zekr.engine.search.ui;

import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.search.IllegalSearchScopeItemException;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.SearchScopeItem;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Mohsen Saboorian
 */
public class SearchScopeForm extends BaseForm {
	private Table table;
	private TableEditor editor;
	private Composite body;
	private boolean canceled = true;
	private SearchScope searchScope;
	private Button addBut, remBut;

	public SearchScopeForm(Shell parent) {
		searchScope = new SearchScope();
		searchScope.add(new SearchScopeItem(1, 1, 114, 7, false));

		this.parent = parent;
		_init();
	}

	public SearchScopeForm(Shell parent, SearchScope searchScope) {
		this.searchScope = searchScope;
		this.parent = parent;
		_init();
	}

	private void _init() {
		display = parent.getDisplay();
		shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

		FillLayout fl = new FillLayout();
		shell.setLayout(fl);
		shell.setText(meaning("TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.searchScope.edit16")),
				new Image(display, resource.getString("icon.searchScope.edit32")) });

		init();
		shell.pack();
		shell.setSize(shell.getSize().x, 400);
	}

	private void init() {
		body = new Composite(shell, lang.getSWTDirection());
		body.setLayout(new GridLayout(1, false));

		GridData gd = new GridData(GridData.FILL_BOTH);

		GridLayout gl = new GridLayout(2, false);
		Group tableGroup = new Group(body, SWT.NONE);
		tableGroup.setLayoutData(gd);
		tableGroup.setLayout(gl);
		tableGroup.setText(meaning("SEARCH_SCOPE"));

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;

		table = new Table(tableGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() == 0)
					remBut.setEnabled(false);
				else
					remBut.setEnabled(true);
			}
		});
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL) {
					remove();
				}
			}
		});

		gd = new GridData();
		gd.horizontalSpan = 2;

		Composite addRemComp = new Composite(tableGroup, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 4;

		addRemComp.setLayout(rl);
		addRemComp.setLayoutData(gd);

		addBut = new Button(addRemComp, SWT.PUSH);
		remBut = new Button(addRemComp, SWT.PUSH);

		RowData rd = new RowData();
		rd.width = 40;
		addBut.setLayoutData(rd);

		rd = new RowData();
		rd.width = 40;
		remBut.setLayoutData(rd);

		addBut.setToolTipText(lang.getMeaning("ADD"));
		addBut.setImage(new Image(display, resource.getString("icon.add")));
		addBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addNewItem(new SearchScopeItem());
			}
		});

		remBut.setToolTipText(lang.getMeaning("DELETE"));
		remBut.setImage(new Image(display, resource.getString("icon.remove")));
		remBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				remove();
			}
		});

		remBut.setEnabled(false);

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.TRAIL;

		rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(body, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		Button okBut = new Button(butComposite, SWT.PUSH);
		Button cancelBut = new Button(butComposite, SWT.PUSH);
		okBut.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		okBut.pack();
		okBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canceled = false;
				try {
					updateSearchScope();
				} catch (IllegalSearchScopeItemException issie) {
					logger.warn("Illegall search scope item: " + issie);
					MessageBoxUtils.showError(meaning("ILLEGAL_SEARCH_SCOPE"));
					return;
				}
				shell.close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		shell.setDefaultButton(okBut);

		cancelBut.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancelBut.pack();
		cancelBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canceled = true;
				shell.close();
			}
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		// set the OK and CANCEL buttons to the same length
		int buttonLength = FormUtils.buttonLength(80, okBut, cancelBut);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		okBut.setLayoutData(rdOk);
		cancelBut.setLayoutData(rdCancel);

		TableColumn fromSuraCol = new TableColumn(table, SWT.NONE);
		fromSuraCol.setText(meaning("FROM_SURA"));
		fromSuraCol.setWidth(100);

		TableColumn fromAyaCol = new TableColumn(table, SWT.NONE);
		fromAyaCol.setText(meaning("FROM_AYA"));
		fromAyaCol.pack();

		TableColumn toSuraCol = new TableColumn(table, SWT.NONE);
		toSuraCol.setText(meaning("TO_SURA"));
		toSuraCol.setWidth(100);

		TableColumn toAyaCol = new TableColumn(table, SWT.NONE);
		toAyaCol.setText(meaning("TO_AYA"));
		toAyaCol.pack();

		TableColumn excludeCol = new TableColumn(table, SWT.NONE);
		excludeCol.setText(lang.getMeaning("EXCLUDE"));
		excludeCol.pack();

		for (SearchScopeItem ssi : searchScope.getScopeItems()) {
			addNewItem(ssi);
		}

		table.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						final CCombo itemEditor;
						if (rect.contains(pt)) {
							final int column = i;
							itemEditor = new CCombo(table, SWT.NONE | SWT.READ_ONLY);
							itemEditor.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									CCombo c = (CCombo) e.widget;
									item.setData(String.valueOf(column), new Integer(c.getSelectionIndex() + 1));
								}
							});
							itemEditor.setVisibleItemCount(10);
							if (column == 4) {
								itemEditor.setItems(new String[] { lang.getMeaning("NO"), lang.getMeaning("YES") });
							} else {
								if (column % 2 == 0) {
									itemEditor.setItems(QuranPropertiesUtils.getIndexedSuraNames());
								} else {
									int suraNum = ((Integer) item.getData(String.valueOf(column - 1))).intValue();
									itemEditor.setItems(QuranPropertiesUtils.getSuraAyas(suraNum));
								}
							}
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, itemEditor.getText());
										itemEditor.dispose();
										if (column % 2 == 0 && column != 4) {
											// reset aya number to 1 if aya is not in range of selected sura's aya count
											if (!QuranLocation.isValidLocation(((Integer) item.getData(String.valueOf(column)))
													.intValue(), ((Integer) item.getData(String.valueOf(column + 1))).intValue())) {
												item.setText(column + 1, "1");
												item.setData(String.valueOf(column + 1), new Integer(1));
											}
										}
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, itemEditor.getText());
											if (column % 2 == 0 && column != 4) {
												// reset aya number to 1 if aya is not in range of selected sura's aya count
												if (!QuranLocation.isValidLocation(((Integer) item.getData(String.valueOf(column)))
														.intValue(), ((Integer) item.getData(String.valueOf(column + 1))).intValue())) {
													item.setText(column + 1, "1");
													item.setData(String.valueOf(column + 1), new Integer(1));
												}
											}
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											itemEditor.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							itemEditor.addListener(SWT.FocusOut, textListener);
							itemEditor.addListener(SWT.Traverse, textListener);

							editor = new TableEditor(table);
							editor.horizontalAlignment = SWT.LEFT;
							editor.grabHorizontal = true;
							editor.setEditor(itemEditor, item, i);

							itemEditor.setText(item.getText(i));
							itemEditor.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
	}

	private TableItem addNewItem(SearchScopeItem ssi) {
		String sf = QuranPropertiesUtils.getIndexedSuraNames()[ssi.getSuraFrom() - 1];
		int af = ssi.getAyaFrom();
		String st = QuranPropertiesUtils.getIndexedSuraNames()[ssi.getSuraTo() - 1];
		int at = ssi.getAyaTo();
		String exclText = ssi.isExclusive() ? lang.getMeaning("YES") : lang.getMeaning("NO");

		final TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { sf, String.valueOf(af), st, String.valueOf(at), exclText });
		item.setData("0", new Integer(ssi.getSuraFrom()));
		item.setData("1", new Integer(af));
		item.setData("2", new Integer(ssi.getSuraTo()));
		item.setData("3", new Integer(at));
		item.setData("4", ssi.isExclusive() ? new Integer(2) : new Integer(1));

		return item;
	}

	public void updateSearchScope() {
		searchScope = new SearchScope();
		TableItem[] ti = table.getItems();
		for (int i = 0; i < ti.length; i++) {
			int sf = ((Integer) ti[i].getData("0")).intValue();
			int af = ((Integer) ti[i].getData("1")).intValue();
			int st = ((Integer) ti[i].getData("2")).intValue();
			int at = ((Integer) ti[i].getData("3")).intValue();
			int excl = ((Integer) ti[i].getData("4")).intValue(); // 1 = false, 2 = true
			SearchScopeItem ssi = new SearchScopeItem(sf, af, st, at, excl == 1 ? false : true);
			searchScope.add(ssi);
		}
	}

	public SearchScope getSearchScope() {
		return searchScope;
	}

	/**
	 * @return <code>true</code> if ok pressed, <code>false</code> otherwise.
	 */
	public boolean open() {
		shell.setLocation(FormUtils.getCenter(parent, shell));
		super.show();
		loopEver();
		return !canceled;
	}

	private void remove() {
		int[] rows = table.getSelectionIndices();
		if (rows.length <= 0)
			return;
		for (int i = rows.length - 1; i >= 0; i--) {
			table.remove(rows[i]);
		}
		remBut.setEnabled(false);
	}

	public String getFormId() {
		return "SEARCH_SCOPE_FORM";
	}
}

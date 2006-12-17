/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 1, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class BookmarkItemForm extends BaseForm {

	private Shell shell;
	private Display display;
	private Table table;
	private TableEditor editor;
	private List excList = new ArrayList();
	private Composite body;
	private Shell parent;
	private boolean canceled = true;
	private BookmarkItem bookmarkItem;
	private Text nameText;
	private Text descText;

	public static final String FORM_ID = "BOOKMARK_ITEM_FORM";

	public BookmarkItemForm(Shell parent, BookmarkItem bookmarkItem) {
		this.parent = parent;
		this.bookmarkItem = bookmarkItem;
		_init();
	}

	public BookmarkItemForm(Shell parent, boolean isFolder) {
		this.parent = parent;
		bookmarkItem = new BookmarkItem();
		bookmarkItem.setFolder(isFolder);
		bookmarkItem.setLocations(new ArrayList());
		bookmarkItem.setDescription("");
		bookmarkItem.setName(meaning(isFolder ? "NEW_FOLDER" : "NEW_BOOKMARK"));
		_init();
	}

	private void _init() {
		display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);
		FillLayout fl = new FillLayout();
		shell.setLayout(fl);
		shell.setText(meaning("TITLE"));
		shell.setImage(new Image(display, resource.getString(bookmarkItem.isFolder() ? "icon.bookmark.closeFolder" : "icon.bookmark.item")));

		init();
		// shell.pack();
		shell.setSize(bookmarkItem.isFolder() ? 280: 360, bookmarkItem.isFolder() ? 220 : 350);
	}

	private void init() {
		body = new Composite(shell, langEngine.getSWTDirection());
		body.setLayout(new GridLayout(1, false));

		GridData gd = new GridData(GridData.FILL_BOTH);

		GridLayout gl = new GridLayout(2, false);
		Group tableGroup = new Group(body, SWT.NONE);
		tableGroup.setLayoutData(gd);
		tableGroup.setLayout(gl);
		tableGroup.setText(shell.getText());

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		Label label = new Label(tableGroup, SWT.NONE);
		label.setText(langEngine.getMeaning("NAME"));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText = new Text(tableGroup, SWT.BORDER);
		nameText.setText(bookmarkItem.getName());
		nameText.setLayoutData(gd);
		nameText.selectAll();

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label = new Label(tableGroup, SWT.NONE | SWT.BEGINNING);
		label.setText(langEngine.getMeaning("DESCRIPTION"));
		label.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 70;
		descText = new Text(tableGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		descText.setText(bookmarkItem.getDescription());
		descText.setLayoutData(gd);

		if (!bookmarkItem.isFolder()) {
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			gd.heightHint = 10;
			Label sep = new Label(tableGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
			sep.setLayoutData(gd);

			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			table = new Table(tableGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			table.setLayoutData(gd);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			gd = new GridData();
			gd.horizontalSpan = 2;

			Composite addRemComp = new Composite(tableGroup, SWT.NONE);
			RowLayout rl = new RowLayout(SWT.HORIZONTAL);
			rl.spacing = 4;

			addRemComp.setLayout(rl);
			addRemComp.setLayoutData(gd);

			Button addBut = new Button(addRemComp, SWT.PUSH);
			Button remBut = new Button(addRemComp, SWT.PUSH);

			RowData rd = new RowData();
			rd.width = 40;
			addBut.setLayoutData(rd);

			rd = new RowData();
			rd.width = 40;
			remBut.setLayoutData(rd);

			addBut.setToolTipText(langEngine.getMeaning("ADD"));
			addBut.setImage(new Image(display, resource.getString("icon.add")));
			addBut.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addNewItem(new QuranLocation(1, 1));
				}
			});

			remBut.setToolTipText(langEngine.getMeaning("DELETE"));
			remBut.setImage(new Image(display, resource.getString("icon.remove")));
			remBut.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int[] rows = table.getSelectionIndices();
					for (int i = rows.length - 1; i >= 0; i--) {
						table.remove(rows[i]);
					}
				}
			});
		}

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.TRAIL;

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(body, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 80;

		Button okBut = new Button(butComposite, SWT.PUSH);
		Button cancelBut = new Button(butComposite, SWT.PUSH);
		okBut.setText("&" + langEngine.getMeaning("OK"));
		okBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canceled = false;
				updateBookmarkItem();
				shell.close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		okBut.setLayoutData(rd);
		shell.setDefaultButton(okBut);

		rd = new RowData();
		rd.width = 80;

		cancelBut.setText("&" + langEngine.getMeaning("CANCEL"));
		cancelBut.setLayoutData(rd);
		cancelBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canceled = true;
				shell.close();
			}
		});

		if (!bookmarkItem.isFolder()) {
			TableColumn suraCol = new TableColumn(table, SWT.NONE);
			suraCol.setText(langEngine.getMeaning("SURA"));
			suraCol.setWidth(100);

			TableColumn ayaCol = new TableColumn(table, SWT.NONE);
			ayaCol.setText(langEngine.getMeaning("AYA"));
			ayaCol.pack();

			TableColumn locationCol = new TableColumn(table, SWT.NONE);
			locationCol.setText(langEngine.getMeaning("LOCATION"));
			locationCol.setWidth(100);

			for (Iterator iter = bookmarkItem.getLocations().iterator(); iter.hasNext();) {
				IQuranLocation loc = (IQuranLocation) iter.next();
				addNewItem(loc);
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
							final Control itemEditor;
							if (rect.contains(pt)) {
								final int column = i;
								if (column == 2) {
									Text t = new Text(table, SWT.NONE);
									t.setText((String) item.getData("2"));
									itemEditor = t;
								} else {
									CCombo cc = new CCombo(table, SWT.NONE | SWT.READ_ONLY);
									cc.addSelectionListener(new SelectionAdapter() {
										public void widgetSelected(SelectionEvent e) {
											CCombo c = (CCombo) e.widget;
											item.setData(String.valueOf(column), new Integer(c
													.getSelectionIndex() + 1));
											item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(), ((Integer) item.getData("1")).intValue()).toString());
										}
									});
									cc.setVisibleItemCount(10);
									if (column == 0) {
										cc.setItems(QuranPropertiesUtils.getIndexedSuraNames());
									} else if (column == 1) {
										int suraNum = ((Integer) item.getData(String.valueOf(column - 1)))
												.intValue();
										cc.setItems(QuranPropertiesUtils.getSuraAyas(suraNum));
									}
									itemEditor = cc;
								}
								Listener textListener = new Listener() {
									public void handleEvent(final Event e) {
										switch (e.type) {
										case SWT.FocusOut:
											if (column == 2)
												item.setText(column, ((Text) itemEditor).getText());
											else
												item.setText(column, ((CCombo) itemEditor).getText());
											itemEditor.dispose();
											if (column == 0) {
												item.setText(column + 1, "1");
												item.setData(String.valueOf(column + 1), new Integer(1));
												item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(), ((Integer) item.getData("1")).intValue()).toString());
											}
											break;
										case SWT.Traverse:
											switch (e.detail) {
											case SWT.TRAVERSE_RETURN:
												if (column == 2)
													item.setText(column, ((Text) itemEditor).getText());
												else
													item.setText(column, ((CCombo) itemEditor).getText());
												if (column == 0) {
													item.setText(column + 1, "1");
													item.setData(String.valueOf(column + 1), new Integer(1));
													item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(), ((Integer) item.getData("1")).intValue()).toString());
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

								if (column == 2)
									((Text) itemEditor).setText(item.getText(i));
								else
									((CCombo) itemEditor).setText(item.getText(i));
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
	}

	private TableItem addNewItem(IQuranLocation loc) {
		String sura = QuranPropertiesUtils.getIndexedSuraNames()[loc.getSura() - 1];
		int aya = loc.getAya();
		String suraAya = loc.toString();

		final TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { sura, "" + aya, suraAya });
		item.setData("0", new Integer(loc.getSura()));
		item.setData("1", new Integer(aya));
		item.setData("2", suraAya);

		return item;
	}

	public void updateBookmarkItem() {
		if (!bookmarkItem.isFolder()) {
			bookmarkItem = new BookmarkItem();
			List locations = new ArrayList();
			TableItem[] ti = table.getItems();
			for (int i = 0; i < ti.length; i++) {
				int sura = ((Integer) ti[i].getData("0")).intValue();
				int aya = ((Integer) ti[i].getData("1")).intValue();
				locations.add(new QuranLocation(sura, aya));
			}
			bookmarkItem.setLocations(locations);
			bookmarkItem.setFolder(false);
		} else {
			bookmarkItem = new BookmarkItem();
			bookmarkItem.setFolder(true);
		}
		bookmarkItem.setName(nameText.getText());
		bookmarkItem.setDescription(descText.getText());
	}

	public BookmarkItem getBookmarkItem() {
		return bookmarkItem;
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

	private String meaning(String key) {
		return langEngine.getMeaningById(FORM_ID, key);
	}

	protected Shell getShell() {
		return shell;
	}

	protected Display getDisplay() {
		return display;
	}

}

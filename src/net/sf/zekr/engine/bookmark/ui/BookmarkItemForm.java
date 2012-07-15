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
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * A form for viewing and managing a single bookmark item or folder.
 * 
 * @author Mohsen Saboorian
 */
public class BookmarkItemForm extends BaseForm {
	private Table table;
	private TableEditor editor;
	private Composite body;
	private boolean canceled = true;
	private BookmarkItem bookmarkItem;
	private Text nameText;
	private Text descText;
	private int bookmarkSetDirection;
	private Button okBut;
	private Button cancelBut;
	private Button addBut;
	private Button remBut;
	private boolean readOnly;
	private SashForm sashForm;

	public BookmarkItemForm(Shell parent, BookmarkItem bookmarkItem, int bookmarkSetDirection) {
		this.parent = parent;
		this.bookmarkItem = bookmarkItem;
		this.bookmarkSetDirection = bookmarkSetDirection;
		_init();
	}

	/**
	 * Makes a new instance of this class. The underling {@link BookmarkItem} is also created, but its ID is
	 * not assigned.
	 * 
	 * @param parent the parent shell
	 * @param isFolder
	 * @param bookmarkSetDirection
	 */
	public BookmarkItemForm(Shell parent, boolean isFolder, int bookmarkSetDirection) {
		this.parent = parent;
		this.bookmarkSetDirection = bookmarkSetDirection;
		bookmarkItem = new BookmarkItem();
		bookmarkItem.setFolder(isFolder);
		bookmarkItem.setLocations(new ArrayList<IQuranLocation>());
		bookmarkItem.setDescription("");
		bookmarkItem.setName(meaning(isFolder ? "NEW_FOLDER" : "NEW_BOOKMARK"));
		_init();
	}

	/**
	 * Makes a new instance of this class, as a bookmark item (not folder). It uses locationList to initialize
	 * {@link IQuranLocation}s this item refers to. The underling {@link BookmarkItem} is also created, but its
	 * ID is not assigned.<br>
	 * This constructor is used for stand-alone bookmarking.
	 * 
	 * @param parent the parent shell
	 * @param locationList a list of {@link IQuranLocation}s to be set as default locations to this bookmark
	 *           item
	 * @param bookmarkName default bookmark name (title). If this value is null, localized value for the key
	 *           NEW_BOOKMARK is used.
	 * @param bookmarkSetDirection
	 */
	public BookmarkItemForm(Shell parent, List<IQuranLocation> locationList, String bookmarkName,
			int bookmarkSetDirection) {
		try {
			this.parent = parent;
			this.bookmarkSetDirection = bookmarkSetDirection;
			bookmarkItem = new BookmarkItem();
			bookmarkItem.setFolder(false);
			bookmarkItem.setLocations(locationList);
			bookmarkItem.setDescription("");
			bookmarkItem.setName(bookmarkName != null ? bookmarkName : meaning("NEW_BOOKMARK"));
			_init();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
	}

	private void _init() {
		display = parent.getDisplay();

		shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		FillLayout fl = new FillLayout();
		shell.setLayout(fl);
		shell.setText(meaning("TITLE"));
		shell.setImage(new Image(display, resource.getString(bookmarkItem.isFolder() ? "icon.bookmark.closeFolder"
				: "icon.bookmark.item")));

		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (!bookmarkItem.isFolder()) {
					Rectangle r = shell.getBounds();
					int[] w = sashForm.getWeights();
					config.getProps().setProperty("view.bookmark.bookarkItemForm.location",
							new String[] { "" + r.x, "" + r.y, "" + r.width, "" + r.height });
					config.getProps().setProperty("view.bookmark.bookarkItemForm.sashWeight",
							new String[] { "" + w[0], "" + w[1] });
				}
			}
		});

		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		body = new Composite(shell, lang.getSWTDirection());
		body.setLayout(new GridLayout(1, false));

		GridData gd = new GridData(GridData.FILL_BOTH);
		GridLayout gl = new GridLayout(1, false);
		Group tableGroup = new Group(body, SWT.NONE);
		tableGroup.setLayoutData(gd);
		tableGroup.setLayout(gl);
		tableGroup.setText(shell.getText());

		gd = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(tableGroup, SWT.SMOOTH | SWT.VERTICAL);
		sashForm.setLayoutData(gd);
		sashForm.SASH_WIDTH = 6;

		gd = new GridData(GridData.FILL_BOTH);
		gl = new GridLayout(2, false);
		gl.marginWidth = gl.marginHeight = 0;
		Composite topComp = new Composite(sashForm, SWT.NONE);
		topComp.setLayout(gl);
		topComp.setLayoutData(gd);

		Label label = new Label(topComp, SWT.NONE);
		label.setText(lang.getMeaning("NAME"));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText = new Text(topComp, SWT.BORDER | bookmarkSetDirection);
		nameText.setText(bookmarkItem.getName());
		nameText.setLayoutData(gd);
		nameText.selectAll();

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label = new Label(topComp, SWT.NONE | SWT.BEGINNING);
		label.setText(lang.getMeaning("DESCRIPTION"));
		label.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 70;
		descText = new Text(topComp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | bookmarkSetDirection);
		descText.setText(bookmarkItem.getDescription());
		descText.setLayoutData(gd);

		if (!bookmarkItem.isFolder()) {
			gd = new GridData(GridData.FILL_BOTH);
			gl = new GridLayout(2, false);
			gl.marginWidth = gl.marginHeight = 0;
			Composite bottomComp = new Composite(sashForm, SWT.NONE);
			bottomComp.setLayout(gl);
			bottomComp.setLayoutData(gd);

			gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			table = new Table(bottomComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
			table.setLayoutData(gd);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (!readOnly) {
						if (table.getSelectionCount() == 0) {
							remBut.setEnabled(false);
						} else {
							remBut.setEnabled(true);
						}
					}
				}
			});

			gd = new GridData();
			gd.horizontalSpan = 2;

			Composite addRemComp = new Composite(bottomComp, SWT.NONE);
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
					addNewItem(new QuranLocation(1, 1));
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
		}

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.TRAIL;

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(body, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		okBut = new Button(butComposite, SWT.PUSH);
		cancelBut = new Button(butComposite, SWT.PUSH);
		okBut.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		okBut.pack();
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
		// set both OK and CANCEL to the same width
		int buttonLength = FormUtils.buttonLength(80, okBut, cancelBut);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		okBut.setLayoutData(rdOk);
		cancelBut.setLayoutData(rdCancel);

		if (!bookmarkItem.isFolder()) {
			TableColumn suraCol = new TableColumn(table, SWT.NONE);
			suraCol.setText(lang.getMeaning("SURA"));
			suraCol.setWidth(100);

			TableColumn ayaCol = new TableColumn(table, SWT.NONE);
			ayaCol.setText(lang.getMeaning("AYA"));
			ayaCol.pack();

			TableColumn locationCol = new TableColumn(table, SWT.NONE);
			locationCol.setText(lang.getMeaning("LOCATION"));
			locationCol.setWidth(100);

			for (IQuranLocation loc : bookmarkItem.getLocations()) {
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
									final Text t = new Text(table, SWT.LEFT_TO_RIGHT);
									t.setText((String) item.getData("2"));
									itemEditor = t;
								} else {
									CCombo cc = new CCombo(table, SWT.NONE | SWT.READ_ONLY);
									cc.addSelectionListener(new SelectionAdapter() {
										public void widgetSelected(SelectionEvent e) {
											CCombo c = (CCombo) e.widget;
											item.setData(String.valueOf(column), new Integer(c.getSelectionIndex() + 1));
										}
									});
									cc.setVisibleItemCount(10);
									if (column == 0) {
										cc.setItems(QuranPropertiesUtils.getIndexedSuraNames());
									} else if (column == 1) {
										int suraNum = ((Integer) item.getData(String.valueOf(column - 1))).intValue();
										cc.setItems(QuranPropertiesUtils.getSuraAyas(suraNum));
									}
									itemEditor = cc;
								}
								Listener textListener = new Listener() {
									public void handleEvent(final Event e) {
										switch (e.type) {
										case SWT.FocusOut:
											if (column == 2) {
												Text t = (Text) itemEditor;
												if (QuranLocation.isValidLocation(t.getText())) {
													IQuranLocation loc = new QuranLocation(t.getText());
													Integer sura = new Integer(loc.getSura());
													Integer aya = new Integer(loc.getAya());
													item.setText(0, QuranPropertiesUtils.getIndexedSuraName(sura.intValue()));
													item.setText(1, aya.toString());
													item.setData("0", sura);
													item.setData("1", aya);
													item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
															((Integer) item.getData("1")).intValue()).toString());
												}
												item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
														((Integer) item.getData("1")).intValue()).toString());
											} else {
												item.setText(column, ((CCombo) itemEditor).getText());
											}
											itemEditor.dispose();
											if (column == 0) {
												// reset aya number to 1 if aya is not in range of selected sura's aya count
												if (!QuranLocation.isValidLocation(((Integer) item.getData("0")).intValue(),
														((Integer) item.getData("1")).intValue())) {
													item.setText(1, "1");
													item.setData("1", new Integer(1));
												}
												item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
														((Integer) item.getData("1")).intValue()).toString());
											} else if (column == 1) {
												item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
														((Integer) item.getData("1")).intValue()).toString());
											}
											break;
										case SWT.Traverse:
											switch (e.detail) {
											case SWT.TRAVERSE_RETURN:
												if (column == 2) {
													Text t = (Text) itemEditor;
													if (QuranLocation.isValidLocation(t.getText())) {
														IQuranLocation loc = new QuranLocation(t.getText());
														Integer sura = new Integer(loc.getSura());
														Integer aya = new Integer(loc.getAya());
														item.setText(0, QuranPropertiesUtils.getIndexedSuraName(sura.intValue()));
														item.setText(1, aya.toString());
														item.setData("0", sura);
														item.setData("1", aya);
														item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
																((Integer) item.getData("1")).intValue()).toString());
													}
													item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
															((Integer) item.getData("1")).intValue()).toString());
												} else {
													item.setText(column, ((CCombo) itemEditor).getText());
												}
												if (column == 0) {
													// reset aya number to 1 if aya is not in range of selected sura's aya count
													if (!QuranLocation.isValidLocation(((Integer) item.getData("0")).intValue(),
															((Integer) item.getData("1")).intValue())) {
														item.setText(1, "1");
														item.setData("1", new Integer(1));
													}
													item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
															((Integer) item.getData("1")).intValue()).toString());
												} else if (column == 1) {
													item.setText(2, new QuranLocation(((Integer) item.getData("0")).intValue(),
															((Integer) item.getData("1")).intValue()).toString());
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

								if (column == 2) {
									((Text) itemEditor).setText(item.getText(i));
								} else {
									((CCombo) itemEditor).setText(item.getText(i));
								}
								itemEditor.setFocus();
								return;
							}
							if (!visible && rect.intersects(clientArea)) {
								visible = true;
							}
						}
						if (!visible) {
							return;
						}
						index++;
					}
				}
			});

			List weights = config.getProps().getList("view.bookmark.bookarkItemForm.sashWeight");
			if (weights.size() != 0) {
				sashForm.setWeights(new int[] { Integer.parseInt(weights.get(0).toString()),
						Integer.parseInt(weights.get(1).toString()) });
			}
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
		String id = bookmarkItem.getId();
		if (!bookmarkItem.isFolder()) {
			bookmarkItem = new BookmarkItem();
			List<IQuranLocation> locations = new ArrayList<IQuranLocation>();
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
		bookmarkItem.setId(id);
	}

	public BookmarkItem getBookmarkItem() {
		return bookmarkItem;
	}

	/**
	 * @param readOnly disables OK button if <code>true</code>.
	 * @return <code>true</code> if ok pressed, <code>false</code> otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean open(boolean readOnly) {
		List b = config.getProps().getList("view.bookmark.bookarkItemForm.location");
		if (b.size() != 0 && !bookmarkItem.isFolder()) {
			shell.setBounds(Integer.parseInt(b.get(0).toString()), Integer.parseInt(b.get(1).toString()), Integer
					.parseInt(b.get(2).toString()), Integer.parseInt(b.get(3).toString()));
		} else {
			shell.setSize(bookmarkItem.isFolder() ? 300 : 360, bookmarkItem.isFolder() ? 230 : 350);
			shell.setLocation(FormUtils.getCenter(parent, shell));
		}
		this.readOnly = readOnly;
		if (readOnly) {
			okBut.setEnabled(false);
			addBut.setEnabled(false);
			remBut.setEnabled(false);
			nameText.setEditable(false);
			descText.setEditable(false);
		}
		super.show();
		loopEver();
		return !canceled;
	}

	private void remove() {
		int[] rows = table.getSelectionIndices();
		if (rows.length <= 0) {
			return;
		}
		for (int i = rows.length - 1; i >= 0; i--) {
			table.remove(rows[i]);
			remBut.setEnabled(false);
		}
	}

	public String getFormId() {
		return "BOOKMARK_ITEM_FORM";
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 28, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.bookmark.BookmarkSaveException;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.language.LanguageEngineNaming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Bookmarks form GUI.
 * 
 * @author Mohsen Saboorian
 */
public class BookmarkSetForm extends BaseForm {
	public static final String FORM_ID = "BOOKMARK_SET_FORM";
	private static final Logger logger = Logger.getLogger(BookmarkSetForm.class);

	Shell parent;
	Composite bookmarksTabBody;
	Composite bookmarksInfoTabBody;
	GridLayout gl;
	GridData gd;
	private Button addFolderBut;
	private Button addItemBut;
	private Button removeBut;
	private Button editBut;
	private Button gotoBut;
	private Tree tree;
	private TabFolder tabFolder;
	private TabItem bookmarksTabItem;
	private TabItem detailsTabItem;
	private Text nameText, authorText, descText, languageText;
	private Combo dirCombo;
	private BookmarkSet bookmarkSet;
	private int bookmarkSetDirection;
	private int dndDetail;

	public BookmarkSetForm(Shell parent) {
		this(config.getBookmark(), parent);
	}

	public BookmarkSetForm(BookmarkSet bookmarkSet, Shell parent) {
		try {
			this.parent = parent;
			this.bookmarkSet = bookmarkSet;
			bookmarkSetDirection = getBookmarkDirection();

			display = parent.getDisplay();
			shell = createShell(parent, SWT.DIALOG_TRIM | SWT.MAX | SWT.MIN | SWT.RESIZE);

			GridLayout gl = new GridLayout(1, false);
			shell.setLayout(gl);
			shell.setText(meaning("TITLE", bookmarkSet.getId()));
			shell.setImages(new Image[] { new Image(display, resource.getString("icon.bookmark.edit16")),
					new Image(display, resource.getString("icon.bookmark.edit32")) });

			shell.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					Rectangle r = shell.getBounds();
					config.getProps().setProperty("view.bookmark.bookarkSetForm.location",
							new String[] { "" + r.x, "" + r.y, "" + r.width, "" + r.height });
				}
			});

			makeForm();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
	}

	private int getBookmarkDirection() {
		return LanguageEngineNaming.RIGHT_TO_LEFT.equals(bookmarkSet.getDirection()) ? SWT.RIGHT_TO_LEFT
				: SWT.LEFT_TO_RIGHT;
	}

	private void makeForm() {
		gd = new GridData(GridData.FILL_BOTH);
		tabFolder = new TabFolder(shell, lang.getSWTDirection());
		tabFolder.setLayoutData(gd);

		bookmarksTabItem = new TabItem(tabFolder, SWT.NONE);
		detailsTabItem = new TabItem(tabFolder, SWT.NONE);
		bookmarksTabItem.setText(lang.getMeaning("BOOKMARKS"));
		detailsTabItem.setText(lang.getMeaning("DETAILS"));

		// DETAILS TAB
		createDetailsTab();

		// BOOKMARKS TAB
		bookmarksTabBody = new Composite(tabFolder, lang.getSWTDirection());
		bookmarksTabBody.setLayout(new GridLayout(1, false));
		bookmarksTabItem.setControl(bookmarksTabBody);

		gd = new GridData(GridData.FILL_BOTH);
		tree = new Tree(bookmarksTabBody, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		tree.setLayoutData(gd);
		tree.setHeaderVisible(true);
		TreeColumn nameCol = new TreeColumn(tree, SWT.NONE);
		nameCol.setText(lang.getMeaning("NAME"));
		nameCol.setWidth(150);
		TreeColumn dataCol = new TreeColumn(tree, SWT.NONE);
		dataCol.setText(lang.getMeaning("LOCATION"));
		dataCol.setWidth(80);
		TreeColumn descCol = new TreeColumn(tree, SWT.NONE);
		descCol.setText(lang.getMeaning("DESCRIPTION"));
		descCol.setWidth(160);

		List<BookmarkItem> bookmarks = bookmarkSet.getBookmarksItems();
		for (BookmarkItem bookmarkItem : bookmarks) {
			TreeItem item = new TreeItem(tree, SWT.FULL_SELECTION);
			BookmarkUtils.addBookmarkItemToTree(item, bookmarkItem);
		}

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int c = tree.getSelectionCount();
				if (c == 0) {
					removeBut.setEnabled(false);
					editBut.setEnabled(false);
					gotoBut.setEnabled(false);
				} else if (c > 1) {
					removeBut.setEnabled(true);
					editBut.setEnabled(false);
					gotoBut.setEnabled(false);
				} else {
					removeBut.setEnabled(true);
					editBut.setEnabled(true);
					if (!((BookmarkItem) e.item.getData()).isFolder()) {
						gotoBut.setEnabled(true);
					} else {
						gotoBut.setEnabled(false);
					}
				}
			}
		});

		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent e) {
				((TreeItem) e.item).setImage(new Image(display, resource.getString("icon.bookmark.openFolder")));
			}

			public void treeCollapsed(TreeEvent e) {
				((TreeItem) e.item).setImage(new Image(display, resource.getString("icon.bookmark.closeFolder")));
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (tree.getSelectionCount() == 1) {
					edit();
				}
			}
		});

		tree.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL) {
					remove();
				}
			}

			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'g' && gotoBut.isEnabled()) {
					gotoBookmark();
				}
			}
		});

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY /* | DND.DROP_LINK */;

		final DragSource source = new DragSource(tree, operations);
		source.setTransfer(types);
		final TreeItem[] dragSourceItem = new TreeItem[1];
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0/* && selection[0].getItemCount() == 0 */) {
					event.doit = true;
					dragSourceItem[0] = selection[0];
				} else {
					event.doit = false;
				}
			};

			public void dragSetData(DragSourceEvent event) {
				event.data = ((BookmarkItem) dragSourceItem[0].getData()).getId();
			}

			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE) {
					dragSourceItem[0].dispose();
				}
				dragSourceItem[0] = null;

				// update buttons status
				int c = tree.getSelectionCount();
				if (c <= 0) {
					removeBut.setEnabled(false);
					editBut.setEnabled(false);
					gotoBut.setEnabled(false);
				}
			}
		});

		// DropTarget target = new DropTarget(tree, operations);
		DropTarget target = new DropTarget(tree, DND.DROP_MOVE);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.detail == DND.DROP_NONE) {
					event.detail = dndDetail;
				} else {
					dndDetail = event.detail;
				}
				if (event.item != null) {
					TreeItem item = (TreeItem) event.item;

					if (event.detail == DND.DROP_MOVE) {
						// check not to add a folder to it's descendants
						BookmarkItem sourceItem = (BookmarkItem) dragSourceItem[0].getData();
						if (sourceItem.isFolder() && hasDescendant(dragSourceItem[0], item)) {
							event.detail = DND.DROP_NONE;
							return;
						}
						if (sourceItem.isFolder() && item.getData().equals(sourceItem)) {
							event.detail = DND.DROP_NONE;
							return;
						}
						if (Arrays.asList(item.getItems()).contains(dragSourceItem[0])) {
							event.detail = DND.DROP_NONE;
							return;
						}
					} else if (event.detail == DND.DROP_COPY) {
						// check not to add a folder to it's descendants
						BookmarkItem sourceItem = (BookmarkItem) dragSourceItem[0].getData();
						if (sourceItem.isFolder() && hasDescendant(dragSourceItem[0], item)) {
							event.detail = DND.DROP_NONE;
							return;
						}
					}

					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					if (pt.y < bounds.y + bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						if (!((BookmarkItem) item.getData()).isFolder()) {
							event.detail = DND.DROP_NONE;
							return;
						}
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				TreeItem targetItem = (TreeItem) event.item;
				BookmarkItem sourceItem = (BookmarkItem) dragSourceItem[0].getData();

				if (targetItem != null && targetItem.getData().equals(sourceItem)) {
					event.detail = DND.DROP_NONE;
					return; // do nothing
				}

				boolean dup = event.detail == DND.DROP_COPY;

				TreeItem parentItem = dragSourceItem[0].getParentItem();
				if (targetItem != null) {
					if (parentItem != null) {
						if (((BookmarkItem) targetItem.getData()).isFolder() && parentItem.getItemCount() == 1) {
							parentItem.setImage(new Image(display, resource.getString("icon.bookmark.closeFolder")));
						}
					}
					if (((BookmarkItem) targetItem.getData()).isFolder() && targetItem.getItemCount() == 0) {
						targetItem.setImage(new Image(display, resource.getString("icon.bookmark.openFolder")));
					}
				}

				if (targetItem == null) {
					// No child for this item to be added. Will be add to the root of the tree
					BookmarkUtils.moveTreeItem(tree, dragSourceItem[0], dup);
				} else {
					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = targetItem.getBounds();
					TreeItem parent = targetItem.getParentItem();
					if (parent != null) {
						TreeItem[] items = parent.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == targetItem) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							BookmarkUtils.moveTreeItem(parent, dragSourceItem[0], index, dup);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							BookmarkUtils.moveTreeItem(parent, dragSourceItem[0], index + 1, dup);
						} else {
							BookmarkUtils.moveTreeItem(targetItem, dragSourceItem[0], dup);
						}
					} else {
						TreeItem[] items = tree.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == targetItem) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							BookmarkUtils.moveTreeItem(tree, dragSourceItem[0], index, dup);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							BookmarkUtils.moveTreeItem(tree, dragSourceItem[0], index + 1, dup);
						} else {
							BookmarkUtils.moveTreeItem(targetItem, dragSourceItem[0], dup);
						}
					}
				}
				dndDetail = DND.DROP_NONE;
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = gl.verticalSpacing = 0;
		gl.marginHeight = gl.marginWidth = 0;
		Composite managerialButtComposite = new Composite(bookmarksTabBody, SWT.NONE);
		managerialButtComposite.setLayout(gl);
		managerialButtComposite.setLayoutData(gd);

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.LEAD;

		Composite crudButtComposite = new Composite(managerialButtComposite, SWT.NONE);
		crudButtComposite.setLayout(rl);
		crudButtComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 40;
		addFolderBut = new Button(crudButtComposite, SWT.PUSH);
		addFolderBut.setToolTipText(meaning("NEW_BOOKMARK_FOLDER"));
		addFolderBut.setImage(new Image(display, resource.getString("icon.bookmark.newFolder")));
		addFolderBut.setLayoutData(rd);
		addFolderBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				add(true);
			};
		});

		rd = new RowData();
		rd.width = 40;
		addItemBut = new Button(crudButtComposite, SWT.PUSH);
		addItemBut.setToolTipText(meaning("NEW_BOOKMARK_ITEM"));
		addItemBut.setImage(new Image(display, resource.getString("icon.bookmark.newItem")));
		addItemBut.setLayoutData(rd);
		addItemBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				add(false);
			};
		});

		rd = new RowData();
		rd.width = 40;
		removeBut = new Button(crudButtComposite, SWT.PUSH);
		removeBut.setToolTipText(lang.getMeaning("REMOVE"));
		removeBut.setImage(new Image(display, resource.getString("icon.remove")));
		removeBut.setLayoutData(rd);
		removeBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				remove();
				if (tree.getSelectionCount() == 0) {
					removeBut.setEnabled(false);
					editBut.setEnabled(false);
				}
			};
		});

		rd = new RowData();
		rd.width = 40;
		editBut = new Button(crudButtComposite, SWT.PUSH);
		editBut.setToolTipText(lang.getMeaning("EDIT"));
		editBut.setImage(new Image(display, resource.getString("icon.bookmark.edit16")));
		editBut.setLayoutData(rd);
		editBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				edit();
			};
		});

		rl = new RowLayout(SWT.HORIZONTAL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;

		Composite gotoButComposite = new Composite(managerialButtComposite, SWT.NONE);
		gotoButComposite.setLayout(rl);
		gotoButComposite.setLayoutData(gd);

		rd = new RowData();
		rd.width = 40;
		gotoBut = new Button(gotoButComposite, SWT.PUSH);
		gotoBut.setToolTipText(lang.getMeaning("GO") + " (Ctrl + G)");
		gotoBut.setImage(new Image(display, resource.getString("icon.bookmark.goto")));
		gotoBut.setLayoutData(rd);
		gotoBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				gotoBookmark();
			};
		});

		removeBut.setEnabled(false);
		editBut.setEnabled(false);
		gotoBut.setEnabled(false);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;

		rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(shell, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		Button ok = new Button(butComposite, SWT.NONE);
		ok.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		ok.pack();
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ok();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});

		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button apply = new Button(butComposite, SWT.NONE);
		apply.setText(FormUtils.addAmpersand(lang.getMeaning("APPLY")));
		apply.pack();
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		RowData rdApply = new RowData();
		// set all three OK, CANCEL, and APPLY buttons to the same length
		int buttonLength = FormUtils.buttonLength(80, ok, cancel, apply);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		rdApply.width = buttonLength;
		ok.setLayoutData(rdOk);
		cancel.setLayoutData(rdCancel);
		apply.setLayoutData(rdApply);

		shell.setDefaultButton(ok);
	}

	protected boolean hasDescendant(TreeItem parent, TreeItem child) {
		if (parent.getItemCount() == 0) {
			return false;
		}

		TreeItem[] children = parent.getItems();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getData().equals(child.getData())) {
				return true;
			}
		}

		for (int i = 0; i < children.length; i++) {
			if (hasDescendant(children[i], child)) {
				return true;
			}
		}

		return false;
	}

	private void createDetailsTab() {
		gl = new GridLayout(3, false);
		bookmarksInfoTabBody = new Composite(tabFolder, lang.getSWTDirection());
		bookmarksInfoTabBody.setLayout(gl);
		detailsTabItem.setControl(bookmarksInfoTabBody);

		Label nameLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		nameLabel.setText(lang.getMeaning("NAME") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		nameText = new Text(bookmarksInfoTabBody, SWT.BORDER | bookmarkSetDirection);
		nameText.setLayoutData(gd);
		nameText.setToolTipText(meaning("UNIQUE_NAME"));
		nameText.setText(getNotNull(bookmarkSet.getName()));

		Label authorLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		authorLabel.setText(lang.getMeaning("AUTHOR") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		authorText = new Text(bookmarksInfoTabBody, SWT.BORDER | bookmarkSetDirection);
		authorText.setLayoutData(gd);
		authorText.setToolTipText(meaning("COMMA_SEPARATED"));
		authorText.setText(getNotNull(bookmarkSet.getAuthor()));

		Label languageLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		languageLabel.setText(lang.getMeaning("LANGUAGE") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		languageText = new Text(bookmarksInfoTabBody, SWT.BORDER);
		languageText.setLayoutData(gd);
		languageText.setToolTipText(meaning("ONLY_ENGLISH"));
		languageText.setText(getNotNull(bookmarkSet.getLanguage()));

		Label directionLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		directionLabel.setText(lang.getMeaning("DIRECTION") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		dirCombo = new Combo(bookmarksInfoTabBody, SWT.BORDER | SWT.READ_ONLY);
		dirCombo.setItems(new String[] { lang.getMeaning("LTR"), lang.getMeaning("RTL") });
		dirCombo.setData(new String[] { "ltr", "rtl" });
		dirCombo.select(LanguageEngineNaming.RIGHT_TO_LEFT.equals(bookmarkSet.getDirection()) ? 1 : 0);
		dirCombo.setLayoutData(gd);
		dirCombo.setToolTipText(lang.getMeaning("LANG_DIRECTION"));

		// Label createDateLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		// createDateLabel.setText("Date created:");
		//
		// gd = new GridData(GridData.GRAB_HORIZONTAL);
		// gd.widthHint = 200;
		// gd.horizontalSpan = 2;
		// Text createDateText = new Text(bookmarksInfoTabBody, SWT.BORDER);
		// createDateText.setLayoutData(gd);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		Label descLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		descLabel.setText(lang.getMeaning("DESCRIPTION") + ":");
		descLabel.setLayoutData(gd);

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL);
		// gd.minimumWidth = 200;
		gd.widthHint = 200;
		gd.heightHint = 100;
		// gd.minimumHeight = 100;
		gd.horizontalSpan = 2;
		descText = new Text(bookmarksInfoTabBody, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | bookmarkSetDirection);
		descText.setLayoutData(gd);
		descText.setText(getNotNull(bookmarkSet.getDescription()));
		descText.setToolTipText(meaning("DESC_TOOLTIP"));
		//
		// gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		// gd.grabExcessHorizontalSpace = true;
		// Label descDetailLabel = new Label(bookmarksInfoTabBody, SWT.WRAP);
		// descDetailLabel.setText("Conventions, quotes, references, and other bookmark-related topics go
		// here");
		// descDetailLabel.setLayoutData(gd);
	}

	private String getNotNull(String str) {
		return str == null ? "" : str;
	}

	private void gotoBookmark() {
		// one and only one item should be selected
		if (tree.getSelectionCount() <= 0) {
			return; // do nothing
		}
		TreeItem ti = tree.getSelection()[0];
		if (ti.getItemCount() == 0) {
			BookmarkUtils.gotoBookmarkLocations(shell, (BookmarkItem) ti.getData());
		}
	}

	private void add(boolean isFolder) {
		BookmarkItemForm bmItemForm = new BookmarkItemForm(shell, isFolder, bookmarkSetDirection);
		logger.debug("Add a new bookmark item/folder");
		if (bmItemForm.open(false)) {
			BookmarkItem newBookmarkItem = bmItemForm.getBookmarkItem();
			newBookmarkItem.setId(bookmarkSet.nextItemId());
			TreeItem[] tis = tree.getSelection();
			TreeItem newTreeItem;
			if (tis.length == 0) {
				newTreeItem = new TreeItem(tree, SWT.NONE);
			} else { // only one element should be selected
				TreeItem ti = tis[0];
				if (((BookmarkItem) ti.getData()).isFolder()) {
					newTreeItem = new TreeItem(ti, SWT.NONE);
					ti.setExpanded(true);
				} else {
					if (ti.getParentItem() == null) {
						newTreeItem = new TreeItem(ti.getParent(), SWT.NONE);
					} else {
						newTreeItem = new TreeItem(ti.getParentItem(), SWT.NONE);
					}
				}
			}
			newTreeItem.setImage(new Image(display, resource.getString("icon.bookmark."
					+ (isFolder ? "closeFolder" : "item"))));
			if (isFolder) {
				newTreeItem.setText(new String[] { newBookmarkItem.getName(), "", newBookmarkItem.getDescription() });
			} else {
				newTreeItem.setText(new String[] { newBookmarkItem.getName(),
						CollectionUtils.toString(newBookmarkItem.getLocations(), ","), newBookmarkItem.getDescription() });
			}
			newTreeItem.setData(newBookmarkItem);

			logger.debug("BookmarkSet item/folder add done.");
		}
	}

	/**
	 * Stand-alone add bookmark.
	 * 
	 * @param shell parent shell
	 * @param quranLocation current Quran location to be bookmarked
	 * @param bookmarkTitle default bookmark title
	 */
	public static void addNew(Shell shell, IQuranLocation quranLocation, String bookmarkTitle) {
		BookmarkSet bookmarkSet = config.getBookmark();
		int direction = LanguageEngineNaming.RIGHT_TO_LEFT.equals(bookmarkSet.getDirection()) ? SWT.RIGHT_TO_LEFT
				: SWT.LEFT_TO_RIGHT;

		List<IQuranLocation> locList = new ArrayList<IQuranLocation>();
		locList.add(quranLocation);
		BookmarkItemForm bmItemForm = new BookmarkItemForm(shell, locList, bookmarkTitle, direction);
		if (bmItemForm.open(false)) {
			BookmarkItem newBookmarkItem = bmItemForm.getBookmarkItem();
			newBookmarkItem.setId(bookmarkSet.nextItemId());
			bookmarkSet.getBookmarksItems().add(newBookmarkItem);
			logger.debug("Added new bookmark item too root: " + newBookmarkItem);
			try {
				bookmarkSet.save();
			} catch (BookmarkSaveException e) {
				MessageBoxUtils.showActionFailureError(e);
				logger.error("Bookmark could not be saved.");
				return;
			}

			logger.info("Recreate bookmark menu.");
			EventUtils.sendEvent(EventProtocol.UPDATE_BOOKMARKS_MENU);
		}
	}

	private void ok() {
		apply();
		shell.close();
	}

	private void apply() {
		logger.info("Apply bookmark settings for: " + bookmarkSet);
		List<BookmarkItem> bookmarkItems = bookmarkSet.getBookmarksItems();
		bookmarkItems.clear();

		TreeItem[] tis = tree.getItems();
		for (int i = 0; i < tis.length; i++) {
			BookmarkItem bi = BookmarkUtils.getBookmarkItemFromTreeItem(tis[i]);
			bookmarkItems.add(bi);
		}
		bookmarkSet.setName(nameText.getText());
		bookmarkSet.setAuthor(authorText.getText());
		bookmarkSet.setLanguage(languageText.getText());
		bookmarkSet.setDirection(((String[]) dirCombo.getData())[dirCombo.getSelectionIndex()]);
		bookmarkSetDirection = getBookmarkDirection();
		bookmarkSet.setDescription(descText.getText());
		try {
			bookmarkSet.save();
		} catch (BookmarkSaveException e) {
			MessageBoxUtils.showActionFailureError(e);
			logger.error("Bookmark could not be saved.");
			return;
		}

		logger.info("Update bookmark menu (reconstruct it)");
		EventUtils.sendEvent(EventProtocol.UPDATE_BOOKMARKS_MENU);

		logger.info("Apply bookmark settings for " + bookmarkSet + " done");
	}

	/**
	 * Remove selected items
	 */
	private void remove() {
		TreeItem[] tis = tree.getSelection();
		if (tis.length <= 0) {
			return;
		}
		for (int i = 0; i < tis.length; i++) {
			if (!tis[i].isDisposed()) {
				if (tis[i].getParentItem() != null) {
					if (tis[i].getParentItem().getItems().length == 1) {
						// close the folder if it left open
						tis[i].getParentItem().setImage(new Image(display, resource.getString("icon.bookmark.closeFolder")));
					}
				}
			}
			tis[i].dispose();
		}
		if (tree.getSelectionCount() == 0) {
			removeBut.setEnabled(false);
			gotoBut.setEnabled(false);
			editBut.setEnabled(false);
		}
		logger.debug("Remove " + tis.length + " bookmark items.");
	}

	private void edit() {
		if (tree.getSelectionCount() <= 0) {
			return;
		}
		TreeItem item = tree.getSelection()[0];
		BookmarkItemForm bmItemForm = new BookmarkItemForm(shell, (BookmarkItem) item.getData(), bookmarkSetDirection);
		logger.debug("Open bookmark item/folder editor.");
		if (bmItemForm.open(false)) {
			BookmarkItem newBookmarkItem = bmItemForm.getBookmarkItem();
			if (newBookmarkItem.isFolder()) {
				item.setText(new String[] { newBookmarkItem.getName(), "", newBookmarkItem.getDescription() });
			} else {
				item.setText(new String[] { newBookmarkItem.getName(),
						CollectionUtils.toString(newBookmarkItem.getLocations(), ","), newBookmarkItem.getDescription() });
			}
			item.setData(newBookmarkItem);
			logger.debug("BookmarkSet item/folder edit done.");
		}
	}

	public void open() {
		open(null);
	}

	@SuppressWarnings("unchecked")
	public void open(DisposeListener disposeListener) {
		if (disposeListener != null) {
			shell.addDisposeListener(disposeListener);
		}
		List b = config.getProps().getList("view.bookmark.bookarkSetForm.location");
		if (b.size() != 0) {
			shell.setBounds(Integer.parseInt(b.get(0).toString()), Integer.parseInt(b.get(1).toString()), Integer
					.parseInt(b.get(2).toString()), Integer.parseInt(b.get(3).toString()));
		} else {
			shell.pack();
			Point size = shell.getSize();
			if (size.x > 500) {
				size.x = 500;
			}
			if (size.y > 360) {
				size.y = 360;
			}
			shell.setSize(size);
			shell.setLocation(FormUtils.getCenter(parent, shell));
		}
		shell.open();
		logger.debug("Open bookmarks form");
	}

	public String getFormId() {
		return FORM_ID;
	}
}

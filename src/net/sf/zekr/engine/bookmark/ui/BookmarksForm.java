/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 28, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.EventProtocol;
import net.sf.zekr.ui.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
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
 * @since Zekr 1.0
 */
public class BookmarksForm {
	public static final String FORM_ID = "BOOKMARKS_FORM";
	private static final LanguageEngine lang = LanguageEngine.getInstance();
	private static final ResourceManager resource = ResourceManager.getInstance();
	private final Logger logger = Logger.getLogger(this.getClass());
	private static final ApplicationConfig config = ApplicationConfig.getInstance();
	private Display display;

	Shell parent, shell;
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
	private BookmarkSet bookmark;

	public BookmarksForm(Shell parent) {
		this(config.getBookmark(), parent);
	}

	public BookmarksForm(BookmarkSet bookmark, Shell parent) {
		this.parent = parent;
		this.bookmark = bookmark;

		display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);

		GridLayout gl = new GridLayout(1, false);
		shell.setLayout(gl);
		shell.setText(meaning("TITLE"));
		shell.setImage(new Image(display, resource.getString("icon.bookmark.manager")));

		makeForm();
	}

	private void makeForm() {
		gd = new GridData(GridData.FILL_BOTH);
		tabFolder = new TabFolder(shell, SWT.NONE);
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

		BookmarkSet bm = config.getBookmark();
		List bookmarks = bm.getBookmarksItems();
		for (Iterator iter = bookmarks.iterator(); iter.hasNext();) {
			BookmarkItem bmItem = (BookmarkItem) iter.next();
			TreeItem item = new TreeItem(tree, SWT.FULL_SELECTION);
			BookmarkUtils.addBookmarkItemToTree(shell, item, bmItem);
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
					if (!((BookmarkItem) e.item.getData()).isFolder())
						gotoBut.setEnabled(true);
					else
						gotoBut.setEnabled(false);
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
				if (tree.getSelectionCount() == 1)
					edit();
			}
		});

		tree.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL)
					remove();
			}

			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'g' && gotoBut.isEnabled())
					gotoBookmark();
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = gl.verticalSpacing = 0;
		gl.marginHeight = gl.marginWidth = 0;
		Composite managerialButComposite = new Composite(bookmarksTabBody, SWT.NONE);
		managerialButComposite.setLayout(gl);
		managerialButComposite.setLayoutData(gd);

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.LEAD;

		Composite crudButComposite = new Composite(managerialButComposite, SWT.NONE);
		crudButComposite.setLayout(rl);
		crudButComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 40;
		addFolderBut = new Button(crudButComposite, SWT.PUSH);
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
		addItemBut = new Button(crudButComposite, SWT.PUSH);
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
		removeBut = new Button(crudButComposite, SWT.PUSH);
		removeBut.setToolTipText(lang.getMeaning("REMOVE"));
		removeBut.setImage(new Image(display, resource.getString("icon.remove")));
		removeBut.setLayoutData(rd);
		removeBut.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				remove();
			};
		});

		rd = new RowData();
		rd.width = 40;
		editBut = new Button(crudButComposite, SWT.PUSH);
		editBut.setToolTipText(lang.getMeaning("EDIT"));
		editBut.setImage(new Image(display, resource.getString("icon.edit")));
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

		Composite gotoButComposite = new Composite(managerialButComposite, SWT.NONE);
		gotoButComposite.setLayout(rl);
		gotoButComposite.setLayoutData(gd);

		rd = new RowData();
		rd.width = 40;
		gotoBut = new Button(gotoButComposite, SWT.PUSH);
		gotoBut.setToolTipText(lang.getMeaning("GOTO") + "(Ctrl + G)");
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

		// gd = new GridData(GridData.FILL_HORIZONTAL);
		// new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;

		rl = new RowLayout(SWT.HORIZONTAL);

		Composite butComposite = new Composite(shell, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		rd = new RowData();
		rd.width = 80;
		Button ok = new Button(butComposite, SWT.NONE);
		ok.setText("&" + lang.getMeaning("OK"));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ok();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		ok.setLayoutData(rd);

		rd = new RowData();
		rd.width = 80;
		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText("&" + lang.getMeaning("CANCEL"));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		cancel.setLayoutData(rd);

		rd = new RowData();
		rd.width = 80;
		Button apply = new Button(butComposite, SWT.NONE);
		apply.setText("&" + lang.getMeaning("APPLY"));
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});
		apply.setLayoutData(rd);
		shell.setDefaultButton(ok);
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
		nameText = new Text(bookmarksInfoTabBody, SWT.BORDER);
		nameText.setLayoutData(gd);
		nameText.setToolTipText("Use a unique name");
		nameText.setText(getNotNull(bookmark.getName()));

		Label authorLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		authorLabel.setText(lang.getMeaning("AUTHOR") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		authorText = new Text(bookmarksInfoTabBody, SWT.BORDER);
		authorText.setLayoutData(gd);
		authorText.setToolTipText("Use comma seperated values, if there are more than one author");
		authorText.setText(getNotNull(bookmark.getAuthor()));

		Label languageLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		languageLabel.setText(lang.getMeaning("LANGUAGE") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.widthHint = 200;
		gd.horizontalSpan = 2;
		languageText = new Text(bookmarksInfoTabBody, SWT.BORDER);
		languageText.setLayoutData(gd);
		languageText.setToolTipText("Use only English names (e.g. English, Farsi, Arabic, ...)");
		languageText.setText(getNotNull(bookmark.getLanguage()));

		Label directionLabel = new Label(bookmarksInfoTabBody, SWT.NONE);
		directionLabel.setText(lang.getMeaning("DIRECTION") + ":");

		gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		dirCombo = new Combo(bookmarksInfoTabBody, SWT.BORDER | SWT.READ_ONLY);
		dirCombo.setItems(new String[] { lang.getMeaning("LTR"), lang.getMeaning("RTL") });
		dirCombo.setData(new String[] { "ltr", "rtl" });
		dirCombo.select(LanguageEngine.RIGHT_TO_LEFT.equals(bookmark.getDirection()) ? 1 : 0);
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

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.minimumWidth = 200;
		gd.widthHint = 200;
		gd.minimumHeight = 100;
		descText = new Text(bookmarksInfoTabBody, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		descText.setLayoutData(gd);
		descText.setText(getNotNull(bookmark.getDescription()));

		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		gd.grabExcessHorizontalSpace = true;
		Label descDetailLabel = new Label(bookmarksInfoTabBody, SWT.WRAP);
		descDetailLabel.setText("Conventions, quotes, references, and other bookmark-related topics go here");
		descDetailLabel.setLayoutData(gd);
	}

	private String getNotNull(String str) {
		return str == null ? "" : str;
	}

	private void gotoBookmark() {
		// one and only one item should be selected when this method is called
		TreeItem ti = tree.getSelection()[0];
		if (ti.getItemCount() == 0) // check if item is a leaf
			BookmarkUtils.gotoBookmarkLocations(shell, parent, (BookmarkItem) ti.getData());
	}

	private void add(boolean isFolder) {
		BookmarkItemForm bmItemForm = new BookmarkItemForm(shell, isFolder);
		logger.debug("Add a new bookmark item/folder");
		if (bmItemForm.open()) {
			BookmarkItem newBookmarkItem = bmItemForm.getBookmarkItem();
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
					if (ti.getParentItem() == null)
						newTreeItem = new TreeItem(ti.getParent(), SWT.NONE);
					else
						newTreeItem = new TreeItem(ti.getParentItem(), SWT.NONE);
				}
			}
			newTreeItem.setImage(new Image(display, resource.getString("icon.bookmark."
					+ (isFolder ? "closeFolder" : "item"))));
			if (isFolder)
				newTreeItem.setText(new String[] { newBookmarkItem.getName(), "", newBookmarkItem.getDescription() });
			else
				newTreeItem.setText(new String[] { newBookmarkItem.getName(),
						CollectionUtils.toString(newBookmarkItem.getLocations(), ","),
						newBookmarkItem.getDescription() });
			newTreeItem.setData(newBookmarkItem);

			logger.debug("BookmarkSet item/folder add done.");
		}
	}

	private void ok() {
		apply();
		shell.close();
	}

	private void apply() {
		logger.info("Apply bookmark settings for " + bookmark);
		BookmarkSet bookmark = config.getBookmark();
		List bookmarkItems = bookmark.getBookmarksItems();
		bookmarkItems.clear();

		TreeItem[] tis = tree.getItems();
		for (int i = 0; i < tis.length; i++) {
			BookmarkItem bi = BookmarkUtils.getBookmarkItemFromTreeItem(tis[i]);
			bookmarkItems.add(bi);
		}
		bookmark.setName(nameText.getText());
		bookmark.setAuthor(authorText.getText());
		bookmark.setLanguage(languageText.getText());
		bookmark.setDirection(languageText.getText());
		bookmark.setDescription(descText.getText());
		bookmark.save();

		logger.info("Update bookmark menu (reconstruct it)");
		sendEvent(EventProtocol.UPDATE_BOOKMARKS_MENU);

		logger.info("Apply bookmark settings for " + bookmark + " done");
	}

	/**
	 * Remove selected items
	 */
	private void remove() {
		TreeItem[] tis = tree.getSelection();
		for (int i = 0; i < tis.length; i++) {
			if (!tis[i].isDisposed())
				if (tis[i].getParentItem() != null)
					if (tis[i].getParentItem().getItems().length == 1)
						// close the folder if it left open
						tis[i].getParentItem().setImage(
								new Image(display, resource.getString("icon.bookmark.closeFolder")));
			tis[i].dispose();
		}
		logger.debug("Remove " + tis.length + " bookmark items.");
	}

	private void edit() {
		// there should be exactly one item selected (otherwise edit shall not be called)
		TreeItem item = tree.getSelection()[0];
		BookmarkItemForm bmItemForm = new BookmarkItemForm(shell, (BookmarkItem) item.getData());
		logger.debug("Open bookmark item/folder editor.");
		if (bmItemForm.open()) {
			BookmarkItem newBookmarkItem = bmItemForm.getBookmarkItem();
			item.setData(newBookmarkItem);
			if (newBookmarkItem.isFolder()) {
				item.setText(new String[] { newBookmarkItem.getName(), newBookmarkItem.getDescription(), "" });
			} else {
				item.setText(new String[] { newBookmarkItem.getName(), newBookmarkItem.getDescription(),
						CollectionUtils.toString(newBookmarkItem.getLocations(), ",") });
			}
			logger.debug("BookmarkSet item/folder edit done.");
		}
	}

	/**
	 * Creates and sends an event to the shell for communication with QuranForm.
	 */
	private void sendEvent(String eventName) {
		Event te = new Event();
		te.data = eventName;
		te.type = SWT.Traverse;
		parent.notifyListeners(SWT.Traverse, te);
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}

	public void open() {
		shell.pack();
		Point size = shell.getSize();
		if (size.x > 500)
			size.x = 500;
		if (size.y > 330)
			size.y = 330;
		// shell.setSize(480, 340);
		shell.setSize(size);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
		logger.debug("Open bookmarks form");
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell sh = new Shell(display, SWT.NO_TRIM);
		sh.setLayout(new FillLayout());
		sh.open();
		// new BookmarksForm(sh).open();
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}

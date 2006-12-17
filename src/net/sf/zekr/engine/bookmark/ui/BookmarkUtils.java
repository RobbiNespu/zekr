/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 30, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.EventProtocol;
import net.sf.zekr.ui.FormUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import sun.util.logging.resources.logging;

/**
 * This class handles a number of functions related to bookmarks as static methods. There functions include
 * making bookmark menu, making bookmark tree, bookmark location selector pop-up, and more.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BookmarkUtils {
	private static final ResourceManager resource = ResourceManager.getInstance();
	private static final LanguageEngine lang = LanguageEngine.getInstance();
	private final static Logger logger = Logger.getLogger(BookmarkUtils.class);

	public static void addBookmarkItemToMenu(final Shell shell, Menu parentMenu,
			final BookmarkItem bookmarkItem) {
		MenuItem menuItem;
		if (bookmarkItem.isFolder()) {
			menuItem = new MenuItem(parentMenu, SWT.CASCADE);
			menuItem.setText(StringUtils.abbreviate(bookmarkItem.getName(),
					GlobalConfig.MAX_MENU_STRING_LENGTH));
			menuItem.setImage(new Image(shell.getDisplay(), resource
					.getString("icon.menu.bookmark.closeFolder")));
			Menu menu = new Menu(shell, SWT.DROP_DOWN);
			menuItem.setMenu(menu);
			menu.addMenuListener(new MenuAdapter() {
				public void menuShown(MenuEvent e) {
					Menu m = (Menu) e.widget;
					MenuItem pmi = m.getParentItem();
					if (pmi != null) {
						pmi.setImage(new Image(shell.getDisplay(), resource
								.getString("icon.menu.bookmark.openFolder")));
					}
				}

				public void menuHidden(MenuEvent e) {
					Menu m = (Menu) e.widget;
					MenuItem pmi = m.getParentItem();
					if (pmi != null) {
						pmi.setImage(new Image(shell.getDisplay(), resource
								.getString("icon.menu.bookmark.closeFolder")));
					}
				}
			});

			List bmChildren = bookmarkItem.getChildren();
			for (int i = 0; i < bmChildren.size(); i++) {
				BookmarkItem newBookmarkItem = (BookmarkItem) bmChildren.get(i);
				BookmarkUtils.addBookmarkItemToMenu(shell, menu, newBookmarkItem);
			}
		} else {
			menuItem = new MenuItem(parentMenu, SWT.PUSH);
			menuItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.bookmark.item")));
			menuItem.setText(StringUtils.abbreviate(bookmarkItem.getName() + " - "
					+ bookmarkItem.getLocations(), GlobalConfig.MAX_MENU_STRING_LENGTH));
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					gotoBookmarkLocations(shell, shell, bookmarkItem);
				}
			});
		}

	}

	public static void addBookmarkItemToTree(Shell shell, TreeItem treeItem, BookmarkItem bookmarkItem) {
		if (bookmarkItem.isFolder()) {
			treeItem.setText(new String[] { bookmarkItem.getName(), "", bookmarkItem.getDescription() });
			treeItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.bookmark.closeFolder")));

			List bmChildren = bookmarkItem.getChildren();
			for (int i = 0; i < bmChildren.size(); i++) {
				BookmarkItem newBookmarkItem = (BookmarkItem) bmChildren.get(i);
				TreeItem childItem = new TreeItem(treeItem, SWT.FULL_SELECTION);
				BookmarkUtils.addBookmarkItemToTree(shell, childItem, newBookmarkItem);
			}
		} else {
			treeItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.bookmark.item")));
			treeItem
					.setText(new String[] { bookmarkItem.getName(),
							CollectionUtils.toString(bookmarkItem.getLocations(), ","),
							bookmarkItem.getDescription() });
		}
		treeItem.setData(bookmarkItem);
	}

	public static BookmarkItem getBookmarkItemFromTreeItem(TreeItem treeItem) {
		BookmarkItem bi = (BookmarkItem) treeItem.getData();
		if (bi.isFolder()) {
			TreeItem[] items = treeItem.getItems();
			bi.clearChilrden();
			for (int i = 0; i < items.length; i++) {
				bi.addChild(getBookmarkItemFromTreeItem(items[i]));
			}
		}
		return bi;
	}

	public static void gotoBookmarkLocations(Shell parent, Shell quranShell, BookmarkItem bookmarkItem) {
		List locs = bookmarkItem.getLocations();
		if (locs.size() == 0) {
			return;
		} else if (locs.size() == 1) {
			IQuranLocation location = (IQuranLocation) locs.get(0);
			sendEvent(quranShell, EventProtocol.GOTO_LOCATION + ":" + location);
		} else {
			int i = chooseBookmarkItem(parent, bookmarkItem);
			parent.forceFocus();
			if (i != -1) {
				IQuranLocation location = (IQuranLocation) locs.get(i);
				sendEvent(quranShell, EventProtocol.GOTO_LOCATION + ":" + location);
			}
		}
	}

	static private int _listIndex;

	private static int chooseBookmarkItem(Shell parent, BookmarkItem bookmarkItem) {
		final Shell shell = new Shell(parent, lang.getSWTDirection() | SWT.TOOL | SWT.APPLICATION_MODAL);
		FillLayout fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 2;
		shell.setLayout(fl);
		_listIndex = -1;

		Group body = new Group(shell, SWT.NONE);
		body.setText(lang.getMeaning("SELECT"));
		fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 2;
		body.setLayout(fl);

		final org.eclipse.swt.widgets.List list;
		list = new org.eclipse.swt.widgets.List(body, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		try {
			list.setItems((String[]) CollectionUtils.toStringArray(bookmarkItem.getLocations(), "toDetailedString"));
		} catch (Exception e) {
			logger.log(e);
		}
		list.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (list.getSelectionCount() != 1)
					return;
				if (e.keyCode == '\r') {
					_listIndex = list.getSelectionIndex();
					shell.close();
				}
			}
		});
		list.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (list.getSelectionCount() != 1)
					return;
				_listIndex = list.getSelectionIndex();
				shell.close();
			}
		});

		shell.pack();
		if (shell.getSize().y > 300)
			shell.setSize(shell.getSize().x, 300);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}

		return _listIndex;
	}

	/**
	 * Creates and sends an event to the shell for communication with QuranForm.
	 */
	private static void sendEvent(Shell shell, String eventName) {
		Event te = new Event();
		te.data = eventName;
		te.type = SWT.Traverse;
		shell.notifyListeners(SWT.Traverse, te);
	}
}

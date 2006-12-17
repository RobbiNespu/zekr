/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 11, 2005
 */
package net.sf.zekr.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.BrowserShop;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.util.IExcractor;
import net.sf.zekr.common.util.UriUtils;
import net.sf.zekr.common.util.ZipUtils;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.bookmark.ui.BookmarkUtils;
import net.sf.zekr.engine.bookmark.ui.BookmarksForm;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.options.OptionsForm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFilter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class QuranFormMenuFactory {
	Shell shell;
	ApplicationConfig config;
	LanguageEngine dict;
	QuranForm form;
	private final static ResourceManager resource = ResourceManager.getInstance();
	private final Logger logger = Logger.getLogger(this.getClass());
	private MenuItem quranLineLayoutItem;
	private MenuItem transLineLayoutItem;
	private MenuItem quranBlockLayoutItem;
	private MenuItem transBlockLayoutItem;
	private MenuItem quranViewType;
	private Menu quranViewMenu;
	private MenuItem transViewType;
	private Menu menu;
	private MenuItem file;
	private MenuItem exportItem;
	private MenuItem printItem;
	private MenuItem exitItem;
	private MenuItem view;
	private Menu viewMenu;
	private MenuItem suraReloadItem;
	private MenuItem langName;
	private MenuItem transName;
	private MenuItem quranOnly;
	private MenuItem transOnly;
	private MenuItem separate;
	private MenuItem mixed;
	private int direction;

	public QuranFormMenuFactory(QuranForm form, Shell shell) {
		this.form = form;
		config = ApplicationConfig.getInstance();
		dict = config.getLanguageEngine();
		this.shell = shell;
		direction = form.langEngine.getSWTDirection();
	}

	public Menu getQuranFormMenu() {
		// create the menu bar
		menu = new Menu(shell, SWT.BAR | direction);

		// ---- File -----
		file = new MenuItem(menu, SWT.CASCADE | direction);
		file.setText("&" + dict.getMeaning("FILE"));

		// set the menu for the File option
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		file.setMenu(fileMenu);

		// save as...
		exportItem = new MenuItem(fileMenu, SWT.PUSH);
		exportItem.setText("&" + dict.getMeaning("SAVE_AS") + "...\tCtrl + S");
		exportItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.export")));
		exportItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				export();
			}
		});

		// print
		printItem = new MenuItem(fileMenu, SWT.PUSH);
		printItem.setText("&" + dict.getMeaning("PRINT") + "...\tCtrl + P");
		printItem.setAccelerator(SWT.CTRL | 'P');
		printItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.print")));
		printItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				print();
			}
		});

		// add exit item
		exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("&" + dict.getMeaning("EXIT") + "\tCtrl + Q");
		exitItem.setAccelerator(SWT.CTRL | 'Q');
		exitItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.exit")));
		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				close();
			}
		});

		// ---- View -----
		view = new MenuItem(menu, SWT.CASCADE | direction);
		view.setText("&" + dict.getMeaning("VIEW"));

		// set the menu for the View option
		viewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		view.setMenu(viewMenu);

		suraReloadItem = new MenuItem(viewMenu, SWT.PUSH);
		suraReloadItem.setText("&" + dict.getMeaning("RELOAD") + "\tCtrl + R");
		suraReloadItem.setAccelerator(SWT.CTRL | 'R');
		suraReloadItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.reload")));
		suraReloadItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				form.reload();
			}
		});

		// separator
		new MenuItem(viewMenu, SWT.SEPARATOR);

		// cascading menu for language selection
		transName = new MenuItem(viewMenu, SWT.CASCADE | direction);
		transName.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.translation")));
		transName.setText("&" + dict.getMeaning("TRANSLATION"));
		Menu transMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		transName.setMenu(transMenu);
		Collection trans = config.getTranslation().getAllTranslation();
		for (Iterator iter = trans.iterator(); iter.hasNext();) {
			TranslationData td = (TranslationData) iter.next();
			final MenuItem transItem = new MenuItem(transMenu, SWT.RADIO);
			transItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.book")));
			transItem.setText(StringUtils.abbreviate(td.localizedName, GlobalConfig.MAX_MENU_STRING_LENGTH) + " - " + td.locale);
			transItem.setData(td.id);
			if (config.getTranslation().getDefault().id.equals(transItem.getData()))
				transItem.setSelection(true);
			transItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MenuItem mi = (MenuItem) event.widget;
					if (mi.getSelection() == true) {
						if (!config.getTranslation().getDefault().id.equals(transItem.getData())) {
							setTrans((String) mi.getData());
						}
					}
				}
			});
		}

		// cascading menu for view type
		MenuItem viewType = new MenuItem(viewMenu, SWT.CASCADE | direction);
		viewType.setText("&" + dict.getMeaning("LAYOUT"));
		viewType.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.layout")));
		Menu viewTypeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		viewType.setMenu(viewTypeMenu);

		quranOnly = new MenuItem(viewTypeMenu, SWT.RADIO);
		transOnly = new MenuItem(viewTypeMenu, SWT.RADIO);

		separate = new MenuItem(viewTypeMenu, SWT.RADIO);
		mixed = new MenuItem(viewTypeMenu, SWT.RADIO);

		new MenuItem(viewTypeMenu, SWT.SEPARATOR | direction);

		quranOnly.setText(dict.getMeaning("QURAN"));
		quranOnly.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.quranOnly")));
		quranOnly.setData("quranOnly");
		transOnly.setText(dict.getMeaning("TRANSLATION"));
		transOnly.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.transOnly")));
		transOnly.setData("transOnly");
		separate.setText(dict.getMeaning("SEPARATE"));
		separate.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.separate")));
		separate.setData("separate");
		mixed.setText(dict.getMeaning("MIXED"));
		mixed.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.mixed")));
		mixed.setData("mixed");

		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Object data = event.widget.getData();
				// if (!((MenuItem) event.widget).getSelection()) {
				// if (data.equals("mixed")) {
				// quranViewType.setEnabled(true);
				// transViewType.setEnabled(true);
				// } else if (data.equals("quranOnly")) {
				// quranViewType.setEnabled(true);
				// } else if (data.equals("transOnly")) {
				// transViewType.setEnabled(true);
				// }
				// return;
				// }
				if (((MenuItem) event.widget).getSelection()) {
					if (data.equals("quranOnly") && quranOnly.getSelection()) {
						config.setViewLayout(ApplicationConfig.QURAN_ONLY_LAYOUT);
						transViewType.setEnabled(false);
						quranViewType.setEnabled(true);
					} else if (data.equals("transOnly") && transOnly.getSelection()) {
						config.setViewLayout(ApplicationConfig.TRANS_ONLY_LAYOUT);
						quranViewType.setEnabled(false);
						transViewType.setEnabled(true);
					} else if (data.equals("separate") && separate.getSelection()) {
						config.setViewLayout(ApplicationConfig.SEPARATE_LAYOUT);
						quranViewType.setEnabled(true);
						transViewType.setEnabled(true);
					} else if (data.equals("mixed") && mixed.getSelection()) {
						config.setViewLayout(ApplicationConfig.MIXED_LAYOUT);
						quranViewType.setEnabled(false);
						transViewType.setEnabled(false);
					}
					reconfigureViewLayout();
				}
			};
		};
		quranOnly.addSelectionListener(sa);
		transOnly.addSelectionListener(sa);
		separate.addSelectionListener(sa);
		mixed.addSelectionListener(sa);

		quranViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		quranViewType.setText("&" + dict.getMeaning("QURAN"));
		transViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		transViewType.setText("&" + dict.getMeaning("TRANSLATION"));

		// Set default selection
		String viewLayout = config.getViewProp("view.viewLayout");
		if (ApplicationConfig.TRANS_ONLY_LAYOUT.equals(viewLayout)) {
			transOnly.setSelection(true);
			quranViewType.setEnabled(false);
		} else if (ApplicationConfig.SEPARATE_LAYOUT.equals(viewLayout)) {
			separate.setSelection(true);
		} else if (ApplicationConfig.MIXED_LAYOUT.equals(viewLayout)) {
			mixed.setSelection(true);
			quranViewType.setEnabled(false);
			transViewType.setEnabled(false);
		} else { // default to QURAN_ONLY
			quranOnly.setSelection(true);
			transViewType.setEnabled(false);
		}

		quranViewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		quranViewType.setMenu(quranViewMenu);
		Menu transViewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		transViewType.setMenu(transViewMenu);

		Listener blockListener = new Listener() {
			public void handleEvent(Event e) {
				if (!((MenuItem) e.widget).getSelection())
					return;
				if (e.widget.getData().equals("quran")) {
					logger.info("Change Quran layout to block layout.");
					config.setQuranLayout(ApplicationConfig.BLOCK);
					// config.updateFile();
					reloadQuran();
				} else {
					logger.info("Change translation layout to block layout.");
					config.setTransLayout(ApplicationConfig.BLOCK);
					// config.updateFile();
					reloadTrans();
				}
			}
		};

		quranBlockLayoutItem = new MenuItem(quranViewMenu, SWT.RADIO);
		quranBlockLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_block")));
		quranBlockLayoutItem.setText("&" + dict.getMeaning("BLOCK"));
		quranBlockLayoutItem.addListener(SWT.Selection, blockListener);
		quranBlockLayoutItem.setData("quran");

		transBlockLayoutItem = new MenuItem(transViewMenu, SWT.RADIO);
		transBlockLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_block")));
		transBlockLayoutItem.setText("&" + dict.getMeaning("BLOCK"));
		transBlockLayoutItem.addListener(SWT.Selection, blockListener);
		transBlockLayoutItem.setData("trans");

		Listener inlineListener = new Listener() {
			public void handleEvent(Event e) {
				if (!((MenuItem) e.widget).getSelection())
					return;
				if (e.widget.getData().equals("quran")) {
					// if (config.getQuranLayout().equals(ApplicationConfig.BLOCK)) {
					logger.info("Change Quran layout to line by line layout.");
					config.setQuranLayout(ApplicationConfig.LINE_BY_LINE);
					// config.updateFile();
					reloadQuran();
					// }
				} else {
					// if (config.getTransLayout().equals(ApplicationConfig.BLOCK)) {
					logger.info("Change translation layout to line by line layout.");
					config.setTransLayout(ApplicationConfig.LINE_BY_LINE);
					// config.updateFile();
					reloadTrans();
					// }
				}
			}
		};

		quranLineLayoutItem = new MenuItem(quranViewMenu, SWT.RADIO);
		quranLineLayoutItem.setText("&" + dict.getMeaning("LINE_BY_LINE"));
		quranLineLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_linebyline")));
		quranLineLayoutItem.addListener(SWT.Selection, inlineListener);
		quranLineLayoutItem.setData("quran");

		transLineLayoutItem = new MenuItem(transViewMenu, SWT.RADIO);
		transLineLayoutItem.setText("&" + dict.getMeaning("LINE_BY_LINE"));
		transLineLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_linebyline")));
		transLineLayoutItem.addListener(SWT.Selection, inlineListener);
		transLineLayoutItem.setData("trans");

		// Set default selection
		String quranLayout = config.getQuranLayout();
		String transLayout = config.getTransLayout();
		if (quranLayout.equals(ApplicationConfig.LINE_BY_LINE))
			quranLineLayoutItem.setSelection(true);
		else if (quranLayout.equals(ApplicationConfig.BLOCK))
			quranBlockLayoutItem.setSelection(true);
		if (transLayout.equals(ApplicationConfig.LINE_BY_LINE))
			transLineLayoutItem.setSelection(true);
		else if (transLayout.equals(ApplicationConfig.BLOCK))
			transBlockLayoutItem.setSelection(true);

		// ---- Bookmarks -----
		createOrUpdateBookmarkMenu();

		// ---- Tools -----
		MenuItem tools = new MenuItem(menu, SWT.CASCADE | direction);
		tools.setText("&" + dict.getMeaning("TOOLS"));

		Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		tools.setMenu(toolsMenu);

		Menu addMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		MenuItem addItem = new MenuItem(toolsMenu, SWT.CASCADE);
		addItem.setText("&" + dict.getMeaning("ADD"));
		addItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.add")));
		addItem.setMenu(addMenu);

		// cascading menu for add...
		MenuItem transAddItem = new MenuItem(addMenu, SWT.PUSH | direction);
		transAddItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.translation")));
		transAddItem.setText("&" + dict.getMeaning("TRANSLATION") + "...");
		transAddItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				importTrans();
			}
		});

		MenuItem themeAddItem = new MenuItem(addMenu, SWT.PUSH | direction);
		themeAddItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.theme")));
		themeAddItem.setText("&" + dict.getMeaning("THEME") + "...");
		themeAddItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				importTheme();
			}
		});

		// separator
		new MenuItem(toolsMenu, SWT.SEPARATOR);
		MenuItem options = new MenuItem(toolsMenu, SWT.PUSH);
		options.setText(dict.getMeaning("OPTIONS") + "...");
		options.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.options")));
		options.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new OptionsForm(shell).open();
			}
		});

		// Help menu
		MenuItem help = new MenuItem(menu, SWT.CASCADE | direction);
		help.setText("&" + dict.getMeaning("HELP"));

		// set the menu for the Help option
		Menu helpMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		help.setMenu(helpMenu);

		MenuItem homePage = new MenuItem(helpMenu, SWT.PUSH);
		homePage.setText("&" + dict.getMeaning("HOMEPAGE"));
		homePage.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.homepage")));
		homePage.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				homepage();
			}
		});

		// separator
		new MenuItem(helpMenu, SWT.SEPARATOR);

		MenuItem aboutItem = new MenuItem(helpMenu, SWT.PUSH);
		aboutItem.setText("&" + dict.getMeaning("ABOUT"));
		aboutItem.setAccelerator(SWT.CTRL | SWT.ALT | SWT.F1);
		aboutItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.about")));
		aboutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				about();
			}
		});

		return menu;
	}

	protected void createOrUpdateBookmarkMenu() {
		Menu bookmarksMenu;
		MenuItem bookmarks;
		if (menu.getItemCount() > 2 && "bookmarks".equals(menu.getItem(2).getData())) {
			bookmarks = menu.getItem(2);
			bookmarksMenu = bookmarks.getMenu();
			bookmarksMenu.dispose();
		} else {
			bookmarks = new MenuItem(menu, SWT.CASCADE);
		}

		bookmarks.setText("&" + dict.getMeaning("BOOKMARKS"));
		bookmarks.setData("bookmarks");

		bookmarksMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		bookmarks.setMenu(bookmarksMenu);

		MenuItem bmManagerItem = new MenuItem(bookmarksMenu, SWT.PUSH);
		bmManagerItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.bookmark.manager")));
		bmManagerItem.setText("&" + dict.getMeaning("CONFIGURE") + "...\tCTRL + M");
		bmManagerItem.setAccelerator(SWT.CTRL | 'B');
		bmManagerItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				manageBookmarks();
			}
		});

		MenuItem bookmarkSetConfigItem = new MenuItem(bookmarksMenu, SWT.PUSH);
		bookmarkSetConfigItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.bookmark.manager")));
		bookmarkSetConfigItem.setText(dict.getMeaning("CONFIGURE_BOOKMARK_SETS") + "...");
		bookmarkSetConfigItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				manageBookmarkSets();
			}
		});


		new MenuItem(bookmarksMenu, SWT.SEPARATOR);

		BookmarkSet bookmark = config.getBookmark();
		List bmItems = bookmark.getBookmarksItems();
		for (int i = 0; i < bmItems.size(); i++) {
			BookmarkItem item = (BookmarkItem) bmItems.get(i);
			BookmarkUtils.addBookmarkItemToMenu(shell, bookmarksMenu, item);
		}
	}

	private void manageBookmarks() {
		new BookmarksForm(shell).open();
	}

	private void manageBookmarkSets() {
		new BookmarksForm(shell).open();
	}

	private void importTrans() {
		try {
			if (doImportFile(ApplicationPath.TRANSLATION_DIR, new String[] { "Zipped Translation File" },
					new String[] { "*.zip" }, null))
				MessageBoxUtils.showMessage(dict.getMeaning("RESTART_APP"));
		} catch (IOException e) {
			MessageBoxUtils.showError(dict.getMeaning("ACTION_FAILED"));
			logger.implicitLog(e);
		}
	}

	private void importTheme() {
		try {
			if (doImportFile(ApplicationPath.THEME_DIR, new String[] { "Zipped Theme File" },
					new String[] { "*.zip" }, new IExcractor() {
						public void extract(File srcFile, String destDir) throws IOException {
							ZipUtils.extract(srcFile, destDir);
						}
					}))
				MessageBoxUtils.showMessage(dict.getMeaning("RESTART_APP"));
		} catch (IOException e) {
			MessageBoxUtils.showError(dict.getMeaning("ACTION_FAILED"));
			logger.implicitLog(e);
		}
	}

	/**
	 * This method can import multiple files (or optionaly extract it) to a destination directory.
	 * 
	 * @param destDir destination path to which files should be imported
	 * @param filterNames names of the filters
	 * @param filterWildcards wildcard filters (e.g. *.zip)
	 * @param extractor a user-defined extractor for optionally (if not <code>null</code>) extracting
	 *            source files in <tt>destDir</tt>
	 * @return <code>true</code> if any file imported successfully, false otherwise.
	 * @throws IOException if any exception occured during importing.
	 */
	private boolean doImportFile(String destDir, String[] filterNames, String[] filterWildcards,
			IExcractor extractor) throws IOException {
		FileDialog fd = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		fd.setFilterNames(filterNames);
		fd.setFilterExtensions(filterWildcards); // Windows wild
		fd.setText(dict.getMeaning("IMPORT"));

		String res = fd.open();
		if (res == null)
			return false;
		String fileNames[] = fd.getFileNames();
		for (int i = 0; i < fileNames.length; i++) {
			File srcFile = new File(fd.getFilterPath(), fileNames[i]);
			FileFilter fileFilter = new WildcardFilter(filterWildcards);

			if (fileFilter.accept(srcFile)) {
				logger.info("Importing selected file: " + srcFile.getName());
				if (extractor != null)
					extractor.extract(srcFile, destDir);
				else
					FileUtils.copyFile(srcFile, new File(destDir + "/" + srcFile.getName()));
			}
		}
		return true;
	}

	private void export() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setFilterNames(new String[] { "HTML Files", "All Files (*.*)" });
		fd.setFilterExtensions(new String[] { "*.html;*.htm", "*.*" }); // Windows wild

		String res = fd.open();
		if (res == null)
			return;
		try {
			File f = new File(res);
			if (f.exists()) {
				if (!MessageBoxUtils.yesNoQuestion(dict.getDynamicMeaning("FILE_ALREADY_EXISTS",
						new String[] { f.getName() }), dict.getMeaning("SAVE_AS")))
					return;
				if (!f.delete())
					throw new IOException("Can not delete already existing file \"" + f + "\".");
			}
			logger.info("Save current view to file: " + f);
			FileUtils.copyFile(UriUtils.toFile(form.getCurrentUri()), f);
		} catch (Exception e) {
			logger.log(e);
		}
	}

	protected void about() {
		AboutForm af = new AboutForm(shell);
		af.getShell().setLocation(FormUtils.getCenter(shell, af.getShell()));
		af.show();
	}

	private void homepage() {
		// new Thread() {
		// public void run() {
		// Program.launch(GlobalConfig.HOME_PAGE);
		// }
		// }.start();
		BrowserShop.openLink(GlobalConfig.HOME_PAGE);
	}

	private void print() {
		form.getQuranBrowser().execute("window.print()");
	}

	private void close() {
		shell.dispose();
	}

	private void reconfigureViewLayout() {
		// very nice business logic!
		boolean uq = form.updateQuran;
		boolean ut = form.updateTrans;
		int oldLayout = form.viewLayout;
		form.setLayout(config.getViewProp("view.viewLayout"));
		boolean uqNew = form.updateQuran;
		boolean utNew = form.updateTrans;
		if (form.viewLayout != QuranForm.MIXED && oldLayout != QuranForm.MIXED) {
			if (uq && form.updateQuran)
				form.updateQuran = false;
			if (ut && form.updateTrans)
				form.updateTrans = false;
		}
		form.suraChanged = true;
		form.updateView();
		form.suraChanged = false;
		if (form.viewLayout != QuranForm.MIXED && oldLayout != QuranForm.MIXED) {
			form.updateQuran = uqNew;
			form.updateTrans = utNew;
		}
	}

	private void reloadQuran() {
		try {
			if (form.viewLayout != QuranForm.MIXED)
				config.getRuntime().recreateQuranCache();
			else
				config.getRuntime().recreateMixedCache();
		} catch (IOException e) {
			logger.log(e);
		}
		form.suraChanged = true;
		form.apply();
	}

	private void reloadTrans() {
		try {
			if (form.viewLayout != QuranForm.MIXED)
				config.getRuntime().recreateTransCache();
			else
				config.getRuntime().recreateMixedCache();
		} catch (IOException e) {
			logger.log(e);
		}
		form.suraChanged = true;
		form.apply();
	}

	private void setTrans(String transId) {
		config.setCurrentTranslation(transId);
		form.reload();
		// config.updateFile();
		// config.reload();
		// recreateForm();
	}

	private void recreateForm() {
		form.recreate();
	}

}

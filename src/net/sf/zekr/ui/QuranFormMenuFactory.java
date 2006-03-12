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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.QuranViewTemplate;
import net.sf.zekr.engine.theme.ThemeData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
	private MenuItem quranInlineLayoutItem;
	private MenuItem transInlineLayoutItem;
	private MenuItem quranBlockLayoutItem;
	private MenuItem transBlockLayoutItem;

	public QuranFormMenuFactory(QuranForm form, Shell shell) {
		this.form = form;
		config = ApplicationConfig.getInstance();
		dict = config.getLanguageEngine();
		this.shell = shell;
	}

	public Menu getQuranFormMenu() {
		// create the menu bar
		int direction = form.langEngine.getSWTDirection();
		Menu menu = new Menu(shell, SWT.BAR | direction);

		// ---- File -----
		MenuItem file = new MenuItem(menu, SWT.CASCADE | direction);
		file.setText("&" + dict.getMeaning("FILE"));

		// set the menu for the File option
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		file.setMenu(fileMenu);

		// save as...
		MenuItem exportItem = new MenuItem(fileMenu, SWT.PUSH);
		exportItem.setText("&" + dict.getMeaning("SAVE_AS") + "\tCtrl + S");
		exportItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.export")));
		exportItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				export();
			}
		});

		// print
		MenuItem printItem = new MenuItem(fileMenu, SWT.PUSH);
		printItem.setText("&" + dict.getMeaning("PRINT") + "\tCtrl + P");
		printItem.setAccelerator(SWT.CTRL + 'P');
		printItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.print")));
		printItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				print();
			}
		});

		// add exit item
		MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("&" + dict.getMeaning("EXIT") + "\tCtrl + Q");
		exitItem.setAccelerator(SWT.CTRL + 'Q');
		exitItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.exit")));
		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				close();
			}
		});

		// ---- View -----
		MenuItem view = new MenuItem(menu, SWT.CASCADE | direction);
		view.setText("&" + dict.getMeaning("VIEW"));

		// set the menu for the View option
		Menu viewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		view.setMenu(viewMenu);

		MenuItem suraReloadItem = new MenuItem(viewMenu, SWT.PUSH);
		suraReloadItem.setText("&" + dict.getMeaning("RELOAD") + "\tCtrl + R");
		suraReloadItem.setAccelerator(SWT.CTRL + 'R');
		suraReloadItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.reload")));
		suraReloadItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				reload();
			}
		});

		// separator
		new MenuItem(viewMenu, SWT.SEPARATOR);

		// cascading menu for language selection
		MenuItem langName = new MenuItem(viewMenu, SWT.CASCADE | direction);
		langName.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.locale")));
		langName.setText("&" + dict.getMeaning("LANGUAGE"));
		Menu langMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		langName.setMenu(langMenu);
		Collection langs = config.getAvailableLanguages();
		for (Iterator iter = langs.iterator(); iter.hasNext();) {
			LanguagePack langPack = (LanguagePack) iter.next();
			MenuItem langItem = new MenuItem(langMenu, SWT.RADIO);
			langItem.setImage(new Image(shell.getDisplay(), langPack.getIconPath()));
			langItem.setText(langPack.getName());
			langItem.setData(langPack.getId());
			if (config.getLanguage().getActiveLanguagePack().getId().equals(langPack.getId()))
				langItem.setSelection(true);
			langItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MenuItem mi = (MenuItem) event.widget;
					if (mi.getSelection() == true) {
						if (!config.getLanguage().getActiveLanguagePack().getId().equals(
								mi.getData())) {
							config.setCurrentLanguage((String) mi.getData());
//							config.updateFile();
							recreateForm();
						}
					}
				}
			});
		}

		// cascading menu for language selection
		MenuItem transName = new MenuItem(viewMenu, SWT.CASCADE | direction);
		transName.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.translation")));
		transName.setText("&" + dict.getMeaning("TRANSLATION"));
		Menu transMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		transName.setMenu(transMenu);
		Collection trans = config.getTranslation().getAllTranslation();
		for (Iterator iter = trans.iterator(); iter.hasNext();) {
			TranslationData td = (TranslationData) iter.next();
			final MenuItem transItem = new MenuItem(transMenu, SWT.RADIO);
			transItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.book")));
			transItem.setText(td.localizedName + " - " + td.locale);
			transItem.setData(td.id);
			if (config.getTranslation().getDefault().id.equals(transItem.getData()))
				transItem.setSelection(true);
			transItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MenuItem mi = (MenuItem) event.widget;
					if (mi.getSelection() == true) {
						if (!config.getTranslation().getDefault().id.equals(transItem.getData())) {
							config.setCurrentTranslation((String) mi.getData());
//							config.updateFile();
							recreateForm();
						}
					}
				}
			});
		}

		// cascading menu for themes
		MenuItem theme = new MenuItem(viewMenu, SWT.CASCADE | direction);
		theme.setText("&" + dict.getMeaning("THEME"));
		theme.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.theme")));
		Menu themeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		theme.setMenu(themeMenu);
		for (Iterator iter = config.getTheme().getAllThemes().iterator(); iter.hasNext();) {
			ThemeData td = (ThemeData) iter.next();
			MenuItem themeItem = new MenuItem(themeMenu, SWT.RADIO);
			themeItem.setText(td.toString());
			themeItem.setData(td.id);
			if (td.id.equals(config.getTheme().getCurrent().id))
				themeItem.setSelection(true);
			themeItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MenuItem mi = (MenuItem) event.widget;
					if (mi.getSelection() == true) {
						if (!config.getTheme().getCurrent().id.equals(mi.getData())) {
							logger.info("Change current theme to \"" + mi.getData() + "\".");
							config.setCurrentTheme((String) mi.getData());
//							config.updateFile();
							recreateForm();
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

		MenuItem quranViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		quranViewType.setText("&" + dict.getMeaning("QURAN"));
		MenuItem transViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		transViewType.setText("&" + dict.getMeaning("TRANSLATION"));

		Menu quranViewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		quranViewType.setMenu(quranViewMenu);
		Menu transViewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		transViewType.setMenu(transViewMenu);

		Listener blockListener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget.getData().equals("quran")) {
					if (config.getQuranLayout().equals(QuranViewTemplate.BLOCK_LAYOUT)) {
						logger.info("Change Quran layout to Line by line layout.");
						config.setQuranLayout(QuranViewTemplate.LINE_BY_LINE_LAYOUT);
//						config.updateFile();
						reloadQuran();
					}
				} else {
					if (config.getTransLayout().equals(QuranViewTemplate.BLOCK_LAYOUT)) {
						logger.info("Change translation layout to Line by line layout.");
						config.setTransLayout(QuranViewTemplate.LINE_BY_LINE_LAYOUT);
//						config.updateFile();
						reloadTrans();
					}
				}
			}
		};

		quranBlockLayoutItem = new MenuItem(quranViewMenu, SWT.RADIO);
		quranBlockLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_linebyline16")));
		quranBlockLayoutItem.setText("&" + dict.getMeaning("LINE_BY_LINE"));
		quranBlockLayoutItem.addListener(SWT.Selection, blockListener);
		quranBlockLayoutItem.setData("quran");

		transBlockLayoutItem = new MenuItem(transViewMenu, SWT.RADIO);
		transBlockLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_linebyline16")));
		transBlockLayoutItem.setText("&" + dict.getMeaning("LINE_BY_LINE"));
		transBlockLayoutItem.addListener(SWT.Selection, blockListener);
		transBlockLayoutItem.setData("trans");

		Listener inlineListener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget.getData().equals("quran")) {
					if (config.getQuranLayout().equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT)) {
						logger.info("Change Quran layout to Block layout.");
						config.setQuranLayout(QuranViewTemplate.BLOCK_LAYOUT);
//						config.updateFile();
						reloadQuran();
					}
				} else {
					if (config.getTransLayout().equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT)) {
						logger.info("Change translation layout to Block layout.");
						config.setTransLayout(QuranViewTemplate.BLOCK_LAYOUT);
//						config.updateFile();
						reloadTrans();
					}
				}
			}
		};

		quranInlineLayoutItem = new MenuItem(quranViewMenu, SWT.RADIO);
		quranInlineLayoutItem.setText("&" + dict.getMeaning("BLOCK"));
		quranInlineLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_block16")));
		quranInlineLayoutItem.addListener(SWT.Selection, inlineListener);
		quranInlineLayoutItem.setData("quran");

		transInlineLayoutItem = new MenuItem(transViewMenu, SWT.RADIO);
		transInlineLayoutItem.setText("&" + dict.getMeaning("BLOCK"));
		transInlineLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_block16")));
		transInlineLayoutItem.addListener(SWT.Selection, inlineListener);
		transInlineLayoutItem.setData("trans");

		// Set default selection
		String quranLayout = config.getQuranLayout();
		String transLayout = config.getTransLayout();
		if (quranLayout.equals(QuranViewTemplate.BLOCK_LAYOUT))
			quranInlineLayoutItem.setSelection(true);
		else if (quranLayout.equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT))
			quranBlockLayoutItem.setSelection(true);
		if (transLayout.equals(QuranViewTemplate.BLOCK_LAYOUT))
			transInlineLayoutItem.setSelection(true);
		else if (transLayout.equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT))
			transBlockLayoutItem.setSelection(true);

		MenuItem help = new MenuItem(menu, SWT.CASCADE | direction);
		help.setText("&" + dict.getMeaning("HELP"));

		// set the menu for the View option
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
		aboutItem.setAccelerator(SWT.CTRL + SWT.ALT + SWT.F1);
		aboutItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.about")));
		aboutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				about();
			}
		});

		return menu;
	}

	protected void export() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setFilterNames(new String[] { "HTML Files", "All Files (*.*)" });
		fd.setFilterExtensions(new String[] { "*.html;*.htm", "*.*" }); // Windows wild
		// cards

		String res = fd.open();
		if (res == null)
			return;
		RandomAccessFile out = null;
		RandomAccessFile in = null;
		try {
			File f = new File(res);
			if (f.exists()) {
				MessageBox mb = new MessageBox(fd.getParent(), SWT.YES | SWT.NO | SWT.ICON_QUESTION
						| dict.getSWTDirection());
				mb.setMessage(dict.getDynamicMeaning("FILE_ALREADY_EXISTS", new String[] { f
						.getName() }));
				mb.setText("Save As");
				if (mb.open() == SWT.NO) {
					return;
				}
				if (!f.delete())
					throw new IOException("Can not delete already existing file \"" + f + "\".");
			}
			out = new RandomAccessFile(res, "rw");
			in = new RandomAccessFile(new File(new URI(form.getQuranUrl())), "r");

			byte[] b = new byte[(int) in.length()];
			in.read(b);
			out.write(b);

			in.close();
			out.close();
		} catch (Exception e) {
			logger.log(e);
		}
	}

	protected void about() {
		AboutForm af = new AboutForm(form.display);
		int x = form.shell.getLocation().x + form.shell.getSize().x / 2;
		int y = form.shell.getLocation().y + form.shell.getSize().y / 2;
		af.shell.setLocation(x - (af.shell.getSize().x / 2), y - (af.shell.getSize().y / 2));
		af.show();
	}

	private void homepage() {
		new Thread() {
			public void run() {
				Program.launch(GlobalConfig.HOME_PAGE);
			}
		}.start();
	}

	private void print() {
		form.getQuranBrowser().execute("window.print()");
	}

	private void close() {
		shell.dispose();
	}

	private void reload() {
		try {
			config.getRuntime().recreateCache();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		form.suraChanged = true;
		form.apply();
	}

	private void reloadQuran() {
		try {
			config.getRuntime().recreateQuranCache();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		form.suraChanged = true;
		form.apply();
	}

	private void reloadTrans() {
		try {
			config.getRuntime().recreateTransCache();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		form.suraChanged = true;
		form.apply();
	}

	private void recreateForm() {
		logger.info("Recreating the form...");
		form.recreate();
		logger.info("Form recreation done.");
	}

}

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
import java.util.Collection;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.runtime.InitRuntime;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.template.QuranViewTemplate;

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

	public QuranFormMenuFactory(QuranForm form, Shell shell) {
		this.form = form;
		config = ApplicationConfig.getInsatnce();
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
							config.updateFile();
							recreateForm();
						}
					}
				}
			});
		}

		// cascading menu for view type
		MenuItem viewType = new MenuItem(viewMenu, SWT.CASCADE | direction);
		viewType.setText("&" + dict.getMeaning("TYPE"));
		Menu viewTypeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		viewType.setMenu(viewTypeMenu);

		final MenuItem blockLayoutItem = new MenuItem(viewTypeMenu, SWT.RADIO);
		blockLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_linebyline16")));
		blockLayoutItem.setText("&" + dict.getMeaning("LINE_BY_LINE"));
		blockLayoutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!getLayout().equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT)) {
					config.setQuranTextLayout(QuranViewTemplate.LINE_BY_LINE_LAYOUT);
					config.updateFile();
					reload();
				}
			}
		});

		final MenuItem inlineLayoutItem = new MenuItem(viewTypeMenu, SWT.RADIO);
		inlineLayoutItem.setText("&" + dict.getMeaning("BLOCK"));
		inlineLayoutItem.setImage(new Image(shell.getDisplay(), resource
				.getString("icon.menu.text_block16")));
		inlineLayoutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (!getLayout().equals(QuranViewTemplate.BLOCK_LAYOUT)) {
					config.setQuranTextLayout(QuranViewTemplate.BLOCK_LAYOUT);
					config.updateFile();
					reload();
				}
			}
		});

		String layout = getLayout();
		if (layout.equals(QuranViewTemplate.BLOCK_LAYOUT))
			inlineLayoutItem.setSelection(true);
		else if (layout.equals(QuranViewTemplate.LINE_BY_LINE_LAYOUT))
			blockLayoutItem.setSelection(true);

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
		fd.setFilterNames (new String [] {"HTML Files", "All Files (*.*)"});
		fd.setFilterExtensions (new String [] {"*.html;*.htm", "*.*"}); // Windows wild cards

		String res = fd.open();
		if (res == null) return;
		RandomAccessFile out = null;
		RandomAccessFile in = null;
		try {
			File f = new File(res);
			if (f.exists()) {
				MessageBox mb = new MessageBox(fd.getParent(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				mb.setMessage(dict.getMeaning("YES_NO"));
				mb.setText("Save As");
				if (mb.open() == SWT.NO) {
					return;
				}
				if (!f.delete())
					throw new IOException("Can not delete already existing file " + f);
			}
			out = new RandomAccessFile(res, "rw");
			in = new RandomAccessFile(form.getUrl(), "r");

			byte[] b = new byte[(int) in.length()];
			in.read(b);
			out.write(b);

			in.close();
			out.close();
		} catch (IOException e) {
			logger.error(e);
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

	private String getLayout() {
		return config.getQuranTextLayout();
	}

	private void print() {
		form.getQuranBrowser().execute("window.print()");
	}

	private void close() {
		shell.dispose();
	}

	private void reload() {
		try {
			InitRuntime.recreateHtmlCache();
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

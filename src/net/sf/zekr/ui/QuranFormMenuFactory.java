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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.ConfigNaming;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.common.util.HyperlinkUtils;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.common.util.UriUtils;
import net.sf.zekr.common.util.ZipUtils;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.ui.BookmarkReferenceForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkSetForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkUtils;
import net.sf.zekr.engine.bookmark.ui.ManageBookmarkSetsForm;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.Range;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.engine.translation.ui.CustomTranslationListForm;
import net.sf.zekr.engine.update.UpdateManager;
import net.sf.zekr.ui.helper.FormUtils;
import net.sf.zekr.ui.options.OptionsForm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * This is not a real factory class, but in fact hides menu creation and updating details from
 * <code>QuranForm</code>.
 * 
 * @author Mohsen Saboorian
 */
public class QuranFormMenuFactory {
	Shell shell;
	ApplicationConfig config;
	LanguageEngine lang;
	QuranForm form;
	private final static ResourceManager resource = ResourceManager.getInstance();
	private static final Logger logger = Logger.getLogger(QuranFormMenuFactory.class);
	private boolean rtl;
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
	private MenuItem multiTrans;
	private int direction;
	private MenuItem randomAyaItem;
	private Menu transMenu;
	private MenuItem customTransList;
	private Menu audioMenu;
	private MenuItem audioItem;
	private MenuItem playItem;
	private MenuItem stopItem;
	private MenuItem nextSura, nextAya, prevSura, prevAya;
	private MenuItem nextJuz, prevJuz, nextHizbQ, prevHizbQ, nextSajda, prevSajda;
	private MenuItem fullScreenItem;

	public QuranFormMenuFactory(QuranForm form, Shell shell) {
		this.form = form;
		config = ApplicationConfig.getInstance();
		lang = config.getLanguageEngine();
		this.shell = shell;
		direction = form.lang.getSWTDirection();
		rtl = direction == SWT.RIGHT_TO_LEFT;
	}

	public Menu getQuranFormMenu() {
		// create the menu bar
		menu = new Menu(shell, SWT.BAR | direction);

		// ---- File -----
		file = new MenuItem(menu, SWT.CASCADE | direction);
		file.setText(FormUtils.addAmpersand(lang.getMeaning("FILE")));

		// set the menu for the File option
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		file.setMenu(fileMenu);

		// save as...
		exportItem = createMenuItem(0, fileMenu, lang.getMeaning("SAVE_AS") + "...", SWT.CTRL | 'S', "icon.menu.export");
		exportItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				export();
			}
		});
		// disabled exporting if HTTP server is enabled
		exportItem.setEnabled(!config.isHttpServerEnabled());

		// print
		printItem = createMenuItem(0, fileMenu, lang.getMeaning("PRINT") + "...", SWT.CTRL | 'P', "icon.menu.print");
		printItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				print();
			}
		});

		// add exit item
		exitItem = createMenuItem(0, fileMenu, lang.getMeaning("EXIT"), SWT.CTRL | 'Q', "icon.menu.exit");
		exitItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				close();
			}
		});

		// ---- View -----
		view = new MenuItem(menu, SWT.CASCADE | direction);
		view.setText(FormUtils.addAmpersand(lang.getMeaning("VIEW")));

		// set the menu for the View option
		viewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		view.setMenu(viewMenu);

		suraReloadItem = createMenuItem(0, viewMenu, lang.getMeaning("RELOAD"), SWT.CTRL | 'R', "icon.menu.reload");
		suraReloadItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				form.reload();
			}
		});

		randomAyaItem = createMenuItem(0, viewMenu, lang.getMeaning("RANDOM_AYA"), SWT.CTRL | SWT.SHIFT | 'R',
				"icon.menu.randomAya");
		randomAyaItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				gotoRandomAya();
			}
		});

		// separator
		new MenuItem(viewMenu, SWT.SEPARATOR);
		transName = createMenuItem(SWT.CASCADE | direction, viewMenu, lang.getMeaning("TRANSLATION"), 0,
				"icon.menu.translation");
		transMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		transName.setMenu(transMenu);
		createOrUpdateTranslationMenu();

		// cascading menu for view type
		MenuItem viewType = createMenuItem(SWT.CASCADE | direction, viewMenu, lang.getMeaning("LAYOUT"), 0,
				"icon.menu.layout");

		Menu viewTypeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		viewType.setMenu(viewTypeMenu);

		quranOnly = createMenuItem(SWT.RADIO, viewTypeMenu, lang.getMeaning("QURAN"), 0, "icon.menu.quranOnly");
		quranOnly.setData("quranOnly");

		transOnly = createMenuItem(SWT.RADIO, viewTypeMenu, lang.getMeaning("TRANSLATION"), 0, "icon.menu.transOnly");
		transOnly.setData("transOnly");

		separate = createMenuItem(SWT.RADIO, viewTypeMenu, lang.getMeaning("SEPARATE"), 0, "icon.menu.separate");
		separate.setData("separate");

		mixed = createMenuItem(SWT.RADIO, viewTypeMenu, lang.getMeaning("MIXED"), 0, "icon.menu.mixed");
		mixed.setData("mixed");

		multiTrans = createMenuItem(SWT.RADIO, viewTypeMenu, lang.getMeaning("MULTI_TRANS"), 0, "icon.menu.mixed");
		multiTrans.setData("customMixed");

		new MenuItem(viewTypeMenu, SWT.SEPARATOR | direction);

		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Object data = event.widget.getData();
				if (((MenuItem) event.widget).getSelection()) {
					if (data.equals("quranOnly") && quranOnly.getSelection()) {
						config.setViewLayout(ApplicationConfig.QURAN_ONLY_LAYOUT);
						quranViewType.setEnabled(true);
						transViewType.setEnabled(false);
					} else if (data.equals("transOnly") && transOnly.getSelection()) {
						config.setViewLayout(ApplicationConfig.TRANS_ONLY_LAYOUT);
						quranViewType.setEnabled(false);
						transViewType.setEnabled(true);
					} else if (data.equals("separate") && separate.getSelection()) {
						config.setViewLayout(ApplicationConfig.SEPARATE_LAYOUT);
						quranViewType.setEnabled(true);
						transViewType.setEnabled(true);
					} else if (data.equals("customMixed") && multiTrans.getSelection()) {
						if (config.getTranslation().getCustomGroup().size() == 0)
							customizeMultiTrans();
						config.setViewLayout(ApplicationConfig.MULTI_TRANS_LAYOUT);
						quranViewType.setEnabled(false);
						transViewType.setEnabled(false);
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
		multiTrans.addSelectionListener(sa);

		quranViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		quranViewType.setText(FormUtils.addAmpersand(lang.getMeaning("QURAN_VIEWTYPE")));
		transViewType = new MenuItem(viewTypeMenu, SWT.CASCADE | direction);
		transViewType.setText(FormUtils.addAmpersand(lang.getMeaning("TRANSLATION_VIEWTYPE")));

		// Set default selection
		if (config.getTranslation().getDefault() == null) { // if no translation found
			viewType.setEnabled(false);
			quranOnly.setSelection(true);
			quranViewType.setEnabled(false);
		} else {
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
			} else if (ApplicationConfig.MULTI_TRANS_LAYOUT.equals(viewLayout)) {
				multiTrans.setSelection(true);
				quranViewType.setEnabled(false);
				transViewType.setEnabled(false);
			} else { // default to QURAN_ONLY
				quranOnly.setSelection(true);
				transViewType.setEnabled(false);
			}
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

		quranBlockLayoutItem = createMenuItem(SWT.RADIO, quranViewMenu, lang.getMeaning("BLOCK"), 0,
				"icon.menu.text_block");
		quranBlockLayoutItem.addListener(SWT.Selection, blockListener);
		quranBlockLayoutItem.setData("quran");

		transBlockLayoutItem = createMenuItem(SWT.RADIO, transViewMenu, lang.getMeaning("BLOCK"), 0,
				"icon.menu.text_block");
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

		quranLineLayoutItem = createMenuItem(SWT.RADIO, quranViewMenu, lang.getMeaning("LINE_BY_LINE"), 0,
				"icon.menu.text_linebyline");
		quranLineLayoutItem.addListener(SWT.Selection, inlineListener);
		quranLineLayoutItem.setData("quran");

		transLineLayoutItem = createMenuItem(SWT.RADIO, transViewMenu, lang.getMeaning("LINE_BY_LINE"), 0,
				"icon.menu.text_linebyline");
		transLineLayoutItem.addListener(SWT.Selection, inlineListener);
		transLineLayoutItem.setData("trans");

		SelectionListener navListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String data = (String) e.widget.getData();
				if (data.equals("next_sura")) {
					form.gotoNextSura();
				} else if (data.equals("prev_sura")) {
					form.gotoPrevSura();
				} else if (data.equals("next_aya")) {
					form.gotoNextAya();
				} else if (data.equals("prev_aya")) {
					form.gotoPrevAya();
				} else if (data.equals("next_juz")) {
					form.gotoNextJuz();
				} else if (data.equals("prev_juz")) {
					form.gotoPrevJuz();
				} else if (data.equals("next_hizb")) {
					form.gotoNextHizb();
				} else if (data.equals("prev_hizb")) {
					form.gotoPrevHizb();
				} else if (data.equals("next_sajda")) {
					// form.gotoPrevSajda();
				} else if (data.equals("prev_sajda")) {
					// form.gotoNextSajda();
				}
			}
		};

		MenuItem gotoMenuItem = createMenuItem(SWT.CASCADE, viewMenu, lang.getMeaning("GOTO"), 0, "icon.menu.goto");
		Menu gotoMenu = new Menu(shell, SWT.DROP_DOWN);
		gotoMenuItem.setMenu(gotoMenu);

		boolean isRTL = ((direction == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);
		String strNext = isRTL ? "Left" : "Right";
		char keyNextJuz = isRTL ? ',' : '.';
		String strPrev = isRTL ? "Right" : "Left";
		char keyPrevJuz = isRTL ? '.' : ',';
		int keyNext = isRTL ? SWT.ARROW_LEFT : SWT.ARROW_RIGHT;
		int keyPrev = isRTL ? SWT.ARROW_RIGHT : SWT.ARROW_LEFT;

		nextSura = new MenuItem(gotoMenu, SWT.PUSH);
		nextSura.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_NEXT_SURA")) + "\tAlt+Down");
		nextSura.setAccelerator(SWT.ALT | SWT.ARROW_DOWN);
		nextSura.setData("next_sura");
		nextSura.addSelectionListener(navListener);

		prevSura = new MenuItem(gotoMenu, SWT.PUSH);
		prevSura.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_PREV_SURA")) + "\tAlt+Up");
		prevSura.setAccelerator(SWT.ALT | SWT.ARROW_UP);
		prevSura.setData("prev_sura");
		prevSura.addSelectionListener(navListener);

		nextAya = new MenuItem(gotoMenu, SWT.PUSH);
		nextAya.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_NEXT_AYA")) + "\tAlt+" + strNext);
		nextAya.setAccelerator(SWT.ALT | keyNext);
		nextAya.setData("next_aya");
		nextAya.addSelectionListener(navListener);

		prevAya = new MenuItem(gotoMenu, SWT.PUSH);
		prevAya.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_PREV_AYA")) + "\tAlt+" + strPrev);
		prevAya.setAccelerator(SWT.ALT | keyPrev);
		prevAya.setData("prev_aya");
		prevAya.addSelectionListener(navListener);

		new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

		nextHizbQ = new MenuItem(gotoMenu, SWT.PUSH);
		nextHizbQ.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_NEXT_HIZBQ")) + "\tCtrl+Shift+" + strNext);
		nextHizbQ.setAccelerator(SWT.CTRL | SWT.SHIFT | keyNext);
		nextHizbQ.setData("next_hizb");
		nextHizbQ.addSelectionListener(navListener);

		prevHizbQ = new MenuItem(gotoMenu, SWT.PUSH);
		prevHizbQ.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_PREV_HIZBQ")) + "\tCtrl+Shift+" + strPrev);
		prevHizbQ.setAccelerator(SWT.CTRL | SWT.SHIFT | keyPrev);
		prevHizbQ.setData("prev_hizb");
		prevHizbQ.addSelectionListener(navListener);

		new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

		nextJuz = new MenuItem(gotoMenu, SWT.PUSH);
		nextJuz.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_NEXT_JUZ")) + "\tCtrl+" + keyNextJuz
				+ (rtl ? I18N.LRM + "" : ""));
		nextJuz.setAccelerator(SWT.CTRL | keyNextJuz);
		nextJuz.setData("next_juz");
		nextJuz.addSelectionListener(navListener);

		prevJuz = new MenuItem(gotoMenu, SWT.PUSH);
		prevJuz.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_PREV_JUZ")) + "\tCtrl+" + keyPrevJuz
				+ (rtl ? I18N.LRM + "" : ""));
		prevJuz.setAccelerator(SWT.CTRL | keyPrevJuz);
		prevJuz.setData("prev_juz");
		prevJuz.addSelectionListener(navListener);

		// new MenuItem(gotoMenu, SWT.SEPARATOR | direction);
		//
		// nextSajda = new MenuItem(gotoMenu, SWT.PUSH);
		// nextSajda.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_NEXT_SAJDA")));
		// nextSajda.setData("next_sajda");
		// nextSajda.addSelectionListener(navListener);
		//
		// prevSajda = new MenuItem(gotoMenu, SWT.PUSH);
		// prevSajda.setText(FormUtils.addAmpersand(lang.getMeaning("MENU_PREV_SAJDA")));
		// prevSajda.setData("prev_sajda");
		// prevSajda.addSelectionListener(navListener);

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

		// fullscreen menu item
		new MenuItem(viewMenu, SWT.SEPARATOR);
		fullScreenItem = createMenuItem(SWT.CHECK, viewMenu, lang.getMeaning("FULL_SCREEN"), SWT.F11,
				"icon.menu.fullScreen");
		fullScreenItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setFullScreen(fullScreenItem.getSelection(), true);
			}
		});

		// ---- Audio ------
		audioItem = new MenuItem(menu, SWT.CASCADE | direction);
		audioItem.setText(FormUtils.addAmpersand(lang.getMeaning("AUDIO")));

		audioMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		audioItem.setMenu(audioMenu);

		playItem = createMenuItem(0, audioMenu, lang.getMeaning("PLAY"), SWT.CTRL | SWT.SHIFT | 'P', "icon.menu.play");
		playItem.setData("pause"); // state
		playItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				playerTogglePlayPause(true);
			}
		});

		stopItem = createMenuItem(0, audioMenu, lang.getMeaning("STOP"), SWT.CTRL | SWT.SHIFT | 'S', "icon.menu.stop");
		stopItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				playerStop(true);
			}
		});

		resetAudioMenuEnableState();
		// MenuItem nextPlayItem = new MenuItem(audioMenu, SWT.PUSH);
		// nextPlayItem.setText(FormUtils.addAmpersand(lang.getMeaning("NEXT")));
		// nextPlayItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.playerPrev")));

		// MenuItem prevPlayItem = new MenuItem(audioMenu, SWT.PUSH);
		// prevPlayItem.setText(FormUtils.addAmpersand(lang.getMeaning("PREV")));
		// prevPlayItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.playerNext")));

		if (config.getAudio().getCurrent() == null) {
			playItem.setEnabled(false);
			stopItem.setEnabled(false);
			// nextPlayItem.setEnabled(false);
			// prevPlayItem.setEnabled(false);
		}

		new MenuItem(audioMenu, SWT.SEPARATOR);

		// cascading menu for audio pack selection
		MenuItem recitationName = createMenuItem(SWT.CASCADE, audioMenu, lang.getMeaning("RECITATION"), 0,
				"icon.menu.playlist");
		Menu recitationListMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		recitationName.setMenu(recitationListMenu);

		if (config.getAudio().getCurrent() != null) {
			Collection recitationList = config.getAudio().getAllAudio();
			for (Iterator iter = recitationList.iterator(); iter.hasNext();) {
				AudioData ad = (AudioData) iter.next();
				final MenuItem audioItem = new MenuItem(recitationListMenu, SWT.RADIO);
				if (!GlobalConfig.isMac)
					audioItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.playlistItem")));
				audioItem.setText(StringUtils.abbreviate(ad.getName(), GlobalConfig.MAX_MENU_STRING_LENGTH)
						+ (rtl ? I18N.LRM + "" : ""));
				audioItem.setData(ad.getId());
				if (config.getAudio().getCurrent().getId().equals(audioItem.getData()))
					audioItem.setSelection(true);
				audioItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						MenuItem mi = (MenuItem) event.widget;
						if (mi.getSelection() == true) {
							if (!config.getAudio().getCurrent().getId().equals(audioItem.getData())) {
								setAudio((String) mi.getData());
							}
						}
					}
				});
			}
		}

		if (config.getAudio().getAllAudio().size() > 0)
			new MenuItem(recitationListMenu, SWT.SEPARATOR);

		final MenuItem moreRecitationItem = new MenuItem(recitationListMenu, SWT.PUSH);
		moreRecitationItem.setText(lang.getMeaning("MORE") + "...");
		moreRecitationItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HyperlinkUtils.openBrowser(GlobalConfig.RESOURCE_PAGE);
			}
		});

		// ---- Bookmarks -----
		createOrUpdateBookmarkMenu();

		// ---- Tools -----
		MenuItem tools = new MenuItem(menu, SWT.CASCADE | direction);
		tools.setText(FormUtils.addAmpersand(lang.getMeaning("TOOLS")));

		Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		tools.setMenu(toolsMenu);

		Menu addMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		MenuItem addItem = createMenuItem(SWT.CASCADE, toolsMenu, lang.getMeaning("ADD"), 0, "icon.menu.add");
		addItem.setMenu(addMenu);

		// cascading menu for add...
		MenuItem transAddItem = createMenuItem(0, addMenu, lang.getMeaning("TRANSLATION") + "...", 0,
				"icon.menu.translation");
		transAddItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				importTrans();
			}
		});

		MenuItem themeAddItem = createMenuItem(0, addMenu, lang.getMeaning("THEME") + "...", 0, "icon.menu.theme");
		themeAddItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				importTheme();
			}
		});

		MenuItem recitationAddItem = createMenuItem(0, addMenu, lang.getMeaning("RECITATION") + "...", 0,
				"icon.menu.addPlaylist");
		recitationAddItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				importPlaylist();
			}
		});

		// separator
		new MenuItem(toolsMenu, SWT.SEPARATOR);
		MenuItem options = createMenuItem(0, toolsMenu, lang.getMeaning("OPTIONS") + "...", 0, "icon.menu.options");
		options.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new OptionsForm(shell).open();
			}
		});

		// Help menu
		MenuItem help = new MenuItem(menu, SWT.CASCADE | direction);
		help.setText(FormUtils.addAmpersand(lang.getMeaning("HELP")));

		// set the menu for the Help option
		Menu helpMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		help.setMenu(helpMenu);

		MenuItem homePage = createMenuItem(0, helpMenu, lang.getMeaning("HOMEPAGE"), 0, "icon.menu.homepage");
		homePage.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				homepage();
			}
		});

		MenuItem onlineHelpItem = createMenuItem(0, helpMenu, lang.getMeaning("ONLINE_HELP"), 0, "icon.menu.onlineHelp");
		onlineHelpItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				HyperlinkUtils.openBrowser(GlobalConfig.HELP_PAGE);
			}
		});

		// separator
		new MenuItem(helpMenu, SWT.SEPARATOR);

		MenuItem check4UpdateItem = createMenuItem(0, helpMenu, lang.getMeaning("CHECK4UPDATE") + "...", 0,
				"icon.menu.check4Update");
		check4UpdateItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				check4Update();
			}
		});
		check4UpdateItem.setEnabled(config.getProps().getBoolean("update.enable"));

		new MenuItem(helpMenu, SWT.SEPARATOR);
		MenuItem aboutItem = createMenuItem(0, helpMenu, lang.getMeaning("ABOUT"), 0, "icon.menu.about");
		aboutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				about();
			}
		});

		return menu;
	}

	private CustomTranslationListForm customizeMultiTrans() {
		CustomTranslationListForm crlf = new CustomTranslationListForm(shell);
		crlf.show();
		crlf.loopEver();
		return crlf;
	}

	private MenuItem createMenuItem(int swtStyle, Menu parentMenu, String text, int accelerator, String imageKey) {
		MenuItem item = new MenuItem(parentMenu, swtStyle == 0 ? SWT.PUSH : swtStyle);
		String accelStr = "";
		if (accelerator != 0) {
			int accKey = accelerator;
			String combKey = "\t";
			boolean ctrl = false, alt = false;
			if ((accelerator & SWT.CONTROL) == SWT.CONTROL) {
				accKey ^= SWT.CONTROL;
				if (GlobalConfig.isMac) {
					accelerator ^= SWT.CONTROL;
					accelerator |= SWT.COMMAND;
					combKey += "Command";
				} else {
					combKey += "Ctrl";
				}
				ctrl = true;
			}
			if ((accelerator & SWT.ALT) == SWT.ALT) {
				accKey ^= SWT.ALT;
				combKey += (ctrl ? "+" : "") + "Alt";
				alt = true;
			}
			if ((accelerator & SWT.SHIFT) == SWT.SHIFT) {
				accKey ^= SWT.SHIFT;
				combKey += ((alt || ctrl) ? "+" : "") + "Shift";
			}
			item.setAccelerator(accelerator);
			if (accKey > 'A' && accKey < 'Z') {
				accelStr = combKey + "+" + (char) accKey;
			} else { // try function keys
				int f = accKey - SWT.F1 + 1;
				accelStr = combKey + "F" + f;
			}
		}
		item.setText(FormUtils.addAmpersand(text) + accelStr);
		if (imageKey != null)
			item.setImage(new Image(shell.getDisplay(), resource.getString(imageKey)));
		return item;
	}

	protected void gotoRandomAya() {
		Random rnd = new Random(new Date().getTime());
		int juz = rnd.nextInt(30) + 1;
		Range r = QuranPropertiesUtils.getSuraInsideJuz(juz);
		int sura = rnd.nextInt(r.to - r.from + 1) + r.from;
		int aya = rnd.nextInt(QuranPropertiesUtils.getSura(sura).getAyaCount()) + 1;
		form.gotoSuraAya(sura, aya);
	}

	private MenuItem getBookmarksMenu() {
		for (int i = 0; i < menu.getItemCount(); i++) {
			if ("bookmarks".equals(menu.getItem(i).getData())) {
				return menu.getItem(i);
			}
		}
		return null;
	}

	protected void createOrUpdateTranslationMenu() {
		// cascading menu for translation selection
		//		if (transName != null && !transName.isDisposed()) {
		//			transName.dispose();
		//		}
		MenuItem[] transItems = transMenu.getItems();
		for (int i = 0; i < transItems.length; i++) {
			transItems[i].dispose();
		}

		Collection trans = config.getTranslation().getAllTranslation();
		for (Iterator iter = trans.iterator(); iter.hasNext();) {
			TranslationData td = (TranslationData) iter.next();

			final MenuItem transItem = createMenuItem(SWT.RADIO, transMenu, StringUtils.abbreviate((rtl ? I18N.RLE + ""
					: "")
					+ "[" + td.locale + "]" + " " + (rtl ? I18N.RLM + "" : "") + td.localizedName,
					GlobalConfig.MAX_MENU_STRING_LENGTH)
					+ (rtl ? I18N.LRM + "" : ""), 0, "icon.menu.book");

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

		new MenuItem(transMenu, SWT.SEPARATOR);
		customTransList = createMenuItem(0, transMenu, lang.getMeaning("CONFIG_CUSTOM_TRANS") + "...", 0,
				"icon.menu.configTransList");
		customTransList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				customizeMultiTrans();
			}
		});

		// ssssssssss
		new MenuItem(transMenu, SWT.SEPARATOR);

		final MenuItem moreTransItem = new MenuItem(transMenu, SWT.PUSH);
		moreTransItem.setText(lang.getMeaning("MORE") + "...");
		moreTransItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HyperlinkUtils.openBrowser(GlobalConfig.RESOURCE_PAGE);
			}
		});
	}

	protected void createOrUpdateBookmarkMenu() {
		Menu bookmarksMenu;
		MenuItem bookmarks;

		bookmarks = getBookmarksMenu();
		if (bookmarks == null) {
			bookmarks = new MenuItem(menu, SWT.CASCADE);
		} else {
			bookmarks.getMenu().dispose();
		}

		bookmarks.setText(FormUtils.addAmpersand(lang.getMeaning("BOOKMARKS")));
		bookmarks.setData("bookmarks");

		bookmarksMenu = new Menu(shell, SWT.DROP_DOWN | direction);
		bookmarks.setMenu(bookmarksMenu);

		MenuItem bmManagerItem = createMenuItem(0, bookmarksMenu, lang.getMeaning("EDIT_BOOKMARK_SET") + "...",
				SWT.CTRL | 'B', "icon.menu.bookmark.edit");
		bmManagerItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				manageBookmarks();
			}
		});

		MenuItem bookmarkSetConfigItem = createMenuItem(0, bookmarksMenu,
				lang.getMeaning("MANAGE_BOOKMARK_SETS") + "...", 0, "icon.menu.bookmark.manage");
		bookmarkSetConfigItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				manageBookmarkSets();
			}
		});

		new MenuItem(bookmarksMenu, SWT.SEPARATOR);

		MenuItem findRefItem = createMenuItem(0, bookmarksMenu, lang.getMeaning("SHOW_REFS") + "...", SWT.CTRL
				| SWT.SHIFT | 'F', "icon.menu.bookmark.findRef");
		findRefItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				findReferences();
			}
		});

		new MenuItem(bookmarksMenu, SWT.SEPARATOR);

		BookmarkSet bookmark = config.getBookmark();
		List bmItems = bookmark.getBookmarksItems();
		for (int i = 0; i < bmItems.size(); i++) {
			BookmarkItem item = (BookmarkItem) bmItems.get(i);
			BookmarkUtils.addBookmarkItemToMenu(bookmarksMenu, item);
		}
	}

	private void findReferences() {
		IQuranLocation loc = config.getQuranLocation();
		logger.info("Find bookmark references to: " + loc);
		List resultList = BookmarkUtils.findReferences(config.getBookmark(), loc);
		logger.debug("Show references in form.");
		new BookmarkReferenceForm(shell, resultList, loc).open();
	}

	BookmarkSetForm bsf = null;

	private void manageBookmarks() {
		if (bsf != null && Arrays.asList(shell.getShells()).contains(bsf.getShell())) { // shell is already
			// open
			bsf.getShell().forceActive();
			return;
		}

		bsf = new BookmarkSetForm(shell);
		bsf.open();
	}

	private void manageBookmarkSets() {
		new ManageBookmarkSetsForm(shell).open();
	}

	private void importTrans() {
		String destDir = ApplicationPath.TRANSLATION_DIR;
		List errorList = new ArrayList();
		List transFileList = new ArrayList(); // prevent NPE
		try {
			transFileList = MessageBoxUtils.importFileDialog(shell, new String[] { "*.trans.zip Translation Files" },
					new String[] { "*.trans.zip" });
			if (transFileList.size() <= 0)
				return;

			int result = MessageBoxUtils.radioQuestionPrompt(
					new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
							lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION",
							"IMPORT_FOR"), lang.getMeaning("QUESTION"));

			if (result == -1)
				return;

			if (result == 0) // import for "me only"
				destDir = Naming.getTransDir();

			for (Iterator iterator = transFileList.iterator(); iterator.hasNext();) {
				File file2Import = (File) iterator.next();

				if (!file2Import.getName().endsWith(ConfigNaming.TRANS_PACK_SUFFIX)) {
					logger.info("Invalid translation (unknown extension): " + file2Import);
					continue;
				}
				logger.info("Copy translation \"" + file2Import.getName() + "\" to " + destDir);
				File targetTransFile = new File(destDir + "/" + file2Import.getName());
				FileUtils.copyFile(file2Import, targetTransFile);
				try {
					config.addNewTranslation(targetTransFile);
				} catch (ZekrMessageException zme) {
					logger.warn(zme);
					errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
					continue;
				}
				logger.debug("Translation imported successfully: " + file2Import);
			}
			if (errorList.size() > 0) {
				String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
				MessageBoxUtils.showWarning(str);
			} else {
				// MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
				MessageBoxUtils.showMessage(lang.getMeaning("ACTION_PERFORMED"));
			}
		} catch (IOException e) {
			MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + e.getMessage());
			logger.implicitLog(e);
		} finally {
			if (config.getTranslation().getDefault() == null && errorList.size() <= 0 && transFileList.size() > 0)
				MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
			else
				createOrUpdateTranslationMenu();
		}
	}

	/**
	 * This method imports one or more themes into Zekr theme installation directory. Imported theme is in
	 * <tt>zip</tt> format, and after importing, it is extracted to <tt>res/ui/theme</tt>.
	 * theme.properties is then copied into <tt>~/.zekr/config/theme</tt>, renaming to
	 * <tt>[theme ID].properties</tt>.<br>
	 * Note that imported zip file should have the same base name as theme ID (theme directory name).
	 */
	private void importTheme() {
		String destDir = ApplicationPath.THEME_DIR;
		try {
			List list = MessageBoxUtils.importFileDialog(shell, new String[] { "*.zip Theme Files", "*.ZIP Theme Files" },
					new String[] { "*.zip", "*.ZIP" });
			if (list.size() <= 0)
				return;

			int result = MessageBoxUtils.radioQuestionPrompt(
					new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
							lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION",
							"IMPORT_FOR"), lang.getMeaning("QUESTION"));

			if (result == -1)
				return;

			if (result == 0) // import for "me only"
				destDir = Naming.getThemeDir();

			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				File file2Import = (File) iterator.next();
				logger.info("Copy and extract theme file \"" + file2Import.getName() + "\" to " + destDir);
				ZipUtils.extract(file2Import, destDir);

				String themeId = FilenameUtils.getBaseName(file2Import.getName());
				File origTheme = new File(destDir + "/" + themeId + "/" + resource.getString("theme.desc"));
				logger.debug("Copy customizable theme properties " + origTheme.getName() + " to "
						+ Naming.getThemePropsDir());
				FileUtils.copyFile(origTheme, new File(Naming.getThemePropsDir() + "/" + themeId + ".properties"));
				logger.debug("Importing theme done successfully.");
			}
			MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
		} catch (IOException e) {
			MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + e.getMessage());
			logger.implicitLog(e);
		}
	}

	private void importPlaylist() {
		String destDir = ApplicationPath.AUDIO_DIR;
		try {
			List list = MessageBoxUtils.importFileDialog(shell, new String[] { "*.properties Recitation Files" },
					new String[] { "*.properties" });
			if (list.size() <= 0)
				return;

			int result = MessageBoxUtils.radioQuestionPrompt(
					new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
							lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION",
							"IMPORT_FOR"), lang.getMeaning("QUESTION"));

			if (result == -1)
				return;

			if (result == 0) // import for "me only"
				destDir = Naming.getAudioDir();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				File file2Import = (File) iterator.next();

				logger.info("Copy recitation playlist \"" + file2Import.getName() + "\" to " + destDir);
				FileUtils.copyFile(file2Import, new File(destDir + "/" + file2Import.getName()));

				logger.debug("Importing recitation playlist done successfully.");
			}

			MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
		} catch (IOException e) {
			MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + e.getMessage());
			logger.implicitLog(e);
		}
	}

	private void export() {
		try {
			File f = MessageBoxUtils.exportFileDialog(shell, new String[] { "HTML Files", "All Files (*.*)" },
					new String[] { "*.html;*.htm", "*.*" });
			if (f == null || f.isDirectory()) // canceled
				return;
			if (!f.getName().toUpperCase().endsWith(".HTM") && !f.getName().toUpperCase().endsWith(".HTML"))
				f = new File(f.getParent(), f.getName() + ".html");
			logger.info("Save current view to file: " + f);
			FileUtils.copyFile(UriUtils.toFile(form.getCurrentUri()), f);
		} catch (Exception e) {
			MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + e.getMessage());
		}
	}

	protected void about() {
		AboutForm af = new AboutForm(shell);
		af.getShell().setLocation(FormUtils.getCenter(shell, af.getShell()));
		af.show();
	}

	private void homepage() {
		HyperlinkUtils.openBrowser(GlobalConfig.HOME_PAGE);
	}

	private void check4Update() {
		UpdateManager manager = new UpdateManager(shell);
		manager.check(true);
	}

	private void print() {
		form.getQuranBrowser().execute("window.print()");
	}

	private void close() {
		shell.dispose();
	}

	private void reconfigureViewLayout() {
		// very nice business logic!
		// boolean uq = form.updateQuran;
		// boolean ut = form.updateTrans;
		// int oldLayout = form.viewLayout;
		form.setLayout(config.getViewProp("view.viewLayout"));
		// boolean uqNew = form.updateQuran;
		// boolean utNew = form.updateTrans;
		// if (form.viewLayout != QuranForm.MIXED && oldLayout != QuranForm.MIXED) {
		// if (uq && form.updateQuran)
		// form.updateQuran = false;
		// if (ut && form.updateTrans)
		// form.updateTrans = false;
		// }
		form.suraChanged = true;
		form.updateView();
		form.suraChanged = false;
		// if (form.viewLayout != QuranForm.MIXED && oldLayout != QuranForm.MIXED) {
		// form.updateQuran = uqNew;
		// form.updateTrans = utNew;
		// }
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
		try {
			config.setCurrentTranslation(transId);
			if (form.viewLayout != QuranForm.QURAN_ONLY)
				form.reload();
		} catch (ZekrMessageException zme) {
			logger.error(zme);
			MessageBoxUtils.showError(zme);
			createOrUpdateTranslationMenu();
		}
	}

	private void setAudio(String audioId) {
		config.setCurrentAudio(audioId);
		form.reload();
	}

	public void toggleFullScreenItem(boolean selected) {
		fullScreenItem.setSelection(selected);
	}

	private void recreateForm() {
		form.recreate();
	}

	public void playerStop(final boolean bubbleEvent) {
		if (playItem.getData().equals("play")) {
			playerTogglePlayPause(bubbleEvent);
		}
		if (bubbleEvent)
			form.sendPlayerStop();
	}

	public void playerTogglePlayPause(final boolean bubbleEvent) {
		String text = playItem.getText();
		String accelText = text.substring(text.indexOf('\t'));
		if (playItem.getData().equals("play")) {
			playItem.setData("pause");
			playItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.play")));
			playItem.setText(FormUtils.addAmpersand(lang.getMeaning("PLAY") + accelText));
		} else {
			playItem.setData("play");
			playItem.setImage(new Image(shell.getDisplay(), resource.getString("icon.menu.pause")));
			playItem.setText(FormUtils.addAmpersand(lang.getMeaning("PAUSE") + accelText));
		}
		if (bubbleEvent)
			form.sendPlayerTogglePlayPause();
	}

	public void resetAudioMenuStatus() {
		if (playItem.getData().equals("play"))
			playerTogglePlayPause(false);
	}

	public void resetMenuStatus() {
		resetAudioMenuStatus();
	}

	public void setAudioMenuEnabled(boolean state) {
		audioItem.setEnabled(state);
	}

	public void resetAudioMenuEnableState() {
		audioItem.setEnabled(config.isAudioEnabled());
	}
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.KeyboardShortcut;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.common.util.HyperlinkUtils;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.common.util.IntallationProgressListener;
import net.sf.zekr.common.util.ZipUtils;
import net.sf.zekr.engine.addonmgr.InvalidResourceException;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.audio.PlayStatus;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.ui.AudioPlayerForm.DockMode;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.ui.BookmarkSetForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.page.FixedAyaPagingData;
import net.sf.zekr.engine.page.HizbQuarterPagingData;
import net.sf.zekr.engine.page.IPagingData;
import net.sf.zekr.engine.page.JuzPagingData;
import net.sf.zekr.engine.page.SuraPagingData;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.helper.CocoaUiEnhancer;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * This is not a real factory class, but in fact hides menu creation and updating details from <code>QuranForm</code>.
 * 
 * @author Mohsen Saboorian
 */
public class QuranFormMenuFactory {
   private final static ResourceManager resource = ResourceManager.getInstance();
   private static final Logger logger = Logger.getLogger(QuranFormMenuFactory.class);

   Shell shell;
   ApplicationConfig config;
   LanguageEngine lang;
   QuranForm quranForm;
   Display display;

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
   private MenuItem saveAsItem;
   private MenuItem printItem;
   private MenuItem exitItem;
   private MenuItem view;
   private Menu viewMenu;
   private MenuItem suraReloadItem;
   private MenuItem langName;
   private MenuItem transName, viewMode;
   private MenuItem quranOnly;
   private MenuItem transOnly;
   private MenuItem separate;
   private MenuItem mixed;
   private MenuItem multiTrans;
   private int direction;
   private MenuItem randomAyaItem;
   private Menu transMenu, viewModeMenu;
   private MenuItem _def;
   private Menu audioMenu;
   private MenuItem playItem;
   private MenuItem stopItem;
   private MenuItem nextSura, nextAya, prevSura, prevAya;
   private MenuItem nextJuz, prevJuz, nextHizbQ, prevHizbQ, nextSajda, prevSajda, nextPage, prevPage;
   private MenuItem fullScreenItem;
   private MenuItem detailPanelItem;
   private MenuItem audioPanelItem;

   private final boolean SHOW_MENU_IMAGE = !GlobalConfig.isMac; // do not show menu item image on Mac

   BookmarkSetForm bsf = null;
   private PropertiesConfiguration props;
   private PlayerController playerController;
   private Menu recitationListMenu;
   private String pauseIconFullPath;
   private String playIconFullPath;

   public QuranFormMenuFactory(QuranForm form, Shell shell) {
      quranForm = form;
      config = ApplicationConfig.getInstance();
      props = config.getProps();
      lang = config.getLanguageEngine();
      this.shell = shell;
      display = shell.getDisplay();
      direction = lang.getSWTDirection();
      rtl = direction == SWT.RIGHT_TO_LEFT;
      playerController = config.getPlayerController();
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
      saveAsItem = createMenuItem(SWT.PUSH, fileMenu, lang.getMeaning("SAVE_AS") + "...", "savePageAs", "icon.menu.save");
      // disabled exporting if HTTP server is enabled
      saveAsItem.setEnabled(!config.isHttpServerEnabled());

      // print
      printItem = createMenuItem(SWT.PUSH, fileMenu, lang.getMeaning("PRINT") + "...", "print", "icon.menu.print");

      // add exit item
      if (!GlobalConfig.isCocoa) {
         exitItem = createMenuItem(SWT.PUSH, fileMenu, lang.getMeaning("EXIT"), "quit", "icon.menu.exit");
      }

      // ---- View -----
      view = new MenuItem(menu, SWT.CASCADE | direction);
      view.setText(FormUtils.addAmpersand(lang.getMeaning("VIEW")));

      // set the menu for the View option
      viewMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      view.setMenu(viewMenu);

      suraReloadItem = createMenuItem(SWT.PUSH, viewMenu, lang.getMeaning("RELOAD"), "reload", "icon.menu.reload");

      MenuItem hwm = createMenuItem(SWT.CHECK, viewMenu, lang.getMeaning("HIDE_WHEN_MINIMIZED"), "hideWhenMinimized", null);
      hwm.setSelection(config.getProps().getBoolean("view.hideWhenMinimized", false));

      // separator
      new MenuItem(viewMenu, SWT.SEPARATOR);
      transName = createMenuItem(SWT.CASCADE | direction, viewMenu, lang.getMeaning("TRANSLATION"), "icon.menu.transList");
      transMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      transName.setMenu(transMenu);
      createOrUpdateTranslationMenu();

      // view mode: sura, aya, juz, hizb or custom
      new MenuItem(viewMenu, SWT.SEPARATOR);
      viewMode = createMenuItem(SWT.CASCADE | direction, viewMenu, lang.getMeaning("PAGING_MODE"), null);
      viewModeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      viewMode.setMenu(viewModeMenu);
      MenuItem suraViewMode = createMenuItem(SWT.RADIO, viewModeMenu, lang.getMeaningById("PAGING_MODE", "SURA"), null);
      MenuItem fixedAyaViewMode = createMenuItem(SWT.RADIO, viewModeMenu,
            lang.getMeaningById("PAGING_MODE", "CONST_AYA") + "...", null);
      MenuItem hizbViewMode = createMenuItem(SWT.RADIO, viewModeMenu, lang.getMeaningById("PAGING_MODE", "HIZB_QUARTER"), null);
      MenuItem juzViewMode = createMenuItem(SWT.RADIO, viewModeMenu, lang.getMeaningById("PAGING_MODE", "JUZ"), null);
      MenuItem customViewMode = createMenuItem(SWT.RADIO, viewModeMenu, lang.getMeaningById("PAGING_MODE", "CUSTOM") + "...",
            null);

      suraViewMode.setData(SuraPagingData.ID);
      fixedAyaViewMode.setData(FixedAyaPagingData.ID);
      juzViewMode.setData(JuzPagingData.ID);
      hizbViewMode.setData(HizbQuarterPagingData.ID);
      // customViewMode.setData("<custom>");
      IPagingData page = config.getQuranPaging().getDefault();

      final Map<Object, MenuItem> pagingItems = new HashMap<Object, MenuItem>();
      pagingItems.put(suraViewMode.getData(), suraViewMode);
      pagingItems.put(fixedAyaViewMode.getData(), fixedAyaViewMode);
      pagingItems.put(juzViewMode.getData(), juzViewMode);
      pagingItems.put(hizbViewMode.getData(), hizbViewMode);

      _def = pagingItems.get(page.getId());
      if (_def == null) {
         _def = customViewMode;
      }
      _def.setSelection(true);

      SelectionListener viewModeSelection = new SelectionAdapter() {
         MenuItem prevItem = _def;

         public void widgetSelected(SelectionEvent e) {
            boolean changeMode = true;
            if (!((MenuItem) e.widget).getSelection()) {
               prevItem = (MenuItem) e.widget;
               return;
            }

            String data = (String) e.widget.getData();
            if (SuraPagingData.ID.equals(data)) {
            } else if (FixedAyaPagingData.ID.equals(data)) {
               if (!setFixedAyaMode()) {
                  ((MenuItem) e.widget).setSelection(false);
                  prevItem.setSelection(true);
                  changeMode = false;
               } else {
                  prevItem = (MenuItem) e.widget;
               }
            } else if (HizbQuarterPagingData.ID.equals(data)) {
            } else if (JuzPagingData.ID.equals(data)) {
            } else {
               logger.info("Choose custom page mode.");
               CustomPageModeForm pageModeForm = new CustomPageModeForm(shell);
               pageModeForm.show();
               pageModeForm.loopEver();
               data = pageModeForm.getPagingMode();
               if (data == null) {
                  ((MenuItem) e.widget).setSelection(false);
                  prevItem.setSelection(true);
                  changeMode = false;
               } else {
                  prevItem = (MenuItem) e.widget;
               }
            }
            if (changeMode) {
               updatePagingMode(data);
            }
         }
      };

      suraViewMode.addSelectionListener(viewModeSelection);
      juzViewMode.addSelectionListener(viewModeSelection);
      hizbViewMode.addSelectionListener(viewModeSelection);
      fixedAyaViewMode.addSelectionListener(viewModeSelection);
      customViewMode.addSelectionListener(viewModeSelection);

      // cascading menu for view type
      MenuItem layoutType = createMenuItem(SWT.CASCADE | direction, viewMenu, lang.getMeaning("LAYOUT"), "icon.menu.layout");

      Menu layoutTypeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      layoutType.setMenu(layoutTypeMenu);

      quranOnly = createMenuItem(SWT.RADIO, layoutTypeMenu, lang.getMeaning("QURAN"), "icon.menu.quranOnly");
      quranOnly.setData("quranOnly");

      transOnly = createMenuItem(SWT.RADIO, layoutTypeMenu, lang.getMeaning("TRANSLATION"), "icon.menu.transOnly");
      transOnly.setData("transOnly");

      separate = createMenuItem(SWT.RADIO, layoutTypeMenu, lang.getMeaning("SEPARATE"), "icon.menu.separate");
      separate.setData("separate");

      mixed = createMenuItem(SWT.RADIO, layoutTypeMenu, lang.getMeaning("MIXED"), "icon.menu.mixed");
      mixed.setData("mixed");

      multiTrans = createMenuItem(SWT.RADIO, layoutTypeMenu, lang.getMeaning("MULTI_TRANS"), "icon.menu.mixed");
      multiTrans.setData("customMixed");

      new MenuItem(layoutTypeMenu, SWT.SEPARATOR | direction);

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
                  /*if (config.getTranslation().getCustomGroup().size() == 0) {*/
                     quranForm.quranFormController.configureMultiTrans();
                  /*}*/
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

      quranViewType = new MenuItem(layoutTypeMenu, SWT.CASCADE | direction);
      quranViewType.setText(FormUtils.addAmpersand(lang.getMeaning("QURAN_VIEWTYPE")));
      transViewType = new MenuItem(layoutTypeMenu, SWT.CASCADE | direction);
      transViewType.setText(FormUtils.addAmpersand(lang.getMeaning("TRANSLATION_VIEWTYPE")));

      // Set default selection
      if (config.getTranslation().getDefault() == null) { // if no translation found
         layoutType.setEnabled(false);
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
            if (!((MenuItem) e.widget).getSelection()) {
               return;
            }
            if (e.widget.getData().equals("quran")) {
               logger.info("Change Quran layout to block layout.");
               config.setQuranLayout(ApplicationConfig.BLOCK);
               reloadQuran();
            } else {
               logger.info("Change translation layout to block layout.");
               config.setTransLayout(ApplicationConfig.BLOCK);
               reloadTrans();
            }
         }
      };

      quranBlockLayoutItem = createMenuItem(SWT.RADIO, quranViewMenu, lang.getMeaning("BLOCK"), "icon.menu.textBlock");
      quranBlockLayoutItem.addListener(SWT.Selection, blockListener);
      quranBlockLayoutItem.setData("quran");

      transBlockLayoutItem = createMenuItem(SWT.RADIO, transViewMenu, lang.getMeaning("BLOCK"), "icon.menu.textBlock");
      transBlockLayoutItem.addListener(SWT.Selection, blockListener);
      transBlockLayoutItem.setData("trans");

      Listener inlineListener = new Listener() {
         public void handleEvent(Event e) {
            if (!((MenuItem) e.widget).getSelection()) {
               return;
            }
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

      quranLineLayoutItem = createMenuItem(SWT.RADIO, quranViewMenu, lang.getMeaning("LINE_BY_LINE"), "icon.menu.textLineByLine");
      quranLineLayoutItem.addListener(SWT.Selection, inlineListener);
      quranLineLayoutItem.setData("quran");

      transLineLayoutItem = createMenuItem(SWT.RADIO, transViewMenu, lang.getMeaning("LINE_BY_LINE"), "icon.menu.textLineByLine");
      transLineLayoutItem.addListener(SWT.Selection, inlineListener);
      transLineLayoutItem.setData("trans");

      MenuItem gotoMenuItem = createMenuItem(SWT.CASCADE, menu, lang.getMeaning("GOTO"), null);
      Menu gotoMenu = new Menu(shell, SWT.DROP_DOWN);
      gotoMenuItem.setMenu(gotoMenu);

      randomAyaItem = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("RANDOM_AYA"), "gotoRandomAya", "icon.menu.randomAya");

      new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

      createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("GOTO") + "...", "gotoForm", "icon.menu.goto");

      nextSura = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_SURA"), "gotoNextSura", null);
      prevSura = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_SURA"), "gotoPrevSura", null);

      nextAya = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_AYA"), "gotoNextAya", null);
      prevAya = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_AYA"), "gotoPrevAya", null);

      new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

      nextPage = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_PAGE"), "gotoNextPage", null);

      prevPage = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_PAGE"), "gotoPrevPage", null);

      new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

      nextHizbQ = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_HIZBQ"), "gotoNextHizbQuarter", null);
      prevHizbQ = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_HIZBQ"), "gotoPrevHizbQuarter", null);

      new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

      nextJuz = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_JUZ"), "gotoNextJuz", null);
      prevJuz = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_JUZ"), "gotoPrevJuz", null);

      new MenuItem(gotoMenu, SWT.SEPARATOR | direction);

      nextSajda = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_NEXT_SAJDA"), "gotoNextSajda", null);
      prevSajda = createMenuItem(SWT.PUSH, gotoMenu, lang.getMeaning("MENU_PREV_SAJDA"), "gotoPrevSajda", null);

      // Set default selection
      String quranLayout = config.getQuranLayout();
      String transLayout = config.getTransLayout();
      if (quranLayout.equals(ApplicationConfig.LINE_BY_LINE)) {
         quranLineLayoutItem.setSelection(true);
      } else if (quranLayout.equals(ApplicationConfig.BLOCK)) {
         quranBlockLayoutItem.setSelection(true);
      }
      if (transLayout.equals(ApplicationConfig.LINE_BY_LINE)) {
         transLineLayoutItem.setSelection(true);
      } else if (transLayout.equals(ApplicationConfig.BLOCK)) {
         transBlockLayoutItem.setSelection(true);
      }

      // show view parts
      MenuItem showView = createMenuItem(SWT.CASCADE, viewMenu, lang.getMeaning("PANEL"), null);
      Menu showViewMenu = new Menu(shell, SWT.DROP_DOWN);
      showView.setMenu(showViewMenu);
      detailPanelItem = createMenuItem(SWT.CHECK, showViewMenu, lang.getMeaning("DETAIL_PANEL"), "toggleDetailPanel", null);
      detailPanelItem.setSelection(props.getBoolean("view.panel.detail", true));

      // full-screen menu item
      new MenuItem(viewMenu, SWT.SEPARATOR);
      fullScreenItem = createMenuItem(SWT.CHECK, viewMenu, lang.getMeaning("FULL_SCREEN"), "toggleFullScreen",
            "icon.menu.fullScreen");

      // ---- Audio ------
      MenuItem audioItem = new MenuItem(menu, SWT.CASCADE | direction);
      audioItem.setText(FormUtils.addAmpersand(lang.getMeaning("AUDIO")));

      audioMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      audioItem.setMenu(audioMenu);

      playIconFullPath = new File(resource.getString(rtl ? "icon.menu.playRtl" : "icon.menu.play")).getAbsolutePath();
      pauseIconFullPath = new File(resource.getString("icon.menu.pause")).getAbsolutePath();

      playItem = createMenuItem(SWT.PUSH, audioMenu, lang.getMeaning("PLAY"), "playerTogglePlayPause", rtl ? "icon.menu.playRtl"
            : "icon.menu.play");
      playItem.setData(PlayStatus.PAUSE); // state

      stopItem = createMenuItem(SWT.PUSH, audioMenu, lang.getMeaning("STOP"), "playerStop", "icon.menu.stop");

      createMenuItem(SWT.PUSH, audioMenu, lang.getMeaning("NEXT_AYA"), "playerNext", rtl ? "icon.menu.playerPrev"
            : "icon.menu.playerNext");
      createMenuItem(SWT.PUSH, audioMenu, lang.getMeaning("PREV_AYA"), "playerPrev", rtl ? "icon.menu.playerNext"
            : "icon.menu.playerPrev");

      new MenuItem(audioMenu, SWT.SEPARATOR);

      audioPanelItem = createMenuItem(SWT.CHECK, audioMenu, lang.getMeaning("AUDIO_PLAYER"), "toggleAudioPanel", null);
      audioPanelItem.setSelection(config.getProps().getBoolean("audio.controller.show", true));

      if (config.getAudio().getCurrent() == null) {
         playItem.setEnabled(false);
         stopItem.setEnabled(false);

         config.getProps().setProperty("audio.controller.show", false);
         toggleAudioPanelState(false);
         audioPanelItem.setEnabled(false);
      }

      MenuItem audioPlayerDock = createMenuItem(SWT.CASCADE, audioMenu, lang.getMeaning("DOCK_MODE"), null);
      // audioPanelItem.setSelection(config.getProps().getString("audio.controller.dock", "bottom"));
      Menu dockModeMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      audioPlayerDock.setMenu(dockModeMenu);

      String dockStr = config.getProps().getString("audio.controller.dock", "bottom");
      for (DockMode dock : DockMode.values()) {
         MenuItem dockItem = new MenuItem(dockModeMenu, SWT.RADIO);
         dockItem.setText(lang.getMeaning(dock.toString()));
         dockItem.setData(dock.toString().toLowerCase());
         dockItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (((MenuItem) e.widget).getSelection()) {
                  Object data = e.widget.getData();
                  config.getProps().setProperty("audio.controller.dock", data);
                  quranForm.quranFormController.changePlayerDockMode();
               }
            }
         });
         if (dock.toString().toLowerCase().equals(dockStr)) {
            dockItem.setSelection(true);
         }
      }

      new MenuItem(audioMenu, SWT.SEPARATOR);

      // cascading menu for audio pack selection
      MenuItem recitationName = createMenuItem(SWT.CASCADE, audioMenu, lang.getMeaning("RECITATION"), "icon.menu.playlist");
      recitationListMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      recitationName.setMenu(recitationListMenu);

      createOrUpdateRecitationMenu();

      // ---- Bookmarks -----
      createOrUpdateBookmarkMenu();

      // ---- Tools -----
      MenuItem tools = new MenuItem(menu, SWT.CASCADE | direction);
      tools.setText(FormUtils.addAmpersand(lang.getMeaning("TOOLS")));

      Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      tools.setMenu(toolsMenu);

      Menu addMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      MenuItem addItem = createMenuItem(SWT.CASCADE, toolsMenu, lang.getMeaning("ADD"), "icon.menu.addResource");
      addItem.setMenu(addMenu);

      // cascading menu for add...
      MenuItem transAddItem = createMenuItem(SWT.PUSH, addMenu, lang.getMeaning("TRANSLATION") + "...", "icon.menu.addTrans");
      transAddItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            importTrans();
         }
      });

      MenuItem themeAddItem = createMenuItem(SWT.PUSH, addMenu, lang.getMeaning("THEME") + "...", "icon.menu.theme");
      themeAddItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            importTheme();
         }
      });

      MenuItem recitationAddItem = createMenuItem(SWT.PUSH, addMenu, lang.getMeaning("RECITATION") + " (*.properties) ...",
            "icon.menu.addOnlineRecitation");
      recitationAddItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            importOnlineRecitation();
         }
      });

      MenuItem recitationPackAddItem = createMenuItem(SWT.PUSH, addMenu, lang.getMeaning("RECITATION") + " (*.recit.zip) ...",
            "icon.menu.addOfflineRecitation");
      recitationPackAddItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(Event e) {
            importRecitationPack();
         }
      });

      // separator
      // new MenuItem(toolsMenu, SWT.SEPARATOR);

      // Add-on Manager
      // TODO: to be revised
      // createMenuItem(SWT.PUSH, toolsMenu, lang.getMeaning("ADDON_MANAGER"), "addOnManager", "icon.menu.add");

      // separator
      new MenuItem(toolsMenu, SWT.SEPARATOR);

      createMenuItem(SWT.PUSH, toolsMenu, lang.getMeaning("OPTIONS") + "...", "options", "icon.menu.options");

      // Help menu
      MenuItem help = new MenuItem(menu, SWT.CASCADE | direction);
      help.setText(FormUtils.addAmpersand(lang.getMeaning("HELP")));

      // set the menu for the Help option
      Menu helpMenu = new Menu(shell, SWT.DROP_DOWN | direction);
      help.setMenu(helpMenu);

      MenuItem homePage = createMenuItem(SWT.PUSH, helpMenu, lang.getMeaning("HOMEPAGE"), "icon.menu.homepage");
      homePage.setData(FormUtils.URL_DATA, GlobalConfig.HOME_PAGE);
      FormUtils.addLinkListener(homePage);

      createMenuItem(SWT.PUSH, helpMenu, lang.getMeaning("ONLINE_HELP"), "onlineHelp", "icon.menu.onlineHelp");

      // separator
      new MenuItem(helpMenu, SWT.SEPARATOR);

      MenuItem check4UpdateItem = createMenuItem(SWT.PUSH, helpMenu, lang.getMeaning("CHECK4UPDATE") + "...", "check4Update",
            "icon.menu.check4Update");
      check4UpdateItem.setEnabled(props.getBoolean("update.enableMenu", true));

      new MenuItem(helpMenu, SWT.SEPARATOR);
      createMenuItem(SWT.PUSH, helpMenu, lang.getMeaning("ABOUT"), "about", "icon.menu.about");

      if (GlobalConfig.isCocoa) {
         hoolSetupMacCocoaApplicationMenu();
      }

      return menu;
   }

   private void hoolSetupMacCocoaApplicationMenu() {
      CocoaUiEnhancer cue = new CocoaUiEnhancer(lang.getMeaning("APP_NAME"));
      Listener quitListener = new Listener() {
         public void handleEvent(Event event) {
            quranForm.quranFormController.quit();
         }
      };
      Runnable preferencesAction = new Runnable() {
         public void run() {
            quranForm.quranFormController.options();
         }
      };
      Runnable aboutAction = new Runnable() {
         public void run() {
            quranForm.quranFormController.about();
         }
      };
      cue.hookApplicationMenu(display, quitListener, aboutAction, preferencesAction);
   }

   private MenuItem createMenuItem(int swtStyle, Menu parentMenu, String text, String imageKey) {
      return createMenuItem(swtStyle, parentMenu, text, null, 0, imageKey, null, null);
   }

   private MenuItem createMenuItem(int swtStyle, Menu parentMenu, String text, String methodName, String imageKey) {
      return createMenuItem(swtStyle, parentMenu, text, methodName, 0, imageKey, null, null);
   }

   private MenuItem createMenuItem(int swtStyle, Menu parentMenu, String text, String action, int accelerator, String imageKey,
         String data, String acceleratorStr) {
      MenuItem item = new MenuItem(parentMenu, swtStyle == 0 ? SWT.PUSH : swtStyle);
      boolean rtl = !lang.isLtr() && GlobalConfig.hasBidiSupport;

      KeyboardShortcut shortcut = config.getShortcut();
      if (action != null && shortcut != null) {
         Integer accel = shortcut.getKeyForAction(action, rtl);
         if (accel != null) {
            item.setAccelerator(accel);
            String keyCodeToString = KeyboardShortcut.keyCodeToString(accel);
            String accelStr = "\t" + keyCodeToString + (rtl ? I18N.LRM + "" : "");
            if (GlobalConfig.isMac) {
               text = FormUtils.addAmpersand(text);
            } else {
               text = FormUtils.addAmpersand(text) + accelStr;
            }
         }
      }
      item.setText(FormUtils.addAmpersand(text));

      if (imageKey != null && SHOW_MENU_IMAGE) {
         try {
            item.setImage(new Image(shell.getDisplay(), resource.getString(imageKey)));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      if (data != null) {
         item.setData(data);
      }
      if (action != null) {
         item.addListener(SWT.Selection, quranForm.quranFormController.registerAction(item, action).toListener());
      }
      return item;
   }

   private boolean setFixedAyaMode() {
      int aypp = config.getProps().getInt("view.pagingMode.ayaPerPage", 20);
      String ayaStr = MessageBoxUtils.textBoxPrompt(lang.getMeaning("QUESTION"),
            lang.getMeaningById("PAGING_MODE", "AYA_PER_SURA"), String.valueOf(aypp));
      if (!StringUtils.isBlank(ayaStr)) {
         try {
            int aya = Integer.parseInt(ayaStr);
            if (aya <= 0 || aya > QuranPropertiesUtils.QURAN_AYA_COUNT) {
               MessageBoxUtils.showError(lang.getDynamicMeaning("NUMBER_LIMIT",
                     new String[] { "1", String.valueOf(QuranPropertiesUtils.QURAN_AYA_COUNT) }));
               logger.error("Aya-per-page out of bound: " + aya);
               return false;
            }
            FixedAyaPagingData fapd = (FixedAyaPagingData) config.getQuranPaging().get(FixedAyaPagingData.ID);
            logger.info("Reload fixed aya paging data with aya-per-page set to: " + aya);
            fapd.reload(aya);

            config.getProps().setProperty("view.pagingMode.ayaPerPage", ayaStr);
            return true;
         } catch (NumberFormatException e) {
            logger.implicitLog(e);
            MessageBoxUtils.showError(lang.getMeaning("ENTER_VALID_NUMBER"));
         }
      }
      return false;
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
      MenuItem[] transItems = transMenu.getItems();
      for (int i = 0; i < transItems.length; i++) {
         transItems[i].dispose();
      }

      List<TranslationData> trans = config.getTranslation().getAllTranslation();
      String transNameMode = props.getString("trans.name.mode", "english");
      for (TranslationData td : trans) {
         String img = getTranslationValidityIcon(td);

         final MenuItem transItem = createMenuItem(SWT.RADIO, transMenu, td.getName(transNameMode, rtl)
               + (rtl ? I18N.LRM + "" : ""), img);

         transItem.setData(td.id);
         if (config.getTranslation().getDefault().id.equals(transItem.getData())) {
            transItem.setSelection(true);
         }
         transItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
               MenuItem mi = (MenuItem) event.widget;
               if (mi.getSelection() == true) {
                  if (!config.getTranslation().getDefault().id.equals(transItem.getData())) {
                     setTrans(mi);
                  }
               }
            }
         });
      }

      new MenuItem(transMenu, SWT.SEPARATOR);
      createMenuItem(SWT.PUSH, transMenu, lang.getMeaning("CONFIG_CUSTOM_TRANS") + "...", "configureMultiTrans",
            "icon.menu.configTransList");
      //		configureMultiTransList.addSelectionListener(new SelectionAdapter() {
      //			public void widgetSelected(SelectionEvent e) {
      //				customizeMultiTrans();
      //			}
      //		});

      if (config.getTranslation().getAllTranslation().size() > 0) {
         new MenuItem(transMenu, SWT.SEPARATOR);
      }

      final MenuItem moreTransItem = new MenuItem(transMenu, SWT.PUSH);
      moreTransItem.setText(lang.getMeaning("MORE") + "...");
      moreTransItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            HyperlinkUtils.openBrowser(GlobalConfig.RESOURCE_PAGE);
         }
      });
   }

   public void createOrUpdateRecitationMenu() {
      MenuItem[] recitationItems = recitationListMenu.getItems();
      for (int i = 0; i < recitationItems.length; i++) {
         recitationItems[i].dispose();
      }

      if (config.getAudio().getCurrent() != null) {
         Collection<AudioData> recitationList = config.getAudio().getAllAudio();

         List<AudioData> ret = new ArrayList<AudioData>(recitationList);
         Collections.sort(ret, new Comparator<AudioData>() {
            public int compare(AudioData o1, AudioData o2) {
               return o1.id.compareTo(o2.id);
            }
         });

         for (AudioData audioData : ret) {
            MenuItem audioItem = new MenuItem(recitationListMenu, SWT.RADIO);
            if (SHOW_MENU_IMAGE) {
               audioItem.setImage(new Image(shell.getDisplay(), audioData.isOffline() ? resource
                     .getString("icon.menu.offlineRecitationPack") : resource.getString("icon.menu.onlineRecitationPack")));
            }
            audioItem.setText(getAudioDataText(audioData));
            audioItem.setData(audioData.id);
            if (config.getAudio().getCurrent().id.equals(audioItem.getData())) {
               audioItem.setSelection(true);
            }
            audioItem.addListener(SWT.Selection, new Listener() {
               public void handleEvent(Event event) {
                  MenuItem mi = (MenuItem) event.widget;
                  if (mi.getSelection() == true) {
                     if (!config.getAudio().getCurrent(event.index).id.equals(mi.getData())) {
                        setAudio(mi.getParent() == recitationListMenu, (String) mi.getData(), event.index);
                     }
                  }
               }
            });
         }
      }

      if (config.getAudio().getAllAudio().size() > 0) {
         new MenuItem(recitationListMenu, SWT.SEPARATOR);
      }

      final MenuItem moreRecitationItem = new MenuItem(recitationListMenu, SWT.PUSH);
      moreRecitationItem.setText(lang.getMeaning("MORE") + "...");
      moreRecitationItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            HyperlinkUtils.openBrowser(GlobalConfig.RESOURCE_PAGE);
         }
      });
   }

   public Menu getRecitationListMenu() {
      return recitationListMenu;
   }

   private String getTranslationValidityIcon(TranslationData td) {
      String img = "icon.menu.transUnknown";
      if (td.getVerificationResult() == TranslationData.AUTHENTIC) {
         img = "icon.menu.transValid";
      } else if (td.getVerificationResult() == TranslationData.NOT_AUTHENTIC) {
         img = "icon.menu.transInvalid";
      }
      return img;
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

      createMenuItem(SWT.PUSH, bookmarksMenu, lang.getMeaning("EDIT_BOOKMARK_SET") + "...", "manageBookmarks",
            "icon.menu.bookmark.edit");

      createMenuItem(SWT.PUSH, bookmarksMenu, lang.getMeaning("MANAGE_BOOKMARK_SETS") + "...", "manageBookmarkSets",
            "icon.menu.bookmark.manage");

      new MenuItem(bookmarksMenu, SWT.SEPARATOR);

      createMenuItem(SWT.PUSH, bookmarksMenu, lang.getMeaning("BOOKMARK_THIS_AYA") + "...", "bookmarkThis",
            "icon.menu.bookmark.add");

      createMenuItem(SWT.PUSH, bookmarksMenu, lang.getMeaning("SHOW_REFS") + "...", "findBookmarkReferences",
            "icon.menu.bookmark.findRef");

      new MenuItem(bookmarksMenu, SWT.SEPARATOR);

      BookmarkSet bookmark = config.getBookmark();
      List<BookmarkItem> bmItems = bookmark.getBookmarksItems();
      for (BookmarkItem item : bmItems) {
         BookmarkUtils.addBookmarkItemToMenu(bookmarksMenu, item);
      }
   }

   private void importTrans() {
      String destDir = ApplicationPath.TRANSLATION_DIR;
      List<String> errorList = new ArrayList<String>();
      List<File> transFileList = new ArrayList<File>(); // prevent NPE
      List<String> addedList = new ArrayList<String>();
      try {
         String transExt = GlobalConfig.isMac ? "*.zip" : "*.trans.zip";
         transFileList = MessageBoxUtils.importFileDialog(shell, new String[] { "Translation Packs (" + transExt + ")",
               "All Files (*.*)" }, new String[] { transExt, "*.*" });
         if (transFileList.size() <= 0) {
            return;
         }

         int result = MessageBoxUtils.radioQuestionPrompt(
               new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
                     lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION", "IMPORT_FOR"),
               lang.getMeaning("QUESTION"));

         if (result == -1) {
            return;
         }

         if (result == 0) {
            destDir = Naming.getTransDir();
         }

         for (File file2Import : transFileList) {
            if (!file2Import.getName().endsWith(ApplicationPath.TRANS_PACK_SUFFIX)) {
               logger.info("Invalid translation (unknown extension): " + file2Import);
               continue;
            }
            logger.info("Copy translation \"" + file2Import.getName() + "\" to " + destDir);
            File tfile = new File(destDir + "/" + file2Import.getName());
            FileUtils.copyFile(file2Import, tfile);
            try {
               try {
                  config.addNewTranslation(tfile);
               } catch (InvalidResourceException e) {
                  errorList.add(lang.getDynamicMeaning("INVALID_TRANSLATION_SIGNATURE", new String[] { tfile.getName() }));
               } finally {
                  addedList.add(tfile.getName());

               }
            } catch (ZekrMessageException zme) {
               logger.warn("Loading translation pack failed", zme);
               errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
               continue;
            }
            logger.debug("Translation imported successfully: " + file2Import);
         }
         if (errorList.size() > 0) {
            String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
            MessageBoxUtils.showWarning(str);
         }
      } catch (IOException e) {
         MessageBoxUtils.showActionFailureError(e);
         logger.implicitLog(e);
      } finally {
         if (config.getTranslation().getDefault() == null && errorList.size() <= 0 && transFileList.size() > 0) {
            MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
         } else if (addedList.size() > 0) {
            createOrUpdateTranslationMenu();
            String str = lang.getMeaning("VIEW") + " > " + lang.getMeaning("TRANSLATION");
            String rlm = rtl ? I18N.RLM + "" : "";
            MessageBoxUtils.showMessage(rlm + lang.getMeaning("ACTION_PERFORMED") + "\n"
                  + lang.getDynamicMeaning("TRANSLATION_ADDED", new String[] { str }) + ":\n    "
                  + CollectionUtils.toString(addedList, lang.getMeaning("COMMA") + "\n    "));
         }
      }
   }

   /**
    * This method imports one or more themes into Zekr theme installation directory. Imported theme is in <tt>zip</tt> format, and
    * after importing, it is extracted to <tt>res/ui/theme</tt>. theme.properties is then copied into
    * <tt>~/.zekr/config/theme</tt>, renaming to <tt>[theme ID].properties</tt>.<br>
    * Note that imported zip file should have the same base name as theme ID (theme directory name).
    */
   private void importTheme() {
      String destDir = ApplicationPath.THEME_DIR;
      try {
         List<File> list = MessageBoxUtils
               .importFileDialog(shell, new String[] { "*.zip Theme Files" }, new String[] { "*.zip" });
         if (list.size() <= 0) {
            return;
         }

         int result = MessageBoxUtils.radioQuestionPrompt(
               new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
                     lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION", "IMPORT_FOR"),
               lang.getMeaning("QUESTION"));

         if (result == -1) {
            return;
         }

         if (result == 0) {
            destDir = Naming.getThemeDir();
         }

         for (File file2Import : list) {
            logger.info("Copy and extract theme file \"" + file2Import.getName() + "\" to " + destDir);
            ZipUtils.extract(file2Import, destDir);

            String themeId = FilenameUtils.getBaseName(file2Import.getName());
            File origTheme = new File(destDir + "/" + themeId + "/" + ApplicationPath.THEME_DESC);
            logger.debug("Copy customizable theme properties " + origTheme.getName() + " to " + Naming.getThemePropsDir());
            FileUtils.copyFile(origTheme, new File(Naming.getThemePropsDir() + "/" + themeId + ".properties"));
            logger.debug("Importing theme done successfully.");
         }
         MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
      } catch (IOException e) {
         MessageBoxUtils.showActionFailureError(e);
         logger.implicitLog(e);
      }
   }

   private void importRecitationPack() {
      final List<File> recitationFileList;
      String recitExt = GlobalConfig.isMac ? "*.zip" : "*.recit.zip";
      try {
         recitationFileList = MessageBoxUtils.importFileDialog(shell, new String[] { "Recitation Packs (" + recitExt + ")" },
               new String[] { recitExt }, false);
         if (recitationFileList.size() <= 0) {
            return;
         }
      } catch (IOException e) {
         MessageBoxUtils.showActionFailureError(e);
         logger.implicitLog(e);
         return;
      }

      if (recitationFileList.size() <= 0) {
         return;
      }
      final File file2Import = recitationFileList.get(0);

      QuestionPromptForm qpf = new QuestionPromptForm(shell, new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
            lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION", "IMPORT_FOR"),
            lang.getMeaning("QUESTION"), true, new QuestionListener() {
               List<String> errorList = new ArrayList<String>();
               List<String> addedList = new ArrayList<String>();
               String destDir;
               boolean progress = true;
               private AudioData audioData;

               public void start(int result) {
                  if (result < 0) {
                     return;
                  }
                  if (result == 0) {
                     destDir = Naming.getAudioDir();
                  } else {
                     destDir = ApplicationPath.AUDIO_DIR;
                  }
                  logger.info("Adding recitation pack \"" + file2Import.getName() + "\" to " + destDir);
                  if ("zip".equalsIgnoreCase(FilenameUtils.getExtension(file2Import.getName()))) {
                     Thread t = new Thread() {
                        public void run() {
                           try {
                              IntallationProgressListener progressListener = new IntallationProgressListener() {
                                 long totalSize = 0;
                                 long sizeToNow = 0;

                                 public void start(long totalSize) {
                                    this.totalSize = totalSize;
                                 }

                                 public boolean progress(long itemSize) {
                                    sizeToNow += itemSize;
                                    final int p = Math.min((int) (100.0 * sizeToNow / totalSize), 99);

                                    display.asyncExec(new Runnable() {
                                       public void run() {
                                          EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS, p);
                                       }
                                    });
                                    return progress;
                                 }

                                 public void finish(AudioData ad) {
                                    audioData = ad;
                                    if (progress) {
                                       display.asyncExec(new Runnable() {
                                          public void run() {
                                             try {
                                                EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS_DONE);
                                             } catch (Exception e) {
                                                logger.implicitLog(e);
                                             }
                                          }
                                       });
                                    }
                                 }

                                 public void finish(Resource producedObject) {
                                    finish((AudioData) producedObject);
                                 }
                              };

                              config.addNewRecitationPack(file2Import, destDir, progressListener);
                           } catch (ZekrMessageException zme) {
                              progress = false;
                              logger.error("Error importing: " + file2Import, zme);
                              errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
                              display.syncExec(new Runnable() {
                                 public void run() {
                                    if (errorList.size() > 0) {
                                       String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
                                       MessageBoxUtils.showError(str);
                                    }
                                    try {
                                       EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS_FAILED);
                                    } catch (Exception e) {
                                       logger.implicitLog(e);
                                    }
                                 }
                              });
                           }
                        };
                     };
                     t.setDaemon(true);
                     t.start();
                  }
               }

               public void done() {
                  if (progress) {
                     addedList.add(audioData == null ? file2Import.toString() : getAudioDataText(audioData));
                  }
                  if (config.getAudio().getCurrent() == null && errorList.size() <= 0 && recitationFileList.size() > 0) {
                     MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
                  } else if (addedList.size() > 0) {
                     // stop player, if it's playing
                     quranForm.closeAudioSilently();
                     createOrUpdateRecitationMenu();
                     String str = lang.getMeaning("AUDIO") + " > " + lang.getMeaning("RECITATION");
                     String rlm = rtl ? I18N.RLM + "" : "";
                     MessageBoxUtils.showMessage(rlm + lang.getMeaning("ACTION_PERFORMED") + "\n"
                           + lang.getDynamicMeaning("RECITATION_ADDED", new String[] { str }) + ":\n    "
                           + CollectionUtils.toString(addedList, lang.getMeaning("COMMA") + "\n    "));
                  }
               }

               public void cancel() {
                  progress = false;
               }
            });
      qpf.show();
   }

   private String getAudioDataText(AudioData audioData) {
      String name = audioData.getLocalizedName();
      return StringUtils.abbreviate(name, GlobalConfig.MAX_MENU_STRING_LENGTH) + "\t" + audioData.quality + " ("
            + lang.getMeaning("ONLINE".equalsIgnoreCase(audioData.type) ? "ONLINE" : "OFFLINE") + ")"
            + (rtl ? I18N.LRM + "" : "");
   }

   private void importOnlineRecitation() {
      String destDir = ApplicationPath.AUDIO_DIR;
      List<String> errorList = new ArrayList<String>();
      List<String> addedList = new ArrayList<String>();
      List<File> recitationFileList = new ArrayList<File>();
      try {
         recitationFileList = MessageBoxUtils.importFileDialog(shell, new String[] { "Online Recitation Files (*.properties)" },
               new String[] { "*.properties" });
         if (recitationFileList.size() <= 0) {
            return;
         }

         int result = MessageBoxUtils.radioQuestionPrompt(
               new String[] { lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"),
                     lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") }, lang.getMeaningById("IMPORT_QUESTION", "IMPORT_FOR"),
               lang.getMeaning("QUESTION"));

         if (result == -1) {
            return;
         }

         if (result == 0) {
            destDir = Naming.getAudioDir();
         }
         for (File file2Import : recitationFileList) {
            logger.info("Copy recitation playlist \"" + file2Import.getName() + "\" to " + destDir);
            File audioFile = new File(destDir + "/" + file2Import.getName());
            FileUtils.copyFile(file2Import, audioFile);
            try {
               AudioData audioData = config.addNewRecitation(audioFile);
               if (audioData == null) {
                  errorList.add(lang.getDynamicMeaning("Invalid recitation format", new String[] { audioFile.getName() }));
               } else {
                  addedList.add(getAudioDataText(audioData));
               }
            } catch (ZekrMessageException zme) {
               logger.warn(zme);
               errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
               continue;
            }
            logger.debug("Recitation imported successfully:" + file2Import);
         }
         if (errorList.size() > 0) {
            String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
            MessageBoxUtils.showError(str);
         }
         // MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
      } catch (IOException e) {
         MessageBoxUtils.showActionFailureError(e);
         logger.implicitLog(e);
      } finally {
         if (config.getAudio().getCurrent() == null && errorList.size() <= 0 && recitationFileList.size() > 0) {
            MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
         } else if (addedList.size() > 0) {
            // stop player, if it's playing
            quranForm.closeAudioSilently();
            createOrUpdateRecitationMenu();
            String str = lang.getMeaning("AUDIO") + " > " + lang.getMeaning("RECITATION");
            String rlm = rtl ? I18N.RLM + "" : "";
            MessageBoxUtils.showMessage(rlm + lang.getMeaning("ACTION_PERFORMED") + "\n"
                  + lang.getDynamicMeaning("RECITATION_ADDED", new String[] { str }) + ":\n    "
                  + CollectionUtils.toString(addedList, lang.getMeaning("COMMA") + "\n    "));
         }
      }
   }

   private void reconfigureViewLayout() {
      quranForm.setLayout(config.getViewProp("view.viewLayout"));
      reloadView();
   }

   /**
    * Change current paging mode to the new mode passed.
    * 
    * @param pagingMode
    */
   private void updatePagingMode(String pagingMode) {
      config.setPagingMode(pagingMode);
      quranForm.uvc.synchPage();
      quranForm.updateNavPageKeysTooltip();
      reloadView();
   }

   private void reloadView() {
      quranForm.pageChanged = true;
      quranForm.updateView();
      quranForm.pageChanged = false;
   }

   private void reloadQuran() {
      try {
         if (quranForm.viewLayout != QuranForm.MIXED) {
            config.getRuntime().recreateQuranCache();
         } else {
            config.getRuntime().recreateMixedCache();
         }
      } catch (IOException e) {
         logger.log(e);
      }
      quranForm.pageChanged = true;
      quranForm.apply();
   }

   private void reloadTrans() {
      try {
         if (quranForm.viewLayout != QuranForm.MIXED) {
            config.getRuntime().recreateTransCache();
         } else {
            config.getRuntime().recreateMixedCache();
         }
      } catch (IOException e) {
         logger.log(e);
      }
      quranForm.pageChanged = true;
      quranForm.apply();
   }

   private void setTrans(MenuItem mi) {
      try {
         String transId = (String) mi.getData();
         config.setCurrentTranslation(transId);
         if (quranForm.viewLayout != QuranForm.QURAN_ONLY) {
            quranForm.reload();
         }
         TranslationData td = config.getTranslation().get(transId);
         mi.setImage(new Image(shell.getDisplay(), resource.getString(getTranslationValidityIcon(td))));
      } catch (ZekrMessageException zme) {
         logger.error(zme);
         MessageBoxUtils.showError(zme);
         createOrUpdateTranslationMenu();
      }
   }

   public void setAudio(boolean fromMainMenu, String audioId, int reciterIndex) {
      quranForm.playerUiController.changeRecitation(audioId, reciterIndex);
      if (fromMainMenu) {
         quranForm.playerUiController.updateRecitationListMenu(reciterIndex);
      } else if (reciterIndex == 0) {
         MenuItem[] mis = recitationListMenu.getItems();
         for (MenuItem menuItem : mis) {
            if (config.getAudio().getCurrent().id.equals(menuItem.getData())) {
               menuItem.setSelection(true);
            } else {
               menuItem.setSelection(false);
            }
         }
      }
   }

   public void toggleFullScreenItem(boolean selected) {
      fullScreenItem.setSelection(selected);
   }

   public void toggleAudioPanelState(boolean selected) {
      audioPanelItem.setSelection(selected);
   }

   protected void playerTogglePlayPause(boolean play) {
      if (play) {
         resumePlayer();
      } else {
         pausePlayer();
      }
   }

   private void pausePlayer() {
      changePlayerMenuState(PlayStatus.PAUSE, lang.getMeaning("PLAY"), playIconFullPath);
   }

   private void resumePlayer() {
      changePlayerMenuState(PlayStatus.PLAY, lang.getMeaning("PAUSE"), pauseIconFullPath);
   }

   private void changePlayerMenuState(PlayStatus data, String text, String image) {
      String itemText = playItem.getText();
      String accelText = itemText.contains("\t") ? itemText.substring(itemText.indexOf('\t')) : "";
      playItem.setData(data);
      if (SHOW_MENU_IMAGE) {
         playItem.setImage(new Image(shell.getDisplay(), image));
      }
      playItem.setText(FormUtils.addAmpersand(text + accelText));
   }

   public void resetAudioMenuStatus() {
      pausePlayer();
   }

   public void resetMenuStatus() {
      resetAudioMenuStatus();
   }

}

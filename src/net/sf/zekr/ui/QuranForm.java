/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 6, 2004
 */

package net.sf.zekr.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.SajdaProperties;
import net.sf.zekr.common.runtime.HtmlGenerationException;
import net.sf.zekr.common.runtime.HtmlRepository;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.page.HizbQuarterPagingData;
import net.sf.zekr.engine.page.IPagingData;
import net.sf.zekr.engine.page.JuzPagingData;
import net.sf.zekr.engine.page.SuraPagingData;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.comparator.SearchResultComparatorFactory;
import net.sf.zekr.engine.search.lucene.IndexingException;
import net.sf.zekr.engine.search.lucene.LuceneIndexManager;
import net.sf.zekr.engine.search.lucene.QuranTextSearcher;
import net.sf.zekr.engine.search.tanzil.AdvancedQuranTextSearch;
import net.sf.zekr.engine.search.tanzil.DefaultSearchScorer;
import net.sf.zekr.engine.search.tanzil.SimpleSearchResultHighlighter;
import net.sf.zekr.engine.search.ui.ManageScopesForm;
import net.sf.zekr.engine.search.ui.SearchScopeForm;
import net.sf.zekr.engine.update.UpdateManager;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Sort;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Main Zekr form. This class contains all the Zekr main screen, except menus which are in
 * <code>QuranFormMenuFactory</code>.
 * 
 * @author Mohsen Saboorian
 */
public class QuranForm extends BaseForm {
	private Composite body;
	Browser quranBrowser;
	Browser transBrowser;
	private Combo suraSelectorCombo;
	private Combo ayaSelectorCombo;
	private Tree sst;
	private TreeItem rootSura;
	private Label suraLabel;
	private Label ayaLabel;
	private Combo searchCombo, advancedSearchCombo;
	private Text searchBox, advancedSearchBox;
	private Button goButton;
	Button quranTargetBut, transTargetBut;
	Button advancedQuranTargetBut, advancedTransTargetBut;
	private Button sync;
	private Button matchDiacCheckBox;
	private Button currentPageCheckBox;
	private Button matchCaseCheckBox;
	private Button toggleMultiLine, advancedToggleMultiLine;
	private Table suraTable;
	private Map suraMap;
	private Composite bgroup, workPane;
	private Group navigationGroup;
	private Group searchGroup;
	private Group navGroup;
	private Group leftGroup;
	private Group detailGroup;
	private SashForm sashForm;
	private SashForm navSashForm;
	private Menu searchMenu;
	private Button searchButton, advancedSearchButton;
	private Button searchArrowBut, advancedSearchArrowBut;
	private Button sortOrderButton, advancedSortOrderButton;
	private Menu advancedSearchMenu;

	private RootTabForm rootTabForm;
	private ProgressAdapter qpl, tpl;
	private String title;
	private Shell fullScreenFloatShell;

	private boolean tree = false;

	// These 6 properties should be package-private
	int viewLayout;
	static final int MIXED = 1;
	static final int SEPARATE = 2;
	static final int QURAN_ONLY = 3;
	static final int TRANS_ONLY = 4;
	static final int MULTI_TRANS = 5;

	private static final String NAV_BUTTON = "NAV_BUTTON";

	/** match case behavior for search */
	// private boolean matchCase;
	private final String FORM_ID = "QURAN_FORM";

	private static final Logger logger = Logger.getLogger(QuranForm.class);

	private QuranProperties quranProp;

	/** Specifies whether page changed. */
	protected boolean pageChanged;

	/** The current Quran URI loaded in the browser */
	private String quranUri;

	/** The current Translation URI loaded in the browser */
	private String transUri;

	private QuranTextSearcher qts;
	private AdvancedQuranTextSearch ats;
	private SearchResultModel asr;

	private AdvancedQuranTextSearch searcher;
	private SearchResultModel sr;

	private ApplicationConfig config;
	private boolean isClosed;
	private boolean isSashed;

	private DisposeListener dl;
	private IQuranLocation quranLoc;

	protected boolean updateTrans = true;
	protected boolean updateQuran = true;
	private QuranFormMenuFactory qmf;
	private boolean clearOnExit = false;
	SearchScope searchScope;
	private List searchScopeList;
	private TabFolder searchTabFolder;
	private Composite searchTabBody, advancedSearchTabBody;
	Menu searchScopeMenu;
	private Combo searchOrderCombo, advancedSearchOrderCombo;
	//	private Composite advancedSearchPaginationComp/*, searchPaginationComp*/;
	//	private Spinner advancedSearchPaginationSpinner, searchPaginationSpinner*/;
	//	private Button advNextPageBut, advPrevPageBut/*, nextPageBut, prevPageBut*/;
	private ScrolledComposite workPaneScroller;
	private Button collapseDetail;

	private Button prevPage, nextPage, prevAya, nextAya;

	/** a state specifying if this is the first aya user focuses on */
	private boolean firstTimePlaying;
	/** specifies if audio player automatically brings user to the next sura */
	boolean playerAutoNextSura = false;

	private UpdateManager updateManager;

	private BrowserCallbackHandler bch = new BrowserCallbackHandler(this);
	private SearchResultNavigator searchNav, advSearchNav, rootNav;

	IUserView uvc;

	private static Map tooltipMap = new HashMap();
	{
		tooltipMap.put(HizbQuarterPagingData.ID, "HIZBQ");
		tooltipMap.put(JuzPagingData.ID, "JUZ");
		tooltipMap.put(SuraPagingData.ID, "SURA");
	}

	private Listener globalKeyListener = new Listener() {
		public void handleEvent(Event event) {
			if (NAV_BUTTON.equals(event.widget.getData())) {
				boolean isRTL = ((lang.getSWTDirection() == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);
				int d = event.keyCode ^ SWT.KEYCODE_BIT;
				if (d == 1) {
					gotoPrevPage();
				} else if (d == 2) {
					gotoNextPage();
				} else if (d == 3) {
					if (isRTL)
						gotoNextAya();
					else
						gotoPrevAya();
				} else if (d == 4) {
					if (isRTL)
						gotoPrevAya();
					else
						gotoNextAya();
				}
			}
		}
	};

	public KeyAdapter textSelectAll = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.stateMask == SWT.CTRL && (e.keyCode == 'a' || e.keyCode == 'A'))
				((Text) e.widget).selectAll();
		}
	};

	/**
	 * Initialize the QuranForm.
	 * 
	 * @param display
	 */
	public QuranForm(Display display) {
		this.display = display;
		config = ApplicationConfig.getInstance();
		quranProp = QuranProperties.getInstance();
		init();
	}

	protected void init() {
		viewLayout = 0; // no layout set yet
		uvc = config.getUserViewController();

		title = meaning("TITLE");
		shell = new Shell(display, SWT.SHELL_TRIM | lang.getSWTDirection());
		shell.setText(title);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")),
				new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")),
				new Image(display, resource.getString("icon.form256")) });
		shell.setMenuBar((qmf = new QuranFormMenuFactory(this, shell)).getQuranFormMenu());

		pageChanged = false;

		logger.info("Loading last visited Quran location: " + quranLoc + ".");

		tree = config.getProps().getString("view.sura.mode", "combo").equals("tree");

		// reset search scope list
		searchScopeList = new ArrayList();
		makeFrame();
		updateSuraNames();

		// set the layout
		if (config.getTranslation().getDefault() == null) { // no translation found
			setLayout(ApplicationConfig.QURAN_ONLY_LAYOUT);
		} else {
			setLayout(config.getViewProp("view.viewLayout"));
		}

		navTo(uvc.getLocation(), true);

		dl = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				close();
				if (!shell.isDisposed())
					shell.removeDisposeListener(dl);
			}
		};
		shell.addDisposeListener(dl);

		updateManager = new UpdateManager(shell);
		shell.addShellListener(new ShellAdapter() {
			public void shellActivated(ShellEvent e) {
				if (config.getProps().getBoolean("update.enable")) {
					if (updateManager.isCheckNeeded()) {
						logger.debug("Time for check for update!");
						updateManager.check(false);
					}
				}
			}
		});

		shell.addListener(EventProtocol.CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event e) {
				if (e.data != null) {
					if (REFRESH_VIEW.equals(e.data)) {
						reload();
					} else if (RECREATE_VIEW.equals(e.data)) {
						recreate();
					} else if (CLEAR_CACHE_ON_EXIT.equals(e.data)) {
						clearOnExit = true;
					} else if (UPDATE_SURA_NAMES.equals(e.data)) {
						updateSuraNames();
					} else if (UPDATE_BOOKMARKS_MENU.equals(e.data)) {
						qmf.createOrUpdateBookmarkMenu();
					} else if (((String) e.data).startsWith(GOTO_LOCATION)) {
						String s = (String) e.data;
						s = s.substring(GOTO_LOCATION.length() + 1);
						IQuranLocation loc = new QuranLocation(s);
						navTo(loc.getSura(), loc.getAya());
					}
					e.doit = false;
				}
			}
		});

		display.removeFilter(SWT.KeyDown, globalKeyListener);
		display.addFilter(SWT.KeyDown, globalKeyListener);
	}

	private void updateSuraNames() {
		QuranPropertiesUtils.resetIndexedSuraNames();
		updateSuraSelector();
		suraMap = QuranPropertiesUtils.getSuraPropsMap(getSelectedSura());
		FormUtils.updateTable(suraTable, suraMap);
	}

	private void updateSuraSelector() {
		int s = getSelectedSura();
		if (tree) {
			TreeItem[] tis = rootSura.getItems();
			String[] suras = QuranPropertiesUtils.getIndexedSuraNames();
			for (int i = 0; i < tis.length; i++) {
				tis[i].dispose();
				tis[i] = new TreeItem(rootSura, SWT.NONE);
				tis[i].setText(suras[i]);
				tis[i].setData(String.valueOf(i + 1));
			}
		} else {
			suraSelectorCombo.setItems(QuranPropertiesUtils.getIndexedSuraNames());
		}
		selectSura(s);
	}

	/**
	 * Recreates the whole cache. All previous cached data are removed.
	 */
	protected void reload() {
		try {
			config.getRuntime().recreateViewCache();
			pageChanged = true;
			qmf.resetMenuStatus();
			apply();
		} catch (IOException e) {
			logger.log(e);
		}
	}

	/**
	 * This method allocates and adds proper widgets to the <b>QuranForm</b>.
	 */
	private void makeFrame() {
		GridData gd;
		GridLayout gl;

		FillLayout fl = new FillLayout(SWT.VERTICAL);
		shell.setLayout(fl);

		GridLayout pageLayout = new GridLayout(2, false);
		body = new Composite(shell, lang.getSWTDirection());
		body.setLayout(pageLayout);

		isSashed = config.getProps().getBoolean("options.general.resizeableTaskPane");
		if (isSashed) {
			navSashForm = new SashForm(body, SWT.HORIZONTAL);
			gd = new GridData(GridData.FILL_BOTH);
			navSashForm.setLayoutData(gd);
		}

		workPaneScroller = new ScrolledComposite(isSashed ? navSashForm : body, SWT.V_SCROLL);
		workPaneScroller.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		workPaneScroller.setExpandHorizontal(true);
		workPaneScroller.setExpandVertical(true);

		workPane = new Composite(workPaneScroller, SWT.NONE);
		gl = new GridLayout(1, false);
		gl.marginHeight = gl.marginWidth = 0;
		gl.marginLeft = gl.marginRight = gl.marginTop = gl.marginBottom = 2;
		workPane.setLayout(gl);

		workPaneScroller.setContent(workPane);

		bgroup = new Composite(isSashed ? navSashForm : body, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		bgroup.setLayoutData(gd);
		fl = new FillLayout(SWT.VERTICAL);
		fl.marginHeight = fl.marginWidth = 2;
		bgroup.setLayout(fl);

		if (isSashed) {
			if (config.getProps().getProperty("view.quranForm.paneSashWeight") != null) {
				List weights = config.getProps().getList("view.quranForm.paneSashWeight");
				navSashForm.setWeights(new int[] { Integer.parseInt(weights.get(0).toString()),
						Integer.parseInt(weights.get(1).toString()) });
			} else {
				navSashForm.setWeights(new int[] { 2, 5 });
			}
		}

		Composite browsers = new Group(bgroup, SWT.NONE);
		fl = new FillLayout(SWT.VERTICAL);
		browsers.setLayout(fl);

		sashForm = new SashForm(browsers, SWT.VERTICAL);
		sashForm.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		sashForm.SASH_WIDTH = 3;

		quranBrowser = new Browser(sashForm, getBrowserStyle());
		fl = new FillLayout(SWT.VERTICAL);
		fl.marginHeight = 2;
		quranBrowser.setLayout(fl);

		StatusTextListener stl = new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				if (StringUtils.isNotEmpty(event.text)) {
					// IE changes status text a lot.
					// logger.debug("Browser status message changed: " + event.text);
					bch.doBrowserCallback(event.text);
				}
			}
		};

		transBrowser = new Browser(sashForm, getBrowserStyle());
		fl = new FillLayout(SWT.VERTICAL);
		transBrowser.setLayout(fl);

		quranBrowser.addStatusTextListener(stl);
		transBrowser.addStatusTextListener(stl);

		gl = new GridLayout(3, false);
		navGroup = new Group(workPane, SWT.NONE);
		navGroup.setText(lang.getMeaning("SELECT") + ":");
		navGroup.setLayout(gl);

		if (tree) {
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		} else {
			gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		}
		navGroup.setLayoutData(gd);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		if (!tree) {
			suraLabel = new Label(navGroup, SWT.NONE);
			suraLabel.setText(lang.getMeaning("SURA") + ":");
			suraLabel.setLayoutData(gd);
		}

		ayaSelectorCombo = new Combo(navGroup, SWT.READ_ONLY);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		if (!tree) {
			gd.widthHint = 60;
			gd.horizontalSpan = 2;
			suraSelectorCombo = new Combo(navGroup, SWT.READ_ONLY);
			suraSelectorCombo.setLayoutData(gd);
			suraSelectorCombo.setItems(QuranPropertiesUtils.getIndexedSuraNames());
			suraSelectorCombo.setVisibleItemCount(15);
			suraSelectorCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					_onSuraChanged();
					ayaSelectorCombo.select(0);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					navTo(getSelectedSura(), 1);
				}
			});
			suraSelectorCombo.select(0);
		} else {
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			sst = new Tree(navGroup, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			sst.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (e.item.getData() != null && e.time > 0) {
						_onSuraChanged();
						ayaSelectorCombo.select(0);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					if (e.item.getData() != null) {
						navTo(getSelectedSura(), 1);
					}
				}
			});
			rootSura = new TreeItem(sst, SWT.NONE);
			rootSura.setText(lang.getMeaning("SURA"));
			String[] suraNames = QuranPropertiesUtils.getIndexedSuraNames();
			for (int i = 0; i < suraNames.length; i++) {
				TreeItem ti = new TreeItem(rootSura, SWT.NONE);
				ti.setText(suraNames[i]);
				ti.setData(String.valueOf(i + 1));
			}
			rootSura.setExpanded(true);
			gd.minimumHeight = 130;
			gd.heightHint = 130;
			gd.horizontalSpan = 3;
			sst.setLayoutData(gd);
			sst.setLinesVisible(false);
		}

		ayaLabel = new Label(navGroup, SWT.NONE);
		ayaLabel.setText(lang.getMeaning("AYA") + ":");

		ayaSelectorCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		ayaSelectorCombo.setItems(QuranPropertiesUtils.getSuraAyas(1));
		ayaSelectorCombo.setVisibleItemCount(10);
		ayaSelectorCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				apply();
			}
		});
		ayaSelectorCombo.select(0);
		ayaSelectorCombo.moveBelow(ayaLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		ayaSelectorCombo.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		goButton = new Button(navGroup, SWT.NONE);
		goButton.setData(NAV_BUTTON);
		goButton.setLayoutData(gd);
		goButton.setText(lang.getMeaning("GO"));
		goButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				navTo(getSelectedSura(), getSelectedAya());
				// apply();
			}
		});

		//		sync = new Button(navGroup, SWT.CHECK);
		//		sync.setText(lang.getMeaning("SYNCHRONOUS"));
		//		if (config.getProps().getProperty("view.location.sync") != null) {
		//			sync.setSelection(config.getProps().getBoolean("view.location.sync"));
		//		}

		Composite navComposite = new Composite(navGroup, SWT.NONE);
		gl = new GridLayout(4, false);
		gl.marginWidth = 0;
		navComposite.setLayout(gl);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		navComposite.setLayoutData(gd);

		int style = SWT.PUSH | SWT.FLAT;
		prevPage = new Button(navComposite, style);
		prevAya = new Button(navComposite, style);
		nextAya = new Button(navComposite, style);
		nextPage = new Button(navComposite, style);
		prevPage.setData(NAV_BUTTON);
		nextPage.setData(NAV_BUTTON);
		prevAya.setData(NAV_BUTTON);
		nextAya.setData(NAV_BUTTON);

		gd = new GridData(GridData.FILL_BOTH);
		prevAya.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		prevPage.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		nextAya.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		nextPage.setLayoutData(gd);

		int l = lang.getSWTDirection();

		// isRTL is only applicable for Windows
		boolean isRTL = ((l == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);

		Image prevPageImg = new Image(display, isRTL ? resource.getString("icon.nextNext") : resource
				.getString("icon.prevPrev"));
		Image prevAyaImg = new Image(display, isRTL ? resource.getString("icon.next") : resource.getString("icon.prev"));
		Image nextAyaImg = new Image(display, isRTL ? resource.getString("icon.prev") : resource.getString("icon.next"));
		Image nextPageImg = new Image(display, isRTL ? resource.getString("icon.prevPrev") : resource
				.getString("icon.nextNext"));

		prevPage.setImage(prevPageImg);
		prevAya.setImage(prevAyaImg);
		nextAya.setImage(nextAyaImg);
		nextPage.setImage(nextPageImg);

		nextAya.setToolTipText(lang.getMeaning("NEXT_AYA"));
		prevAya.setToolTipText(lang.getMeaning("PREV_AYA"));
		updateNavPageKeysTooltip();

		prevPage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoPrevPage();
			}
		});

		prevAya.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoPrevAya();
			}
		});

		nextAya.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoNextAya();
			}
		});

		nextPage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoNextPage();
			}
		});

		detailGroup = new Group(workPane, SWT.NONE);
		detailGroup.setText(lang.getMeaning("DETAILS") + ":");
		gl = new GridLayout(1, false);

		detailGroup.setLayout(gl);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		detailGroup.setLayoutData(gd);
		if (!config.getProps().getBoolean("view.panel.detail", true)) {
			detailGroup.setVisible(false);
			gd.exclude = true;
		}

		suraMap = QuranPropertiesUtils.getSuraPropsMap(getSelectedSura());

		Menu propsMenu = new Menu(shell, lang.getSWTDirection());
		MenuItem copyItem = new MenuItem(propsMenu, SWT.CASCADE);
		copyItem.setText(lang.getMeaning("COPY"));
		copyItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final Clipboard cb = new Clipboard(display);
				Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
				Object[] data = { QuranPropertiesUtils.propsToClipboadrFormat(suraMap) };
				cb.setContents(data, types);
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessVerticalSpace = true;
		suraTable = FormUtils.getTableFromMap(detailGroup, suraMap, lang.getMeaning("NAME"), lang.getMeaning("VALUE"),
				SWT.DEFAULT, SWT.DEFAULT, gd, SWT.HIDE_SELECTION);
		suraTable.setMenu(propsMenu);

		// create search scope menu
		searchScopeMenu = createSearchScopeMenu();

		if (tree) {
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		} else {
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		}
		searchTabFolder = new TabFolder(workPane, lang.getSWTDirection());
		searchTabFolder.setLayoutData(gd);
		rootTabForm = new RootTabForm(this, searchTabFolder);

		TabItem normalSearchTab = new TabItem(searchTabFolder, SWT.NONE);
		TabItem advancedSearchTab = new TabItem(searchTabFolder, SWT.NONE);
		rootTabForm.createTabItem();

		normalSearchTab.setText(lang.getMeaning("SEARCH"));
		advancedSearchTab.setText(lang.getMeaning("ADVANCED"));

		int selectedSearchTab = config.getProps().getInt("view.search.tab", 0);
		if (!config.isRootDatabaseEnabled() && selectedSearchTab == 2) {
			selectedSearchTab = 0;
		}
		searchTabFolder.setSelection(selectedSearchTab);

		gl = new GridLayout(2, false);
		searchTabBody = new Composite(searchTabFolder, SWT.NONE);
		searchTabBody.setLayout(gl);
		normalSearchTab.setControl(searchTabBody);

		advancedSearchTabBody = new Composite(searchTabFolder, SWT.NONE);
		advancedSearchTabBody.setLayout(new GridLayout(2, false));
		advancedSearchTab.setControl(advancedSearchTabBody);

		createLuceneSearchTabContent();
		createSearchTabContent();

		workPaneScroller.setMinSize(workPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// this progress should be in the heart of makeFrame method!
		logger.info("UI somewhat initialized.");
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "UI Initialized");
	}

	void updateNavPageKeysTooltip() {
		String key = (String) tooltipMap.get(config.getPagingMode());
		if (key == null)
			key = "PAGE";

		nextPage.setToolTipText(lang.getMeaning("MENU_NEXT_" + key));
		prevPage.setToolTipText(lang.getMeaning("MENU_PREV_" + key));
	}

	private int getBrowserStyle() {
		return config.useMozilla() ? SWT.MOZILLA : SWT.NONE;
	}

	void playerStop() {
		qmf.playerStop(false);
	}

	void playerTogglePlayPause() {
		qmf.playerTogglePlayPause(false);
	}

	protected void sendPlayerStop() {
		logger.debug("Stop player.");
		if (viewLayout == SEPARATE || viewLayout == MIXED || viewLayout == MULTI_TRANS || viewLayout == QURAN_ONLY) {
			quranBrowser.execute("swtStopPlayer();");
		}
		if (viewLayout == SEPARATE || viewLayout == TRANS_ONLY) {
			transBrowser.execute("swtStopPlayer();");
		}
	}

	protected void sendPlayerTogglePlayPause() {
		logger.debug("Toggle play/pause state.");
		if (viewLayout == SEPARATE || viewLayout == MIXED || viewLayout == MULTI_TRANS || viewLayout == QURAN_ONLY) {
			quranBrowser.execute("swtTogglePlayPause();");
		}
		if (viewLayout == SEPARATE || viewLayout == TRANS_ONLY) {
			transBrowser.execute("swtTogglePlayPause();");
		}
	}

	private void createLuceneSearchTabContent() {
		SelectionListener advancedSearchListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doAdvancedFind();
			}
		};

		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.verticalSpan = 2;
		gd.heightHint = 60;
		gd.verticalIndent = 6;

		final StackLayout advancedSearchStackLayout = new StackLayout();

		final Composite advancedSearchTextComp = new Composite(advancedSearchTabBody, SWT.NONE);
		advancedSearchTextComp.setLayout(advancedSearchStackLayout);
		advancedSearchTextComp.setLayoutData(gd);

		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = gl.verticalSpacing = 0;
		gl.marginHeight = gl.marginWidth = 0;
		final Composite advancedSearchComboComp = new Composite(advancedSearchTextComp, SWT.NONE);
		advancedSearchComboComp.setLayout(gl);

		advancedSearchCombo = new Combo(advancedSearchComboComp, SWT.DROP_DOWN);
		advancedSearchCombo.setVisibleItemCount(8);
		advancedSearchCombo.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					doAdvancedFind();
				}
			}
		});
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		advancedSearchCombo.setLayoutData(gd);

		advancedSearchBox = new Text(advancedSearchTextComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		advancedSearchBox.addSelectionListener(advancedSearchListener);
		advancedSearchBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 13) { // Ctrl + Enter
					doAdvancedFind();
					e.doit = false;
				}
			}
		});
		advancedSearchBox.addKeyListener(textSelectAll);

		advancedSearchStackLayout.topControl = advancedSearchComboComp;

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		Composite searchButComp = new Composite(advancedSearchTabBody, SWT.NONE);
		searchButComp.setLayout(gl);
		searchButComp.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		advancedSearchButton = new Button(searchButComp, SWT.PUSH);
		advancedSearchButton.setText(lang.getMeaning("SEARCH"));
		advancedSearchButton.setLayoutData(gd);
		advancedSearchButton.addSelectionListener(advancedSearchListener);

		// search option button
		// gd = new GridData(GlobalConfig.isLinux ? GridData.BEGINNING : GridData.BEGINNING);
		gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gd.horizontalIndent = -1;

		advancedSearchArrowBut = new Button(searchButComp, SWT.TOGGLE);
		advancedSearchMenu = searchScopeMenu;
		advancedSearchMenu.addMenuListener(new MenuAdapter() {
			public void menuHidden(MenuEvent e) {
				advancedSearchArrowBut.setSelection(false);
			}
		});
		advancedSearchArrowBut.setImage(new Image(display, resource.getString("icon.down")));
		advancedSearchArrowBut.setLayoutData(gd);
		advancedSearchArrowBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Point loc = display.map(advancedSearchArrowBut, null, 0, 0);
				Point size = advancedSearchArrowBut.getSize();
				advancedSearchMenu.setLocation(loc.x, loc.y + size.y);
				advancedSearchMenu.setVisible(true);
			}
		});

		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		advancedToggleMultiLine = new Button(advancedSearchTabBody, SWT.CHECK);
		advancedToggleMultiLine.setLayoutData(gd);
		advancedToggleMultiLine.setText(lang.getMeaning("MULTILINE"));
		advancedToggleMultiLine.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (advancedToggleMultiLine.getSelection() == true) {
					advancedSearchStackLayout.topControl = advancedSearchBox;
					if (!advancedSearchBox.getText().replaceAll("\\r\\n|\\n|\\r", " ").equals(advancedSearchCombo.getText()))
						advancedSearchBox.setText(advancedSearchCombo.getText());
				} else {
					advancedSearchStackLayout.topControl = advancedSearchComboComp;
					advancedSearchCombo.setText(advancedSearchBox.getText().replaceAll("\\r\\n|\\n|\\r", " "));
				}
				advancedSearchTextComp.layout();
			}
		});
		advancedToggleMultiLine.setSelection(config.getProps().getBoolean("view.search.advanced.multiLine"));
		if (advancedToggleMultiLine.getSelection()) {
			advancedSearchStackLayout.topControl = advancedSearchBox;
		} else {
			advancedSearchStackLayout.topControl = advancedSearchComboComp;
		}

		// ===== advanced search target radio buttons
		KeyAdapter ka = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					doAdvancedFind();
				}
			}
		};

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		final Composite advancedSearchScopeComp = new Composite(advancedSearchTabBody, SWT.NONE);
		advancedSearchScopeComp.setLayoutData(gd);
		advancedSearchScopeComp.setLayout(new FillLayout());

		advancedQuranTargetBut = new Button(advancedSearchScopeComp, SWT.RADIO);
		advancedQuranTargetBut.setText(meaning("QURAN_SCOPE"));

		advancedQuranTargetBut.addKeyListener(ka);

		advancedTransTargetBut = new Button(advancedSearchScopeComp, SWT.RADIO);
		advancedTransTargetBut.setText(meaning("TRANSLATION_SCOPE"));
		advancedTransTargetBut.addKeyListener(ka);
		if (config.getTranslation().getDefault() == null) {
			advancedTransTargetBut.setEnabled(false);
		} else {
			if (config.getProps().getString("view.search.advanced.target", "quran").equals("quran")) {
				advancedQuranTargetBut.setSelection(true);
			} else {
				advancedTransTargetBut.setSelection(true);
			}
		}

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(3, false);
		gl.marginWidth = 0;
		Composite advancedSearchOptionsComp = new Composite(advancedSearchTabBody, SWT.NONE);
		advancedSearchOptionsComp.setLayout(gl);
		advancedSearchOptionsComp.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		Label sortResult = new Label(advancedSearchOptionsComp, SWT.NONE);
		sortResult.setLayoutData(gd);
		sortResult.setText(meaning("SORT_BY") + ":");

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		advancedSearchOrderCombo = new Combo(advancedSearchOptionsComp, SWT.READ_ONLY);
		advancedSearchOrderCombo.setItems(new String[] { meaning("RELEVANCE"), meaning("NATURAL_ORDER"),
				lang.getMeaning("REVEL_ORDER"), meaning("AYA_LENGTH") });
		advancedSearchOrderCombo.setLayoutData(gd);
		advancedSearchOrderCombo.select(config.getProps().getInt("view.search.advanced.sortBy"));

		advancedSearchOrderCombo.setData("0", null);
		advancedSearchOrderCombo.setData("1", null);
		advancedSearchOrderCombo.setData("2", "net.sf.zekr.engine.search.comparator.RevelationOrderComparator");
		advancedSearchOrderCombo.setData("3", "net.sf.zekr.engine.search.comparator.AyaLengthComparator");
		advancedSearchOrderCombo.addKeyListener(ka);

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		advancedSortOrderButton = new Button(advancedSearchOptionsComp, SWT.PUSH | SWT.FLAT);
		advancedSortOrderButton.setData(config.getProps().getString("view.search.advanced.sortOrder", "des"));
		addSortOrderButton(advancedSortOrderButton, gd);

		advSearchNav = new SearchResultNavigator(advancedSearchTabBody, new IPageNavigator() {
			public void gotoPage(int page) {
				advancedFindGoto(page);
			}
		});
	}

	private void createSearchTabContent() {
		SelectionListener searchListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		};

		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.verticalSpan = 2;
		gd.heightHint = 60;
		gd.verticalIndent = 6;

		final StackLayout searchStackLayout = new StackLayout();

		final Composite searchTextComp = new Composite(searchTabBody, SWT.NONE);
		searchTextComp.setLayout(searchStackLayout);
		searchTextComp.setLayoutData(gd);

		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = gl.verticalSpacing = 0;
		gl.marginHeight = gl.marginWidth = 0;
		final Composite searchComboComp = new Composite(searchTextComp, SWT.NONE);
		searchComboComp.setLayout(gl);

		searchCombo = new Combo(searchComboComp, SWT.DROP_DOWN);
		searchCombo.setVisibleItemCount(8);
		searchCombo.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					doFind();
				}
			}
		});
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		searchCombo.setLayoutData(gd);

		searchBox = new Text(searchTextComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		searchBox.addSelectionListener(searchListener);
		searchBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) == SWT.CTRL && e.keyCode == 13) { // Ctrl + Enter
					doFind();
					e.doit = false;
				}
			}
		});
		searchBox.addKeyListener(textSelectAll);

		searchStackLayout.topControl = searchComboComp;

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		Composite searchButComp = new Composite(searchTabBody, SWT.NONE);
		searchButComp.setLayout(gl);
		searchButComp.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		searchButton = new Button(searchButComp, SWT.PUSH);
		searchButton.setText(lang.getMeaning("SEARCH"));
		searchButton.setLayoutData(gd);
		searchButton.addSelectionListener(searchListener);

		// search option button
		// gd = new GridData(GlobalConfig.isLinux ? GridData.BEGINNING : GridData.BEGINNING);
		gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		gd.horizontalIndent = -1;

		searchArrowBut = new Button(searchButComp, SWT.TOGGLE);
		searchMenu = searchScopeMenu;
		searchMenu.addMenuListener(new MenuAdapter() {
			public void menuHidden(MenuEvent e) {
				searchArrowBut.setSelection(false);
			}
		});
		searchArrowBut.setImage(new Image(display, resource.getString("icon.down")));
		searchArrowBut.setLayoutData(gd);
		searchArrowBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Point loc = display.map(searchArrowBut, null, 0, 0);
				Point size = searchArrowBut.getSize();
				searchMenu.setLocation(loc.x, loc.y + size.y);
				searchMenu.setVisible(true);
			}
		});

		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		toggleMultiLine = new Button(searchTabBody, SWT.CHECK);
		toggleMultiLine.setLayoutData(gd);
		toggleMultiLine.setText(lang.getMeaning("MULTILINE"));
		toggleMultiLine.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (toggleMultiLine.getSelection() == true) {
					searchStackLayout.topControl = searchBox;
					if (!searchBox.getText().replaceAll("\\r\\n|\\n|\\r", " ").equals(searchCombo.getText()))
						searchBox.setText(searchCombo.getText());
				} else {
					searchStackLayout.topControl = searchComboComp;
					searchCombo.setText(searchBox.getText().replaceAll("\\r\\n|\\n|\\r", " "));
				}
				searchTextComp.layout();
			}
		});
		toggleMultiLine.setSelection(config.getProps().getBoolean("view.search.multiLine"));
		if (toggleMultiLine.getSelection()) {
			searchStackLayout.topControl = searchBox;
		} else {
			searchStackLayout.topControl = searchComboComp;
		}

		// ===== search target radio buttons
		KeyAdapter ka = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					doFind();
				}
			}
		};

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		final Composite searchScopeComp = new Composite(searchTabBody, SWT.NONE);
		searchScopeComp.setLayoutData(gd);
		searchScopeComp.setLayout(new FillLayout());

		quranTargetBut = new Button(searchScopeComp, SWT.RADIO);
		quranTargetBut.setText(meaning("QURAN_SCOPE"));

		quranTargetBut.addKeyListener(ka);

		transTargetBut = new Button(searchScopeComp, SWT.RADIO);
		transTargetBut.setText(meaning("TRANSLATION_SCOPE"));
		transTargetBut.addKeyListener(ka);
		if (config.getTranslation().getDefault() == null) {
			transTargetBut.setEnabled(false);
		} else {
			if (config.getProps().getString("view.search.target", "quran").equals("quran")) {
				quranTargetBut.setSelection(true);
			} else {
				transTargetBut.setSelection(true);
			}
		}

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(3, false);
		gl.marginWidth = 0;
		Composite searchOptionsComp = new Composite(searchTabBody, SWT.NONE);
		searchOptionsComp.setLayout(gl);
		searchOptionsComp.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		Label sortResult = new Label(searchOptionsComp, SWT.NONE);
		sortResult.setLayoutData(gd);
		sortResult.setText(meaning("SORT_BY") + ":");

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		searchOrderCombo = new Combo(searchOptionsComp, SWT.READ_ONLY);
		searchOrderCombo.setItems(new String[] { meaning("RELEVANCE"), meaning("NATURAL_ORDER"),
				lang.getMeaning("REVEL_ORDER"), meaning("AYA_LENGTH") });
		searchOrderCombo.setLayoutData(gd);
		searchOrderCombo.select(config.getProps().getInt("view.search.sortBy"));

		searchOrderCombo.setData("0", "net.sf.zekr.engine.search.comparator.SimilarityComparator");
		searchOrderCombo.setData("1", null);
		searchOrderCombo.setData("2", "net.sf.zekr.engine.search.comparator.RevelationOrderComparator");
		searchOrderCombo.setData("3", "net.sf.zekr.engine.search.comparator.AyaLengthComparator");
		searchOrderCombo.addKeyListener(ka);

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		sortOrderButton = new Button(searchOptionsComp, SWT.PUSH | SWT.FLAT);
		sortOrderButton.setData(config.getProps().getString("view.search.sortOrder", "des"));
		addSortOrderButton(sortOrderButton, gd);

		searchNav = new SearchResultNavigator(searchTabBody, new IPageNavigator() {
			public void gotoPage(int page) {
				findGoto(page);
			}
		});

	}

	void addSortOrderButton(Button button, GridData gd) {
		button.setLayoutData(gd);
		button.setToolTipText(lang.getMeaning("DESCENDING"));
		final Image desImage = new Image(display, resource.getString("icon.descending"));
		final Image ascImage = new Image(display, resource.getString("icon.ascending"));
		final String descending = lang.getMeaning("DESCENDING");
		final String ascending = lang.getMeaning("ASCENDING");
		if (button.getData().equals("des")) {
			button.setImage(desImage);
			button.setToolTipText(descending);
		} else {
			button.setImage(ascImage);
			button.setToolTipText(ascending);
		}

		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button but = (Button) e.widget;
				if (but.getData().equals("des")) {
					but.setImage(ascImage);
					but.setToolTipText(ascending);
					but.setData("asc");
				} else {
					but.setImage(desImage);
					but.setToolTipText(descending);
					but.setData("des");
				}
			}
		});
	}

	private void doAdvancedFind() {
		if (advancedToggleMultiLine.getSelection()) {
			advancedSearchCombo.setText(advancedSearchBox.getText());
		} else {
			advancedSearchBox.setText(advancedSearchCombo.getText());
		}
		advancedFind();
	}

	private void doFind() {
		if (toggleMultiLine.getSelection()) {
			searchCombo.setText(searchBox.getText());
		} else {
			searchBox.setText(searchCombo.getText());
		}
		find();
	}

	protected void gotoNextAya() {
		IQuranLocation nextLoc = uvc.getLocation().getNext();
		if (nextLoc != null)
			navTo(nextLoc);
	}

	protected void gotoPrevAya() {
		IQuranLocation prevLoc = uvc.getLocation().getPrev();
		if (prevLoc != null)
			navTo(prevLoc);
	}

	protected void gotoNextSura() {
		if (uvc.getLocation().getSura() < QuranPropertiesUtils.QURAN_SURA_COUNT) {
			navTo(uvc.getLocation().getSura() + 1, 1);
		}
	}

	protected void gotoPrevSura() {
		if (uvc.getLocation().getSura() > 1) {
			navTo(uvc.getLocation().getSura() - 1, 1);
		}
	}

	protected void gotoNextPage() {
		if (uvc.getPage() < config.getQuranPaging().getDefault().size()) {
			navTo(config.getQuranPaging().getDefault().getQuranPage(uvc.getPage() + 1).getFrom());
		}
	}

	protected void gotoPrevPage() {
		if (uvc.getPage() > 1) {
			navTo(config.getQuranPaging().getDefault().getQuranPage(uvc.getPage() - 1).getFrom());
		}
	}

	protected void gotoNextJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (jp.getIndex() < 30) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() + 1);
			navTo(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	protected void gotoPrevJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (jp.getIndex() > 1) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() - 1);
			navTo(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	protected void gotoPrevSajda() {
		List sajdaList = QuranPropertiesUtils.getSajdaList();
		IQuranLocation prevSajda = null;
		int i = sajdaList.size() - 1;
		for (; i >= 0; i--) {
			SajdaProperties sp = (SajdaProperties) sajdaList.get(i);
			prevSajda = new QuranLocation(sp.getSuraNumber(), sp.getAyaNumber());
			if (prevSajda.compareTo(uvc.getLocation()) < 0) {
				break;
			}
		}
		if (i > 0) {
			navTo(prevSajda);
		}
	}

	protected void gotoNextSajda() {
		List sajdaList = QuranPropertiesUtils.getSajdaList();
		IQuranLocation nextSajda = null;
		int i = 0;
		for (; i < sajdaList.size(); i++) {
			SajdaProperties sp = (SajdaProperties) sajdaList.get(i);
			nextSajda = new QuranLocation(sp.getSuraNumber(), sp.getAyaNumber());
			if (nextSajda.compareTo(uvc.getLocation()) > 0) {
				break;
			}
		}
		if (i < sajdaList.size()) {
			navTo(nextSajda);
		}
	}

	protected void gotoNextHizb() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(uvc.getLocation());
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (quad < 7) {
			IQuranLocation newLoc = jp.getHizbQuarters()[quad + 1];
			navTo(newLoc);
		} else if (jp.getIndex() < 30) {
			gotoNextJuz();
		}
	}

	protected void gotoPrevHizb() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(uvc.getLocation());
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (quad > 0) {
			IQuranLocation newLoc = jp.getHizbQuarters()[quad - 1];
			navTo(newLoc);
		} else if (jp.getIndex() > 1) {
			gotoPrevJuz();
		}
	}

	void apply() {
		logger.info("Start updating view...");
		updateView();
		suraMap = QuranPropertiesUtils.getSuraPropsMap(getSelectedSura());
		FormUtils.updateTable(suraTable, suraMap);
		logger.info("Updating view done.");
		pageChanged = false;
	}

	void browserGoto(int sura, int aya, int page) {
		IQuranLocation newLoc;
		if (uvc.getPage() != page && page > 0) {
			if (page > config.getQuranPaging().getDefault().size())
				return; // page out of range
			newLoc = config.getQuranPaging().getDefault().getQuranPage(page).getFrom();
			navTo(newLoc);
		} else {
			if (QuranPropertiesUtils.isValid(sura, aya)) {
				navTo(sura, aya);
			} else if (sura < QuranPropertiesUtils.QURAN_SURA_COUNT) {
				navTo(sura, 1);
			}
		}
	}

	void browserGoto(int sura, int aya, int page, boolean changePage) {
		IQuranLocation newLoc;
		if (uvc.getPage() != page && page > 0) {
			if (page > config.getQuranPaging().getDefault().size())
				return; // page out of range
			newLoc = config.getQuranPaging().getDefault().getQuranPage(page).getFrom();
			navTo(newLoc, changePage);
		} else {
			if (QuranPropertiesUtils.isValid(sura, aya)) {
				navTo(sura, aya, changePage);
			} else if (sura < QuranPropertiesUtils.QURAN_SURA_COUNT) {
				navTo(sura, 1, changePage);
			}
		}
	}

	void navTo(int sura, int aya) {
		navTo(sura, aya, false);
	}

	void navTo(int sura, int aya, boolean changePage) {
		navTo(new QuranLocation(sura, aya), changePage);
	}

	void navTo(IQuranLocation loc) {
		navTo(loc, false);
	}

	void navTo(IQuranLocation loc, boolean changePage) {
		if (loc.isValid()) {
			IPagingData qp = config.getQuranPaging().getDefault();
			int p = uvc.getPage();
			IQuranPage cp = qp.getContainerPage(loc);

			if (uvc.getViewMode() != IUserView.VM_QURAN_TRANS || changePage || cp.getPageNum() != p) {
				pageChanged = true;
			} else {
				pageChanged = false;
			}

			if (changePage || loc.getSura() != uvc.getLocation().getSura()) {
				selectSura(loc.getSura());
				_onSuraChanged();
			}
			ayaSelectorCombo.select(loc.getAya() - 1);

			uvc.changeTo(loc);
			apply();
		} else { // invalid location: update view
			updateView();
		}
		uvc.setViewMode(IUserView.VM_QURAN_TRANS);
	}

	protected void gotoSuraAya(IQuranLocation loc) {
		gotoSuraAya(loc.getSura(), loc.getAya());
	}

	protected void gotoSuraAya(int sura, int aya) {
		if (sura <= QuranPropertiesUtils.QURAN_SURA_COUNT && sura >= 1) {
			uvc.changeTo(new QuranLocation(sura, aya));
			selectSura(sura);
			onSuraChanged();
			int ayaCount = QuranProperties.getInstance().getSura(sura).getAyaCount();
			if (aya <= ayaCount && aya >= 1) {
				ayaSelectorCombo.select(aya - 1);
			}
			apply();
		} else {
			// illegal sura, will update view to the previous legal one
			updateView();
		}
	}

	/**
	 * @param sura sura number (counted from 1)
	 * @param aya aya number (counted from 1)
	 */
	private void gotoAya(int sura, int aya) {
		if (getSelectedSura() != sura) { // user changed sura, should be fully updated
			gotoSuraAya(sura, aya);
			uvc.changeTo(new QuranLocation(sura, aya));
		} else {
			int ayaCount = QuranProperties.getInstance().getSura(uvc.getLocation().getSura()).getAyaCount();
			if (aya <= ayaCount && aya >= 1) {
				ayaSelectorCombo.select(aya - 1);
				uvc.changeTo(new QuranLocation(sura, aya));
				apply();
			} else {
				// illegal aya, will update view to the previous legal one
				updateView();
			}
		}
	}

	protected void updateView() {
		qmf.resetAudioMenuEnableState();

		final int sura = uvc.getLocation().getSura();
		final int aya = uvc.getLocation().getAya();

		logger.info("Set location to " + uvc.getLocation());

		qpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				focusOnAya(quranBrowser, sura, aya);
				quranBrowser.removeProgressListener(this);
			}
		};
		tpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				focusOnAya(transBrowser, sura, aya);
				transBrowser.removeProgressListener(this);
			}
		};
		if (updateQuran)
			updateQuranView();
		if (updateTrans)
			updateTransView();
	}

	private int getSelectedAya() {
		return ayaSelectorCombo.getSelectionIndex() + 1;
	}

	private int getSelectedSura() {
		if (tree) {
			TreeItem[] tis = sst.getSelection();
			int sura = 0;
			if (tis.length == 0 || tis[0].getData() == null)
				sura = uvc.getLocation().getSura();
			else
				sura = Integer.parseInt((String) tis[0].getData());
			selectSura(sura);
			return sura;
		} else {
			return suraSelectorCombo.getSelectionIndex() + 1;
		}
	}

	/**
	 * @param sura
	 */
	private void selectSura(int sura) {
		if (tree) {
			TreeItem ti = rootSura.getItem(sura - 1);
			sst.setSelection(ti);
		} else {
			suraSelectorCombo.select(sura - 1);
		}
	}

	private void updateTransView() {
		if (pageChanged) {
			try {
				transBrowser.addProgressListener(tpl);
				logger.info("Set translation location to " + uvc.getLocation());
				transBrowser.setUrl(transUri = HtmlRepository.getTransUri(uvc.getLocation()));
			} catch (HtmlGenerationException e) {
				logger.log(e);
			}
		} else {
			focusOnAya(transBrowser, uvc.getLocation());
		}
	}

	private void focusOnAya(final Browser browser, int sura, int aya) {
		final String misc = getMiscOptions();
		if (GlobalConfig.isWindows) {
			browser.execute("focusOnAya(" + sura + "," + aya + (misc == null ? "" : "," + misc) + ");");
		} else {
			SwtBrowserUtils.trickyExecute(display, browser, "focusOnAya(" + sura + "," + aya
					+ (misc == null ? "" : "," + misc) + ");");
		}
	}

	private void focusOnAya(final Browser browser, IQuranLocation loc) {
		focusOnAya(browser, loc.getSura(), loc.getAya());
	}

	private String getMiscOptions() {
		PropertiesConfiguration p = config.getProps();
		String ret = null;
		if (config.isAudioEnabled()) {
			ret = "{volume:" + p.getProperty("audio.volume") + ",contAya:" + p.getProperty("audio.continuousAya")
					+ ",firstTime:" + firstTimePlaying + ",autoPlay:" + playerAutoNextSura + "}";
			firstTimePlaying = false;
			playerAutoNextSura = false;
		}
		return ret;
	}

	private void updateQuranView() {
		try {
			if (pageChanged) {
				quranBrowser.addProgressListener(qpl);
				logger.info("Set Quran location to " + uvc.getLocation());
				if (viewLayout == MIXED) {
					quranUri = HtmlRepository.getMixedUri(uvc.getLocation());
				} else if (viewLayout == MULTI_TRANS) {
					quranUri = HtmlRepository.getCustomMixedUri(uvc.getLocation());
				} else {
					quranUri = HtmlRepository.getQuranUri(uvc.getLocation());
				}
				quranBrowser.setUrl(quranUri);
			} else {
				focusOnAya(quranBrowser, uvc.getLocation());
			}
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	private void onSuraChanged() {
		ayaSelectorCombo.setItems(QuranPropertiesUtils.getSuraAyas(getSelectedSura()));
		ayaSelectorCombo.select(0);
		pageChanged = true; // It must be set to false after apply()
	}

	private void _onSuraChanged() {
		ayaSelectorCombo.setItems(QuranPropertiesUtils.getSuraAyas(getSelectedSura()));
		//		ayaSelectorCombo.setItems(QuranPropertiesUtils.getSuraAyas(uvc.getLocation().getSura()));
		//		ayaSelectorCombo.select(0);
		// suraChanged = false; // It must be set to false after apply()
	}

	private void advancedFind() {
		String str;
		if (advancedToggleMultiLine.getSelection())
			str = advancedSearchBox.getText();
		else
			str = advancedSearchCombo.getText();
		if ("".equals(str.trim()))
			return; // do nothing

		LuceneIndexManager lim = config.getLuceneIndexManager();
		try {
			if (advancedQuranTargetBut.getSelection())
				qts = new QuranTextSearcher(lim, searchScope);
			else
				qts = new QuranTextSearcher(lim, searchScope, config.getTranslation().getDefault());
		} catch (IndexingException e) {
			logger.implicitLog(e);
			MessageBoxUtils.showError("Indexing Error: " + e);
			return; // search failed
		}
		if (!qts.isIndexReaderOpen())
			return; // indexing probably interrupted

		str = str.trim();
		if (!"".equals(str)) {
			if (advancedSearchCombo.getItemCount() <= 0 || !str.equals(advancedSearchCombo.getItem(0)))
				advancedSearchCombo.add(str, 0);
			if (advancedSearchCombo.getItemCount() > 40)
				advancedSearchCombo.remove(40, advancedSearchCombo.getItemCount() - 1);

			logger.info("Search started: " + str);
			Date date1 = new Date();

			int sortBy = advancedSearchOrderCombo.getSelectionIndex();
			boolean relevance = sortBy == 0 ? true : false;
			try {
				qts.setSortResultOrder(relevance ? Sort.RELEVANCE : Sort.INDEXORDER);
				qts.setAscending(advancedSortOrderButton.getData().equals("asc"));
				qts.setSearchResultComparator(SearchResultComparatorFactory.getComparator((String) advancedSearchOrderCombo
						.getData(String.valueOf(sortBy))));
				asr = qts.search(str);
			} catch (Exception e) {
				logger.implicitLog(e);
				MessageBoxUtils.showError("Advanced Search Error: " + e);
				searchNav.setVisible(false);
				return; // search failed
			}
			Date date2 = new Date();
			logger.info("Search for " + str + " finished; took " + (date2.getTime() - date1.getTime()) + " ms.");

			int pageCount = asr.getResultPageCount();
			logger.debug("Search result has " + pageCount + " pages.");
			if (pageCount > 1) {
				advSearchNav.setVisible(true);
			} else {
				advSearchNav.setVisible(false);
				advSearchNav.nextPageBut.setEnabled(true);
			}

			advSearchNav.resetSearch(pageCount);
		}
	}

	private void find() {
		String str;
		if (toggleMultiLine.getSelection())
			str = searchBox.getText();
		else
			str = searchCombo.getText();
		if ("".equals(str.trim()))
			return; // do nothing

		str = str.trim();
		if (!"".equals(str)) {
			if (searchCombo.getItemCount() <= 0 || !str.equals(searchCombo.getItem(0)))
				searchCombo.add(str, 0);
			if (searchCombo.getItemCount() > 40)
				searchCombo.remove(40, searchCombo.getItemCount() - 1);

			logger.info("Search started: " + str);
			Date date1 = new Date();

			int sortBy = searchOrderCombo.getSelectionIndex();
			try {
				if (quranTargetBut.getSelection())
					ats = new AdvancedQuranTextSearch(QuranText.getSimpleTextInstance(),
							new SimpleSearchResultHighlighter(), new DefaultSearchScorer());
				else
					ats = new AdvancedQuranTextSearch(config.getTranslation().getDefault(),
							new SimpleSearchResultHighlighter(), new DefaultSearchScorer());
			} catch (Exception e) {
				logger.implicitLog(e);
				MessageBoxUtils.showError("Basic searcher failed to initialize:\n\t" + e);
				return; // search failed
			}
			if (searchScope != ats.getSearchScope()) { // no need to .equals()
				ats.setSearchScope(searchScope);
			}
			ats.setSearchResultComparator(SearchResultComparatorFactory.getComparator((String) searchOrderCombo
					.getData(String.valueOf(sortBy))));
			ats.setAscending(sortOrderButton.getData().equals("asc"));
			try {
				sr = ats.search(str);
			} catch (SearchException se) {
				MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + se.toString());
				searchNav.setVisible(false);
				return; // search failed
			}
			Date date2 = new Date();
			logger.info("Search for " + str + " finished; took " + (date2.getTime() - date1.getTime()) + " ms.");

			int pageCount = sr.getResultPageCount();
			logger.debug("Search result has " + pageCount + " pages.");
			if (pageCount > 1) {
				searchNav.setVisible(true);
			} else {
				searchNav.setVisible(false);
				searchNav.nextPageBut.setEnabled(true);
			}
			searchNav.resetSearch(pageCount);
		}
	}

	void doPreFind() {
		qmf.setAudioMenuEnabled(false);
		qmf.resetAudioMenuStatus();
	}

	/**
	 * @param pageNo one-based page number. 0 means the first page.
	 */
	private void advancedFindGoto(int pageNo) {
		try {
			uvc.setViewMode(IUserView.VM_ADVANCED_SEARCH);
			if (asr == null) {
				logger.error("Advanced search is not done yet!");
				return;
			}
			if (pageNo > asr.getResultPageCount()) {
				logger.error("No such page in results: " + pageNo);
				MessageBoxUtils.showError("No such page in search results: " + pageNo);
				return;
			}

			doPreFind();
			if (pageNo > 1) {
				advSearchNav.prevPageBut.setEnabled(true);
			} else {
				advSearchNav.prevPageBut.setEnabled(false);
			}
			if (pageNo < asr.getResultPageCount()) {
				advSearchNav.nextPageBut.setEnabled(true);
			} else {
				advSearchNav.nextPageBut.setEnabled(false);
			}

			pageNo = pageNo == 0 ? 1 : pageNo;
			logger.info("Navigate to page #" + pageNo + " of advanced search result.");
			Browser searchBrowser = viewLayout == TRANS_ONLY ? transBrowser : quranBrowser;
			searchBrowser.setUrl(HtmlRepository.getAdvancedSearchQuranUri(asr, pageNo - 1));
			pageChanged = true;
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	/**
	 * @param pageNo one-based page number. 0 means the first page.
	 */
	private void findGoto(int pageNo) {
		try {
			uvc.setViewMode(IUserView.VM_SEARCH);
			if (sr == null) {
				logger.error("Search is not done yet!");
				return;
			}
			if (pageNo > sr.getResultPageCount()) {
				logger.error("No such page in results: " + pageNo);
				MessageBoxUtils.showError("No such page in search results: " + pageNo);
				return;
			}

			doPreFind();
			if (pageNo > 1) {
				searchNav.prevPageBut.setEnabled(true);
			} else {
				searchNav.prevPageBut.setEnabled(false);
			}
			if (pageNo < sr.getResultPageCount()) {
				searchNav.nextPageBut.setEnabled(true);
			} else {
				searchNav.nextPageBut.setEnabled(false);
			}

			pageNo = pageNo == 0 ? 1 : pageNo;
			logger.info("Navigate to page #" + pageNo + " of search result.");
			Browser searchBrowser = viewLayout == TRANS_ONLY ? transBrowser : quranBrowser;
			searchBrowser.setUrl(HtmlRepository.getAdvancedSearchQuranUri(sr, pageNo - 1));
			pageChanged = true;
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	void recreate() {
		logger.info("Recreating Quran form...");
		shell.close();
		init();
		show();
	}

	/**
	 * Shows Quran shell. The size and location are based on the property <tt>view.shell.maximized</tt> and
	 * <tt>view.shell.location</tt>
	 */
	public void show() {
		if (config.getProps().getBoolean("view.shell.maximized"))
			shell.setMaximized(true);
		else {
			List l = config.getProps().getList("view.shell.location");
			shell.setLocation(new Integer(l.get(0).toString()).intValue(), new Integer((String) l.get(1).toString())
					.intValue());
			shell.setSize(new Integer((String) l.get(2).toString()).intValue(), new Integer((String) l.get(3).toString())
					.intValue());
		}
		shell.open();
	}

	protected void setFullScreen(boolean full, boolean fromMenu) {
		saveLocationAndSize();
		if (full) {
			shell.setMaximized(true);
			shell.setFullScreen(true);
			fullScreenFloatShell = MessageBoxUtils.getFullScreenToolbar(this);
		} else {
			if (fullScreenFloatShell != null && !fullScreenFloatShell.isDisposed()) {
				fullScreenFloatShell.close();
			}
			show();
		}
		if (!fromMenu) {
			qmf.toggleFullScreenItem(full);
		}
	}

	public void togglePanel(String panel, boolean toggleState) {
		logger.info("Toggle panel " + panel + " visibility state to " + toggleState);
		((GridData) detailGroup.getLayoutData()).exclude = !toggleState;
		detailGroup.setVisible(toggleState);
		if (toggleState)
			detailGroup.pack();
		int minh = workPaneScroller.getMinHeight();
		if (!toggleState) {
			workPaneScroller.setMinHeight(minh - detailGroup.getSize().y);
		} else {
			workPaneScroller.setMinHeight(minh + detailGroup.getSize().y);
		}
		config.getProps().setProperty("view.panel.detail", String.valueOf(toggleState));
		workPaneScroller.layout(true, true);
	}

	public Browser getQuranBrowser() {
		return quranBrowser;
	}

	public void setQuranBrowser(Browser quranBrowser) {
		this.quranBrowser = quranBrowser;
	}

	public String getQuranUri() {
		return quranUri;
	}

	public String getCurrentUri() {
		if (viewLayout == TRANS_ONLY)
			return transUri;
		else
			return quranUri;
	}

	protected void setLayout(String layout) {
		logger.info("Set layout to " + layout);
		if (layout.equals(ApplicationConfig.TRANS_ONLY_LAYOUT)) {
			sashForm.setMaximizedControl(transBrowser);
			viewLayout = TRANS_ONLY;
			updateQuran = false;
			updateTrans = true;
		} else if (layout.equals(ApplicationConfig.SEPARATE_LAYOUT)) {
			if (viewLayout == SEPARATE) // if already is separate, reset sizing
				sashForm.setWeights(new int[] { 1, 1 });
			if (viewLayout == 0) { // Application just started up
				List weights = config.getProps().getList("view.quranForm.layoutSashWeight");
				if (weights.size() != 0) {
					sashForm.setWeights(new int[] { Integer.parseInt(weights.get(0).toString()),
							Integer.parseInt(weights.get(1).toString()) });
				} else {
					sashForm.setWeights(new int[] { 1, 1 });
				}
			}
			sashForm.setMaximizedControl(null);
			viewLayout = SEPARATE;
			updateQuran = true;
			updateTrans = true;
		} else if (layout.equals(ApplicationConfig.MIXED_LAYOUT)) {
			sashForm.setMaximizedControl(quranBrowser);
			viewLayout = MIXED;
			updateQuran = true;
			updateTrans = false;
		} else if (layout.equals(ApplicationConfig.MULTI_TRANS_LAYOUT)) {
			sashForm.setMaximizedControl(quranBrowser);
			viewLayout = MULTI_TRANS;
			updateQuran = true;
			updateTrans = false;
		} else { // assume layout is Quran-only
			sashForm.setMaximizedControl(quranBrowser);
			viewLayout = QURAN_ONLY;
			updateQuran = true;
			updateTrans = false;
			config.setViewLayout(ApplicationConfig.QURAN_ONLY_LAYOUT);
		}
	}

	private void saveConfigProps() {
		saveLocationAndSize();

		config.getProps().setProperty("view.quranLoc", uvc.getLocation().toString());
		config.getProps().setProperty("view.page", String.valueOf(uvc.getPage()));

		// basic search props
		config.getProps().setProperty("view.search.tab", String.valueOf(searchTabFolder.getSelectionIndex()));
		config.getProps().setProperty("view.search.target", quranTargetBut.getSelection() ? "quran" : "trans");
		config.getProps().setProperty("view.search.advanced.target",
				advancedQuranTargetBut.getSelection() ? "quran" : "trans");

		// advanced search props
		config.getProps().setProperty("view.search.advanced.sortBy",
				String.valueOf(advancedSearchOrderCombo.getSelectionIndex()));
		config.getProps().setProperty("view.search.advanced.sortOrder", advancedSortOrderButton.getData());
		config.getProps().setProperty("view.search.advanced.multiLine",
				String.valueOf(advancedToggleMultiLine.getSelection()));
		config.getProps().setProperty("view.search.sortBy", String.valueOf(searchOrderCombo.getSelectionIndex()));
		config.getProps().setProperty("view.search.sortOrder", sortOrderButton.getData());
		config.getProps().setProperty("view.search.multiLine", String.valueOf(toggleMultiLine.getSelection()));

		// root search props
		config.getProps().setProperty("view.search.root.sortBy",
				String.valueOf(rootTabForm.searchOrderCombo.getSelectionIndex()));
		config.getProps().setProperty("view.search.root.sortOrder", rootTabForm.sortOrderButton.getData());

		// TODO: add search scopes

		// sash props
		if (viewLayout == SEPARATE) {
			int[] w = sashForm.getWeights();
			config.getProps().setProperty("view.quranForm.layoutSashWeight", new String[] { "" + w[0], "" + w[1] });
		}
		if (isSashed) {
			int[] w = navSashForm.getWeights();
			config.getProps().setProperty("view.quranForm.paneSashWeight", new String[] { "" + w[0], "" + w[1] });
		}

	}

	private void saveLocationAndSize() {
		// add form size and location
		List list = new ArrayList();
		Rectangle r = shell.getBounds();
		list.add(new Integer(r.x));
		list.add(new Integer(r.y));
		list.add(new Integer(r.width));
		list.add(new Integer(r.height));
		if (!shell.getFullScreen()) { // don't save fullscreen state
			config.getProps().setProperty("view.shell.location", list);
			config.getProps().setProperty("view.shell.maximized", new Boolean(shell.getMaximized()));
		}
	}

	public void close() {
		saveConfigProps();
		config.updateFile();
		if (clearOnExit) {
			logger.info("Clear cache directory.");
			config.getRuntime().clearCache();
			clearOnExit = false;
		}
		logger.info("Disposing all resources...");
	}

	private Menu createSearchScopeMenu() {
		final Menu scopeMenu = new Menu(shell, SWT.POP_UP | lang.getSWTDirection());

		final MenuItem helpItem = new MenuItem(scopeMenu, SWT.PUSH);
		helpItem.setText(lang.getMeaning("HELP") + "...");
		helpItem.setData(FormUtils.URL_DATA, GlobalConfig.SEARCH_HELP_PAGE);
		FormUtils.addLinkListener(helpItem);
		new MenuItem(scopeMenu, SWT.SEPARATOR);

		final MenuItem newScopeItem = new MenuItem(scopeMenu, SWT.PUSH);
		newScopeItem.setText(meaning("NEW_SCOPE") + "...");
		newScopeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SearchScopeForm ssf = new SearchScopeForm(shell);
				if (ssf.open()) {
					searchScope = ssf.getSearchScope();
					searchScopeList.add(searchScope);

					// deselect all items
					MenuItem[] mis = scopeMenu.getItems();
					for (int i = 5; i < mis.length; i++) {
						mis[i].setSelection(false);
					}

					MenuItem item = addNewScopeMenuItem(scopeMenu, searchScope);
					item.setSelection(true);
				}
			}
		});

		final MenuItem editItem = new MenuItem(scopeMenu, SWT.PUSH);
		editItem.setText(lang.getMeaning("EDIT") + "...");
		editItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ManageScopesForm msl = new ManageScopesForm(shell, new ArrayList(searchScopeList));
				if (msl.open()) {
					searchScopeList = msl.getSearchScopeList();
					int selectedIndex = msl.getSelectedIndex();
					MenuItem[] mis = scopeMenu.getItems();
					int c = scopeMenu.getItemCount();

					if (c > 6) {
						for (int i = c - 1; i >= 6; i--) {
							mis[i].dispose();
						}
					}

					MenuItem item = null;
					for (Iterator iter = searchScopeList.iterator(); iter.hasNext();) {
						SearchScope ss = (SearchScope) iter.next();
						item = addNewScopeMenuItem(scopeMenu, ss);
					}

					MenuItem wholeQuranItem = scopeMenu.getItems()[5];
					if (item != null && selectedIndex != -1) {
						MenuItem selItem = scopeMenu.getItems()[6 + selectedIndex];
						selItem.setSelection(true);
						searchScope = (SearchScope) selItem.getData();
						wholeQuranItem.setSelection(false); // deselect the first item (whole Quran)
					} else {
						wholeQuranItem.setSelection(true);
					}
				}
			}
		});

		new MenuItem(scopeMenu, SWT.SEPARATOR);

		final MenuItem wholeQuranItem = new MenuItem(scopeMenu, SWT.RADIO);
		wholeQuranItem.setText(meaning("NO_SCOPE"));
		wholeQuranItem.setSelection(true);
		wholeQuranItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (wholeQuranItem.getSelection())
					searchScope = null;
			}
		});

		return scopeMenu;
	}

	public MenuItem addNewScopeMenuItem(Menu parentMenu, SearchScope scope) {
		final MenuItem item = new MenuItem(parentMenu, SWT.RADIO);
		String s = scope.toString();
		item.setText(StringUtils.abbreviate(s, GlobalConfig.MAX_MENU_STRING_LENGTH));
		item.setData(scope);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				searchScope = (SearchScope) item.getData();
			}
		});
		return item;
	}

	protected Shell getShell() {
		return shell;
	}

	protected Display getDisplay() {
		return display;
	}

	String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}
}

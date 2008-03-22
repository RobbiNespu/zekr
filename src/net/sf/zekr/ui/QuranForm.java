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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.runtime.HtmlGenerationException;
import net.sf.zekr.common.runtime.HtmlRepository;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.lucene.AdvancedSearchResult;
import net.sf.zekr.engine.search.lucene.QuranTextSearcher;
import net.sf.zekr.engine.search.tanzil.AdvancedTextSearch;
import net.sf.zekr.engine.search.tanzil.DefaultSearchScorer;
import net.sf.zekr.engine.search.tanzil.SearchResult;
import net.sf.zekr.engine.search.tanzil.SimpleSearchHighlighter;
import net.sf.zekr.engine.search.ui.ManageScopesForm;
import net.sf.zekr.engine.search.ui.SearchScopeForm;
import net.sf.zekr.engine.translation.TranslationData;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Main Zekr form. This class contains all the Zekr main screen, except menus which are in
 * <code>QuranFormMenuFactory</code>.
 * 
 * @author Mohsen Saboorian
 */
public class QuranForm extends BaseForm {
	private Composite body;
	private Browser quranBrowser;
	private Browser transBrowser;
	private Combo suraSelector;
	private Combo ayaSelector;
	private Label suraLabel;
	private Label ayaLabel;
	private Combo searchCombo, advancedSearchCombo;
	//	private Composite advancedSearchComboComp;
	private Text searchBox, advancedSearchBox;
	private Button applyButton;
	private Button searchButton;
	private Button searchArrowBut;
	private Button quranScopeBut;
	private Button transScopeBut;
	private Button sync;
	private Button matchDiacCheckBox;
	private Button currentPageCheckBox;
	private Button matchCaseCheckBox;
	private Button toggleMultiLine, advancedToggleMultiLine;
	private Table suraTable;
	private Map suraMap;
	private Group navigationGroup;
	private Group searchGroup;
	private Group navGroup;
	private Group leftGroup;
	private Group detailGroup;
	private SashForm sashForm;
	private SashForm navSashForm;
	private Menu searchMenu;
	//	private StackLayout advancedSearchStackLayout;
	private Button advancedSearchButton;
	private Button advancedSearchArrowBut;
	private Menu advancedSearchMenu;

	private ProgressAdapter qpl, tpl;
	private String title;

	// These 6 properties should be package-private
	int viewLayout;
	static final int MIXED = 1;
	static final int SEPARATE = 2;
	static final int QURAN_ONLY = 3;
	static final int TRANS_ONLY = 4;
	static final int MULTI_TRANS = 5;

	private static final String NAV_BUTTON = "NAV_BUTTON";

	private int searchTarget;
	/** match case behavior for search */
	// private boolean matchCase;
	private final String FORM_ID = "QURAN_FORM";

	private static final Logger logger = Logger.getLogger(QuranForm.class);

	private QuranProperties quranProp;

	/** Specifies whether aya selector changed since a sura was selected. */
	protected boolean ayaChanged;

	/** Specifies whether sura selector changed for making a new sura view. */
	protected boolean suraChanged;

	/** The current Quran URI loaded in the browser */
	private String quranUri;

	/** The current Translation URI loaded in the browser */
	private String transUri;

	private QuranTextSearcher qts;
	private AdvancedTextSearch ats;
	private AdvancedSearchResult asr;

	private AdvancedTextSearch searcher;
	private SearchResult sr;

	private ApplicationConfig config;
	private boolean isClosed;
	private boolean isSashed;

	private DisposeListener dl;
	private QuranLocation quranLoc;

	protected boolean updateTrans = true;
	protected boolean updateQuran = true;
	private QuranFormMenuFactory qmf;
	private boolean clearOnExit = false;
	private SearchScope searchScope;
	private List searchScopeList;
	private TabFolder searchTabFolder;
	private Composite searchTabBody, advancedSearchTabBody;
	private Menu searchScopeMenu;
	private Combo searchOrderCombo, advancedSearchOrderCombo;
	private Composite advancedSearchPaginationComp, searchPaginationComp;
	private Spinner advancedSearchPaginationSpinner, searchPaginationSpinner;
	private Button advNextPageBut, advPrevPageBut, nextPageBut, prevPageBut;

	/** a state specifying if this is the first aya user focuses on */
	private boolean firstTimePlaying;
	/** specifies if audio player automatically brings user to the next sura */
	private boolean playerAutoNextSura = false;

	private UpdateManager updateManager;

	private Listener globalKeyListener = new Listener() {
		public void handleEvent(Event event) {
			if (NAV_BUTTON.equals(event.widget.getData())) {
				boolean isRTL = ((lang.getSWTDirection() == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);
				int d = event.keyCode ^ SWT.KEYCODE_BIT;
				if (d == 1) {
					gotoPrevSura();
				} else if (d == 2) {
					gotoNextSura();
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
		searchTarget = QURAN_ONLY;
		// matchCase = false;

		viewLayout = 0; // no layout set yet

		title = meaning("TITLE");
		shell = new Shell(display, SWT.SHELL_TRIM | lang.getSWTDirection());
		shell.setText(title);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")),
				new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")),
				new Image(display, resource.getString("icon.form256")) });
		shell.setMenuBar((qmf = new QuranFormMenuFactory(this, shell)).getQuranFormMenu());

		ayaChanged = false;
		suraChanged = false;

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
		initQuranLocation();

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
						gotoSuraAya(loc.getSura(), loc.getAya());
					}
					e.doit = false;
				}
			}
		});

		display.removeFilter(SWT.KeyDown, globalKeyListener);
		display.addFilter(SWT.KeyDown, globalKeyListener);
	}

	private void updateSuraNames() {
		int i = suraSelector.getSelectionIndex();
		QuranPropertiesUtils.resetIndexedSuraNames();
		suraSelector.setItems(QuranPropertiesUtils.getIndexedSuraNames());
		suraSelector.select(i);
		// suraSelector.layout();
	}

	/**
	 * Recreates the whole cache. All previous cached data are removed.
	 */
	protected void reload() {
		try {
			config.getRuntime().recreateViewCache();
			suraChanged = true;
			qmf.resetMenuStatus();
			apply();
		} catch (IOException e) {
			logger.log(e);
		}
	}

	private void initQuranLocation() {
		// quranLoc = config.getQuranLocation();
		quranLoc = new QuranLocation(config.getProps().getString("view.quranLoc"));
		logger.info("Loading last visited Quran location: " + quranLoc + ".");
		if (quranLoc.getAya() == 1)
			gotoSuraAya(quranLoc.getSura(), 0);
		else
			gotoSuraAya(quranLoc.getSura(), quranLoc.getAya());
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

		Composite workPane = new Composite(isSashed ? navSashForm : body, SWT.NONE);
		gd = new GridData(GridData.FILL_VERTICAL);
		workPane.setLayoutData(gd);
		gl = new GridLayout(1, false);
		gl.marginHeight = gl.marginWidth = 0;
		gl.marginLeft = gl.marginRight = gl.marginTop = gl.marginBottom = 2;
		workPane.setLayout(gl);

		//		ScrolledComposite workPaneScroller = new ScrolledComposite(isSashed ? navSashForm : body, SWT.H_SCROLL
		//				| SWT.V_SCROLL);
		//		workPaneScroller.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		//		workPaneScroller.setLayout(new FillLayout());
		//		workPaneScroller.setExpandHorizontal(true);
		//		workPaneScroller.setExpandVertical(true);

		//		Composite workPane = new Composite(workPaneScroller, SWT.NONE);
		//		gl = new GridLayout(1, false);
		//		gl.marginHeight = gl.marginWidth = 0;
		//		gl.marginLeft = gl.marginRight = gl.marginTop = gl.marginBottom = 2;
		//		workPane.setLayout(gl);

		//		workPaneScroller.setContent(workPane);
		//		workPaneScroller.setMinSize(workPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite bgroup = new Composite(isSashed ? navSashForm : body, SWT.NONE);
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
					doBrowserCallback(event.text);
				}
			}

			private void doBrowserCallback(String message) {
				if (message.startsWith("ZEKR::")) {
					quranBrowser.execute("window.status='';"); // clear the status text
					if (transBrowser != null)
						transBrowser.execute("window.status='';"); // clear the status text

					if (message.startsWith("ZEKR::GOTO")) {
						int sura;
						int aya;
						try {
							sura = Integer.parseInt(message.substring(message.indexOf(' '), message.indexOf('-')).trim());
							aya = Integer.parseInt(message.substring(message.indexOf('-') + 1, message.indexOf(';')).trim());
						} catch (NumberFormatException e) {
							return; // do nothing
						}
						if (sura < 1 || aya < 1)
							return; // do nothing
						logger.info("Goto (" + sura + ", " + aya + ")");
						gotoSuraAya(sura, aya);
					} else if (message.startsWith("ZEKR::NAVTO")) {
						int sura;
						int aya;
						try {
							sura = Integer.parseInt(message.substring(message.indexOf(' '), message.indexOf('-')).trim());
							aya = Integer.parseInt(message.substring(message.indexOf('-') + 1, message.indexOf(';')).trim());
						} catch (NumberFormatException e) {
							return; // do nothing
						}
						if (sura < 1 || aya < 1)
							return; // do nothing
						logger.info("Goto (" + aya + ")");
						gotoAya(sura, aya);
					} else if (message.startsWith("ZEKR::TRANS") && config.getTranslation().getDefault() != null) {
						int sura;
						int aya;
						try {
							sura = Integer.parseInt(message.substring(message.indexOf(' '), message.indexOf('-')).trim());
							aya = Integer.parseInt(message.substring(message.indexOf('-') + 1, message.indexOf(';')).trim());
						} catch (NumberFormatException e1) {
							return; // do nothing
						}
						PopupBox pe = null;
						if (searchTarget == QURAN_ONLY) {
							logger.info("Show translation: (" + sura + ", " + aya + ")");
							TranslationData td = config.getTranslation().getDefault();
							pe = new PopupBox(shell, meaning("TRANSLATION_SCOPE"), td.get(sura, aya), FormUtils
									.toSwtDirection(td.direction));
						} else {
							logger.info("Show quran: (" + sura + ", " + aya + ")");
							try {
								pe = new PopupBox(shell, meaning("QURAN_SCOPE"), QuranText.getSimpleTextInstance().get(sura,
										aya), SWT.RIGHT_TO_LEFT);
							} catch (IOException e) {
								logger.log(e);
							}
						}
						Point p = display.getCursorLocation();
						p.y += 15;
						int x = 300;
						pe.open(new Point(x, 100), new Point(p.x - x / 2, p.y));
					} else if (message.startsWith("ZEKR::PLAYER_VOLUME")) {
						Integer volume = new Integer(message.substring(message.indexOf(' '), message.indexOf(';')).trim());
						config.getProps().setProperty("audio.volume", volume);
					} else if (message.startsWith("ZEKR::PLAYER_PLAYPAUSE")) {
						playerTogglePlayPause();
					} else if (message.startsWith("ZEKR::PLAYER_STOP")) {
						playerStop();
					} else if (message.startsWith("ZEKR::PLAYER_NEXT_SURA")) {
						playerAutoNextSura = true;
					} else if (message.startsWith("ZEKR::PLAYER_CONT")) {
						String contAya = message.substring(message.indexOf(' ') + 1, message.indexOf(';')).trim();
						config.getProps().setProperty("audio.continuousAya", contAya);
					}
				}
			}
		};

		transBrowser = new Browser(sashForm, getBrowserStyle());
		fl = new FillLayout(SWT.VERTICAL);
		transBrowser.setLayout(fl);

		quranBrowser.addStatusTextListener(stl);
		transBrowser.addStatusTextListener(stl);

		gl = new GridLayout(2, false);
		navGroup = new Group(workPane, SWT.NONE);
		navGroup.setText(lang.getMeaning("SELECT") + ":");
		navGroup.setLayout(gl);
		navGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		suraLabel = new Label(navGroup, SWT.NONE);
		suraLabel.setText(lang.getMeaning("SURA") + ":");

		suraSelector = new Combo(navGroup, SWT.READ_ONLY);
		ayaSelector = new Combo(navGroup, SWT.READ_ONLY);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 80;
		suraSelector.setLayoutData(gd);
		suraSelector.setItems(QuranPropertiesUtils.getIndexedSuraNames());
		suraSelector.setVisibleItemCount(15);
		suraSelector.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onSuraChanged();
				if (sync.getSelection())
					apply();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				onSuraChanged();
				apply();
			}
		});
		suraSelector.select(0);

		ayaLabel = new Label(navGroup, SWT.NONE);
		ayaLabel.setText(lang.getMeaning("AYA") + ":");

		ayaSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		ayaSelector.setItems(QuranPropertiesUtils.getSuraAyas(1));
		ayaSelector.setVisibleItemCount(10);
		ayaSelector.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ayaChanged = true;
				if (sync.getSelection())
					apply();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				ayaChanged = true;
				apply();
			}
		});
		ayaSelector.select(0);
		ayaSelector.moveBelow(ayaLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		ayaSelector.setLayoutData(gd);

		sync = new Button(navGroup, SWT.CHECK);
		sync.setText(lang.getMeaning("SYNCHRONOUS"));
		if (config.getProps().getProperty("view.location.sync") != null) {
			sync.setSelection(config.getProps().getBoolean("view.location.sync"));
		}

		applyButton = new Button(navGroup, SWT.NONE);
		applyButton.setData(NAV_BUTTON);
		gd = new GridData(GridData.FILL_HORIZONTAL);

		applyButton.setLayoutData(gd);
		applyButton.setText(lang.getMeaning("SHOW"));
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});

		Composite navComposite = new Composite(navGroup, SWT.NONE);
		gl = new GridLayout(4, false);
		navComposite.setLayout(gl);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		navComposite.setLayoutData(gd);

		int style = SWT.PUSH | SWT.FLAT;
		Button prevSura = new Button(navComposite, style);
		Button prevAya = new Button(navComposite, style);
		Button nextAya = new Button(navComposite, style);
		Button nextSura = new Button(navComposite, style);
		prevSura.setData(NAV_BUTTON);
		nextSura.setData(NAV_BUTTON);
		prevAya.setData(NAV_BUTTON);
		nextAya.setData(NAV_BUTTON);

		gd = new GridData(GridData.FILL_BOTH);
		prevAya.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		prevSura.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		nextAya.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		nextSura.setLayoutData(gd);

		int l = lang.getSWTDirection();

		// isRTL is only applicable for Windows
		boolean isRTL = ((l == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);

		Image prevSuraImg = new Image(display, isRTL ? resource.getString("icon.nextNext") : resource
				.getString("icon.prevPrev"));
		Image prevAyaImg = new Image(display, isRTL ? resource.getString("icon.next") : resource.getString("icon.prev"));
		Image nextAyaImg = new Image(display, isRTL ? resource.getString("icon.prev") : resource.getString("icon.next"));
		Image nextSuraImg = new Image(display, isRTL ? resource.getString("icon.prevPrev") : resource
				.getString("icon.nextNext"));

		prevSura.setImage(prevSuraImg);
		prevAya.setImage(prevAyaImg);
		nextAya.setImage(nextAyaImg);
		nextSura.setImage(nextSuraImg);

		prevSura.setToolTipText(lang.getMeaning("PREV_SURA"));
		prevAya.setToolTipText(lang.getMeaning("PREV_AYA"));
		nextAya.setToolTipText(lang.getMeaning("NEXT_AYA"));
		nextSura.setToolTipText(lang.getMeaning("NEXT_SURA"));

		prevSura.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoPrevSura();
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

		nextSura.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gotoNextSura();
			}
		});

		detailGroup = new Group(workPane, SWT.NONE);
		detailGroup.setText(lang.getMeaning("DETAILS") + ":");
		gl = new GridLayout(1, true);
		detailGroup.setLayout(gl);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		detailGroup.setLayoutData(gd);

		suraMap = QuranPropertiesUtils.getSuraPropsMap(suraSelector.getSelectionIndex() + 1);

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

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		suraTable = FormUtils.getTableFromMap(detailGroup, suraMap, lang.getMeaning("NAME"), lang.getMeaning("VALUE"),
				SWT.DEFAULT, SWT.DEFAULT, gd, SWT.HIDE_SELECTION);
		suraTable.setMenu(propsMenu);

		// searchGroup = new Group(workPane, SWT.NONE);
		// searchGroup.setText(langEngine.getMeaning("SEARCH"));
		// searchGroup.setLayout(new FillLayout());
		// gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		// searchGroup.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		searchTabFolder = new TabFolder(workPane, lang.getSWTDirection());
		searchTabFolder.setLayoutData(gd);

		TabItem normalSearchTab = new TabItem(searchTabFolder, SWT.NONE);
		TabItem advancedSearchTab = new TabItem(searchTabFolder, SWT.NONE);
		normalSearchTab.setText(lang.getMeaning("SEARCH"));
		advancedSearchTab.setText(lang.getMeaning("ADVANCED"));

		if (config.getProps().getInt("view.search.tab") != 0)
			searchTabFolder.setSelection(1);

		searchTabBody = new Composite(searchTabFolder, lang.getSWTDirection());
		searchTabBody.setLayout(new GridLayout(2, false));
		normalSearchTab.setControl(searchTabBody);

		advancedSearchTabBody = new Composite(searchTabFolder, lang.getSWTDirection());
		advancedSearchTabBody.setLayout(new GridLayout(2, false));
		advancedSearchTab.setControl(advancedSearchTabBody);

		searchScopeMenu = createSearchScopeMenu();
		createLuceneSearchTabContent();
		createSearchTabContent();
		// createSimpleSearchTabContent();

		// this progress should be in the heart of makeFrame method!
		logger.info("UI relatively initialized.");
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "UI Initialized");
	}

	private int getBrowserStyle() {
		return config.useMozilla() ? SWT.MOZILLA : SWT.NONE;
	}

	private void playerStop() {
		qmf.playerStop(false);
	}

	private void playerTogglePlayPause() {
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

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(2, false);
		gl.marginWidth = 0;
		Composite searchOptionsComp = new Composite(advancedSearchTabBody, SWT.NONE);
		searchOptionsComp.setLayout(gl);
		searchOptionsComp.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		Label sortResult = new Label(searchOptionsComp, SWT.NONE);
		sortResult.setLayoutData(gd);
		sortResult.setText(meaning("SORT_BY") + ":");

		gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		advancedSearchOrderCombo = new Combo(searchOptionsComp, SWT.READ_ONLY);
		advancedSearchOrderCombo.setItems(new String[] { meaning("RELEVANCE"), meaning("NATURAL_ORDER") });
		advancedSearchOrderCombo.setLayoutData(gd);
		advancedSearchOrderCombo.select(config.getProps().getInt("view.search.advanced.sortBy"));

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(3, true);
		gl.marginWidth = 0;
		advancedSearchPaginationComp = new Composite(advancedSearchTabBody, SWT.NONE);
		advancedSearchPaginationComp.setLayout(gl);
		advancedSearchPaginationComp.setLayoutData(gd);
		advancedSearchPaginationComp.setVisible(false);

		boolean isRTL = ((lang.getSWTDirection() == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);
		Image prevPageImg = new Image(display, isRTL ? resource.getString("icon.nextNext") : resource
				.getString("icon.prevPrev"));
		Image nextPageImg = new Image(display, isRTL ? resource.getString("icon.prevPrev") : resource
				.getString("icon.nextNext"));

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		advPrevPageBut = new Button(advancedSearchPaginationComp, SWT.PUSH);
		advPrevPageBut.setLayoutData(gd);
		advPrevPageBut.setToolTipText(lang.getMeaning("PREVIOUS"));
		advPrevPageBut.setImage(prevPageImg);
		advPrevPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPage = advancedSearchPaginationSpinner.getSelection() - 1;
				advancedSearchPaginationSpinner.setSelection(newPage);
				advancedFindGoto(newPage);
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		advancedSearchPaginationSpinner = new Spinner(advancedSearchPaginationComp, SWT.BORDER);
		advancedSearchPaginationSpinner.setLayoutData(gd);
		advancedSearchPaginationSpinner.setToolTipText(lang.getMeaning("PAGE"));
		advancedSearchPaginationSpinner.setMinimum(1);
		advancedSearchPaginationSpinner.setMaximum(1000);
		advancedSearchPaginationSpinner.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					advancedFindGoto(advancedSearchPaginationSpinner.getSelection());
				}
			}
		});
		advancedSearchPaginationSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int p = advancedSearchPaginationSpinner.getSelection();
				if (p > 1) {
					advPrevPageBut.setEnabled(true);
				} else {
					advPrevPageBut.setEnabled(false);
				}
				if (p < asr.getResultPageCount()) {
					advNextPageBut.setEnabled(true);
				} else {
					advNextPageBut.setEnabled(false);
				}
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		advNextPageBut = new Button(advancedSearchPaginationComp, SWT.PUSH);
		advNextPageBut.setLayoutData(gd);
		advNextPageBut.setToolTipText(lang.getMeaning("NEXT"));
		advNextPageBut.setImage(nextPageImg);
		advNextPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPage = advancedSearchPaginationSpinner.getSelection() + 1;
				advancedSearchPaginationSpinner.setSelection(newPage);
				advancedFindGoto(newPage);
			}
		});

		advNextPageBut.setEnabled(false);
		advPrevPageBut.setEnabled(false);
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
		toggleMultiLine.setSelection(config.getProps().getBoolean("view.search.advanced.multiLine"));
		if (toggleMultiLine.getSelection()) {
			searchStackLayout.topControl = searchBox;
		} else {
			searchStackLayout.topControl = searchComboComp;
		}

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(2, false);
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
		searchOrderCombo.setItems(new String[] { meaning("RELEVANCE"), meaning("NATURAL_ORDER"), meaning("AYA_LENGTH"),
				meaning("REVELATION_ORDER") });
		searchOrderCombo.setData("0", "net.sf.zekr.engine.search.tanzil.SimilarityComparator");
		searchOrderCombo.setData("1", null);
		searchOrderCombo.setData("2", "net.sf.zekr.engine.search.tanzil.AyaLengthComparator");
		searchOrderCombo.setData("3", "net.sf.zekr.engine.search.tanzil.RevelationOrderComparator");
		
		searchOrderCombo.setLayoutData(gd);
		searchOrderCombo.select(config.getProps().getInt("view.search.advanced.sortBy"));

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		gl = new GridLayout(3, true);
		gl.marginWidth = 0;
		searchPaginationComp = new Composite(searchTabBody, SWT.NONE);
		searchPaginationComp.setLayout(gl);
		searchPaginationComp.setLayoutData(gd);
		searchPaginationComp.setVisible(false);

		boolean isRTL = ((lang.getSWTDirection() == SWT.RIGHT_TO_LEFT) && GlobalConfig.hasBidiSupport);
		Image prevPageImg = new Image(display, isRTL ? resource.getString("icon.nextNext") : resource
				.getString("icon.prevPrev"));
		Image nextPageImg = new Image(display, isRTL ? resource.getString("icon.prevPrev") : resource
				.getString("icon.nextNext"));

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		prevPageBut = new Button(searchPaginationComp, SWT.PUSH);
		prevPageBut.setLayoutData(gd);
		prevPageBut.setToolTipText(lang.getMeaning("PREVIOUS"));
		prevPageBut.setImage(prevPageImg);
		prevPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPage = searchPaginationSpinner.getSelection() - 1;
				searchPaginationSpinner.setSelection(newPage);
				advancedFindGoto(newPage);
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		searchPaginationSpinner = new Spinner(searchPaginationComp, SWT.BORDER);
		searchPaginationSpinner.setLayoutData(gd);
		searchPaginationSpinner.setToolTipText(lang.getMeaning("PAGE"));
		searchPaginationSpinner.setMinimum(1);
		searchPaginationSpinner.setMaximum(1000);
		searchPaginationSpinner.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					advancedFindGoto(searchPaginationSpinner.getSelection());
				}
			}
		});
		searchPaginationSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int p = searchPaginationSpinner.getSelection();
				if (p > 1) {
					prevPageBut.setEnabled(true);
				} else {
					prevPageBut.setEnabled(false);
				}
				if (p < sr.getResultPageCount()) {
					nextPageBut.setEnabled(true);
				} else {
					nextPageBut.setEnabled(false);
				}
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		nextPageBut = new Button(searchPaginationComp, SWT.PUSH);
		nextPageBut.setLayoutData(gd);
		nextPageBut.setToolTipText(lang.getMeaning("NEXT"));
		nextPageBut.setImage(nextPageImg);
		nextPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPage = searchPaginationSpinner.getSelection() + 1;
				searchPaginationSpinner.setSelection(newPage);
				findGoto(newPage);
			}
		});

		nextPageBut.setEnabled(false);
		prevPageBut.setEnabled(false);
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

	/*
		private void createSimpleSearchTabContent() {
			searchCombo = new Combo(searchTabBody, SWT.DROP_DOWN);
			searchCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			searchCombo.setVisibleItemCount(8);
			searchCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetDefaultSelected(SelectionEvent e) {
					find();
				}
			});
			// searchCombo.addKeyListener(comboSelectAll);

			GridLayout gl = new GridLayout(2, false);
			gl.horizontalSpacing = 0;
			gl.marginWidth = 0;
			gl.verticalSpacing = 0;

			Composite searchButComp = new Composite(searchTabBody, SWT.NONE);
			searchButComp.setLayout(gl);

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);

			searchButton = new Button(searchButComp, SWT.PUSH);
			searchButton.setText(lang.getMeaning("SEARCH"));
			searchButton.setLayoutData(gd);
			searchButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					find();
				}
			});

			// search option button
			gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
			// gd = new GridData(GlobalConfig.isLinux ? GridData.FILL_BOTH : GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = -1;

			searchArrowBut = new Button(searchButComp, SWT.TOGGLE);
			searchMenu = _searchScopeMenu;
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

			KeyAdapter ka = new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 13) {
						find();
					}
				}
			};

			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			final Composite searchScopeComp = new Composite(searchTabBody, SWT.NONE);
			searchScopeComp.setLayoutData(gd);
			searchScopeComp.setLayout(new FillLayout());

			quranScopeBut = new Button(searchScopeComp, SWT.RADIO);
			quranScopeBut.setText(meaning("QURAN_SCOPE"));

			boolean searchQuranContent = config.getProps().getBoolean("view.search.simple.searchQuranContent");

			quranScopeBut.setSelection(searchQuranContent);
			quranScopeBut.addKeyListener(ka);

			transScopeBut = new Button(searchScopeComp, SWT.RADIO);
			transScopeBut.setText(meaning("TRANSLATION_SCOPE"));
			SelectionListener transScopeSA = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (transScopeBut.getSelection()) {
						searchTarget = TRANS_ONLY;
						matchDiacCheckBox.setEnabled(false);
						matchCaseCheckBox.setEnabled(true);
					} else {
						searchTarget = QURAN_ONLY;
						matchDiacCheckBox.setEnabled(true);
						matchCaseCheckBox.setEnabled(false);
					}
				}
			};
			transScopeBut.addKeyListener(ka);
			transScopeBut.setSelection(!searchQuranContent);
			transScopeBut.addSelectionListener(transScopeSA);
			if (config.getTranslation().getDefault() == null)
				transScopeBut.setEnabled(false);

			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
			gd.horizontalSpan = 2;
			currentPageCheckBox = new Button(searchTabBody, SWT.CHECK);
			currentPageCheckBox.setText(meaning("CURRENT_PAGE"));
			currentPageCheckBox.setLayoutData(gd);
			currentPageCheckBox.addKeyListener(ka);
			SelectionListener currPageSA = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (currentPageCheckBox.getSelection()) {
						searchArrowBut.setEnabled(false);
						quranScopeBut.setEnabled(false);
						transScopeBut.setEnabled(false);
						matchCaseCheckBox.setEnabled(true);
						matchDiacCheckBox.setEnabled(true);
					} else {
						searchArrowBut.setEnabled(true);
						quranScopeBut.setEnabled(true);
						transScopeBut.setEnabled(config.getTranslation().getDefault() != null);
						if (transScopeBut.getSelection()) {
							matchCaseCheckBox.setEnabled(true);
							matchDiacCheckBox.setEnabled(false);
						} else {
							matchCaseCheckBox.setEnabled(false);
							matchDiacCheckBox.setEnabled(true);
						}
					}
				}
			};
			currentPageCheckBox.setSelection(config.getProps().getBoolean("view.search.simple.currentPageOnly"));
			currentPageCheckBox.addSelectionListener(currPageSA);

			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
			gd.horizontalSpan = 2;
			matchDiacCheckBox = new Button(searchTabBody, SWT.CHECK);
			matchDiacCheckBox.setText(lang.getMeaning("MATCH_DIACRITIC"));
			matchDiacCheckBox.setLayoutData(gd);
			matchDiacCheckBox.addKeyListener(ka);
			matchDiacCheckBox.setSelection(config.getProps().getBoolean("view.search.simple.matchDiacritics"));

			gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
			gd.horizontalSpan = 2;
			matchCaseCheckBox = new Button(searchTabBody, SWT.CHECK);
			matchCaseCheckBox.setText(lang.getMeaning("MATCH_CASE"));
			matchCaseCheckBox.setLayoutData(gd);
			matchCaseCheckBox.addKeyListener(ka);
			matchCaseCheckBox.setSelection(config.getProps().getBoolean("view.search.simple.matchCase"));
			// matchCaseCheckBox.addSelectionListener(new SelectionAdapter() {
			// public void widgetSelected(SelectionEvent e) {
			// matchCase = matchCaseCheckBox.getSelection();
			// }
			// });

			transScopeSA.widgetSelected(null); // this call causes some checkbox states to be set
			currPageSA.widgetSelected(null); // this call causes all check box states to be set (disabled/enabled)
		}
	*/
	protected void gotoNextAya() {
		if (ayaSelector.getSelectionIndex() < ayaSelector.getItemCount() - 1) {
			ayaSelector.select(ayaSelector.getSelectionIndex() + 1);
			ayaChanged = true;
			apply();
		}
	}

	protected void gotoPrevAya() {
		if (ayaSelector.getSelectionIndex() > 0) {
			ayaSelector.select(ayaSelector.getSelectionIndex() - 1);
			ayaChanged = true;
			apply();
		}
	}

	protected void gotoNextSura() {
		if (suraSelector.getSelectionIndex() < suraSelector.getItemCount() - 1) {
			suraSelector.select(suraSelector.getSelectionIndex() + 1);
			onSuraChanged();
			apply();
		}
	}

	protected void gotoPrevSura() {
		if (suraSelector.getSelectionIndex() > 0) {
			suraSelector.select(suraSelector.getSelectionIndex() - 1);
			onSuraChanged();
			apply();
		}
	}

	protected void gotoNextJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(quranLoc);
		if (jp.getIndex() < 30) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() + 1);
			gotoSuraAya(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	protected void gotoPrevJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(quranLoc);
		if (jp.getIndex() > 1) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() - 1);
			gotoSuraAya(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	protected void gotoNextHizb() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(quranLoc);
		JuzProperties juz = QuranPropertiesUtils.getJuzOf(quranLoc);
		if (quad < 7) {
			QuranLocation newLoc = juz.getHizbQuarters()[quad + 1];
			gotoSuraAya(newLoc);
		} else if (juz.getIndex() < 30) {
			gotoNextJuz();
		}
	}

	protected void gotoPrevHizb() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(quranLoc);
		JuzProperties juz = QuranPropertiesUtils.getJuzOf(quranLoc);
		if (quad > 0) {
			QuranLocation newLoc = juz.getHizbQuarters()[quad - 1];
			gotoSuraAya(newLoc);
		} else if (juz.getIndex() > 1) {
			gotoSuraAya(QuranPropertiesUtils.getJuz(juz.getIndex() - 1).getHizbQuarters()[7]);
		}
	}

	void apply() {
		logger.info("Start updating view...");
		updateView();
		suraMap = QuranPropertiesUtils.getSuraPropsMap(suraSelector.getSelectionIndex() + 1);
		FormUtils.updateTable(suraTable, suraMap);
		logger.info("Updating view done.");
		suraChanged = false;
	}

	protected void gotoSuraAya(IQuranLocation loc) {
		gotoSuraAya(loc.getSura(), loc.getAya());
	}

	protected void gotoSuraAya(int sura, int aya) {
		if (sura <= 114 && sura >= 1) {
			suraSelector.select(sura - 1);
			onSuraChanged();
			int ayaCount = QuranProperties.getInstance().getSura(sura).getAyaCount();
			if (aya <= ayaCount && aya >= 1) {
				ayaSelector.select(aya - 1);
				ayaChanged = true;
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
		int sur = suraSelector.getSelectionIndex() + 1;
		if (sur != sura) { // user changed sura, should be fully updated
			gotoSuraAya(sura, aya);
		} else {
			int ayaCount = QuranProperties.getInstance().getSura(quranLoc.getSura()).getAyaCount();
			if (aya <= ayaCount && aya >= 1) {
				ayaSelector.select(aya - 1);
				ayaChanged = true;
				apply();
			} else {
				// illegal aya, will update view to the previous legal one
				updateView();
			}
		}
	}

	protected void updateView() {
		qmf.resetAudioMenuEnableState();
		final int aya = ayaSelector.getSelectionIndex() + 1;
		final int sura = suraSelector.getSelectionIndex() + 1;

		quranLoc = new QuranLocation(sura, aya);
		logger.info("Set location to " + quranLoc);
		config.getProps().setProperty("view.quranLoc", quranLoc.toString());

		qpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				// if (ayaChanged) {
				focusOnAya(quranBrowser, sura, aya);
				// }
				quranBrowser.removeProgressListener(this);
			}
		};
		tpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				// if (ayaChanged) {
				focusOnAya(transBrowser, sura, aya);
				// }
				transBrowser.removeProgressListener(this);
			}
		};
		if (updateQuran)
			updateQuranView();
		if (updateTrans)
			updateTransView();
	}

	private void updateTransView() {
		if (suraChanged) {
			try {
				transBrowser.addProgressListener(tpl);
				logger.info("Set translation location to " + quranLoc);
				transBrowser.setUrl(transUri = HtmlRepository.getTransUri(quranLoc.getSura(), quranLoc.getAya()));
			} catch (HtmlGenerationException e) {
				logger.log(e);
			}
		} else {
			focusOnAya(transBrowser, quranLoc.getSura(), quranLoc.getAya());
		}
	}

	private void focusOnAya(final Browser browser, final int sura, final int aya) {
		final String misc = getMiscOptions();
		// browser.execute("focusOnAya(" + sura + "," + aya + (misc == null ? "" : "," + misc) + ");");
		SwtBrowserUtils.trickyExecute(display, browser, "focusOnAya(" + sura + "," + aya
				+ (misc == null ? "" : "," + misc) + ");");
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
			if (suraChanged) {
				quranBrowser.addProgressListener(qpl);
				logger.info("Set Quran location to " + quranLoc);
				if (viewLayout == MIXED) {
					quranUri = HtmlRepository.getMixedUri(quranLoc.getSura(), quranLoc.getAya());
				} else if (viewLayout == MULTI_TRANS) {
					quranUri = HtmlRepository.getCustomMixedUri(quranLoc.getSura(), quranLoc.getAya());
				} else {
					quranUri = HtmlRepository.getQuranUri(quranLoc.getSura(), quranLoc.getAya());
				}
				quranBrowser.setUrl(quranUri);
			} else {
				focusOnAya(quranBrowser, quranLoc.getSura(), quranLoc.getAya());
			}
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	private void onSuraChanged() {
		ayaSelector.setItems(QuranPropertiesUtils.getSuraAyas(suraSelector.getSelectionIndex() + 1));
		ayaSelector.select(0);
		ayaChanged = false; // It must be set to true after ayaSelector.select
		suraChanged = true; // It must be set to false after apply()
	}

	private void advancedFind() {
		doPreFind();

		String str;
		if (advancedToggleMultiLine.getSelection())
			str = advancedSearchBox.getText();
		else
			str = advancedSearchCombo.getText();
		if ("".equals(str.trim()))
			return; // do nothing

		String indexDir = config.createQuranIndex();

		if (indexDir == null) {
			return;
		}

		str = str.trim();
		if (!"".equals(str)) {
			if (advancedSearchCombo.getItemCount() <= 0 || !str.equals(advancedSearchCombo.getItem(0)))
				advancedSearchCombo.add(str, 0);
			if (advancedSearchCombo.getItemCount() > 40)
				advancedSearchCombo.remove(40, advancedSearchCombo.getItemCount() - 1);

			logger.info("Search started: " + str);
			Date date1 = new Date();

			boolean relevance = advancedSearchOrderCombo.getSelectionIndex() == 0 ? true : false;
			qts = new QuranTextSearcher(indexDir, searchScope);
			qts.setSortResultOrder(relevance ? Sort.RELEVANCE : Sort.INDEXORDER);
			try {
				asr = qts.search(str);
			} catch (Exception e) {
				logger.implicitLog(e);
				MessageBoxUtils.showError("Advanced Search Error: " + e);
				return; // search failed
			}
			Date date2 = new Date();
			logger.info("Search for " + str + " finished; took " + (date2.getTime() - date1.getTime()) + " ms.");

			int _pc = asr.getResultPageCount();
			logger.debug("Search result has " + _pc + " pages.");
			if (_pc > 1) {
				searchPaginationComp.setVisible(true);
			} else {
				searchPaginationComp.setVisible(false);
				nextPageBut.setEnabled(true);
			}

			// reset spinner
			searchPaginationSpinner.setMaximum(_pc >= 1 ? _pc : 1);
			searchPaginationSpinner.setSelection(1);

			advancedFindGoto(0); // 0 means the first page
		}
	}

	private void find() {
		doPreFind();

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
			if (ats == null) {
				try {
					ats = new AdvancedTextSearch(QuranText.getSimpleTextInstance(), new SimpleSearchHighlighter(),
							new DefaultSearchScorer());
				} catch (Exception e) {
					logger.implicitLog(e);
					MessageBoxUtils.showError("Searcher failed to initialize:\n\t" + e);
					return; // search failed
				}
			}
			if (!searchScope.equals(ats.getSearchScope())) {
				ats.setSearchScope(searchScope);
			}
			ats.setSearchResultComparator(SearchResultComparatorFactory.getComparator((String) searchOrderCombo
					.getData(String.valueOf(sortBy))));
			sr = ats.search(str);
			Date date2 = new Date();
			logger.info("Search for " + str + " finished; took " + (date2.getTime() - date1.getTime()) + " ms.");

			int pageCount = asr.getResultPageCount();
			logger.debug("Search result has " + pageCount + " pages.");
			if (pageCount > 1) {
				searchPaginationComp.setVisible(true);
			} else {
				searchPaginationComp.setVisible(false);
				nextPageBut.setEnabled(true);
			}

			// reset spinner
			searchPaginationSpinner.setMaximum(pageCount >= 1 ? pageCount : 1);
			searchPaginationSpinner.setSelection(1);

			findGoto(0); // 0 = first page
		}
	}

	private void doPreFind() {
		qmf.setAudioMenuEnabled(false);
		qmf.resetAudioMenuStatus();
	}

	/**
	 * @param pageNo one-based page number. 0 means the first page.
	 */
	private void advancedFindGoto(int pageNo) {
		doPreFind();
		try {
			if (asr == null) {
				logger.error("Advanced search is not done yet!");
				return;
			}
			if (pageNo > asr.getResultPageCount()) {
				logger.error("No such page in results: " + pageNo);
				MessageBoxUtils.showError("No such page in search results: " + pageNo);
				return;
			}

			if (pageNo > 1) {
				advPrevPageBut.setEnabled(true);
			} else {
				advPrevPageBut.setEnabled(false);
			}
			if (pageNo < asr.getResultPageCount()) {
				advNextPageBut.setEnabled(true);
			} else {
				advNextPageBut.setEnabled(false);
			}

			pageNo = pageNo == 0 ? 1 : pageNo;
			logger.info("Navigate to page #" + pageNo + " of advanced search result.");
			Browser searchBrowser = viewLayout == TRANS_ONLY ? transBrowser : quranBrowser;
			searchBrowser.setUrl(HtmlRepository.getAdvancedSearchQuranUri(asr, pageNo - 1));
			ayaChanged = true;
			suraChanged = true;
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	/**
	 * @param pageNo one-based page number. 0 means the first page.
	 */
	private void findGoto(int pageNo) {
		doPreFind();
		try {
			if (sr == null) {
				logger.error("Search is not done yet!");
				return;
			}
			if (pageNo > sr.getResultPageCount()) {
				logger.error("No such page in results: " + pageNo);
				MessageBoxUtils.showError("No such page in search results: " + pageNo);
				return;
			}

			if (pageNo > 1) {
				prevPageBut.setEnabled(true);
			} else {
				prevPageBut.setEnabled(false);
			}
			if (pageNo < sr.getResultPageCount()) {
				nextPageBut.setEnabled(true);
			} else {
				nextPageBut.setEnabled(false);
			}

			pageNo = pageNo == 0 ? 1 : pageNo;
			logger.info("Navigate to page #" + pageNo + " of search result.");
			Browser searchBrowser = viewLayout == TRANS_ONLY ? transBrowser : quranBrowser;
			searchBrowser.setUrl(HtmlRepository.getAdvancedSearchQuranUri(sr, pageNo - 1));
			ayaChanged = true;
			suraChanged = true;
		} catch (HtmlGenerationException e) {
			logger.log(e);
		}
	}

	/*
		private void find() {
			doPreFind();

			String str = searchCombo.getText();
			Browser searchBrowser = viewLayout == TRANS_ONLY ? transBrowser : quranBrowser;

			if (!"".equals(str.trim()) && str.indexOf('$') == -1 && str.indexOf('\\') == -1) {

				if (searchCombo.getItemCount() <= 0 || !str.equals(searchCombo.getItem(0)))
					searchCombo.add(str, 0);
				if (searchCombo.getItemCount() > 40)
					searchCombo.remove(40, searchCombo.getItemCount() - 1);

				if (!currentPageCheckBox.getSelection()) {
					ayaChanged = true;
					suraChanged = true;
					try {
						if (searchTarget == QURAN_ONLY) {
							logger.info("Search on the Quran for \"" + str + "\" with diacritic match set to "
									+ matchDiacCheckBox.getSelection() + ", on scope: " + searchScope);
							searchBrowser.setUrl(quranUri = HtmlRepository.getSearchQuranUri(str, matchDiacCheckBox
									.getSelection(), searchScope));
						} else { // TRANS_ONLY
							logger.info("Search on the translation for \"" + str + "\" with diacritic match set to "
									+ matchDiacCheckBox.getSelection() + ", on scope: " + searchScope);
							searchBrowser.setUrl(quranUri = HtmlRepository.getSearchTransUri(str, matchDiacCheckBox
									.getSelection(), matchCaseCheckBox.getSelection(), searchScope));
						}
						logger.info("End of search.");
					} catch (HtmlGenerationException e) {
						logger.log(e);
					}
				} else {
					logger.info("Start searching the current page for \"" + str + "\" with diacritic match set to "
							+ matchDiacCheckBox.getSelection() + ".");
					searchBrowser.execute("find(\"" + str + "\", " + matchDiacCheckBox.getSelection() + ", "
							+ matchCaseCheckBox.getSelection() + ");");
					logger.info("End of search.");
				}
			}
		}
	*/
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
		// add form size and location
		List list = new ArrayList();
		Rectangle r = shell.getBounds();
		list.add(new Integer(r.x));
		list.add(new Integer(r.y));
		list.add(new Integer(r.width));
		list.add(new Integer(r.height));
		config.getProps().setProperty("view.shell.location", list);
		config.getProps().setProperty("view.shell.maximized", new Boolean(shell.getMaximized()));

		// syncing options
		config.getProps().setProperty("view.location.sync", String.valueOf(sync.getSelection()));

		// search props
		config.getProps().setProperty("view.search.tab", String.valueOf(searchTabFolder.getSelectionIndex()));
		//		config.getProps().setProperty("view.search.simple.searchQuranContent",
		//				String.valueOf(!transScopeBut.getSelection()));
		//		config.getProps().setProperty("view.search.simple.matchCase", String.valueOf(matchCaseCheckBox.getSelection()));
		//		config.getProps().setProperty("view.search.simple.matchDiacritics",
		//				String.valueOf(matchDiacCheckBox.getSelection()));
		//		config.getProps().setProperty("view.search.simple.currentPageOnly",
		//				String.valueOf(currentPageCheckBox.getSelection()));
		config.getProps().setProperty("view.search.advanced.sortBy",
				String.valueOf(advancedSearchOrderCombo.getSelectionIndex()));
		config.getProps().setProperty("view.search.advanced.multiLine",
				String.valueOf(advancedToggleMultiLine.getSelection()));

		// add search scopes
		// searchScopeList

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
					for (int i = 3; i < mis.length; i++) {
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

					if (c > 4) {
						for (int i = c - 1; i >= 4; i--) {
							mis[i].dispose();
						}
					}

					MenuItem item = null;
					for (Iterator iter = searchScopeList.iterator(); iter.hasNext();) {
						SearchScope ss = (SearchScope) iter.next();
						item = addNewScopeMenuItem(scopeMenu, ss);
					}

					MenuItem wholeQuranItem = scopeMenu.getItems()[3];
					if (item != null && selectedIndex != -1) {
						MenuItem selItem = scopeMenu.getItems()[4 + selectedIndex];
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

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}
}

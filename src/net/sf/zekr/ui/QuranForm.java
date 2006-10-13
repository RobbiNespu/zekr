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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.runtime.HtmlRepository;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchScope;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Table;

/**
 * Main Zekr form. This class contains all the Zekr main screen, execpt menus which are in
 * <code>QuranFormMenuFactory</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranForm extends BaseForm {
	// Form widgets:
	private Display display;
	private Shell shell;
	private Composite body;
	private Browser quranBrowser;
	private Browser transBrowser;
	private Combo suraSelector;
	private Combo ayaSelector;
	private Label suraLabel;
	private Label ayaLabel;
	private Combo searchText;
	private Button applyButton;
	private Button searchButton;
	private Button searchArrowBut;
	private Button quranScopeBut;
	private Button transScopeBut;
	private Button sync;
	private Button matchDiacCheckBox;
	private Button currentPageCheckBox;
	private Button matchCaseCheckBox;
	private Table suraTable;
	private Map suraMap;
	private Group navigationGroup;
	private Group searchGroup;
	private Group navGroup;
	private Group leftGroup;
	private Group detailGroup;
	private SashForm sashForm;
	private Menu searchMenu;

	private ProgressAdapter qpl, tpl;
	private String title;

	// These 6 properties should be package-private
	int viewLayout;
	static final int MIXED = 1;
	static final int SEPARATE = 2;
	static final int QURAN_ONLY = 3;
	static final int TRANS_ONLY = 4;
	static final String NAV_BUTTON = "NAV_BUTTON";

	private int searchTarget;
	/** match case behavior for search */
	private boolean matchCase;

	private final String FORM_ID = "QURAN_FORM";

	private final Logger logger = Logger.getLogger(this.getClass());

	private QuranProperties quranProp;

	/** Specifies whether aya selector changed since a sura was selected. */
	protected boolean ayaChanged;

	/** Specifies whether sura selector changed for making a new sura view. */
	protected boolean suraChanged;

	/** The current Quran URI loaded in the browser */
	private String quranUri;

	/** The current Translation URI loaded in the browser */
	private String transUri;

	private ApplicationConfig config;
	private boolean isClosed;

	private DisposeListener dl;
	private QuranLocation quranLoc;

	protected boolean updateTrans = true;
	protected boolean updateQuran = true;
	private QuranFormMenuFactory qmf;
	private boolean clearOnExit = false;
	private SearchScope searchScope;
	private List searchScopeList;
	
	private Listener globalKeyListener = new Listener() {
		public void handleEvent(Event event) {
			if (NAV_BUTTON.equals(event.widget.getData())) {
				boolean isRTL = ((langEngine.getSWTDirection() == SWT.RIGHT_TO_LEFT) && GlobalConfig.isWindows);
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
		matchCase = false;

		title = meaning("TITLE");
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(title);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		shell.setMenuBar((qmf = new QuranFormMenuFactory(this, shell)).getQuranFormMenu());

		ayaChanged = false;
		suraChanged = false;

		// reset search scope list
		searchScopeList = new ArrayList();
		makeFrame();

		setLayout(config.getViewProp("view.viewLayout")); // set the layout
		initQuranLocation();

		dl = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				close();
				if (!shell.isDisposed())
					shell.removeDisposeListener(dl);
			}
		};
		shell.addDisposeListener(dl);

		shell.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (REFRESH_VIEW.equals(e.data)) {
					reload();
				} else if (RECREATE_VIEW.equals(e.data)) {
					recreate();
				} else if (CLEAR_CACHE_ON_EXIT.equals(e.data)) {
					clearOnExit = true;
				}
			}
		});

		display.removeFilter(SWT.KeyDown,globalKeyListener);
		display.addFilter(SWT.KeyDown, globalKeyListener);
	}

	protected void reload() {
		try {
			config.getRuntime().recreateCache();
			suraChanged = true;
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
		body = new Composite(shell, langEngine.getSWTDirection());
		body.setLayout(pageLayout);

		Composite workPane = new Composite(body, SWT.NONE);
		gd = new GridData(GridData.FILL_VERTICAL);
		workPane.setLayoutData(gd);
		gl = new GridLayout(1, false);
		gl.marginHeight = gl.marginWidth = 2;
		workPane.setLayout(gl);

		Composite bgroup = new Composite(body, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		bgroup.setLayoutData(gd);
		fl = new FillLayout(SWT.VERTICAL);
		fl.marginHeight = fl.marginWidth = 2;
		bgroup.setLayout(fl);

		Composite browsers = new Group(bgroup, SWT.NONE);
		fl = new FillLayout(SWT.VERTICAL);
		browsers.setLayout(fl);

		sashForm = new SashForm(browsers, SWT.SMOOTH | SWT.VERTICAL);
		sashForm.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sashForm.SASH_WIDTH = 3;

		quranBrowser = new Browser(sashForm, SWT.NONE);
		fl = new FillLayout(SWT.VERTICAL);
		fl.marginHeight = 2;
		quranBrowser.setLayout(fl);

		StatusTextListener stl = new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				if (event.text != null && !"".equals(event.text)) {
					doBrowserCallback(event.text);
				}
			}

			private void doBrowserCallback(String message) {
				if (message.startsWith("ZEKR::")) {
					quranBrowser.execute("window.status='';"); // clear the status text
					if (transBrowser != null)
						transBrowser.execute("window.status='';"); // clear the status text

					if (message.substring(6, 10).equals("GOTO")) {
						int sura = Integer.parseInt(message.substring(message.indexOf(' '),
								message.indexOf('-')).trim());
						int aya = Integer.parseInt(message.substring(message.indexOf('-') + 1,
								message.indexOf(';')).trim());
						logger.info("Goto (" + sura + ", " + aya + ")");
						gotoSuraAya(sura, aya);
					} else if (message.substring(6, 11).equals("NAVTO")) {
						int sura = Integer.parseInt(message.substring(message.indexOf(' '),
								message.indexOf('-')).trim());
						int aya = Integer.parseInt(message.substring(message.indexOf('-') + 1,
								message.indexOf(';')).trim());
						logger.info("Goto (" + aya + ")");
						gotoAya(sura, aya);
					} else if (message.substring(6, 11).equals("TRANS")) {
						int sura = Integer.parseInt(message.substring(message.indexOf(' '),
								message.indexOf('-')).trim());
						int aya = Integer.parseInt(message.substring(message.indexOf('-') + 1,
								message.indexOf(';')).trim());
						PopupBox pe = null;
						if (searchTarget == QURAN_ONLY) {
							logger.info("Show translation: (" + sura + ", " + aya + ")");
							TranslationData td = config.getTranslation().getDefault();
							td.load(); // make sure that translation is loaded.
							pe = new PopupBox(shell, langEngine.getMeaning("TRANSLATION"), td.get(sura, aya),
									FormUtils.toSwtDirection(td.direction));
						} else {
							logger.info("Show quran: (" + sura + ", " + aya + ")");
							try {
								pe = new PopupBox(shell, langEngine.getMeaning("QURAN"), QuranText
										.getInstance().get(sura, aya), SWT.RIGHT_TO_LEFT);
							} catch (IOException e) {
								logger.log(e);
							}
						}

						Point p = display.getCursorLocation();
						p.y += 15;
						int x = 300;
						pe.open(new Point(x, 100), new Point(p.x - x / 2, p.y));
					}
				}
			}
		};

		transBrowser = new Browser(sashForm, SWT.NONE);
		fl = new FillLayout(SWT.VERTICAL);
		transBrowser.setLayout(fl);

		quranBrowser.addStatusTextListener(stl);
		transBrowser.addStatusTextListener(stl);

		navGroup = new Group(workPane, SWT.NONE);
		navGroup.setText(langEngine.getMeaning("OPTION") + ":");
		gl = new GridLayout(2, false);
		navGroup.setLayout(gl);
		navGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		suraLabel = new Label(navGroup, SWT.NONE);
		suraLabel.setText(langEngine.getMeaning("SURA") + ":");

		suraSelector = new Combo(navGroup, SWT.READ_ONLY);
		ayaSelector = new Combo(navGroup, SWT.READ_ONLY);

		suraSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
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
		ayaLabel.setText(langEngine.getMeaning("AYA") + ":");

		ayaSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING));
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
		sync.setText(langEngine.getMeaning("SYNCHRONOUS"));

		applyButton = new Button(navGroup, SWT.NONE);
		applyButton.setData(NAV_BUTTON);
		gd = new GridData(GridData.FILL_HORIZONTAL);

		applyButton.setLayoutData(gd);
		applyButton.setText(langEngine.getMeaning("SHOW"));
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

		int style = SWT.PUSH;
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

		int l = langEngine.getSWTDirection();

		// isRTL is only applicable for Windows
		boolean isRTL = ((l == SWT.RIGHT_TO_LEFT) && GlobalConfig.isWindows);

		Image prevSuraImg = new Image(display, isRTL ? resource.getString("icon.nextNext")
				: resource.getString("icon.prevPrev"));
		Image prevAyaImg = new Image(display, isRTL ? resource.getString("icon.next")
				: resource.getString("icon.prev"));
		Image nextAyaImg = new Image(display, isRTL ? resource.getString("icon.prev")
				: resource.getString("icon.next"));
		Image nextSuraImg = new Image(display, isRTL ? resource.getString("icon.prevPrev")
				: resource.getString("icon.nextNext"));

		prevSura.setImage(prevSuraImg);
		prevAya.setImage(prevAyaImg);
		nextAya.setImage(nextAyaImg);
		nextSura.setImage(nextSuraImg);

		prevSura.setToolTipText(langEngine.getMeaning("PREV_SURA"));
		prevAya.setToolTipText(langEngine.getMeaning("PREV_AYA"));
		nextAya.setToolTipText(langEngine.getMeaning("NEXT_AYA"));
		nextSura.setToolTipText(langEngine.getMeaning("NEXT_SURA"));

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
		detailGroup.setText(langEngine.getMeaning("DETAILS") + ":");
		gl = new GridLayout(1, true);
		detailGroup.setLayout(gl);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		detailGroup.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		suraMap = QuranPropertiesUtils.getSuraPropMap(suraSelector.getSelectionIndex() + 1);
		suraTable = FormUtils.getTableForMap(detailGroup, suraMap, langEngine.getMeaning("NAME"), langEngine
				.getMeaning("VALUE"), 70, 70, gd, SWT.NONE);

		searchGroup = new Group(workPane, SWT.NONE);
		searchGroup.setText(langEngine.getMeaning("SEARCH"));
		gl = new GridLayout(2, false);
		searchGroup.setLayout(gl);
		gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		searchGroup.setLayoutData(gd);

		searchText = new Combo(searchGroup, SWT.DROP_DOWN);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setVisibleItemCount(8);
		searchText.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				find();
			}
		});

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;

		Composite searchButComp = new Composite(searchGroup, SWT.NONE);
		searchButComp.setLayout(gl);

		gd = new GridData(GridData.FILL_HORIZONTAL);

		searchButton = new Button(searchButComp, SWT.PUSH);
		searchButton.setText(langEngine.getMeaning("SEARCH"));
		searchButton.setLayoutData(gd);
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				find();
			}
		});

		// search option button
		gd = new GridData(GlobalConfig.isLinux ? GridData.FILL_BOTH : GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = -1;

		searchArrowBut = new Button(searchButComp, SWT.TOGGLE);
		searchMenu = createSearchScopeMenu();
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
		final Composite searchScopeComp = new Composite(searchGroup, SWT.NONE);
		searchScopeComp.setLayoutData(gd);
		searchScopeComp.setLayout(new FillLayout());

		quranScopeBut = new Button(searchScopeComp, SWT.RADIO);
		quranScopeBut.setText(langEngine.getMeaning("QURAN"));
		quranScopeBut.setSelection(true);
		transScopeBut = new Button(searchScopeComp, SWT.RADIO);
		transScopeBut.setText(langEngine.getMeaning("TRANSLATION"));
		transScopeBut.addSelectionListener(new SelectionAdapter() {
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
			};
		});

		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		currentPageCheckBox = new Button(searchGroup, SWT.CHECK);
		currentPageCheckBox.setText(meaning("CURRENT_PAGE"));
		currentPageCheckBox.setLayoutData(gd);
		currentPageCheckBox.addKeyListener(ka);
		currentPageCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (currentPageCheckBox.getSelection()) {
					searchArrowBut.setEnabled(false);
					quranScopeBut.setEnabled(false);
					transScopeBut.setEnabled(false);
				} else {
					searchArrowBut.setEnabled(true);
					quranScopeBut.setEnabled(true);
					transScopeBut.setEnabled(true);
				}
			};
		});

		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		matchDiacCheckBox = new Button(searchGroup, SWT.CHECK);
		matchDiacCheckBox.setText(langEngine.getMeaning("MATCH_DIACRITIC"));
		matchDiacCheckBox.setLayoutData(gd);
		matchDiacCheckBox.addKeyListener(ka);
		matchDiacCheckBox.pack();

		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		matchCaseCheckBox = new Button(searchGroup, SWT.CHECK);
		matchCaseCheckBox.setText(langEngine.getMeaning("MATCH_CASE"));
		matchCaseCheckBox.setLayoutData(gd);
		matchCaseCheckBox.addKeyListener(ka);
		matchCaseCheckBox.pack();
		matchCaseCheckBox.setEnabled(false);
		matchCaseCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				matchCase = matchCaseCheckBox.getSelection();
			}
		});

	}

	private void gotoNextAya() {
		if (ayaSelector.getSelectionIndex() < ayaSelector.getItemCount() - 1) {
			ayaSelector.select(ayaSelector.getSelectionIndex() + 1);
			ayaChanged = true;
			apply();
		}
	}

	private void gotoPrevAya() {
		if (ayaSelector.getSelectionIndex() > 0) {
			ayaSelector.select(ayaSelector.getSelectionIndex() - 1);
			ayaChanged = true;
			apply();
		}
	}

	private void gotoNextSura() {
		if (suraSelector.getSelectionIndex() < suraSelector.getItemCount() - 1) {
			suraSelector.select(suraSelector.getSelectionIndex() + 1);
			onSuraChanged();
			apply();
		}
	}

	private void gotoPrevSura() {
		if (suraSelector.getSelectionIndex() > 0) {
			suraSelector.select(suraSelector.getSelectionIndex() - 1);
			onSuraChanged();
			apply();
		}
	}

	void apply() {
		logger.info("Start updating view...");
		updateView();
		suraMap = QuranPropertiesUtils.getSuraPropMap(suraSelector.getSelectionIndex() + 1);
		FormUtils.updateTable(suraTable, suraMap);
		logger.info("Updating view done.");
		suraChanged = false;
	}

	private void gotoSuraAya(int sura, int aya) {
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
		final int aya = ayaSelector.getSelectionIndex() + 1;
		final int sura = suraSelector.getSelectionIndex() + 1;

		quranLoc = new QuranLocation(sura, aya);
		logger.info("Set location to " + quranLoc);
		config.getProps().setProperty("view.quranLoc", quranLoc.toString());

		qpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				if (ayaChanged) {
					quranBrowser.execute("focusOnAya(" + sura + "," + aya + ");");
				}
				removeProgressListener(qpl);
			}

			void removeProgressListener(ProgressListener pl) {
				quranBrowser.removeProgressListener(pl);
			}
		};
		tpl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				if (ayaChanged) {
					transBrowser.execute("focusOnAya(" + sura + "," + aya + ");");
				}
				removeProgressListener(tpl);
			}

			void removeProgressListener(ProgressListener pl) {
				transBrowser.removeProgressListener(pl);
			}
		};
		if (updateQuran)
			updateQuranView();
		if (updateTrans)
			updateTransView();
	}

	private void updateTransView() {
		if (suraChanged) {
			transBrowser.addProgressListener(tpl);
			logger.info("Set translation location to " + quranLoc);
			transBrowser.setUrl(transUri = HtmlRepository.getTransUri(quranLoc.getSura(), quranLoc.getAya()));
		} else {
			transBrowser.execute("focusOnAya(" + quranLoc.getSura() + "," + quranLoc.getAya() + ");");
		}
	}

	private void updateQuranView() {
		if (suraChanged) {
			quranBrowser.addProgressListener(qpl);
			logger.info("Set Quran location to " + quranLoc);
			if (viewLayout == MIXED)
				quranBrowser.setUrl(quranUri = HtmlRepository.getMixedUri(quranLoc.getSura(), quranLoc
						.getAya()));
			else
				quranBrowser.setUrl(quranUri = HtmlRepository.getQuranUri(quranLoc.getSura(), quranLoc
						.getAya()));
		} else {
			quranBrowser.execute("focusOnAya(" + quranLoc.getSura() + "," + quranLoc.getAya() + ");");
		}
	}

	private void onSuraChanged() {
		ayaSelector.setItems(QuranPropertiesUtils.getSuraAyas(suraSelector.getSelectionIndex() + 1));
		ayaSelector.select(0);
		ayaChanged = false; // It must be set to true after ayaSelector.select
		suraChanged = true; // It must be set to false after apply()
	}

	private void find() {
		String str = searchText.getText();
		if (searchText.getItemCount() <= 0 || !str.equals(searchText.getItem(0)))
			if (!"".equals(str))
				searchText.add(str, 0);
		if (searchText.getItemCount() > 40)
			searchText.remove(40, searchText.getItemCount() - 1);
		if (!"".equals(str.trim()) && str.indexOf('$') == -1 && str.indexOf('\\') == -1) {
			if (!currentPageCheckBox.getSelection()) {
				ayaChanged = true;
				suraChanged = true;
				if (searchTarget == QURAN_ONLY) {
					logger.info("Search the whole Quran for \"" + str + "\" with dicritic match set to "
							+ matchDiacCheckBox.getSelection()  + ", on scope: " + searchScope);
					quranBrowser.setUrl(quranUri = HtmlRepository.getSearchQuranUri(str, matchDiacCheckBox
							.getSelection(), searchScope));
				} else { // TRANS_ONLY
					logger.info("Search the whole translation for \"" + str
							+ "\" with dicritic match set to " + matchDiacCheckBox.getSelection() + ", on scope: " + searchScope);
					quranBrowser.setUrl(quranUri = HtmlRepository.getSearchTransUri(str, matchDiacCheckBox
							.getSelection(), matchCase, searchScope));
				}
				logger.info("End of search.");
			} else {
				logger.info("Start searching the current page for \"" + str
						+ "\" with diacritic match set to " + matchDiacCheckBox.getSelection() + ".");
				if (viewLayout != TRANS_ONLY)
					quranBrowser.execute("find(\"" + str + "\", " + matchDiacCheckBox.getSelection() + ");");
				else
					transBrowser.execute("find(\"" + str + "\", " + matchDiacCheckBox.getSelection() + ", "
							+ matchCaseCheckBox.getSelection() + ");");
				logger.info("End of search.");
			}
		}
	}

	void recreate() {
		logger.info("Recreating the form...");
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
			shell.setLocation(new Integer(l.get(0).toString()).intValue(), new Integer((String) l.get(1)
					.toString()).intValue());
			shell.setSize(new Integer((String) l.get(2).toString()).intValue(), new Integer((String) l.get(3)
					.toString()).intValue());
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
		if (layout.equals(ApplicationConfig.QURAN_ONLY_LAYOUT)) {
			sashForm.setMaximizedControl(quranBrowser);
			viewLayout = QURAN_ONLY;
			updateQuran = true;
			updateTrans = false;
		} else if (layout.equals(ApplicationConfig.TRANS_ONLY_LAYOUT)) {
			sashForm.setMaximizedControl(transBrowser);
			viewLayout = TRANS_ONLY;
			updateQuran = false;
			updateTrans = true;
		} else if (layout.equals(ApplicationConfig.SEPARATE_LAYOUT)) {
			if (viewLayout == SEPARATE) // if already is separate, balance weights
				sashForm.setWeights(new int[] { 1, 1 });
			sashForm.setMaximizedControl(null);
			viewLayout = SEPARATE;
			updateQuran = true;
			updateTrans = true;
		} else if (layout.equals(ApplicationConfig.MIXED_LAYOUT)) {
			sashForm.setMaximizedControl(quranBrowser);
			viewLayout = MIXED;
			updateQuran = true;
			updateTrans = false;
		}
	}

	private void updateSizeAndLocation() {
		List list = new ArrayList();
		Rectangle r = shell.getBounds();
		list.add(new Integer(r.x));
		list.add(new Integer(r.y));
		list.add(new Integer(r.width));
		list.add(new Integer(r.height));
		config.getProps().setProperty("view.shell.location", list);
		config.getProps().setProperty("view.shell.maximized", new Boolean(shell.getMaximized()));
	}

	public void close() {
		updateSizeAndLocation();
		config.updateFile();
		if (clearOnExit) {
			logger.info("Clear cache directory.");
			config.getRuntime().clearCache();
			clearOnExit = false;
		}
		logger.info("Disposing all resources...");
	}

	private Menu createSearchScopeMenu() {
		final Menu scopeMenu = new Menu(shell, SWT.POP_UP | langEngine.getSWTDirection());

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
		editItem.setText(langEngine.getMeaning("EDIT") + "...");
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
						// searchScope = null;
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
		int l = s.length();
		if (l > 35)
			s = s.substring(0, 35);
		item.setText(s);
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
		return langEngine.getMeaningById(FORM_ID, key);
	}

}

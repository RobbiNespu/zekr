/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 6, 2004
 */

package net.sf.zekr.ui;

import java.util.Map;

import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.dynamic.QuranHTMLRepository;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class QuranForm extends BaseForm {
	// Form widgets:
	private Composite body;
	private Browser quranBrowser;
	private Combo suraSelector;
	private Combo ayaSelector;
	private Label suraLabel;
	private Label ayaLabel;
	private Combo searchText;
	private Button applyButton;
	private Button searchButton;
	private Button sync;
	private Button match;
	private Button whole;
	private Table suraTable;
	private Map suraMap;
	private Group navigationGroup;
	private Group searchGroup;
	private Group navGroup;
	private Group leftGroup;
	private Group detailGroup;

	private final String ID = "QURAN_FORM";

	private final Logger logger = Logger.getLogger(this.getClass());

	private QuranProperties quranProp;
	private PropertyGenerator widgetProp;

	/** Specifies whether aya selector changed since a sura was selected. */
	protected boolean ayaChanged;

	/** Specifies whether sura selector changed for making a new sura view. */
	protected boolean suraChanged;

	/** The current URL loaded in the browser */
	private String url;

	/**
	 * Initialize the QuranForm.
	 * @param display
	 */
	public QuranForm(Display display) {
		this.display = display;

		widgetProp = new PropertyGenerator(config);
		quranProp = QuranProperties.getInstance();
		init();
	}

	protected void init() {
		title = langEngine.getMeaningById(ID, "TITLE");
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(title);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		shell.setMenuBar(new QuranFormMenuFactory(this, shell).getQuranFormMenu());
		ayaChanged = false;
		suraChanged = false;
		makeFrame();
	}

	/**
	 * This method allocates and adds proper widgets to the <b>QuranForm</b>.
	 */
	private void makeFrame() {
		GridData gridData;
		GridLayout gridLayout;
		
		FillLayout fl = new FillLayout(SWT.VERTICAL);
		shell.setLayout(fl);
		
		GridLayout pageLayout = new GridLayout();
		pageLayout.numColumns = 2;
		body = new Composite(shell, langEngine.getSWTDirection());
		body.setLayout(pageLayout);


		navGroup = new Group(body, SWT.NONE);
		navGroup.setText(langEngine.getMeaning("OPTION") + ":");
		gridLayout = new GridLayout(2, false);
		navGroup.setLayout(gridLayout);

		gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.widthHint = 170;
		navGroup.setLayoutData(gridData);

		quranBrowser = new Browser(body, SWT.BORDER);
		quranBrowser.setUrl(url = QuranHTMLRepository.getUrl(1, 0, true));
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 3;
		quranBrowser.setLayoutData(gridData);
		quranBrowser.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				if (event.text != null && !"".equals(event.text)) {
					doBrowserCallback(event.text);
				}
			}

			private void doBrowserCallback(String message) {
				if (message.startsWith("ZEKR::")) {
					quranBrowser.execute("window.status='';"); // clear the status text
					if (message.substring(6, 10).equals("GOTO")) {
						int sura = Integer.parseInt(message.substring(message.indexOf(' '), message.indexOf('-')).trim());
						int aya = Integer.parseInt(message.substring(message.indexOf('-') + 1, message.indexOf(';')).trim());
						logger.info("goto (" + sura + ", " + aya+ ")");
						setQuranView(sura, aya);
					}
				}
			}
		});


		suraLabel = new Label(navGroup, SWT.NONE);
		suraLabel.setText(langEngine.getMeaning("SURA") + ":");

		suraSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);
		ayaSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);

		suraSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		suraSelector.setItems(QuranPropertiesUtils.getIndexedSuraNames());
		suraSelector.setVisibleItemCount(10);
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
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		ayaSelector.setLayoutData(gridData);

		sync = new Button(navGroup, SWT.CHECK);
		sync.setText(langEngine.getMeaning("SYNCHRONOUS"));

		applyButton = new Button(navGroup, SWT.NONE);
		// FillLayout fillLayout = new FillLayout();
		// fillLayout.type = SWT.VERTICAL;
		gridData = new GridData(GridData.FILL_HORIZONTAL);

		applyButton.setLayoutData(gridData);
		applyButton.setText(langEngine.getMeaning("SHOW"));
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// quranBrowser.setText(QuranViewTemplate.transformSura(suraSelector.getSelectionIndex()));
				apply();
			}
		});

		Composite navComposite = new Composite(navGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 160;
		navComposite.setLayoutData(gridData);
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 2;

		gridLayout = new GridLayout(4, false);
		navComposite.setLayout(gridLayout);

		int style = SWT.PUSH | SWT.FLAT;
		Button prevSura = new Button(navComposite, style);
		Button prevAya = new Button(navComposite, style);
		Button nextAya = new Button(navComposite, style);
		Button nextSura = new Button(navComposite, style);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		prevAya.setLayoutData(gridData);
		prevSura.setLayoutData(gridData);
		nextAya.setLayoutData(gridData);
		nextSura.setLayoutData(gridData);

		int l = langEngine.getSWTDirection();
		prevSura.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.nextNext") : resource.getString("icon.prevPrev")));
		prevAya.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.next") : resource.getString("icon.prev")));
		nextAya.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.prev") : resource.getString("icon.next")));
		nextSura.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.prevPrev") : resource.getString("icon.nextNext")));

		prevSura.setToolTipText(langEngine.getMeaning("PREV_SURA"));
		prevAya.setToolTipText(langEngine.getMeaning("PREV_AYA"));
		nextAya.setToolTipText(langEngine.getMeaning("NEXT_AYA"));
		nextSura.setToolTipText(langEngine.getMeaning("NEXT_SURA"));

		prevSura.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (suraSelector.getSelectionIndex() > 0) {
					suraSelector.select(suraSelector.getSelectionIndex() - 1);
					onSuraChanged();
					apply();
				}
			}
		});
		
		prevAya.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (ayaSelector.getSelectionIndex() > 0) {
					ayaSelector.select(ayaSelector.getSelectionIndex() - 1);
					ayaChanged = true;
					apply();
				}
			}
		});
		
		nextAya.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (ayaSelector.getSelectionIndex() < ayaSelector.getItemCount() - 1) {
					ayaSelector.select(ayaSelector.getSelectionIndex() + 1);
					ayaChanged = true;
					apply();
				}
			}
		});
		
		nextSura.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (suraSelector.getSelectionIndex() < suraSelector.getItemCount() - 1) {
					suraSelector.select(suraSelector.getSelectionIndex() + 1);
					onSuraChanged();
					apply();
				}
			}
		});
		
		detailGroup = new Group(body, SWT.NONE);
		detailGroup.setText(langEngine.getMeaning("DETAILS") + ":");
		gridLayout = new GridLayout(1, true);
		detailGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.heightHint = 140;
		// gridData.widthHint = 100;
		detailGroup.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
		gridData.grabExcessVerticalSpace = true;
		suraMap = QuranPropertiesUtils.getSuraPropMap(suraSelector.getSelectionIndex() + 1);
		suraTable = FormUtils.getTableForMap(detailGroup, suraMap, langEngine.getMeaning("NAME"),
			langEngine.getMeaning("VALUE"), gridData);

		searchGroup = new Group(body, SWT.NONE);
		searchGroup.setText(langEngine.getMeaning("SEARCH"));
		gridLayout = new GridLayout(2, false);
		searchGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		searchGroup.setLayoutData(gridData);

		searchText = new Combo(searchGroup, SWT.DROP_DOWN | SWT.RIGHT_TO_LEFT);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				find();
			}
		});

		searchButton = new Button(searchGroup, SWT.PUSH);
		searchButton.setText(langEngine.getMeaning("SEARCH"));
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				find();
			}
		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		KeyAdapter ka = new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					find();
				}
			}
		};

		whole = new Button(searchGroup, SWT.CHECK);
		whole.setSelection(true);
		whole.setText(langEngine.getMeaning("WHOLE_QURAN"));
		whole.setLayoutData(gridData);
		whole.addKeyListener(ka);

		match = new Button(searchGroup, SWT.CHECK);
		match.setText(langEngine.getMeaning("MATCH_DIACRITIC"));
		match.setLayoutData(gridData);
		match.addKeyListener(ka);
	}

	void apply() {
		logger.info("Start updating view...");
		updateView();
		suraMap = QuranPropertiesUtils.getSuraPropMap(suraSelector.getSelectionIndex() + 1);
		FormUtils.updateTable(suraTable, suraMap);
		logger.info("Updating view done.");
		suraChanged = false;
	}
	
	private void setQuranView(int sura, int aya) {
		suraSelector.select(sura - 1);
		onSuraChanged();
		ayaSelector.select(aya - 1);
		ayaChanged = true;
		apply();
	}

	private ProgressAdapter pl;
	private void updateView() {
		final int aya = ayaSelector.getSelectionIndex() + 1;
		final int sura = suraSelector.getSelectionIndex() + 1;

		pl = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				if (ayaChanged) {
					quranBrowser.execute("focusOnAya('" + sura + "_" + aya + "');");
				}
				removeProgressListener(pl);
			}
			void removeProgressListener(ProgressListener pl) {
				quranBrowser.removeProgressListener(pl);
			}
		};
		if (suraChanged) {
			quranBrowser.addProgressListener(pl);
			quranBrowser.setUrl(url = QuranHTMLRepository.getUrl(sura, 0));
		} else {
//			System.out.println("ayaChanged: " + ayaChanged);
//			quranBrowser.addProgressListener(pl);
//			quranBrowser.execute("window.location.reload(false);");
//			quranBrowser.execute("window.location.href = window.location.href;");
			quranBrowser.execute("focusOnAya('" + sura + "_" + aya + "');");
		}
//		quranBrowser.setUrl(QuranHTMLRepository.getUrl(sura, ayaChanged ? aya - 1 : 0));
	}

	private void onSuraChanged() {
		ayaSelector.setItems(QuranPropertiesUtils.getSuraAyas(suraSelector.getSelectionIndex() + 1));
		ayaSelector.select(0);
		ayaChanged = false; // It must be set to true after ayaSelector.select
		suraChanged = true; // It must be set to false after apply()
	}

	void find() {
		String str = searchText.getText();
		if (searchText.getItemCount() <= 0 || !str.equals(searchText.getItem(0))) 
			searchText.add(str, 0);
		if (searchText.getItemCount() > 40)
			searchText.remove(40, searchText.getItemCount() - 1);
		if (!"".equals(str.trim()) && str.indexOf('$') == -1 && str.indexOf('\\') == -1) {
			if (whole.getSelection()) {
				ayaChanged = true;
				suraChanged = true;
				logger.info("Will search the whole Quran for \"" + str + "\" with dicritic match set to " + match.getSelection() + ".");
				quranBrowser.setUrl(url = QuranHTMLRepository.getSearchUrl(str, match.getSelection()));
				logger.info("End of search.");
			} else {
				logger.info("Start searching the current Quran view for \"" + str + "\" with diacritic match set to " + match.getSelection() + ".");
				quranBrowser.execute("find(\"" + str + "\", " + match.getSelection() + ");");
				logger.info("End of search.");
			}
		}
	}
	
	void recreate() {
		Point size = shell.getSize();
		Point loc = shell.getLocation();
		boolean mx = shell.getMaximized();
		shell.close();
		init();
		shell.setLocation(loc);
		shell.setMaximized(mx);
		show(size.x, size.y);
		loopForever();
	}

	/**
	 * Shows maximized Quran shell.
	 */
	public void show() {
		shell.setMaximized(true);
		shell.open();
	}

	public void show(int width, int height) {
		shell.setSize(width, height);
		shell.open();
	}

	public void loopForever() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void dispose() {
		logger.info("Disposing all resources...");
		shell.dispose();
		logger.info("Zekr will be closed now.");
	}

	public Browser getQuranBrowser() {
		return quranBrowser;
	}

	public void setQuranBrowser(Browser quranBrowser) {
		this.quranBrowser = quranBrowser;
	}
	
	public String getUrl() {
		return url;
	}

}

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

import org.apache.velocity.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranForm extends BaseForm {
	// Form widgets:
	private Composite body;
	private Browser quranBrowser;
	private Combo sooraSelector;
	private Combo ayaSelector;
	private Label sooraLabel;
	private Label ayaLabel;
	private Text searchText;
	private Button applyButton;
	private Button searchButton;
	private Button sync;
	private Button match;
	private Button whole;
	private Table sooraTable;
	private Map sooraMap;
	private Group navigationGroup;
	private Group searchGroup;
	private Group navGroup;
	private Group leftGroup;
	private Group detailGroup;

	private final String ID = "QURAN_FORM";

	private final static Logger logger = Logger.getLogger(QuranForm.class);

	private QuranProperties quranProp;
	private PropertyGenerator widgetProp;

	// Other global variables
	/**
	 * Specifies whether aya selector changed since a soora was selected.
	 */
	private boolean ayaChanged = false;

	/**
	 * @param display
	 */
	public QuranForm(Display display) {
		this.display = display;

//		config = ApplicationConfig.getInsatnce();
//		langEngine = config.getLanguageEngine();
		widgetProp = new PropertyGenerator(config);
		quranProp = QuranProperties.getInstance();
		init();
	}

	protected void init() {
		// langEngine.setScope(LanguageEngineNaming.FORM);
		title = langEngine.getMeaningById(ID, "TITLE");
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText(title);
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		shell.setMenuBar(new QuranFormMenuFactory(this, shell).getQuranFormMenu());

		makeFrame();
	}

	/**
	 * This method allocates and adds proper widgets to the <b>QuranShell </b>
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
//		shell.setLayout(pageLayout);


		navGroup = new Group(body, SWT.NONE);
		navGroup.setText(langEngine.getMeaning("OPTION") + ":");
		gridLayout = new GridLayout(2, false);
		navGroup.setLayout(gridLayout);

		gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.widthHint = 170;
		navGroup.setLayoutData(gridData);

		quranBrowser = new Browser(body, SWT.BORDER);
		quranBrowser.setUrl(QuranHTMLRepository.getUrl(1, 0, true));
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 3;
		quranBrowser.setLayoutData(gridData);


		sooraLabel = new Label(navGroup, SWT.NONE);
		sooraLabel.setText(langEngine.getMeaning("SOORA") + ":");

		sooraSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);
		ayaSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);

		sooraSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		sooraSelector.setItems(QuranPropertiesUtils.getSooraNames());
		sooraSelector.setVisibleItemCount(10);
		sooraSelector.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onSooraChanged();
				if (sync.getSelection())
					apply();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				onSooraChanged();
				apply();
			}
		});
		sooraSelector.select(0);

		ayaLabel = new Label(navGroup, SWT.NONE);
		ayaLabel.setText(langEngine.getMeaning("AYA") + ":");

		ayaSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING));
		ayaSelector.setItems(QuranPropertiesUtils.getSooraAyas(1));
		ayaSelector.setVisibleItemCount(10);
		ayaSelector.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ayaChanged = true;
				if (sync.getSelection())
					apply();
				// quranBrowser.setUrl(QuranHTMLRepository.getUrl(sooraSelector.getSelectionIndex()
				// + 1,
				// ayaSelector.getSelectionIndex() + 1));
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
				// quranBrowser.setText(QuranViewTemplate.transformSoora(sooraSelector.getSelectionIndex()));
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
		Button prevSoora = new Button(navComposite, style);
		Button prevAya = new Button(navComposite, style);
		Button nextAya = new Button(navComposite, style);
		Button nextSoora = new Button(navComposite, style);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		prevAya.setLayoutData(gridData);
		prevSoora.setLayoutData(gridData);
		nextAya.setLayoutData(gridData);
		nextSoora.setLayoutData(gridData);

		int l = langEngine.getSWTDirection();
		prevSoora.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.nextNext") : resource.getString("icon.prevPrev")));
		prevAya.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.next") : resource.getString("icon.prev")));
		nextAya.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.prev") : resource.getString("icon.next")));
		nextSoora.setImage(new Image(display, l == SWT.RIGHT_TO_LEFT ? resource.getString("icon.prevPrev") : resource.getString("icon.nextNext")));

		prevSoora.setToolTipText(langEngine.getMeaning("PREV_SOORA"));
		prevAya.setToolTipText(langEngine.getMeaning("PREV_AYA"));
		nextAya.setToolTipText(langEngine.getMeaning("NEXT_AYA"));
		nextSoora.setToolTipText(langEngine.getMeaning("NEXT_SOORA"));

		prevSoora.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (sooraSelector.getSelectionIndex() > 0) {
					sooraSelector.select(sooraSelector.getSelectionIndex() - 1);
					onSooraChanged();
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
		
		nextSoora.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (sooraSelector.getSelectionIndex() < sooraSelector.getItemCount() - 1) {
					sooraSelector.select(sooraSelector.getSelectionIndex() + 1);
					onSooraChanged();
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
		sooraMap = QuranPropertiesUtils.getSooraPropMap(sooraSelector.getSelectionIndex() + 1);
		sooraTable = FormUtils.getTableForMap(detailGroup, sooraMap, langEngine.getMeaning("NAME"),
			langEngine.getMeaning("VALUE"), gridData);

		searchGroup = new Group(body, SWT.NONE);
		searchGroup.setText(langEngine.getMeaning("SEARCH"));
		gridLayout = new GridLayout(2, false);
		searchGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		searchGroup.setLayoutData(gridData);
		searchText = new Text(searchGroup, SWT.BORDER | SWT.RIGHT_TO_LEFT);
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

		whole = new Button(searchGroup, SWT.CHECK);
		whole.setSelection(true);
		whole.setText(langEngine.getMeaning("WHOLE_QURAN"));
		whole.setLayoutData(gridData);

		match = new Button(searchGroup, SWT.CHECK);
		match.setText(langEngine.getMeaning("MATCH_DIACRITIC"));
		match.setLayoutData(gridData);
		
	}

	void apply() {
		updateView();
		sooraMap = QuranPropertiesUtils.getSooraPropMap(sooraSelector.getSelectionIndex() + 1);
		FormUtils.updateTable(sooraTable, sooraMap);
	}

	void updateView() {
		quranBrowser.setUrl(QuranHTMLRepository.getUrl(sooraSelector.getSelectionIndex() + 1,
			ayaChanged ? ayaSelector.getSelectionIndex() + 1 : 0));
	}

	private void onSooraChanged() {
		ayaSelector.setItems(QuranPropertiesUtils.getSooraAyas(sooraSelector.getSelectionIndex() + 1));
		ayaSelector.select(0);
		ayaChanged = false; // It must be set after ayaSelector.select
	}

	void find() {
		String str = searchText.getText();
		if (!"".equals(str.trim())) {
			if (whole.getSelection()) {
				quranBrowser.setUrl(QuranHTMLRepository.getSearchUrl(str));
			} else {
				quranBrowser.execute("find(\"" + str + "\", " + match.getSelection() + ");");
			}
		}
	}

	void recreate() {
		Point size = shell.getSize();
		shell.close();
		init();
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
		logger.info("Zekr will be closed now.\n");
	}

	public Browser getQuranBrowser() {
		return quranBrowser;
	}

	public void setQuranBrowser(Browser quranBrowser) {
		this.quranBrowser = quranBrowser;
	}

}

package net.sf.zekr.misc;

import java.io.IOException;
import java.util.Map;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
 * Main Zekr form.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */

public class TestQuranForm {
	Display display = new Display();
	protected Shell shell;
	protected String title;

	public static void main(String[] args) {
		TestQuranForm tqf = new TestQuranForm();
		tqf.show();
		tqf.loopEver();
	}

	public void loopEver() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	// Form widgets:
	private Composite body;
	private Browser quranBrowser;
	private Browser transBrowser;
	private Combo sSelector;
	private Combo aSelector;
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
	private SashForm sashForm;

	protected int viewLayout;
	protected static final int MIXED = 1;
	protected static final int SEPARATE = 2;
	protected static final int QURAN_ONLY = 3;
	protected static final int TRANS_ONLY = 4;

	public TestQuranForm() {
		init();
	}

	protected void init() {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("Test Form");
		makeFrame();
	}

	private void makeFrame() {
		GridData gridData;
		GridLayout gridLayout;

		FillLayout fl = new FillLayout(SWT.VERTICAL);
		shell.setLayout(fl);

		GridLayout pageLayout = new GridLayout(2, false);
		body = new Composite(shell, SWT.NONE);
		body.setLayout(pageLayout);

		Composite workPane = new Composite(body, SWT.NONE);
		fl = new FillLayout(SWT.HORIZONTAL);
		workPane.setLayout(fl);
		gridData = new GridData(GridData.FILL_VERTICAL);
		workPane.setLayoutData(gridData);
		gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = gridLayout.marginWidth = 2;
		workPane.setLayout(gridLayout);

		Composite bgroup = new Composite(body, SWT.NONE);
		gridData = new GridData(GridData.FILL_BOTH);
		bgroup.setLayoutData(gridData);
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
		fl.marginHeight = 2;
		quranBrowser.setLayout(fl);

		transBrowser = new Browser(sashForm, SWT.NONE);
		transBrowser.setLayout(fl);

		navGroup = new Group(workPane, SWT.NONE);
		navGroup.setText("Option");
		gridLayout = new GridLayout(2, false);
		navGroup.setLayout(gridLayout);
		navGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));

		suraLabel = new Label(navGroup, SWT.NONE);
		suraLabel.setText("Text1");

		sSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);
		aSelector = new Combo(navGroup, SWT.NONE | SWT.READ_ONLY);

		sSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		sSelector.setItems(new String[] {"Item1", "Item2"});
		sSelector.setVisibleItemCount(15);

		ayaLabel = new Label(navGroup, SWT.NONE);
		ayaLabel.setText("Text2");

		aSelector.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING));
		aSelector.setItems(new String[] {"Item1", "Item2", "Item3"});
		aSelector.moveBelow(ayaLabel);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		aSelector.setLayoutData(gridData);

		sync = new Button(navGroup, SWT.CHECK);
		sync.setText("Synch");

		applyButton = new Button(navGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);

		applyButton.setLayoutData(gridData);
		applyButton.setText("Show");

		Composite navComposite = new Composite(navGroup, SWT.NONE);
		gridLayout = new GridLayout(4, false);
		navComposite.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		navComposite.setLayoutData(gridData);

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

		prevSura.setText("<<");
		prevAya.setText("<");
		nextAya.setText(">");
		nextSura.setText(">>");

		searchGroup = new Group(workPane, SWT.NONE);
		searchGroup.setText("Search");
		gridLayout = new GridLayout(2, false);
		searchGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		searchGroup.setLayoutData(gridData);

		searchText = new Combo(searchGroup, SWT.DROP_DOWN | SWT.RIGHT_TO_LEFT);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setVisibleItemCount(8);

		searchButton = new Button(searchGroup, SWT.PUSH);
		searchButton.setText("Search");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		whole = new Button(searchGroup, SWT.CHECK);
		whole.setSelection(true);
		whole.setText("Short one!");
		whole.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		match = new Button(searchGroup, SWT.CHECK);
		match.setText("An exremely long long long text is put here");
		match.setLayoutData(gridData);
	}

	public void show() {
		shell.setMaximized(true);
		shell.open();
	}

	public void show(int width, int height) {
		shell.setSize(width, height);
		shell.open();
	}
}

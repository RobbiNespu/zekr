/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2006
 */
package net.sf.zekr.ui.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Options form GUI.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class OptionsForm {
	public static final String FORM_ID = "OPTIONS_FORM";
	private static final LanguageEngine lang = LanguageEngine.getInstance();
	private static final ResourceManager resource = ResourceManager.getInstance();
	private static final Logger logger = Logger.getLogger(OptionsForm.class);
	private static final ApplicationConfig config = ApplicationConfig.getInstance();
	private Display display;

	Shell parent, shell;
	Composite body;
	GridLayout gl;
	GridData gd;
	Composite nav, det;

	ToolItem general, view;

	Composite detGroup;
	Composite navGroup;

	private StackLayout sl;

	Composite generalTab, viewTab;
	private ThemeData td = config.getTheme().getCurrent();
	private Table table;

	private boolean refreshView;
	private boolean restart;

	private PropertiesConfiguration props = config.getProps();
	private Button showSplash;
	private Image image;
	private Combo langSelect;
	private Spinner spinner;
	private boolean pressOkToApply;
	private LanguagePack selectedLangPack;
	private ThemeData selectedTheme;
	private Combo themeSelect;
	private Button addBut;
	private Button delBut;
	private Button resizeablePane;
	private boolean rtl;

	private static final List packs = new ArrayList(lang.getLangPacks());
	private static final List themes = new ArrayList(config.getTheme().getAllThemes());

	private String[] suraNameType = new String[] { "arabic", "t9n", "t13n", "en-t9n", "en-t13n" };
	private Combo suraNameMode;

	public OptionsForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();

		rtl = (lang.getSWTDirection() == SWT.RIGHT_TO_LEFT);

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);
		shell.setLayout(new FillLayout());
		shell.setText(lang.getMeaning("OPTIONS"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.options16")),
				new Image(display, resource.getString("icon.options32")) });
		makeForm();
	}

	private void makeForm() {
		body = new Composite(shell, lang.getSWTDirection());
		gl = new GridLayout(2, false);
		body.setLayout(gl);

		nav = new Composite(body, SWT.NONE);
		det = new Composite(body, SWT.NONE);

		gd = new GridData(GridData.FILL_VERTICAL);
		nav.setLayoutData(gd);
		nav.setLayout(new FillLayout());

		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 400;
		gd.heightHint = 340;

		det.setLayoutData(gd);
		det.setLayout(new FillLayout());

		navGroup = new Group(nav, SWT.NONE);
		navGroup.setLayout(new RowLayout());

		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ToolItem ti = (ToolItem) e.widget;
				sl.topControl = (Control) ti.getData();
				detGroup.layout();
			}
		};
		detGroup = new Group(det, SWT.MULTI);
		sl = new StackLayout();
		detGroup.setLayout(sl);

		ToolBar bar = new ToolBar(navGroup, SWT.VERTICAL | SWT.FLAT);

		createGeneralTab();
		general = new ToolItem(bar, SWT.RADIO);
		general.setText(meaning("GENERAL_OPTIONS"));
		general.setImage(new Image(display, resource.getString("icon.general")));
		general.setSelection(true);
		general.setData(generalTab);
		general.addSelectionListener(sa);

		createViewTab();
		view = new ToolItem(bar, SWT.RADIO);
		view.setImage(new Image(display, resource.getString("icon.view")));
		view.setText(meaning("VIEW_OPTIONS"));
		view.setData(viewTab);
		view.addSelectionListener(sa);

		sl.topControl = generalTab;

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.END;
		Composite buttons = new Composite(body, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		buttons.setLayout(rl);

		buttons.setLayoutData(gd);

		Button ok = new Button(buttons, SWT.NONE);
		ok.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
		ok.pack();
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ok();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});

		Button cancel = new Button(buttons, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button apply = new Button(buttons, SWT.NONE);
		apply.setText(FormUtils.addAmpersand(lang.getMeaning("APPLY")));
		apply.pack();
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply(false);
			}
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		RowData rdApply = new RowData();
		// set all three OK, CANCEL, and APLLY buttons to the same length
		int buttonLength = FormUtils.buttonLength(80, ok, cancel, apply);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		rdApply.width = buttonLength;
		ok.setLayoutData(rdOk);
		cancel.setLayoutData(rdCancel);
		apply.setLayoutData(rdApply);

		shell.setDefaultButton(ok);
	}

	private void ok() {
		apply(true);
		boolean tmpOkToApply = pressOkToApply; // shell.close event uses pressOkToApply
		pressOkToApply = false;
		shell.close();
		if (tmpOkToApply) {
			config.setCurrentLanguage(selectedLangPack.id);
			config.setCurrentTheme(selectedTheme.id);
			try {
				config.getRuntime().recreateViewCache();
			} catch (IOException e) {
				logger.log(e);
			}
			EventUtils.sendEvent(EventProtocol.RECREATE_VIEW);
		}
	}

	private void apply(boolean fromOk) {
		logger.log("Update general model.");
		updateGeneralModel(fromOk);
		logger.log("Update view model.");
		saveViewModel();
		logger.log("Store configuration changes to disk.");
		try {
			config.saveConfig();
			if (refreshView) {
				logger.info("Store theme configuration to disk");
				Theme.save(td);
			}
		} catch (IOException e) {
			logger.log(e);
		}

		if (pressOkToApply && !fromOk) {
			MessageBoxUtils.showMessage(meaning("PRESS_OK_TO_APPLY"));
			// config.saveConfig();
			// createEvent(EventProtocol.CLEAR_CACHE_ON_EXIT);
		}
		if (refreshView)
			EventUtils.sendEvent(EventProtocol.REFRESH_VIEW);
	}

	private void updateGeneralModel(boolean fromOk) {
		selectedLangPack = (LanguagePack) packs.get(langSelect.getSelectionIndex());
		selectedTheme = (ThemeData) themes.get(themeSelect.getSelectionIndex());
		boolean isPaneResizeable = resizeablePane.getSelection();
		if (!config.getLanguage().getActiveLanguagePack().id.equals(selectedLangPack.id)
				|| !td.id.equals(selectedTheme.id)
				|| props.getBoolean("options.general.resizeableTaskPane") != isPaneResizeable) {
			pressOkToApply = true;
		}

		config.setShowSplash(showSplash.getSelection());
		props.setProperty("options.search.maxResult", "" + spinner.getSelection());
		props.setProperty("quran.sura.name", suraNameType[suraNameMode.getSelectionIndex()]);
		if (fromOk && pressOkToApply) {
			props.setProperty("lang.default", selectedLangPack.id);
			props.setProperty("theme.default", selectedTheme.id);
			props.setProperty("options.general.resizeableTaskPane", new Boolean(isPaneResizeable));
		}
	}

	private void saveViewModel() {
		if (!refreshView) {
			logger.info("Table is not changed!");
			return;
		}

		TableItem[] tis = table.getItems();
		td.props.clear();
		for (int i = 0; i < tis.length; i++) {
			td.props.put(tis[i].getText(0), tis[i].getText(1));
		}
	}

	private void createGeneralTab() {
		RowLayout rl;
		gl = new GridLayout(1, false);

		generalTab = new Composite(detGroup, SWT.NONE);
		generalTab.setLayout(gl);
		showSplash = new Button(generalTab, SWT.CHECK);
		showSplash.setText(meaning("SHOW_SPLASH"));
		showSplash.setSelection(config.getShowSplash());

		rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 10;
		Composite comp = new Composite(generalTab, SWT.NONE);
		comp.setLayout(rl);

		new Label(comp, SWT.NONE).setText(lang.getMeaning("LANGUAGE") + " :");

		langSelect = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		langSelect.setVisibleItemCount(8);
		String[] items = new String[lang.getLangPacks().size()];
		int s = 0;
		LanguagePack activeLang = config.getLanguage().getActiveLanguagePack();
		for (int i = 0; i < packs.size(); i++) {
			LanguagePack lp = (LanguagePack) packs.get(i);
			if (activeLang.id.equals(lp.id)) {
				s = i;
			}
			items[i] = lp.name + " - " + lp.localizedName + (rtl ? I18N.LRM + "" : "");
		}
		langSelect.setItems(items);
		langSelect.select(s);

		RowData rd = new RowData(24, 16);
		image = new Image(shell.getDisplay(), activeLang.getIconPath());
		final Canvas flag = new Canvas(comp, SWT.NONE);
		flag.setLayoutData(rd);
		flag.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});

		langSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				image = new Image(shell.getDisplay(), ((LanguagePack) packs.get(langSelect.getSelectionIndex()))
						.getIconPath());
				flag.redraw();
			}
		});

		rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 10;
		comp = new Composite(generalTab, SWT.NONE);
		comp.setLayout(rl);

		Label ct = new Label(comp, SWT.NONE);
		ct.setText(meaning("THEME_OPTIONS") + ":");
		// theme names should be in Roman characters
		themeSelect = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		Map themeMap = new LinkedHashMap();
		int selectedNum = 0;
		for (int i = 0; i < themes.size(); i++) {
			themeMap.put(((ThemeData) themes.get(i)).id, ((ThemeData) themes.get(i)).name + (rtl ? I18N.LRM + "" : ""));
			if (((ThemeData) themes.get(i)).id.equals(td.id))
				selectedNum = i;
		}
		themeSelect.setItems((String[]) themeMap.values().toArray(new String[0]));
		themeSelect.select(selectedNum);

		rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 10;
		comp = new Composite(generalTab, SWT.NONE);
		comp.setLayout(rl);

		new Label(comp, SWT.NONE).setText(meaning("MAX_SEARCH_RESULT") + " :");
		spinner = new Spinner(comp, SWT.BORDER);
		spinner.setMaximum(props.getInt("options.search.maxResult.maxSpinner"));
		spinner.setSelection(props.getInt("options.search.maxResult"));
		spinner.setMinimum(1);

		resizeablePane = new Button(generalTab, SWT.CHECK);
		resizeablePane.setText(meaning("RESIZEABLE_TASK_PANE"));
		resizeablePane.setSelection(config.getProps().getBoolean("options.general.resizeableTaskPane"));

		rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 10;
		comp = new Composite(generalTab, SWT.NONE);
		comp.setLayout(rl);

		new Label(comp, SWT.NONE).setText("Show sura names in :");

		String[] suraNameKey = new String[] { "Arabic", "Translation (if available)", "Transliteration (if available)",
				"English translation", "English transliteration" };
		suraNameMode = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);
		suraNameMode.setItems(suraNameKey);
		int modeSelect = 0;
		String selectedMode = config.getProps().getString("quran.sura.name");
		for (int i = 0; i < suraNameType.length; i++) {
			if (suraNameType[i].equals(selectedMode)) {
				modeSelect = i;
				break;
			}
		}
		suraNameMode.select(modeSelect);
	}

	private void createViewTab() {
		GridData gd = new GridData(GridData.FILL_BOTH);
		viewTab = new Composite(detGroup, SWT.NONE);
		viewTab.setLayout(new GridLayout(2, false));
		viewTab.setLayoutData(gd);

		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		Label l = new Label(viewTab, SWT.NONE);
		l.setText(meaning("THEME_SETTING") + ":");
		l.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		table = FormUtils.getTableFromMap(viewTab, td.props, lang.getMeaning("NAME"), lang.getMeaning("VALUE"), 140, 200,
				gd, SWT.LEFT_TO_RIGHT);

		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;

		Composite addDel = new Composite(viewTab, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		rl.spacing = 4;

		addDel.setLayout(rl);
		addDel.setLayoutData(gd);

		addBut = new Button(addDel, SWT.PUSH);
		addBut.setToolTipText(lang.getMeaning("ADD"));
		addBut.setImage(new Image(display, resource.getString("icon.add")));
		addBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String key = MessageBoxUtils.textBoxPrompt(meaning("NEW_KEY"), lang.getMeaning("QUESTION"));
				if (key == null || "".equals(key.trim()))
					return;
				logger.info("Add a table row");
				FormUtils.addRow(table, key, "");
				refreshView = true;
			}
		});
		RowData rd = new RowData();
		rd.width = 40;
		addBut.setLayoutData(rd);

		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.horizontalAlignment = SWT.LEFT;

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int c = table.getSelectionCount();
				if (c >= 1)
					delBut.setEnabled(true);
				else
					delBut.setEnabled(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(1));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(1, text.getText());
					}
				});
				newEditor.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						refreshView = true;
						Control oldEditor = editor.getEditor();
						if (oldEditor != null)
							oldEditor.dispose();
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 1);
			}
		});

		delBut = new Button(addDel, SWT.PUSH);
		delBut.setToolTipText(lang.getMeaning("REMOVE"));
		delBut.setImage(new Image(display, resource.getString("icon.remove")));
		delBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() > 0)
					if (MessageBoxUtils.showYesNoConfirmation(lang.getMeaning("YES_NO"), lang.getMeaning("REMOVE"))) {
						shell.forceActive();
						logger.info("Remove table row: " + table.getSelectionIndex());
						refreshView = true;
						Control oldEditor = editor.getEditor();
						if (oldEditor != null)
							oldEditor.dispose();
						table.remove(table.getSelectionIndex());
						if (table.getSelectionCount() <= 0)
							delBut.setEnabled(false);
					}
			}
		});
		delBut.setEnabled(false);

		rd = new RowData();
		rd.width = 40;
		delBut.setLayoutData(rd);

		// gd = new GridData(GridData.BEGINNING);
		// gd.widthHint = 40;
		// gd.horizontalAlignment = SWT.FILL;
		// del.setLayoutData(gd);
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}

	public void open() {
		shell.pack();
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}
}

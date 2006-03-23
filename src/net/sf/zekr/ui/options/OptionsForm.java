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

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.ui.EventProtocol;
import net.sf.zekr.ui.FormUtils;
import net.sf.zekr.ui.MessageBoxUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
	private LanguageEngine lang = LanguageEngine.getInstance();
	private final ResourceManager resource = ResourceManager.getInstance();
	private final Logger logger = Logger.getLogger(this.getClass());
	private final ApplicationConfig config = ApplicationConfig.getInstance();
	private Device display;

	Shell parent, shell;
	Composite body;
	GridLayout gl;
	GridData gd;
	Composite nav, det;

	ToolItem general, view;

	Composite detGroup;
	Composite navGroup;

	Composite generalTab, viewTab;
	FontDialog fontDialog;
	private ThemeData td = config.getTheme().getCurrent();
	private FontData fd;
	private String name;
	private int height;
	private int style;
	private Text transSample;
	private Group g;
	private Table table;

	private boolean tableChanged;

	private PropertiesConfiguration props = config.getProps();
	private Button showSplash;

	public OptionsForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.SHELL_TRIM | SWT.SYSTEM_MODAL);
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
				Composite comp = (Composite) ti.getData();
				((GridData) comp.getLayoutData()).exclude = !ti.getSelection();
				comp.setVisible(ti.getSelection());
				detGroup.layout();
			}
		};
		detGroup = new Group(det, SWT.MULTI);
		detGroup.setLayout(new GridLayout(2, false));

		ToolBar bar = new ToolBar(navGroup, SWT.VERTICAL | SWT.FLAT);

		createGeneralTab();
		general = new ToolItem(bar, SWT.RADIO);
		general.setText(lang.getMeaning("GENERAL"));
		general.setImage(new Image(display, resource.getString("icon.general")));
		general.setSelection(true);
		general.setData(generalTab);
		general.addSelectionListener(sa);

		createViewTab();
		view = new ToolItem(bar, SWT.RADIO);
		view.setImage(new Image(display, resource.getString("icon.view")));
		view.setText(lang.getMeaning("VIEW"));
		view.setData(viewTab);
		view.addSelectionListener(sa);

		((GridData) generalTab.getLayoutData()).exclude = false;

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
		RowData rd = new RowData();
		rd.width = 70;

		Button ok = new Button(buttons, SWT.NONE);
		ok.setText("&" + lang.getMeaning("OK"));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ok();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		ok.setLayoutData(rd);

		Button cancel = new Button(buttons, SWT.NONE);
		cancel.setText("&" + lang.getMeaning("CANCEL"));
		cancel.setLayoutData(rd);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button apply = new Button(buttons, SWT.NONE);
		apply.setText("&" + lang.getMeaning("APPLY"));
		apply.setLayoutData(rd);
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});
		shell.setDefaultButton(ok);
	}

	private void ok() {
		apply();
		shell.close();
	}

	private void apply() {
		logger.log("Update general model.");
		updateGeneralModel();
		logger.log("Update view model.");
		saveViewModel();
		logger.log("Store configuration changes to disk.");
		try {
			config.saveConfig();
			if (tableChanged) {
				logger.info("Store theme configuration to disk");
				Theme.save(td);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tableChanged) {
			// trivial event for communication with QuranForm.
			Event te = new Event();
			te.data = EventProtocol.REFRESH_VIEW;
			te.type = SWT.Traverse;
			shell.getParent().notifyListeners(SWT.Traverse, te);
		}
	}

	private void updateGeneralModel() {
		props.setProperty("options.general.showSplash",
				Boolean.toString(showSplash.getSelection()));
	}

	private void saveViewModel() {
		if (!tableChanged) {
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
		GridData gd = new GridData();
		gd.exclude = true;
		generalTab = new Composite(detGroup, SWT.NONE);
		generalTab.setLayout(new RowLayout(SWT.VERTICAL));
		generalTab.setLayoutData(gd);
		showSplash = new Button(generalTab, SWT.CHECK);
		showSplash.setText(meaning("SHOW_SPLASH"));
		showSplash.setSelection(props.getBoolean("options.general.showSplash"));
	}

	private void createViewTab() {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.exclude = true;
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
		table = FormUtils.getTableForMap(viewTab, td.props, lang.getMeaning("NAME"), lang.getMeaning("VALUE"), 140, 200, gd,
				SWT.LEFT_TO_RIGHT);
		Button add = new Button(viewTab, SWT.PUSH);
		add.setToolTipText(lang.getMeaning("ADD"));
		add.setImage(new Image(display, resource.getString("icon.add")));
		add.setSize(100, 30);
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String key = MessageBoxUtils.textBoxPrompt(meaning("NEW_KEY"), lang
						.getMeaning("QUESTION"));
				if (key == null || "".equals(key))
					return;
				logger.info("Add a table row");
				FormUtils.addRow(table, key, "");
				tableChanged = true;
			}
		});

		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		table.addSelectionListener(new SelectionAdapter() {
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
						tableChanged = true;
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

		Button del = new Button(viewTab, SWT.PUSH);
		del.setToolTipText(lang.getMeaning("DELETE"));
		del.setImage(new Image(display, resource.getString("icon.remove")));
		del.setSize(100, 30);
		del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() > 0)
					if (MessageBoxUtils.yesNoQuestion(lang.getMeaning("YES_NO"), lang
							.getMeaning("DELETE"))) {
						logger.info("Remove table row: " + table.getSelectionIndex());
						tableChanged = true;
						Control oldEditor = editor.getEditor();
						if (oldEditor != null)
							oldEditor.dispose();
						table.remove(table.getSelectionIndex());
					}
			}
		});
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

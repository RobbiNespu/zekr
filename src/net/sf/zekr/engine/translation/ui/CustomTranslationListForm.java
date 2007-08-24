/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 31, 2007
 */
package net.sf.zekr.engine.translation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CustomTranslationListForm {
	public static final String FORM_ID = "CONFIG_CUSTOM_TRANS";
	private static final LanguageEngine lang = LanguageEngine.getInstance();
	private static final ResourceManager resource = ResourceManager.getInstance();
	private static final ApplicationConfig config = ApplicationConfig.getInstance();
	private final static Logger logger = Logger.getLogger(CustomTranslationListForm.class);
	private Shell shell;
	private Shell parent;
	private Display display;
	private Button okBut;
	private Button cancelBut;
	private List sourceList, targetList;
	private Button addBut;
	private Button remBut;
	private Button upBut;
	private Button downBut;

	private java.util.List sourceData = new ArrayList();
	private java.util.List targetData = new ArrayList();

	public CustomTranslationListForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);

		shell.setLayout(new FillLayout());
		shell.setText(meaning("TITLE"));
		shell.setImage(new Image(display, resource.getString("icon.configTransList")));

		init();
	}

	private void init() {
		GridLayout gl = new GridLayout(1, false);
		Composite formBody = new Composite(shell, lang.getSWTDirection());
		formBody.setLayout(gl);

		gl = new GridLayout(4, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		Composite body = new Group(formBody, SWT.NONE);
		body.setLayoutData(gd);
		body.setLayout(gl);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		Label sourceLabel = new Label(body, SWT.NONE);
		sourceLabel.setText(meaning("AVAILABLE") + ":");
		sourceLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		Label targetLabel = new Label(body, SWT.NONE);
		targetLabel.setText(meaning("CURRENT") + ":");
		targetLabel.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 200;
		sourceList = new List(body, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		sourceList.setLayoutData(gd);
		sourceList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int cnt = sourceList.getSelectionCount();
				if (cnt > 0) {
					addBut.setEnabled(true);
				} else {
					addBut.setEnabled(false);
				}
			}
		});
		sourceList.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (sourceList.getSelectionCount() > 0) {
					add();
				}
			}
		});

		Collection transCollection = config.getTranslation().getAllTranslation();
		java.util.List customList = config.getCustomTranslationList();
		transCollection.removeAll(customList);
		String[] sourceItems = new String[transCollection.size()];

		int i = 0;
		for (Iterator iter = transCollection.iterator(); iter.hasNext(); i++) {
			TranslationData td = (TranslationData) iter.next();
			sourceItems[i] = td.localizedName + " - " + td.locale;
			sourceData.add(td.id);
		}
		sourceList.setItems(sourceItems);

		// isRTL is only applicable for Windows
		int d = lang.getSWTDirection();
		int direction = (GlobalConfig.isWindows ? d : SWT.LEFT_TO_RIGHT);

		gd = new GridData(SWT.CENTER);
		RowLayout rl = new RowLayout(SWT.VERTICAL);
		Composite addRemComp = new Composite(body, direction);
		addRemComp.setLayout(rl);
		addRemComp.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 90;
		addBut = new Button(addRemComp, SWT.PUSH);
		addBut.setText(lang.getMeaning("ADD") + " -> ");
		addBut.setLayoutData(rd);
		addBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				add();
			}
		});

		rd = new RowData();
		rd.width = 90;
		remBut = new Button(addRemComp, SWT.PUSH);
		remBut.setText("<- " + lang.getMeaning("REMOVE"));
		remBut.setLayoutData(rd);
		remBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				rem();
			}
		});

		addBut.setEnabled(false);
		remBut.setEnabled(false);

		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 200;
		targetList = new List(body, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		targetList.setLayoutData(gd);
		targetList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int cnt = targetList.getSelectionCount();
				if (cnt > 0) {
					if (cnt == 1) {
						upBut.setEnabled(true);
						downBut.setEnabled(true);
					} else {
						upBut.setEnabled(false);
						downBut.setEnabled(false);
					}
					remBut.setEnabled(true);
				} else {
					remBut.setEnabled(false);
				}
			}
		});
		targetList.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (targetList.getSelectionCount() > 0) {
					rem();
				}
			}
		});

		i = 0;
		String[] targetItems = new String[customList.size()];
		for (Iterator iter = customList.iterator(); iter.hasNext(); i++) {
			TranslationData td = (TranslationData) iter.next();
			targetItems[i] = td.localizedName + " - " + td.locale;
			targetData.add(td.id);
		}
		targetList.setItems(targetItems);

		gd = new GridData(SWT.CENTER);
		rl = new RowLayout(SWT.VERTICAL);
		Composite upDownComp = new Composite(body, SWT.NONE);
		upDownComp.setLayout(rl);
		upDownComp.setLayoutData(gd);

		upBut = new Button(upDownComp, SWT.PUSH);
		upBut.setToolTipText(lang.getMeaning("MOVE_UP"));
		upBut.setData("up");
		upBut.setImage(new Image(display, resource.getString("icon.goUp")));

		downBut = new Button(upDownComp, SWT.PUSH);
		downBut.setToolTipText(lang.getMeaning("MOVE_DOWN"));
		downBut.setData("down");
		downBut.setImage(new Image(display, resource.getString("icon.goDown")));

		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String s = (String) e.widget.getData();
				int index = targetList.getSelectionIndex();
				if (s.equals("up")) {
					if (index > 0) {
						String item = targetList.getItem(index);
						targetList.remove(index);
						targetList.add(item, index - 1);
						targetList.select(index - 1);

						String id = (String) targetData.remove(index);
						targetData.add(index - 1, id);
					}
				} else {
					if (index < targetList.getItemCount() - 1) {
						String item = targetList.getItem(index);
						targetList.remove(index);
						targetList.add(item, index + 1);
						targetList.select(index + 1);

						String id = (String) targetData.remove(index);
						targetData.add(index + 1, id);
					}
				}
			}
		};

		upBut.addSelectionListener(sa);
		downBut.addSelectionListener(sa);
		upBut.setEnabled(false);
		downBut.setEnabled(false);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		Label sep = new Label(formBody, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;
		rl = new RowLayout(SWT.HORIZONTAL);
		Composite butComposite = new Composite(formBody, SWT.NONE);
		butComposite.setLayout(rl);
		butComposite.setLayoutData(gd);

		rd = new RowData();
		rd.width = 80;
		Button ok = new Button(butComposite, SWT.NONE);
		ok.setText(FormUtils.addAmpersand( lang.getMeaning("OK")) );
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ok();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}
		});
		ok.setLayoutData(rd);

		rd = new RowData();
		rd.width = 80;
		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand( lang.getMeaning("CANCEL")) );
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		cancel.setLayoutData(rd);

		rd = new RowData();
		rd.width = 80;
		Button apply = new Button(butComposite, SWT.NONE);
		apply.setText(FormUtils.addAmpersand( lang.getMeaning("APPLY")) );
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});
		apply.setLayoutData(rd);
		shell.setDefaultButton(ok);
	}

	private void ok() {
		apply();
		shell.close();
	}

	private void apply() {
		config.setCustomTranslationList(targetData);
		if (config.getViewLayout().equals(ApplicationConfig.MULTI_TRANS_LAYOUT))
			EventUtils.sendEvent(EventProtocol.REFRESH_VIEW);
	}

	private void add() {
		int[] indices = sourceList.getSelectionIndices();
		for (int i = 0; i < indices.length; i++) {
			targetList.add(sourceList.getItem(indices[i]));
			targetData.add(sourceData.get(indices[i]));
		}
		for (int i = indices.length - 1; i >= 0; i--) {
			sourceData.remove(indices[i]);
		}
		sourceList.remove(indices);
		addBut.setEnabled(false);
	}

	private void rem() {
		int[] indices = targetList.getSelectionIndices();
		for (int i = 0; i < indices.length; i++) {
			sourceList.add(targetList.getItem(indices[i]));
			sourceData.add(targetData.get(indices[i]));
		}
		for (int i = indices.length - 1; i >= 0; i--) {
			targetData.remove(indices[i]);
		}
		targetList.remove(indices);
		remBut.setEnabled(false);
		upBut.setEnabled(false);
		downBut.setEnabled(false);
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}

	public void open() {
		shell.pack();
		if (shell.getSize().y < 250)
			shell.setSize(shell.getSize().x, 250);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}
}

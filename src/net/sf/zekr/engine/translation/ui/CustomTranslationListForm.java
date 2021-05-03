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

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * Customize multi-translation layout.
 * 
 * @author Mohsen Saboorian
 */
public class CustomTranslationListForm extends BaseForm {
	public static final String FORM_ID = "CONFIG_CUSTOM_TRANS";
	private Button okBut;
	private Button cancelBut;
	private List sourceList, targetList;
	private Button addBut;
	private Button remBut;
	private Button upBut;
	private Button downBut;

	private java.util.List<String> sourceData = new ArrayList<String>();
	private java.util.List<String> targetData = new ArrayList<String>();
	private boolean okayed = false;
	private boolean rtl;

	public CustomTranslationListForm(Shell parent) {
		try {
			this.parent = parent;
			display = parent.getDisplay();
			shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

			shell.setLayout(new FillLayout());
			shell.setText(meaning("TITLE"));
			shell.setImage(new Image(display, resource.getString("icon.configTransList")));

			rtl = lang.isRtl();

			init();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
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

		Collection<TranslationData> transCollection = config.getTranslation().getAllTranslation();
		java.util.List<TranslationData> customList = config.getCustomTranslationList();
		transCollection.removeAll(customList);
		String[] sourceItems = new String[transCollection.size()];

		int i = 0;
		String transNameMode = config.getProps().getString("trans.name.mode", "english");
		for (Iterator<TranslationData> iter = transCollection.iterator(); iter.hasNext(); i++) {
			TranslationData td = iter.next();
			sourceItems[i] = td.getName(transNameMode, rtl);
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

		addBut = new Button(addRemComp, SWT.PUSH);
		addBut.setText(meaning("ADD_CUSTOM") + " -> ");
		addBut.pack(); // we pack to set the length
		addBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				add();
			}
		});

		remBut = new Button(addRemComp, SWT.PUSH);
		remBut.setText(" <- " + meaning("REMOVE_CUSTOM"));
		remBut.pack(); // the same as for addBut
		remBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				rem();
			}
		});

		// let's set both buttons to the same length
		// (after pack-ing we read the width
		// and set the max width to both buttons)
		RowData rdAddBut = new RowData();
		RowData rdRemBut = new RowData();
		// give both buttons the same length
		int buttonLength = FormUtils.buttonLength(80, addBut, remBut);
		rdAddBut.width = buttonLength;
		rdRemBut.width = buttonLength;
		addBut.setLayoutData(rdAddBut);
		remBut.setLayoutData(rdRemBut);

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

		String[] targetItems = new String[customList.size()];
		for (i = 0; i < customList.size(); i++) {
			TranslationData td = customList.get(i);
			targetItems[i] = td.getName(transNameMode, rtl);
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

						String id = targetData.remove(index);
						targetData.add(index - 1, id);
					}
				} else {
					if (index < targetList.getItemCount() - 1) {
						String item = targetList.getItem(index);
						targetList.remove(index);
						targetList.add(item, index + 1);
						targetList.select(index + 1);

						String id = targetData.remove(index);
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

		Button ok = new Button(butComposite, SWT.NONE);
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

		Button cancel = new Button(butComposite, SWT.NONE);
		cancel.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		Button apply = new Button(butComposite, SWT.NONE);
		apply.setText(FormUtils.addAmpersand(lang.getMeaning("APPLY")));
		apply.pack();
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				apply();
			}
		});
		RowData rdOk = new RowData();
		RowData rdCancel = new RowData();
		RowData rdApply = new RowData();
		// set all three OK, CANCEL, and APLLY buttons to the same length
		buttonLength = FormUtils.buttonLength(80, ok, cancel, apply);
		rdOk.width = buttonLength;
		rdCancel.width = buttonLength;
		rdApply.width = buttonLength;
		ok.setLayoutData(rdOk);
		cancel.setLayoutData(rdCancel);
		apply.setLayoutData(rdApply);

		shell.setDefaultButton(ok);
	}

	private void ok() {
		apply();
		shell.close();
	}

	private void apply() {
		try {
			okayed = true;
			config.setCustomTranslationList(targetData);
			if (config.getViewLayout().equals(ApplicationConfig.MULTI_TRANS_LAYOUT))
				EventUtils.sendEvent(EventProtocol.REFRESH_VIEW);
		} catch (ZekrMessageException zme) {
			logger.error(zme);
			MessageBoxUtils.showError(zme);
		}
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

	public boolean isOkayed() {
		return okayed;
	}

	public void show() {
		shell.pack();
		if (shell.getSize().y < 250)
			shell.setSize(shell.getSize().x, 250);
		if (shell.getSize().y > 400)
			shell.setSize(shell.getSize().x, 400);
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}

	public String getFormId() {
		return FORM_ID;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 11, 2010
 */
package net.sf.zekr.ui;

import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.revelation.RevelationData;
import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * An advanced form for different 'goto' options.
 * 
 * @author Mohsen Saboorian
 */
public class GotoForm extends BaseForm implements FocusListener {
	private static final String FORM_ID = "GOTO_FORM";

	private final ApplicationConfig config = ApplicationConfig.getInstance();
	private final LanguageEngine lang = config.getLanguageEngine();
	private final ResourceManager resource = ResourceManager.getInstance();
	private final static Logger logger = Logger.getLogger(GotoForm.class);
	private final int CUSTOM_TRAVERSE = 1 << 20;

	private TabFolder tabFolder;
	private Combo searchCombo;
	private org.eclipse.swt.widgets.List suraAyaList;
	private Composite body;
	private Button cancelButton;
	private Group smartBody;
	private Group normalBody;
	private List<Control> focusList;
	private Spinner juzSpinner;
	private Spinner hizbQuarterSpinner;
	private Combo suraCombo, suraRevelCombo;
	private QuranForm quranForm;
	private Control lastFocusedControl;
	private Text suraAyaBox;
	private IUserView uvc;

	private Button gotoBut;

	private Button reviewBut;

	public GotoForm(Shell parent, QuranForm quranForm) {
		display = parent.getDisplay();
		this.parent = parent;
		this.quranForm = quranForm;
		uvc = config.getUserViewController();

		init();
	}

	private void init() {
		GridData gd;
		GridLayout gl;

		focusList = new ArrayList<Control>();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setText(meaning("TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.goto.form16")),
				new Image(display, resource.getString("icon.goto.form32")) });
		shell.setLayout(new FillLayout());
		shell.setData("FORM_ID", FORM_ID);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				config.getProps().setProperty("goto.focus", lastFocusedControl.getData("id"));
			}
		});

		body = new Composite(shell, SWT.NONE | lang.getSWTDirection());

		gl = new GridLayout(2, true);
		body.setLayout(gl);

		boolean reverseGotoForm = false;
		if (reverseGotoForm) {
			smartBody = new Group(body, SWT.NONE);
			normalBody = new Group(body, SWT.NONE);
		} else {
			normalBody = new Group(body, SWT.NONE);
			smartBody = new Group(body, SWT.NONE);
		}

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gl = new GridLayout(2, false);
		normalBody.setLayout(gl);
		normalBody.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gl = new GridLayout(2, false);
		smartBody.setLayout(gl);
		smartBody.setLayoutData(gd);

		createSmartBody();
		createNormalBody();
		createButtons();

		focusOnLastFocustElement();

		shell.setDefaultButton(gotoBut);
	}

	private void focusOnLastFocustElement() {
		String focusId = config.getProps().getString("goto.focus", "search");
		for (Control control : focusList) {
			Object id = control.getData("id");
			if (ObjectUtils.equals(focusId, id)) {
				control.forceFocus();
				break;
			}
		}
	}

	private void createSmartBody() {
		GridData gd;

		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		searchCombo = new Combo(smartBody, SWT.DROP_DOWN | SWT.NONE);
		searchCombo.setData("id", "search");
		searchCombo.setLayoutData(gd);
		searchCombo.setVisibleItemCount(6);
		searchCombo.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					e.doit = false;
				}
			}
		});
		searchCombo.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN || e.detail == CUSTOM_TRAVERSE) {
					selectTextInList();
					gotoSuraAya();
				} else if (e.keyCode == SWT.ARROW_DOWN && e.detail == SWT.TRAVERSE_ARROW_NEXT) {
					suraAyaList.setFocus();
					if (suraAyaList.getItemCount() > 0) {
						suraAyaList.select(0);
					}
				} else if (e.detail == SWT.TRAVERSE_ESCAPE) {
					resetSearchBox();
				}
			}
		});
		searchCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterList(searchCombo.getText());
			}
		});

		searchCombo.addFocusListener(this);
		focusList.add(searchCombo);

		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gd.verticalIndent = -1;
		cancelButton = new Button(smartBody, SWT.PUSH | SWT.FLAT);
		cancelButton.setImage(new Image(display, resource.getString("icon.cancel")));
		cancelButton.setLayoutData(gd);
		cancelButton.setToolTipText(lang.getMeaning("RESET"));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				resetSearchBox();
			}
		});

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		gd.horizontalSpan = 2;
		suraAyaList = new org.eclipse.swt.widgets.List(smartBody, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		suraAyaList.setData("id", "suraAyaList");
		suraAyaList.setLayoutData(gd);
		suraAyaList.setItems(getSuraNameList().toArray(new String[0]));
		suraAyaList.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (gotoSuraAya()) {
					close();
				}
			}
		});
		suraAyaList.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					gotoSuraAya();
				} else if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS && suraAyaList.getSelectionIndex() <= 0) {
					searchCombo.setFocus();
				} else if (e.detail == SWT.TRAVERSE_RETURN) {
					// selectTextInList();
					if (!gotoSuraAya()) {
						e.doit = false;
					}
				}
			}
		});
		suraAyaList.addFocusListener(this);
		focusList.add(suraAyaList);
	}

	private void createNormalBody() {
		GridData gd;
		IQuranLocation loc = uvc.getLocation();

		Label suraAyaLabel = new Label(normalBody, SWT.NONE);
		suraAyaLabel.setText(meaning("SURA_AYA"));

		gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
		gd.widthHint = 80;
		suraAyaBox = new Text(normalBody, SWT.BORDER);
		suraAyaBox.setLayoutData(gd);
		suraAyaBox.setData("id", "suraAyaBox");
		suraAyaBox.setText(loc.getSura() + ":" + loc.getAya());
		suraAyaBox.addFocusListener(this);
		focusList.add(suraAyaBox);
		suraAyaBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					String text = suraAyaBox.getText();
					if (StringUtils.isNotBlank(text)) {
						String[] suraAya = StringUtils.split(text, ":");
						if (suraAya.length == 1) {
							try {
								int sura = Integer.parseInt(suraAya[0]);
								gotoSura(sura);
							} catch (NumberFormatException e1) {
							}
						} else if (suraAya.length > 1) {
							try {
								int sura = Integer.parseInt(suraAya[0]);
								int aya = Integer.parseInt(suraAya[1]);
								IQuranLocation location = QuranPropertiesUtils.getLocation(sura, aya);
								if (location.isValid()) {
									navTo(location);
								}
							} catch (NumberFormatException e1) {
							}
						}
					}
				}
			}
		});

		Label hizbQuarterLabel = new Label(normalBody, SWT.NONE);
		hizbQuarterLabel.setText(meaning("HIZB_QUARTER") + ":");

		gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
		hizbQuarterSpinner = new Spinner(normalBody, SWT.BORDER);
		hizbQuarterSpinner.setLayoutData(gd);
		hizbQuarterSpinner.setData("id", "hizbQuarter");
		hizbQuarterSpinner.setMinimum(1);
		hizbQuarterSpinner.setMaximum(239);
		hizbQuarterSpinner.setSelection((QuranPropertiesUtils.getJuzOf(loc).getIndex() - 1) * 8
				+ QuranPropertiesUtils.getHizbQuadIndex(loc));
		focusList.add(hizbQuarterSpinner);
		hizbQuarterSpinner.addFocusListener(this);
		hizbQuarterSpinner.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					String text = hizbQuarterSpinner.getText();
					if (StringUtils.isNotBlank(text)) {
						gotoHizbQuarter(Integer.parseInt(text));
					}
				}
			}
		});

		Label juzLabel = new Label(normalBody, SWT.NONE);
		juzLabel.setText(meaning("JUZ") + ":");

		gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
		juzSpinner = new Spinner(normalBody, SWT.BORDER);
		juzSpinner.setLayoutData(gd);
		juzSpinner.setData("id", "juz");
		juzSpinner.setMinimum(1);
		juzSpinner.setMaximum(30);
		juzSpinner.setSelection(QuranPropertiesUtils.getJuzOf(loc).getIndex());
		focusList.add(juzSpinner);
		juzSpinner.addFocusListener(this);
		juzSpinner.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					String text = juzSpinner.getText();
					if (StringUtils.isNotBlank(text)) {
						gotoJuz(Integer.parseInt(text));
					}
				}
			}
		});

		gd = new GridData(SWT.BEGINNING, SWT.END, true, false);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		Label suraLabel1 = new Label(normalBody, SWT.NONE);
		suraLabel1.setText(meaning("SURA"));
		suraLabel1.setLayoutData(gd);

		Label normalOrderLabel = new Label(normalBody, SWT.NONE);
		normalOrderLabel.setText(meaning("NORMAL") + ":");

		gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
		suraCombo = new Combo(normalBody, SWT.READ_ONLY);
		suraCombo.setLayoutData(gd);
		suraCombo.setData("id", "sura");
		suraCombo.setItems(QuranPropertiesUtils.getIndexedSuraNames());
		suraCombo.setVisibleItemCount(10);
		suraCombo.select(loc.getSura() - 1);
		focusList.add(suraCombo);
		suraCombo.addFocusListener(this);
		suraCombo.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					int sura = suraCombo.getSelectionIndex();
					gotoSura(sura + 1);
				}
			}
		});

		final RevelationData ro = config.getRevelation().getDefault();
		if (ro != null) {
			Label revelOrderLabel = new Label(normalBody, SWT.NONE);
			revelOrderLabel.setText(lang.getDynamicMeaningById(FORM_ID, "REVEL_ORDER", new String[] { ro
					.getLocalizedName() })
					+ ":");

			gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
			suraRevelCombo = new Combo(normalBody, SWT.READ_ONLY);
			suraRevelCombo.setLayoutData(gd);
			suraRevelCombo.setData("id", "suraRevel");
			suraRevelCombo.setItems(QuranPropertiesUtils.getIndexedRevelationOrderedSuraNames());
			suraRevelCombo.setVisibleItemCount(10);
			suraRevelCombo.select(ro.getOrder(loc.getSura()) - 1);
			focusList.add(suraRevelCombo);
			suraRevelCombo.addFocusListener(this);
			suraRevelCombo.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					if (e.detail == CUSTOM_TRAVERSE) {
						int order = suraRevelCombo.getSelectionIndex();
						RevelationData revel = ro;
						if (revel != null) {
							gotoSura(revel.getSuraOfOrder(order + 1));
						} else {
							gotoSura(order + 1);
						}
					}
				}
			});
		}
	}

	private void createButtons() {
		GridData gd;
		GridLayout gl;
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.CENTER;
		Composite butComp = new Composite(body, SWT.NONE);
		gl = new GridLayout(2, true);
		gl.horizontalSpacing = 10;
		butComp.setLayout(gl);
		butComp.setLayoutData(gd);

		GridData gotoGd = new GridData(SWT.LEAD, SWT.FILL, true, true);
		gotoBut = new Button(butComp, SWT.PUSH);
		gotoBut.setText(FormUtils.addAmpersand(meaning("GOTO")));
		gotoBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lastFocusedControl.traverse(CUSTOM_TRAVERSE);
				close();
			}
		});

		GridData reviewGd = new GridData(SWT.TRAIL, SWT.FILL, true, true);
		reviewBut = new Button(butComp, SWT.PUSH);
		reviewBut.setText(FormUtils.addAmpersand(meaning("REVIEW")));
		reviewBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lastFocusedControl.traverse(CUSTOM_TRAVERSE);
			}
		});

		int buttonLength = FormUtils.buttonLength(80, gotoBut, reviewBut);
		gotoGd.minimumWidth = buttonLength;
		reviewGd.minimumWidth = buttonLength;
		gotoBut.setLayoutData(gotoGd);
		reviewBut.setLayoutData(reviewGd);
	}

	private void gotoSura(int suraNum) {
		navTo(suraNum, 1);
	}

	private void gotoJuz(int juzNum) {
		JuzProperties juz = QuranPropertiesUtils.getJuz(juzNum);
		navTo(juz.getLocation());
	}

	private void gotoHizbQuarter(int hizbQuarter) {
		JuzProperties juz = QuranPropertiesUtils.getJuz(hizbQuarter / 8 + 1);
		IQuranLocation hqLoc = juz.getHizbQuarters()[hizbQuarter % 8];
		navTo(hqLoc);
	}

	private void navTo(IQuranLocation loc) {
		quranForm.navTo(loc);
	}

	private void navTo(int sura, int aya) {
		quranForm.navTo(sura, aya);
	}

	private void selectTextInList() {
		if (suraAyaList.getItemCount() == 1) {
			suraAyaList.select(0);
			return;
		}

		String q = simplifyText(searchCombo.getText());
		String[] items = suraAyaList.getItems();
		for (int i = 0; i < items.length; i++) {
			if (simplifyText(items[i]).equals(q)) {
				suraAyaList.select(i);
				return;
			}
		}

		// still nothing is selected. just select first list element.
		suraAyaList.select(0);
	}

	private boolean internalGotoSuraAya() {
		String str;
		str = searchCombo.getText().trim();
		if ("".equals(str)) {
			return false; // do nothing
		}

		if (searchCombo.getItemCount() <= 0 || !str.equals(searchCombo.getItem(0))) {
			searchCombo.add(str, 0);
		}
		if (searchCombo.getItemCount() > 40) {
			searchCombo.remove(40, searchCombo.getItemCount() - 1);
		}

		String[] s = StringUtils.split(str, ":");
		if (s.length < 2) {
			return false;
		}
		int sura = getSuraNameList().indexOf(s[0]);
		if (sura < 0) {
			return false;
		}
		sura++;
		int aya = 1;
		try {
			aya = Integer.parseInt(s[1]);
		} catch (Exception e) {
			// do nothing
		}
		navTo(sura, aya);
		return true;
	}

	private String filterList(String filter) {
		filter = filter.trim();
		List<String> newList = new ArrayList<String>();
		if (StringUtils.isBlank(filter)) {
			newList = getSuraNameList();
		} else {
			int idx = filter.indexOf(':');
			String num = "";
			if (idx > 0) {
				num = filter.substring(idx + 1).trim();
				filter = simplifyText(filter.substring(0, idx));
			} else {
				filter = simplifyText(filter);
			}
			List<SuraProperties> suraList = QuranPropertiesUtils.getSuraList();
			for (int i = 0; i < suraList.size(); i++) {
				SuraProperties sura = suraList.get(i);
				String suraName = sura.toText();
				if (simplifyText(suraName).startsWith(filter)) {
					if (StringUtils.isBlank(num)) {
						for (int j = 1; j <= sura.getAyaCount(); j++) {
							newList.add(suraName + ":" + j);
						}
					} else {
						for (int j = 1; j <= sura.getAyaCount(); j++) {
							if (String.valueOf(j).startsWith(num)) {
								newList.add(suraName + ":" + j);
							}
						}
					}
				}

			}
		}
		suraAyaList.setItems(newList.toArray(new String[0]));
		return filter;
	}

	private List<String> getSuraNameList() {
		return QuranPropertiesUtils.getLocalizedSuraNameList();
	}

	private static final String simplifyText(String filter) {
		return SearchUtils.simplifySuranameText(filter);
	}

	private void resetSearchBox() {
		searchCombo.setText("");
	}

	private boolean gotoSuraAya() {
		int[] selection = suraAyaList.getSelectionIndices();
		if (selection.length > 0) {
			searchCombo.setText(suraAyaList.getItem(selection[0]));
			return internalGotoSuraAya();
		}
		return false;
	}

	public void focusGained(FocusEvent e) {
		Widget source = (Widget) e.getSource();
		if (focusList.contains(source)) {
			for (Control control : focusList) {
				control.setBackground(null);
			}
			Control control = (Control) source;
			control.setBackground(new Color(display, 225, 230, 255));
			lastFocusedControl = control;
			if (suraAyaBox.equals(source)) {
				suraAyaBox.selectAll();
			}
		}
	}

	public void focusLost(FocusEvent e) {
	}

	public void open() {
		shell.pack();
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}

	private void close() {
		shell.close();
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}
}
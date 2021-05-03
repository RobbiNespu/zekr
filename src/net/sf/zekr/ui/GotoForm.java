/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 11, 2010
 */
package net.sf.zekr.ui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;
import net.sf.zekr.engine.page.IPagingData;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
	private static final String UPDATE = "UPDATE";
	public static final String FORM_ID = "GOTO_FORM";
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
	private Spinner pageSpinner;
	private Spinner hizbQuarterSpinner;
	private Combo suraCombo, suraRevelCombo;
	private QuranForm quranForm;
	private Control lastFocusedControl;
	private Text suraAyaBox;
	private IUserView uvc;

	private Button gotoBut;

	private Button reviewBut;
	private List<String> suraNameList;

	private static final NumberFormat NF = NumberFormat.getInstance();;

	public GotoForm(Shell parent, QuranForm quranForm) {
		try {
			display = parent.getDisplay();
			this.parent = parent;
			this.quranForm = quranForm;
			uvc = config.getUserViewController();

			suraNameList = Collections.unmodifiableList(QuranPropertiesUtils.getLocalizedSuraNameList());

			init();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
	}

	private void init() {
		GridData gd;
		GridLayout gl;

		focusList = new ArrayList<Control>();

		shell = createShell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setText(meaning("TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.goto.form16")),
				new Image(display, resource.getString("icon.goto.form32")) });
		shell.setLayout(new FillLayout());
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
		searchCombo = new Combo(smartBody, SWT.DROP_DOWN | SWT.SEARCH);
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
		searchCombo.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					IQuranLocation location = uvc.getLocation();
					String str = suraNameList.get(location.getSura() - 1) + ":" + location.getAya();
					searchCombo.setText(str);
					addToSearchCombo(str);
				}
			}
		});

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
		suraAyaList.setData("id", "search"); // id is equal to searchCombo, so that it focuses on search combo the next time
		suraAyaList.setLayoutData(gd);
		suraAyaList.setItems(suraNameList.toArray(new String[0]));
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
		suraAyaBox = new Text(normalBody, SWT.BORDER | SWT.LEFT_TO_RIGHT);
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
								int sura = NF.parse(suraAya[0]).intValue();
								if (QuranLocation.isValidLocation(sura, 1)) {
									gotoSura(sura);
								}
							} catch (ParseException e1) {
							}
						} else if (suraAya.length > 1) {
							try {
								int sura = NF.parse(suraAya[0]).intValue();
								int aya = NF.parse(suraAya[1]).intValue();
								if (QuranLocation.isValidLocation(sura, aya)) {
									navTo(sura, aya);
								}
							} catch (ParseException e1) {
							}
						}
					}
				}
			}
		});
		suraAyaBox.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					IQuranLocation loc = uvc.getLocation();
					suraAyaBox.setText(loc.getSura() + ":" + loc.getAya());
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
						try {
							gotoHizbQuarter(NF.parse(text).intValue());
						} catch (ParseException e1) {
						}
					}
				}
			}
		});
		hizbQuarterSpinner.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					IQuranLocation loc = uvc.getLocation();
					hizbQuarterSpinner.setSelection((QuranPropertiesUtils.getJuzOf(loc).getIndex() - 1) * 8
							+ QuranPropertiesUtils.getHizbQuadIndex(loc));
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
						try {
							gotoJuz(NF.parse(text).intValue());
						} catch (ParseException e1) {
						}
					}
				}
			}
		});
		juzSpinner.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					IQuranLocation loc = uvc.getLocation();
					juzSpinner.setSelection(QuranPropertiesUtils.getJuzOf(loc).getIndex());
				}
			}
		});

		Label pageLabel = new Label(normalBody, SWT.NONE);
		pageLabel.setText(meaning("PAGE") + ":");

		gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
		pageSpinner = new Spinner(normalBody, SWT.BORDER);
		pageSpinner.setLayoutData(gd);
		pageSpinner.setData("id", "page");
		pageSpinner.setMinimum(1);
		pageSpinner.setMaximum(config.getQuranPaging().getDefault().size());
		pageSpinner.setSelection(uvc.getPage());
		focusList.add(pageSpinner);
		pageSpinner.addFocusListener(this);
		pageSpinner.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == CUSTOM_TRAVERSE) {
					String text = pageSpinner.getText();
					if (StringUtils.isNotBlank(text)) {
						try {
							gotoPage(NF.parse(text).intValue());
						} catch (ParseException e1) {
						}
					}
				}
			}
		});
		pageSpinner.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					pageSpinner.setSelection(uvc.getPage());
				}
			}
		});

		gd = new GridData(SWT.BEGINNING, SWT.END, true, false);
		gd.horizontalSpan = 2;
		gd.verticalIndent = 10;
		Label suraLabel1 = new Label(normalBody, SWT.NONE);
		suraLabel1.setText(meaning("SURA_ORDER"));
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
		suraCombo.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event event) {
				if (UPDATE.equals(event.text)) {
					suraCombo.select(uvc.getLocation().getSura() - 1);
				}
			}
		});

		final RevelationData revelOrder = config.getRevelation().getDefault();
		if (revelOrder != null) {
			Label revelOrderLabel = new Label(normalBody, SWT.NONE);
			revelOrderLabel.setText(meaning("REVELATION", revelOrder.getLocalizedName()) + ":");

			gd = new GridData(SWT.FILL, SWT.LEAD, true, false);
			suraRevelCombo = new Combo(normalBody, SWT.READ_ONLY);
			suraRevelCombo.setLayoutData(gd);
			suraRevelCombo.setData("id", "suraRevel");
			suraRevelCombo.setItems(QuranPropertiesUtils.getIndexedRevelationOrderedSuraNames());
			suraRevelCombo.setVisibleItemCount(10);
			suraRevelCombo.select(revelOrder.getOrder(loc.getSura()) - 1);
			focusList.add(suraRevelCombo);
			suraRevelCombo.addFocusListener(this);
			suraRevelCombo.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					if (e.detail == CUSTOM_TRAVERSE) {
						int order = suraRevelCombo.getSelectionIndex();
						RevelationData revel = revelOrder;
						if (revel != null) {
							gotoSura(revel.getSuraOfOrder(order + 1));
						}
					}
				}
			});
			suraRevelCombo.addListener(CUSTOM_ZEKR_EVENT, new Listener() {
				public void handleEvent(Event event) {
					if (UPDATE.equals(event.text)) {
						suraRevelCombo.select(revelOrder.getOrder(uvc.getLocation().getSura()) - 1);
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
				for (Control control : focusList) {
					if (control != lastFocusedControl
							&& (lastFocusedControl == suraAyaList && control != searchCombo || lastFocusedControl != suraAyaList)) {
						Event event = new Event();
						event.text = UPDATE;
						control.notifyListeners(CUSTOM_ZEKR_EVENT, event);
					}
				}
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

	private void gotoPage(int pageNum) {
		IPagingData paging = config.getQuranPaging().getDefault();
		if (pageNum > paging.size()) {
			pageNum = paging.size();
		} else if (pageNum < 1) {
			pageNum = 1;
		}
		navTo(paging.getQuranPage(pageNum).getFrom());
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

		addToSearchCombo(str);

		String[] s = StringUtils.split(str, ":");
		if (s.length < 2) {
			return false;
		}
		int sura = suraNameList.indexOf(s[0]);
		if (sura < 0) {
			return false;
		}
		sura++;
		int aya = 1;
		try {
			aya = NF.parse(s[1]).intValue();
		} catch (Exception e) {
			// do nothing
		}
		navTo(sura, aya);
		return true;
	}

	private void addToSearchCombo(String str) {
		if (searchCombo.getItemCount() <= 0 || !str.equals(searchCombo.getItem(0))) {
			searchCombo.add(str, 0);
		}
		if (searchCombo.getItemCount() > 40) {
			searchCombo.remove(40, searchCombo.getItemCount() - 1);
		}
	}

	private String filterList(String filter) {
		filter = filter.trim();
		List<String> newList = new ArrayList<String>();
		if (StringUtils.isBlank(filter)) {
			newList = suraNameList;
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
						try {
							num = NF.parse(num).toString();
						} catch (ParseException e) {
						}
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

	private static final String simplifyText(String filter) {
		return SearchUtils.simplifySuranameText(filter);
	}

	private void resetSearchBox() {
		searchCombo.setText("");
	}

	private boolean gotoSuraAya() {
		int[] selection = suraAyaList.getSelectionIndices();
		if (selection.length == 0) {
			if (suraAyaList.getItemCount() > 0) {
				suraAyaList.select(0);
			}
		}
		selection = suraAyaList.getSelectionIndices();
		if (selection.length > 0) {
			String str = suraAyaList.getItem(selection[0]);
			// addToSearchCombo(str);
			searchCombo.setText(str);
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

	public String getFormId() {
		return "GOTO_FORM";
	}
}

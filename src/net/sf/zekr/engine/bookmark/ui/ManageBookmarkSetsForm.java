/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 28, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.sf.zekr.common.ZekrBaseException;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.bookmark.BookmarkSaveException;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkSetGroup;
import net.sf.zekr.engine.bookmark.BookmarkTransformer;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Bookmarks Sets GUI form.
 * 
 * @author Mohsen Saboorian
 */
public class ManageBookmarkSetsForm extends BaseForm {
	public static final String FORM_ID = "BOOKMARK_SET_MANAGE_FORM";

	private BookmarkSet bookmark;
	private BookmarkSetGroup bmsg;

	private Shell parent;
	private Composite body;
	private Button editButt;
	private Button removeButt;
	private Button newButt;
	private Button importButt;
	private Button exportButt;
	private Button export4webButt;
	private Button defaultButt;
	private Label hintLab;
	private Table table;

	public ManageBookmarkSetsForm(Shell parent) {
		try {
			this.parent = parent;
			bookmark = config.getBookmark();

			display = parent.getDisplay();
			shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

			bmsg = config.getBookmarkSetGroup();

			GridLayout gl = new GridLayout(1, false);
			shell.setLayout(gl);
			shell.setText(meaning("TITLE"));
			shell.setImages(new Image[] { new Image(display, resource.getString("icon.bookmark.manage16")),
					new Image(display, resource.getString("icon.bookmark.manage32")) });

			makeForm();
		} catch (RuntimeException re) {
			FormUtils.disposeGracefully(shell);
			throw re;
		}
	}

	private void makeForm() {
		GridLayout gl = new GridLayout(2, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		Composite body = new Composite(shell, lang.getSWTDirection());
		body.setLayoutData(gd);
		gl = new GridLayout(1, false);
		body.setLayout(gl);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		hintLab = new Label(body, SWT.NONE);
		hintLab.setText(meaning("DBL_CLICK"));
		hintLab.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		table = new Table(body, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() == 0) {
					removeButt.setEnabled(false);
					editButt.setEnabled(false);
					exportButt.setEnabled(false);
					export4webButt.setEnabled(false);
				} else if (table.getSelectionCount() > 1) {
					removeButt.setEnabled(true);
					editButt.setEnabled(false);
					exportButt.setEnabled(false);
					export4webButt.setEnabled(false);
				} else {
					removeButt.setEnabled(true);
					editButt.setEnabled(true);
					exportButt.setEnabled(true);
					export4webButt.setEnabled(true);
				}

				// default selected
				if (e.detail == SWT.CHECK) {
					TableItem tableItem = (TableItem) e.item;
					if (!tableItem.getChecked()) {
						tableItem.setChecked(true);
					} else {
						tableItem.setChecked(false);
						if (MessageBoxUtils.showYesNoConfirmation(meaning("SET_AS_DEFAULT", tableItem.getText(0)), lang
								.getMeaning("SET_DEFAULT"))) {
							// each table item should have data, and this field is never null
							// (although it might not loaded yet)
							BookmarkSet bms = (BookmarkSet) tableItem.getData();
							if (!bms.isLoaded()) {
								bms.load();
								if (!bms.isLoaded()) {
									MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED"));
									return;
								}
								tableItem.setText(bms.getIdAndName());
							}
							bmsg.setAsDefault(bms);
							config.getProps().setProperty("bookmark.default", bms.getId());
							config.saveConfig();
							tableItem.setChecked(true);
							EventUtils.sendEvent(EventProtocol.UPDATE_BOOKMARKS_MENU);
						} else {
							return;
						}
					}
					shell.forceFocus();

					int index = table.indexOf(tableItem);
					TableItem[] items = table.getItems();
					for (int i = 0; i < items.length; i++) {
						if (i != index) {
							items[i].setChecked(false);
						}
					}
					e.doit = false;
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.DEL) {
					remove();
				}
			}
		});

		TableColumn idCol = new TableColumn(table, SWT.NONE);
		idCol.setText(lang.getMeaning("ID"));
		idCol.setWidth(100);

		TableColumn nameCol = new TableColumn(table, SWT.NONE);
		nameCol.setText(lang.getMeaning("NAME"));
		nameCol.setWidth(100);

		table.addListener(SWT.MouseDoubleClick, new Listener() {
			private TableEditor editor;

			public void handleEvent(Event event) {
				// Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				final int index = table.getSelectionIndex();
				// no item selected
				if (index == -1) {
					return;
				}

				// boolean visible = false;
				final TableItem item = table.getItem(index);
				Rectangle rect = item.getBounds(0);
				final Text itemEditor;
				if (rect.contains(pt)) {
					itemEditor = new Text(table, SWT.NONE);
					Listener textListener = new Listener() {
						public void handleEvent(final Event e) {
							String newId = itemEditor.getText();
							switch (e.type) {
							case SWT.Traverse:
								switch (e.detail) {
								case SWT.TRAVERSE_RETURN:
									try {
										newId = changeId(index, newId);
										item.setText(0, newId);
									} catch (ZekrBaseException ex) {
										itemEditor.dispose();
										MessageBoxUtils.showError("Error changing ID:\n" + ex.getMessage());
									}
									// fall through
								case SWT.TRAVERSE_ESCAPE:
									itemEditor.dispose();
									e.doit = false;
								}
								break;
							case SWT.FocusOut:
								try {
									newId = changeId(index, newId);
									item.setText(0, newId);
									itemEditor.dispose();
									e.doit = false;
								} catch (ZekrBaseException ex) {
									itemEditor.dispose();
									e.doit = false;
									MessageBoxUtils.showError("Error changing ID:\n" + ex.getMessage());
								}
								break;
							}
						}
					};
					itemEditor.addListener(SWT.FocusOut, textListener);
					itemEditor.addListener(SWT.Traverse, textListener);

					editor = new TableEditor(table);
					editor.horizontalAlignment = SWT.LEFT;
					editor.grabHorizontal = true;
					editor.setEditor(itemEditor, item, 0);

					itemEditor.setText(item.getText(0));
					itemEditor.setFocus();
					return;
				} else {
					rect = item.getBounds(0);
					rect.add(item.getBounds(1));
					if (rect.contains(pt)) {
						edit();
					}
				}
			}

			private String changeId(int itemIndex, String newId) throws ZekrBaseException {
				newId = newId.trim();
				TableItem item = table.getItem(itemIndex);
				String oldId = item.getText(0);
				if (oldId.equals(newId.trim())) {
					return newId;
				}
				BookmarkSet bms = (BookmarkSet) item.getData();
				bms.changeIdIfPossible(newId);
				if (item.getChecked()) { // item is the default bookmark set
					config.getProps().setProperty("bookmark.default", bms.getId());
					config.saveConfig();
				}
				return newId;
			}
		});

		Collection<BookmarkSet> bmNames = bmsg.getBookmarkSets();
		for (BookmarkSet bms : bmNames) {
			final TableItem item = new TableItem(table, SWT.NONE);
			String[] idn = bms.getIdAndName();
			item.setData(bms);
			item.setText(idn);
			if (bmsg.getDefault().getId().equals(bms.getId())) {
				item.setChecked(true);
			}
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);

		gl = new GridLayout(2, false);
		gl.horizontalSpacing = gl.verticalSpacing = 0;
		gl.marginHeight = gl.marginWidth = 0;
		Composite managerialButtComposite = new Composite(body, SWT.NONE);
		managerialButtComposite.setLayout(gl);
		managerialButtComposite.setLayoutData(gd);

		RowLayout rl = new RowLayout(SWT.HORIZONTAL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.LEAD;

		Composite crudButtComposite = new Composite(managerialButtComposite, SWT.NONE);
		crudButtComposite.setLayout(rl);
		crudButtComposite.setLayoutData(gd);

		RowData rd = new RowData();
		rd.width = 40;
		newButt = new Button(crudButtComposite, SWT.PUSH);
		newButt.setToolTipText(lang.getMeaning("NEW"));
		newButt.setImage(new Image(display, resource.getString("icon.add")));
		newButt.setLayoutData(rd);
		newButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				add();
			};
		});

		rd = new RowData();
		rd.width = 40;
		removeButt = new Button(crudButtComposite, SWT.PUSH);
		removeButt.setToolTipText(lang.getMeaning("REMOVE"));
		removeButt.setImage(new Image(display, resource.getString("icon.remove")));
		removeButt.setLayoutData(rd);
		removeButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				remove();
			};
		});

		rd = new RowData();
		rd.width = 40;
		editButt = new Button(crudButtComposite, SWT.PUSH);
		editButt.setToolTipText(lang.getMeaning("EDIT"));
		editButt.setImage(new Image(display, resource.getString("icon.bookmark.edit16")));
		editButt.setLayoutData(rd);
		editButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				edit();
			};
		});

		rl = new RowLayout(SWT.HORIZONTAL);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.TRAIL;

		Composite gotoButComposite = new Composite(managerialButtComposite, SWT.NONE);
		gotoButComposite.setLayout(rl);
		gotoButComposite.setLayoutData(gd);

		rd = new RowData();
		rd.width = 40;
		importButt = new Button(gotoButComposite, SWT.PUSH);
		importButt.setToolTipText(lang.getMeaning("IMPORT"));
		importButt.setImage(new Image(display, resource.getString("icon.bookmark.import")));
		importButt.setLayoutData(rd);
		importButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				importBookmark();
			};
		});

		rd = new RowData();
		rd.width = 40;
		exportButt = new Button(gotoButComposite, SWT.PUSH);
		export4webButt = new Button(gotoButComposite, SWT.PUSH);

		exportButt.setToolTipText(lang.getMeaning("EXPORT"));
		exportButt.setImage(new Image(display, resource.getString("icon.bookmark.export")));
		exportButt.setLayoutData(rd);
		exportButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				exportBookmark();
			};
		});

		export4webButt.setToolTipText(meaning("EXPORT4WEB"));
		export4webButt.setImage(new Image(display, resource.getString("icon.bookmark.export4web")));
		export4webButt.setLayoutData(rd);
		export4webButt.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			};

			public void widgetSelected(SelectionEvent e) {
				export4webBookmark();
			};
		});

		removeButt.setEnabled(false);
		editButt.setEnabled(false);
		exportButt.setEnabled(false);
		export4webButt.setEnabled(false);
	}

	private void importBookmark() {
		try {
			List<File> list = MessageBoxUtils.importFileDialog(shell, new String[] { "XML Bookmark Files" },
					new String[] { "*.xml" });
			if (list.size() <= 0) {
				return;
			}
			for (File srcFile : list) {
				File destFile = new File(Naming.getBookmarkDir() + "/" + srcFile.getName());
				BookmarkSet newBms = new BookmarkSet(destFile.getPath());
				if (destFile.exists()) {
					if (!MessageBoxUtils.showYesNoConfirmation(lang.getDynamicMeaning("FILE_ALREADY_EXISTS",
							new String[] { destFile.getName() }), lang.getMeaning("OVERWRITE"))) {
						continue;
					} else {
						String id = FilenameUtils.getBaseName(destFile.getName());
						BookmarkSet oldBms = bmsg.getBookmarkSet(id);

						if (oldBms.equals(bmsg.getDefault())) { // if the previous bookmark was the default bookmark set
							MessageBoxUtils.showError(meaning("CANNOT_OVERWRITE_DEFAULT"));
							continue;
						}

						bmsg.removeBookmarkSet(oldBms);
						bmsg.addBookmarkSet(newBms);

						// update table items
						TableItem[] items = table.getItems();
						for (int i = 0; i < items.length; i++) {
							if (((BookmarkSet) items[i].getData()).getId().equals(id)) {
								items[i].setData(newBms);
								items[i].setText(newBms.getIdAndName());
								break;
							}
						}
						FileUtils.copyFile(srcFile, destFile);
					}
				} else {
					FileUtils.copyFile(srcFile, destFile);
					addTableItem(newBms);
					bmsg.addBookmarkSet(newBms);
				}
			}
		} catch (IOException e) {
			MessageBoxUtils.showActionFailureError(e);
			logger.implicitLog(e);
		}
	}

	private void export4webBookmark() {
		int i = table.getSelectionIndex();
		if (i <= -1) {
			return;
		}
		TableItem item = table.getItem(i);
		try {
			File destFile = MessageBoxUtils.exportFileDialog(shell, new String[] { "HTML Files", "All Files (*.*)" },
					new String[] { "*.html;*.htm", "*.*" });
			if (destFile == null || destFile.isDirectory()) {
				return;
			}

			String dfn = destFile.getName().toUpperCase();
			if (!dfn.endsWith(".HTML") && !dfn.endsWith(".HTM")) {
				destFile = new File(destFile.getParent(), destFile.getName() + ".html");
			}

			BookmarkSet bms = (BookmarkSet) item.getData();
			logger.info("Export (for web) bookmark " + bms.getId() + " to " + destFile);
			//new BookmarkTransformer(bms, destFile).export();
			BookmarkTransformer.getInstance().export(bms, destFile);
		} catch (Exception e) {
			MessageBoxUtils.showActionFailureError(e);
			logger.implicitLog(e);
		}
	}

	private void exportBookmark() {
		int i = table.getSelectionIndex();
		if (i <= -1) {
			return;
		}
		TableItem item = table.getItem(i);
		try {
			File destFile = MessageBoxUtils.exportFileDialog(shell,
					new String[] { "XML Bookmark Files", "All Files (*.*)" }, new String[] { "*.xml", "*.*" });
			if (destFile == null || destFile.isDirectory()) {
				return;
			}
			if (!destFile.getName().toUpperCase().endsWith(".XML")) {
				destFile = new File(destFile.getParent(), destFile.getName() + ".xml");
			}
			BookmarkSet bms = (BookmarkSet) item.getData();
			File sourceFile = bms.getFile();
			logger.info("Export bookmark " + bms.getId() + " to " + destFile);
			FileUtils.copyFile(sourceFile, destFile);
		} catch (IOException e) {
			MessageBoxUtils.showActionFailureError(e);
			logger.implicitLog(e);
		}
	}

	private void add() {
		String id = MessageBoxUtils.textBoxPrompt(meaning("NEW_BOOKMARK_SET"), meaning("ENTER_ID") + ":");
		if (id != null && !"".equals(id.trim())) {
			id = id.trim();
			if (!bmsg.containsId(id)) {
				logger.info("Add a new bookmark set: " + id);
				BookmarkSet bms = new BookmarkSet(id.trim(), true);
				try {
					bms.save();
					bmsg.addBookmarkSet(bms);
					addTableItem(bms);
				} catch (BookmarkSaveException bse) {
					MessageBoxUtils.showActionFailureError(bse);
					logger.error("Adding new bookmark set failed: " + id);
				}
			} else {
				MessageBoxUtils.show("Choose another ID.\nA bookmark with this ID already exists", "Duplicate ID",
						SWT.ICON_WARNING);
			}
		}
	}

	private void addTableItem(BookmarkSet bms) {
		TableItem newItem = new TableItem(table, SWT.NONE);
		newItem.setData(bms);
		newItem.setText(bms.getIdAndName());
	}

	private void remove() {
		boolean defSelected = false;
		int[] indices = table.getSelectionIndices();
		if (indices.length <= 0) {
			return;
		}

		for (int i = 0; i < indices.length; i++) {
			if (table.getItem(indices[i]).getChecked()) {
				defSelected = true;
			}
		}

		if (defSelected == true) {
			MessageBoxUtils.showError(meaning("CANNOT_REMOVE_DEFAULT"));
			return;
		}

		if (MessageBoxUtils.showYesNoConfirmation(lang.getDynamicMeaning("DEL_YES_NO", new String[] { ""
				+ table.getSelectionCount() }), lang.getMeaning("REMOVE"))) {
			for (int i = indices.length - 1; i >= 0; i--) {
				TableItem item = table.getItem(indices[i]);
				BookmarkSet bms = (BookmarkSet) item.getData();
				try {
					bms.remove();
					bmsg.removeBookmarkSet(bms.getId());
					table.remove(indices[i]);
				} catch (ZekrBaseException e) {
					MessageBoxUtils.showError(e.getMessage());
				}
			}
			if (table.getSelectionCount() == 0) {
				removeButt.setEnabled(false);
				editButt.setEnabled(false);
				exportButt.setEnabled(false);
			}
		}
	}

	private void edit() {
		final int i = table.getSelectionIndex();
		if (i <= -1) {
			return;
		}
		TableItem item = table.getItem(i);
		final BookmarkSet bms = (BookmarkSet) item.getData();
		bms.load();
		new BookmarkSetForm(bms, shell).open(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				table.getItem(i).setText(bms.getIdAndName());
			}
		});
	}

	public void open() {
		shell.setSize(400, 280);
		// ID 1783886 - to make the hint show in full 
		shell.pack();
		shell.setLocation(FormUtils.getCenter(parent, shell));
		shell.open();
	}

	public String getFormId() {
		return "BOOKMARK_SET_MANAGE_FORM";
	}

}

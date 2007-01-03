/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 28, 2006
 */
package net.sf.zekr.engine.bookmark.ui;

import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkItem;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.EventProtocol;
import net.sf.zekr.ui.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Bookmarks Sets GUI form.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BookmarkSetGroupForm {
	public static final String FORM_ID = "BOOKMARK_SET_GROUP_FORM";
	private static final LanguageEngine lang = LanguageEngine.getInstance();
	private static final ResourceManager resource = ResourceManager.getInstance();
	private final Logger logger = Logger.getLogger(this.getClass());
	private static final ApplicationConfig config = ApplicationConfig.getInstance();

	private BookmarkSet bookmark;

	private Display display;
	private Shell shell;
	private Shell parent;
	private Composite body;
	private List bookmarksSetsList;
	private org.eclipse.swt.widgets.List listWidget;
	private Button editBut;
	private Button removeBut;
	private Button newBut;

	public BookmarkSetGroupForm(Shell parent) {
		this.parent = parent;
		bookmark = config.getBookmark();

		display = parent.getDisplay();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL | SWT.RESIZE);

		GridLayout gl = new GridLayout(1, false);
		shell.setLayout(gl);
		shell.setText(meaning("TITLE"));
		shell.setImage(new Image(display, resource.getString("icon.bookmark.manager")));

		makeForm();
	}

	private void makeForm() {
	}

	private String meaning(String key) {
		return lang.getMeaningById(FORM_ID, key);
	}
}

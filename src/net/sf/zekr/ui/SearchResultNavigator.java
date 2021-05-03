/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Mohsen Saboorian
 */
public class SearchResultNavigator {
	private final ApplicationConfig config = ApplicationConfig.getInstance();
	private final LanguageEngine lang = config.getLanguageEngine();
	private final ResourceManager resource = ResourceManager.getInstance();
	private final Logger logger = Logger.getLogger(SearchResultNavigator.class);

	private final Display display;
	Spinner pageSpinner;
	private Composite paginationComp;
	private final Composite body;
	Button nextPageBut;
	Button prevPageBut;

	private final IPageNavigator pageNavigator;

	public SearchResultNavigator(Composite body, IPageNavigator pageNavigator) {
		this.body = body;
		display = body.getDisplay();
		this.pageNavigator = pageNavigator;

		init();
	}

	private void init() {
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.horizontalSpan = 2;
		final GridLayout gl = new GridLayout(3, true);
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		paginationComp = new Composite(body, SWT.NONE);
		paginationComp.setLayout(gl);
		paginationComp.setLayoutData(gd);
		paginationComp.setVisible(false);

		final boolean isRTL = lang.getSWTDirection() == SWT.RIGHT_TO_LEFT && GlobalConfig.hasBidiSupport;
		final Image prevPageImg = new Image(display, isRTL ? resource.getString("icon.nextNext")
				: resource.getString("icon.prevPrev"));
		final Image nextPageImg = new Image(display, isRTL ? resource.getString("icon.prevPrev")
				: resource.getString("icon.nextNext"));

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		prevPageBut = new Button(paginationComp, SWT.PUSH);
		prevPageBut.setLayoutData(gd);
		prevPageBut.setToolTipText(lang.getMeaning("PREVIOUS"));
		prevPageBut.setImage(prevPageImg);
		prevPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final int newPage = pageSpinner.getSelection() - 1;
				pageSpinner.setSelection(newPage);
				pageNavigator.gotoPage(newPage);
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		pageSpinner = new Spinner(paginationComp, SWT.BORDER);
		pageSpinner.setLayoutData(gd);
		pageSpinner.setToolTipText(lang.getMeaning("PAGE"));
		pageSpinner.setMinimum(1);
		pageSpinner.setMaximum(1000);
		pageSpinner.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					pageNavigator.gotoPage(pageSpinner.getSelection());
				}
			}
		});
		pageSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final int p = pageSpinner.getSelection();
				if (p > 1) {
					prevPageBut.setEnabled(true);
				} else {
					prevPageBut.setEnabled(false);
				}
				if (p < pageSpinner.getMaximum()) {
					nextPageBut.setEnabled(true);
				} else {
					nextPageBut.setEnabled(false);
				}
			}
		});

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		nextPageBut = new Button(paginationComp, SWT.PUSH);
		nextPageBut.setLayoutData(gd);
		nextPageBut.setToolTipText(lang.getMeaning("NEXT"));
		nextPageBut.setImage(nextPageImg);
		nextPageBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final int newPage = pageSpinner.getSelection() + 1;
				pageSpinner.setSelection(newPage);
				pageNavigator.gotoPage(newPage);
			}
		});

		nextPageBut.setEnabled(false);
		prevPageBut.setEnabled(false);
	}

	public void setVisible(boolean visible) {
		paginationComp.setVisible(visible);
	}

	public void resetSearch(int pageCount) {
		// reset spinner
		pageSpinner.setMaximum(pageCount >= 1 ? pageCount : 1);
		pageSpinner.setSelection(1);
		pageNavigator.gotoPage(0); // 0 = first page
	}

	public void dispose() {
		paginationComp.dispose();
	}

}

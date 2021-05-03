/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 12, 2010
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 */
public abstract class ZekrForm implements IZekrForm {
	public static final String FORM_ID = "FORM_ID";
	protected final static ApplicationConfig config = ApplicationConfig.getInstance();
	protected final static LanguageEngine lang = LanguageEngine.getInstance();

	protected final Logger logger = Logger.getLogger(this.getClass());

	protected Display display;

	protected String meaning(String key) {
		return lang.getMeaningById(getFormId(), key);
	}

	protected String meaning(String key, String value) {
		return lang.getDynamicMeaningById(getFormId(), key, new String[] { value });
	}

	protected String meaning(String key, String[] values) {
		return lang.getDynamicMeaningById(getFormId(), key, values);
	}

	/**
	 * Creates a {@link Shell} and sets current form ID into it.
	 * 
	 * @param parent
	 * @param style
	 * @return newly created {@link Shell}
	 */
	protected Shell createShell(Shell parent, int style) {
		Shell shell = new Shell(parent, style);
		shell.setData(FORM_ID, getFormId());
		return shell;
	}

	/**
	 * Creates a {@link Shell} and sets current form ID into it.
	 * 
	 * @param display
	 * @param style
	 * @return
	 */
	protected Shell createShell(Display display, int style) {
		Shell shell = new Shell(display, style);
		shell.setData(FORM_ID, getFormId());
		return shell;
	}

}

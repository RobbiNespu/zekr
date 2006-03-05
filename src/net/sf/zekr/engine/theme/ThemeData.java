/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2006
 */
package net.sf.zekr.engine.theme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationPath;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ThemeData {
	/**
	 * Configuration properties set in <tt>theme.properties</tt> in the theme folder.
	 */
	public Map props = new HashMap();

	/** Theme name */
	public String name;

	/**
	 * Unique identifier for this theme. This is always equal to the folder of the this
	 * theme.
	 */
	public String id;

	/** Theme author */
	public String author;

	/**
	 * @return application relative theme path (e.g. <tt>res/theme/default</tt>)
	 */
	public String getPath() {
		return ApplicationPath.THEME_DIR + File.separator + id;
	}

	public String toString() {
		return name + " - " + id;
	}
}

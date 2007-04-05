/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     20/01/2005
 */

package net.sf.zekr.common.runtime;

import net.sf.zekr.common.config.GlobalConfig;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class Naming {
	// Application home directory
	public static final String HOME_DIR_NAME = ".zekr";
	public static final String HOME_PATH = GlobalConfig.USER_HOME_PATH + "/" + HOME_DIR_NAME;

	/* Cache Directories */
	public static final String CACHE_DIR = HOME_PATH + "/cache";
	public static final String QURAN_CACHE_DIR = CACHE_DIR + "/quran";
	public static final String TRANS_CACHE_DIR = CACHE_DIR + "/trans";
	public static final String MIXED_CACHE_DIR = CACHE_DIR + "/mixed";
	public static final String SEARCH_CACHE_DIR = CACHE_DIR + "/search";

	public static final String CONFIG_DIR = HOME_PATH + "/config";
	public static final String THEME_PROPS_DIR = CONFIG_DIR + "/theme";
	public static final String BOOKMARK_DIR = HOME_PATH + "/bookmark";

	/* Cache Directories */
	public static final String INDEX_DIR = HOME_PATH + "/index";
	public static final String QURAN_INDEX_DIR = INDEX_DIR + "/quran";

	/** Directory to import a translation for a user only. */
	public static final String TRANS_DIR = HOME_PATH + "/text/trans";

	/** Directory to import a theme for a user only. */
	public static final String THEME_DIR = HOME_PATH + "/theme";
}

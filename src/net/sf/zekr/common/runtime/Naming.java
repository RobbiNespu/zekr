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

	private static String WORKSPACE = GlobalConfig.USER_HOME_PATH + "/" + HOME_DIR_NAME;

	/* Cache Directories */
	// private static final String CACHE_DIR = WORKSPACE + "/cache";
	// private static final String QURAN_CACHE_DIR = getCacheDir() + "/quran";
	// private static final String TRANS_CACHE_DIR = getCacheDir() + "/trans";
	// private static final String MIXED_CACHE_DIR = getCacheDir() + "/mixed";
	// private static final String SEARCH_CACHE_DIR = getCacheDir() + "/search";
	// private static final String CONFIG_DIR = WORKSPACE + "/config";
	// private static final String THEME_PROPS_DIR = getConfigDir() + "/theme";
	// private static final String BOOKMARK_DIR = WORKSPACE + "/bookmark";
	/* Cache Directories */
	// private static final String INDEX_DIR = WORKSPACE + "/index";
	// private static final String QURAN_INDEX_DIR = getIndexDir() + "/quran";
	/** Directory to import a translation for a user only. */
	// private static final String TRANS_DIR = WORKSPACE + "/text/trans";
	/** Directory to import a theme for a user only. */
	// private static final String THEME_DIR = WORKSPACE + "/theme";
	/**
	 * Use with caution! This method should be called before any path is loaded from this file.
	 * 
	 * @param workspace
	 *           default workspace to set
	 */
	public static void setWorkspace(String workspace) {
		WORKSPACE = workspace;
	}

	public static String getWorkspace() {
		return WORKSPACE;
	}

	public static String getCacheDir() {
		return getWorkspace() + "/cache";
	}

	public static String getQuranCacheDir() {
		return getCacheDir() + "/quran";
	}

	public static String getTransCacheDir() {
		return getCacheDir() + "/trans";
	}

	public static String getMixedCacheDir() {
		return getCacheDir() + "/mixed";
	}

	public static String getSearchCacheDir() {
		return getCacheDir() + "/search";
	}

	public static String getConfigDir() {
		return getWorkspace() + "/config";
	}

	public static String getThemePropsDir() {
		return getConfigDir() + "/theme";
	}

	public static String getBookmarkDir() {
		return getWorkspace() + "/bookmark";
	}

	public static String getIndexDir() {
		return getWorkspace() + "/index";
	}

	public static String getQuranIndexDir() {
		return getIndexDir() + "/quran";
	}

	public static String getTransDir() {
		return getWorkspace() + "/text/trans";
	}

	public static String getThemeDir() {
		return getWorkspace() + "/theme";
	}
}

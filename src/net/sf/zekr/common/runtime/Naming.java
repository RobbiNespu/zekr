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
 * @version 0.2
 */
public class Naming {
	// Application home directory
	public static final String HOME_DIR = ".zekr";
	public static final String HOME_PATH = GlobalConfig.USER_HOME_PATH + "/"
			+ HOME_DIR;
	public static final String CACHE_DIR = HOME_PATH + "/cache";
	public static final String QURAN_CACHE_DIR = CACHE_DIR + "/quran";
	public static final String TRANS_CACHE_DIR = CACHE_DIR + "/trans";
	public static final String MIXED_CACHE_DIR = CACHE_DIR + "/mixed";
	public static final String SEARCH_CACHE_DIR = CACHE_DIR + "/search";
	public static final String CONFIG_PATH = HOME_PATH + "/config";
	public static final String BOOKMARK_PATH = HOME_PATH + "/bookmark";
}

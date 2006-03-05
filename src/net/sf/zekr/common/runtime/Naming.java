/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     20/01/2005
 */

package net.sf.zekr.common.runtime;

import java.io.File;

import net.sf.zekr.common.config.GlobalConfig;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class Naming {
	// Application home directory
	public static final String HOME_DIR = ".zekr";
	public static final String HOME_PATH = GlobalConfig.USER_HOME_PATH + File.separator
			+ HOME_DIR;
	public static final String CACHE_DIR = HOME_PATH + File.separator + "cache";
	public static final String COMMON_DIR = CACHE_DIR;
	public static final String HTML_QURAN_CACHE_DIR = CACHE_DIR + File.separator + "quran";
	public static final String HTML_SEARCH_CACHE_DIR = CACHE_DIR + File.separator + "search";
	public static final String CONFIG_PATH = HOME_PATH + File.separator + "config";
}

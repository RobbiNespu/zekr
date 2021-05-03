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
 */
public class Naming {
	// Application home directory
	public static final String HOME_DIR_NAME = ".zekr";

	private static String WORKSPACE = GlobalConfig.USER_HOME_PATH + "/" + HOME_DIR_NAME;

	/**
	 * Use with caution! This method MUST be called before any path is loaded from this file.
	 * 
	 * @param workspace default workspace to set
	 */
	public static void setWorkspace(String workspace) {
		WORKSPACE = workspace;
	}

	public static String getWorkspace() {
		return WORKSPACE;
	}

	public static String getViewCacheDir() {
		return getWorkspace() + "/cache";
	}

	public static String getQuranCacheDir() {
		return getQuranCacheDir(getViewCacheDir());
	}

	public static String getQuranCacheDir(String base) {
		return base + "/quran";
	}

	public static String getTransCacheDir() {
		return getTransCacheDir(getViewCacheDir());
	}

	public static String getTransCacheDir(String base) {
		return base + "/trans";
	}

	public static String getMixedCacheDir() {
		return getMixedCacheDir(getViewCacheDir());
	}

	public static String getMixedCacheDir(String base) {
		return base + "/mixed";
	}

	public static String getSearchCacheDir() {
		return getSearchCacheDir(getViewCacheDir());
	}

	public static String getSearchCacheDir(String base) {
		return base + "/search";
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

	public static String getTransIndexDir(String transId) {
		return getIndexDir() + "/trans/" + transId;
	}

	public static String getThemeDir() {
		return getWorkspace() + "/theme";
	}

	public static String getAudioDir() {
		return getWorkspace() + "/audio";
	}

	public static String getAudioCacheDir() {
		return getAudioDir() + "/cache";
	}
}

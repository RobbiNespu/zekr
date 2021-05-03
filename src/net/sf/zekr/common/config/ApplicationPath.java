/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.common.config;

import net.sf.zekr.common.runtime.Naming;

/**
 * This class contains Zekr directory hierarchy.
 * 
 * @author Mohsen Saboorian
 */
public class ApplicationPath {
	private static ResourceManager rm = ResourceManager.getInstance();

	// Directories
	public static final String RESOURCE_DIR = rm.getString("resource.baseDir");
	public static final String IMAGE_DIR = rm.getString("image.baseDir");
	public static final String ICON_DIR = rm.getString("icon.baseDir");

	/**
	 * This is the directory containing quran source and other related sources.
	 */
	public static final String QURAN_TEXT_DIR = rm.getString("text.quran.baseDir");
	public static final String SIMPLE_QURAN_TEXT_FILE = rm.getString("text.quran.simple");
	public static final String UTHMANI_QURAN_TEXT_FILE = rm.getString("text.quran.uthmani");
	public static final String QURAN_INDEX_DIR = rm.getString("text.quran.indexDir");
	public static final String TRANS_INDEX_DIR = rm.getString("text.trans.indexDir");
	public static final String TRANSLATION_DIR = rm.getString("text.trans.baseDir");
	public static final String TRANSLATION_DESC = rm.getString("text.trans.desc");
	public static final String REVELATION_DIR = rm.getString("text.revel.baseDir");
	public static final String REVELATION_DESC = rm.getString("text.revel.desc");
	public static final String PAGING_DIR = rm.getString("text.paging.baseDir");

	// extensions
	public static final String REVEL_PACK_SUFFIX = rm.getString("text.revel.suffix");
	public static final String TRANS_PACK_SUFFIX = rm.getString("text.trans.suffix");
	public static final String RECIT_PACK_SUFFIX = rm.getString("audio.recit.suffix");
	public static final String PAGING_PACK_SUFFIX = rm.getString("text.paging.suffix");

	/**
	 * The directory relative path, containing language packs and language settings.
	 */
	public static final String LANGUAGE_DIR = rm.getString("lang.baseDir");

	public static final String UI_DIR = rm.getString("ui.baseDir");
	public static final String THEME_DIR = rm.getString("theme.baseDir");
	public static final String THEME_DESC = rm.getString("theme.desc");

	public static final String AUDIO_DIR = rm.getString("audio.baseDir");

	public static final String BOOKMARK_DIR = rm.getString("bookmark.baseDir");

	// Files

	/** Original configuration file */
	public static final String CONFIG_DIR = rm.getString("config.baseDir");
	public static final String MAIN_CONFIG = rm.getString("config.original"); // original config
	public static final String MAIN_SEARCH_INFO = rm.getString("config.searchInfo.original"); // original search info
	public static final String MAIN_SHORTCUT = rm.getString("config.shortcut.original"); // original key shortcuts

	/** User-customized configuration file */
	public static final String USER_CONFIG = rm.getString("config.user", new String[] { Naming.getConfigDir() });
	public static final String USER_SEARCH_INFO = rm.getString("config.searchInfo.user", new String[] { Naming.getConfigDir() });
	public static final String USER_SHORTCUT = rm.getString("config.shortcut.user", new String[] { Naming.getConfigDir() });

	/** Log4J property file */
	public static final String DEFAULT_LOGGER = rm.getString("config.logger");

	/** Velocity property file */
	public static final String VELOCITY_CONFIG = rm.getString("config.template");
	
	public static final String RECITATION_DESC = rm.getString("audio.recit.desc");
}

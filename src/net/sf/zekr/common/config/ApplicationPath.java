/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.common.config;


/**
 * This class contains Zekr directory hierarchy. Directories names are stored in uppercase
 * variables (by convention). Each directory end with a <code>DIR_DELIM</code> which is
 * equal to java <code>File.separator</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
/*
 * FIXME: All explicit file path should be moved to ApplicationConfig. This class should
 * only hold directory names (except for some fixed files, e.g zekr-config.xml).
 */
public class ApplicationPath {
	private static ResourceManager rm = ResourceManager.getInstance();

	// Directories
	public static final String RESOURCE_DIR = rm.getString("resource.baseDir");
	public static final String IMAGE_DIR = rm.getString("image.baseDir");
	public static final String ICON_DIR = rm.getString("icon.baseDir");
//	public static final String CONFIG_DIR = RESOURCE_DIR + "/config";
//	public static final String LIB_CONFIG_DIR = rm.getString("config.lib");

	/**
	 * This is the directory containing quran source and other related sources.
	 */
	public static final String TEXT_DIR = rm.getString("text.quran.baseDir");
	
	public static final String TRANSLATION_DIR = rm.getString("text.trans.baseDir");

	/**
	 * The directory relative path, containing language packs and language settings.
	 */
	public static final String LANGUAGE_DIR = rm.getString("lang.baseDir");

	public static final String THEME_DIR = rm.getString("theme.baseDir");

	// Files

	// XML Files
	public static final String CONFIG_FILE = rm.getString("config.zekr");

//	public static final String QURAN_TEXT = TEXT_DIR + "quran-1256.txt";

	// Velocity Files
//	/**
//	 * Please note that this string contains only the file name, not the full path,
//	 * because <code>Velocity.getTemplate()</code> works for directories added to
//	 * <code>"file.resource.loader.path"</code> property.
//	 */

	// Log4J Property Files
	public static final String DEFAULT_LOGGER = rm.getString("config.logger");

	// Velocity Property Files
	public static final String VELOCITY_CONFIG = rm.getString("config.template");

}

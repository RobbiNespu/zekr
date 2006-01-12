/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.common.config;

import java.io.File;

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

	// Directories
	public static final String DIR_DELIM = File.separator;
	public static final String RESOURCE_DIR = "res" + DIR_DELIM;
	public static final String IMAGE_DIR = RESOURCE_DIR + "image" + DIR_DELIM;
	public static final String ICON_DIR = IMAGE_DIR + "icon" + DIR_DELIM;
	public static final String CONFIG_DIR = RESOURCE_DIR + "config" + DIR_DELIM;
	public static final String LIB_CONFIG_DIR = CONFIG_DIR + "lib" + DIR_DELIM;

	/**
	 * This is the directory containing quran source and other related sources.
	 */
	public static final String TEXT_DIR = RESOURCE_DIR + "text" + DIR_DELIM;

	/**
	 * The directory relative path, containing language packs and language settings.
	 */
	public static final String LANGUAGE_DIR = RESOURCE_DIR + "lang" + DIR_DELIM;
	public static final String TEMPLATE_DIR = RESOURCE_DIR + "template" + DIR_DELIM;

	// Files

	// XML Files
	public static final String CONFIG_FILE = CONFIG_DIR + "zekr-config.xml";

//	public static final String QURAN_TEXT = TEXT_DIR + "quran-1256.txt";
	public static final String PATH_RESOURCE_FILE = RESOURCE_DIR + "resource-path.properties";

	// Velocity Files
//	/**
//	 * Please note that this string contains only the file name, not the full path,
//	 * because <code>Velocity.getTemplate()</code> works for directories added to
//	 * <code>"file.resource.loader.path"</code> property.
//	 */
	public static final String SOORA_VIEW_TEMPLATE = TEMPLATE_DIR + "soora-view-template.vm";
	public static final String SEARCH_RESULT_TEMPLATE = TEMPLATE_DIR + "search-result-template.vm";

	// Log4J Property Files
	public static final String DEFAULT_LOGGER = LIB_CONFIG_DIR + "logger.properties";

	// Velocity Property Files
	public static final String VELOCITY_CONFIG = LIB_CONFIG_DIR + "velocity.properties";

}

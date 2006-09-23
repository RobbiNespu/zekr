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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeTemplate;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ApplicationRuntime {
	private ArrayList dirList = new ArrayList();
	private static final Logger logger = Logger.getLogger(ApplicationRuntime.class);

	public ApplicationRuntime() {
		dirList.add(Naming.HOME_PATH);
		dirList.add(Naming.QURAN_CACHE_DIR);
		dirList.add(Naming.TRANS_CACHE_DIR);
		dirList.add(Naming.MIXED_CACHE_DIR);
		dirList.add(Naming.SEARCH_CACHE_DIR);
		dirList.add(Naming.CONFIG_PATH);
	}

	/**
	 * Makes the application home directory
	 * 
	 * @throws IOException
	 */
	public void configureDirectories() throws IOException {
		logger.info("Making application required directories (if not exist)...");
		// recreateHtmlCache();

		for (Iterator iter = dirList.iterator(); iter.hasNext();) {
			File file = new File((String) iter.next());
			if (!file.exists() && !file.mkdirs())
				throw new IOException("Can not create \'" + file.getAbsoluteFile() + "\'.");
		}
	}

	/**
	 * Recreates cache for Quran, translation and
	 * 
	 * @throws IOException
	 */
	public void recreateHtmlCache() throws IOException {
		logger.info("Recreate HTML cache directory.");

		File cache = new File(Naming.CACHE_DIR);
		if (cache.exists())
			org.apache.commons.io.FileUtils.deleteDirectory(cache);
		cache.mkdir();
		new File(Naming.QURAN_CACHE_DIR).mkdir();
		new File(Naming.TRANS_CACHE_DIR).mkdir();
		new File(Naming.MIXED_CACHE_DIR).mkdir();
		new File(Naming.SEARCH_CACHE_DIR).mkdir();
	}

	public void configure() throws IOException {
		configureDirectories();
		createCommonFiles();
	}

	private void createCommonFiles() {
		logger.info("Create common configuration files.");
		Theme theme = ApplicationConfig.getInstance().getTheme();
		ThemeTemplate ct = new ThemeTemplate(theme.getCurrent());
		ct.transform();
	}

	/**
	 * Will recreate all theme-related directories and files:
	 * <ul>
	 * <li>HTML for quran, translation and search result text</li>
	 * <li>Common config files such as CSS file</li>
	 * </ul>
	 * 
	 * @throws IOException
	 */
	public void recreateCache() throws IOException {
		logger.info("Recreate cache.");
		recreateHtmlCache();
		createCommonFiles();
	}

	public void recreateQuranCache() throws IOException {
		FileUtils.recreateDirectory(Naming.QURAN_CACHE_DIR);
	}

	public void recreateTransCache() throws IOException {
		FileUtils.recreateDirectory(Naming.TRANS_CACHE_DIR);
	}

	public void recreateMixedCache() throws IOException {
		FileUtils.recreateDirectory(Naming.MIXED_CACHE_DIR);
	}

	public void clearCache() {
		FileUtils.delete(new File(Naming.CACHE_DIR));
	}

	public void clearConfig() {
		FileUtils.delete(new File(Naming.CONFIG_PATH));
	}

	/**
	 * Clear <tt>cache</tt> and <tt>config</tt> directories.
	 */
	public void clearAll() {
		clearCache();
		clearConfig();
	}
}

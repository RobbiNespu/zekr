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
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.template.ITransformer;
import net.sf.zekr.engine.template.TemplateTransformationException;
import net.sf.zekr.engine.template.ThemeTemplate;
import net.sf.zekr.engine.theme.Theme;

import org.apache.commons.io.FileUtils;

/**
 * @author Mohsen Saboorian
 */
public class ApplicationRuntime {
	private ArrayList<String> dirList = new ArrayList<String>();
	private final Logger logger = Logger.getLogger(ApplicationRuntime.class);

	public ApplicationRuntime() {
		dirList.add(Naming.getWorkspace());
		dirList.add(Naming.getQuranCacheDir());
		dirList.add(Naming.getTransCacheDir());
		dirList.add(Naming.getMixedCacheDir());
		dirList.add(Naming.getSearchCacheDir());
		dirList.add(Naming.getConfigDir());
		dirList.add(Naming.getThemePropsDir());
		dirList.add(Naming.getBookmarkDir());
		dirList.add(Naming.getTransDir());
		dirList.add(Naming.getThemeDir());
		dirList.add(Naming.getAudioDir());
		dirList.add(Naming.getAudioCacheDir());
	}

	/**
	 * Makes the application home directory.
	 * 
	 * @throws IOException
	 */
	public void configureDirectories() throws IOException {
		logger.info("Making application required directories (if not exist)...");

		for (Iterator<String> iter = dirList.iterator(); iter.hasNext();) {
			File file = new File(iter.next());
			if (!file.exists() && !file.mkdirs())
				throw new IOException("Can not create \'" + file.getAbsoluteFile() + "\'.");
		}
	}

	/**
	 * Recreates cache for Quran, translation.
	 * 
	 * @throws IOException
	 */
	public void recreateHtmlCache() throws IOException {
		logger.info("Recreate HTML cache directory.");

		File cache = new File(Naming.getViewCacheDir());
		if (cache.exists())
			FileUtils.deleteDirectory(cache);
		cache.mkdir();
		new File(Naming.getQuranCacheDir()).mkdir();
		new File(Naming.getTransCacheDir()).mkdir();
		new File(Naming.getMixedCacheDir()).mkdir();
		new File(Naming.getSearchCacheDir()).mkdir();
	}

	public void configure() throws IOException {
		configureDirectories();
		createCommonFiles();
	}

	private void createCommonFiles() {
		logger.info("Create common configuration files...");

		// theme
		Theme theme = ApplicationConfig.getInstance().getTheme();
		ITransformer ct = new ThemeTemplate(theme.getCurrent());
		try {
			ct.transform();
		} catch (TemplateTransformationException e) {
			logger.log(e);
		}

		logger.info("Creating common configuration files done.");
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
	public void recreateViewCache() throws IOException {
		logger.info("Recreate view cache.");
		recreateHtmlCache();
		createCommonFiles();
	}

	public void recreatePlaylistCache() throws IOException {
		logger.info("Recreate playlist cache.");
		net.sf.zekr.common.util.FileUtils.recreateDirectory(Naming.getAudioCacheDir());
	}

	public void recreateQuranCache() throws IOException {
		net.sf.zekr.common.util.FileUtils.recreateDirectory(Naming.getQuranCacheDir());
	}

	public void recreateTransCache() throws IOException {
		net.sf.zekr.common.util.FileUtils.recreateDirectory(Naming.getTransCacheDir());
	}

	public void recreateMixedCache() throws IOException {
		net.sf.zekr.common.util.FileUtils.recreateDirectory(Naming.getMixedCacheDir());
	}

	public void clearCache() {
		try {
			FileUtils.deleteDirectory(new File(Naming.getViewCacheDir()));
		} catch (IOException e) {
			logger.error("Error while deleting directory: " + new File(Naming.getViewCacheDir()));
			logger.log(e);
		}
	}

	public void clearConfig() {
		try {
			FileUtils.deleteDirectory(new File(Naming.getConfigDir()));
		} catch (IOException e) {
			logger.error("Error while deleting directory: " + new File(Naming.getConfigDir()));
			logger.log(e);
		}
	}

	/**
	 * Clear <tt>cache</tt> and <tt>config</tt> directories.
	 */
	public void clearAll() {
		clearCache();
		clearConfig();
	}

	public void recreateThemePropertiesDirectory() {
		try {
			FileUtils.deleteDirectory(new File(Naming.getThemePropsDir()));
			new File(Naming.getThemePropsDir()).mkdirs();
		} catch (IOException e) {
			logger.error("Error while saving config to: " + Naming.getThemePropsDir());
		}
	}
}

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

import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @see TODO
 * @version 0.1
 */
public class InitRuntime {
	private File appHome = null;
	private ArrayList dirList = new ArrayList();
	private static final Logger logger = Logger.getLogger(InitRuntime.class);

	/**
	 * 
	 */
	public InitRuntime() {
		dirList.add(Naming.HOME_PATH);
		dirList.add(Naming.HTML_QURAN_CACHE_DIR);
		dirList.add(Naming.HTML_SEARCH_CACHE_DIR);
		dirList.add(Naming.CONFIG_PATH);
	}

	/**
	 * Makes the application home directory
	 * 
	 * @throws IOException
	 */
	public void configureDirectories() throws IOException {
		logger.info("Making application required directories (if not exist)...");
		recreateHtmlCache();

		for (Iterator iter = dirList.iterator(); iter.hasNext();) {
			File file = new File((String) iter.next());
			if (!file.exists() && !file.mkdirs())
				throw new IOException("Can not create application home directory at \'"
						+ appHome.getAbsoluteFile() + "\'");
		}
	}

	public static void recreateHtmlCache() throws IOException {
		logger.info("Recreate HTML cache directory.");

		File cache = new File(Naming.HTML_QURAN_CACHE_DIR);
		if (cache.exists())
			if (!FileUtils.delete(cache))
				throw new IOException("Can not delete HTML cache directory at \'"
						+ Naming.HTML_QURAN_CACHE_DIR + "\'");
		
		cache.mkdir();
	}

}

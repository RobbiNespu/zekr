/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 2, 2006
 */
package net.sf.zekr.engine.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.ThemeData;

import org.apache.commons.io.IOUtils;

/**
 * @author Mohsen Saboorian
 */
public class ThemeTemplate extends BaseViewTemplate {

	private static final Logger logger = Logger.getLogger(ThemeTemplate.class);
	private ThemeData themeData;

	public ThemeTemplate(ThemeData themeData) {
		this.themeData = themeData;
	}

	/**
	 * Transforms and persists all the theme CSS files if doesn't exists in the cache (
	 * <tt>Naming.CACHE_DIR</tt>).
	 * 
	 * @return result CSS, or null if no transformation done
	 */
	public String doTransform() {
		String retStr = null;
		String[] cssFileNames = resource.getStrings("theme.css.fileName");
		for (int i = 0; i < cssFileNames.length; i++) {
			File destFile = new File(Naming.getViewCacheDir() + "/" + cssFileNames[i]);

			// create destination CSS file if it doesn't exist
			if (!destFile.exists() || destFile.length() == 0) {
				logger.debug("Theme CSS doesn't exist, will create it: " + cssFileNames[i]);
				File srcFile = new File(themeData.getPath() + "/" + resource.getString("theme.cssDir") + "/"
						+ cssFileNames[i]);
				if (config.getTranslation().getDefault() != null) {
					themeData.process(config.getTranslation().getDefault().locale.getLanguage());
				} else {
					themeData.process("en");
				}
				engine.putAll(themeData.processedProps);

				try {
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile));
					retStr = engine.getUpdated(srcFile.getPath());
					osw.write(retStr);
					IOUtils.closeQuietly(osw);
				} catch (Exception e) {
					Logger.getLogger(this.getClass()).log(e);
				}
			}
		}
		return retStr;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 2, 2006
 */
package net.sf.zekr.engine.theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.velocity.tools.generic.MathTool;

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ThemeTemplate extends BaseViewTemplate {

	private ThemeData themeData;

	public ThemeTemplate(ThemeData themeData) {
		this.themeData = themeData;
	}

	/**
	 * Transforms and persists the theme CSS file if doesn't exists in the cache (<tt>Naming.CACHE_DIR</tt>).
	 * 
	 * @return result CSS, or null if no transformation done
	 */
	public String transform() {
		String retStr = null;
		File destFile = new File(Naming.CACHE_DIR + "/" + resource.getString("theme.css.fileName"));

		// create destination CSS file if it doesn't exist
		if (!destFile.exists() || destFile.length() == 0) {
			File srcFile = new File(resource.getString("theme.css", new String[] { themeData.id }));
			themeData.process(config.getTranslation().getDefault().locale.getLanguage());
			engine.putAll(themeData.processedProps);

			try {
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile));
				retStr = engine.getUpdated(srcFile.getPath());
				osw.write(retStr);
				osw.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).log(e);
			}
		}
		return retStr;
	}
}

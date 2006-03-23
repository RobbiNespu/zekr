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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ThemeTemplate extends BaseViewTemplate {

	public String transform(ThemeData themeData) {
		String retStr = null;
		File srcFile = new File(resource.getString("theme.css", new String[] { themeData.id }));
		File destFile = new File(Naming.CACHE_DIR + "/" + srcFile.getName());

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
		return retStr;
	}
}

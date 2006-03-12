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
		Map overwrites = new HashMap();
		Map props = themeData.props;
		String lang = config.getTranslation().getDefault().locale.getLanguage();
		File srcFile = new File(resource.getString("theme.css", new String[] { themeData.id }));
		File destFile = new File(Naming.CACHE_DIR + File.separator + srcFile.getName());
		if (destFile.exists())
			destFile.delete();
		for (Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			String key = (String) entry.getKey();
			int index;
			StringBuffer sb = new StringBuffer(key);
			if ((index = key.indexOf("_" + lang + "_")) != -1)
				overwrites.put(sb.replace(index, index + 3, "").toString(), entry.getValue());
			else
				engine.put((String) entry.getKey(), entry.getValue());
		}
		for (Iterator iter = overwrites.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			engine.put((String) entry.getKey(), entry.getValue());
		}
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

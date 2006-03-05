/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 2, 2006
 */
package net.sf.zekr.engine.theme;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.engine.log.Logger;

public class ThemeTemplate extends BaseViewTemplate {
	public String transform(String file, Map props) {
		String retStr = null;
		for (Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			engine.put((String) entry.getKey(), entry.getValue());
		}
		try {
			retStr = engine.getUpdated(file);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		return retStr;
	}
}

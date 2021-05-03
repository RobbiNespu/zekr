/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2006
 */
package net.sf.zekr.engine.theme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Mohsen Saboorian
 */
public class ThemeData {
	/**
	 * Configuration properties set in <tt>theme.properties</tt> in the theme folder.
	 */
	public Map<String, String> props;

	/**
	 * Processed (localized) properties, extracted from <code>props</code>.
	 */
	public Map<String, Object> processedProps;

	/** Theme name */
	public String name;

	/**
	 * Unique identifier for this theme. This should always be equal with the folder name of the theme data
	 * files (on res/theme/).
	 */
	public String id;

	/** Theme author */
	public String author;

	public String fileName;

	public String baseDir;

	/** Theme descriptor version */
	public String version;

	/**
	 * @return application relative theme path (e.g. <tt>res/theme/default</tt>). This is the directory of the
	 *         original theme content on Zekr installed dir.
	 */
	public String getPath() {
		return baseDir + "/" + id;
	}

	/**
	 * Will fill <code>processedProps</code> from <code>props</code> field
	 * 
	 * @param transLang default translation language
	 */
	public void process(String transLang) {
		processedProps = new HashMap<String, Object>();
		for (Iterator<Entry<String, String>> iter = props.entrySet().iterator(); iter.hasNext();) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			int index;
			StringBuffer sb = new StringBuffer(key);
			if ((index = key.indexOf("_" + transLang + "_")) != -1)
				processedProps.put(sb.replace(index, index + 3, "").toString(), entry.getValue());
			else
				processedProps.put(entry.getKey(), entry.getValue());
		}
		processedProps.remove("version");
	}

	public String toString() {
		return name + " - " + id;
	}

	public String getName() {
		return name;
	}
}

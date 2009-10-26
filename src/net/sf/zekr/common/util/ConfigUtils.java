/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 22, 2006
 */
package net.sf.zekr.common.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.zekr.common.config.GlobalConfig;

import org.apache.commons.configuration.Configuration;

/**
 * @author Mohsen Saboorian
 */
public class ConfigUtils {
	public static void write(Properties p, Writer w) {
	}

	public static void write(Configuration configuration, Writer w) throws IOException {
		Iterator keys = configuration.getKeys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = configuration.getProperty(key);
			w.write(key);
			w.write(" = ");
			if (value instanceof Collection) {
				w.write(CollectionUtils.toString((List) value, ", "));
			} else {
				w.write(value.toString());
			}

			if (keys.hasNext()) {
				w.write(GlobalConfig.LINE_SEPARATOR);
			}
		}
	}

}

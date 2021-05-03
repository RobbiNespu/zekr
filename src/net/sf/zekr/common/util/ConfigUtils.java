/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 22, 2006
 */
package net.sf.zekr.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.config.GlobalConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ObjectUtils;

/**
 * @author Mohsen Saboorian
 */
public class ConfigUtils {
	@SuppressWarnings("unchecked")
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
				w.write(ObjectUtils.toString(value));
			}

			if (keys.hasNext()) {
				w.write(GlobalConfig.LINE_SEPARATOR);
			}
		}
	}

	public static PropertiesConfiguration loadConfig(File configFile, String encoding) throws ConfigurationException,
			IOException {
		return loadConfig(configFile, null, encoding);
	}

	public static PropertiesConfiguration loadConfig(InputStream configStream, String encoding)
			throws ConfigurationException, IOException {
		return loadConfig(configStream, null, encoding);
	}

	public static PropertiesConfiguration loadConfig(File configFile, String basePath, String encoding)
			throws ConfigurationException, IOException {
		return loadConfig(new FileInputStream(configFile), basePath, encoding);
	}

	/**
	 * Loads a configuration properties file (configStream) and close it.
	 * 
	 * @param configStream
	 * @param basePath
	 * @param encoding
	 * @return
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	public static PropertiesConfiguration loadConfig(InputStream configStream, String basePath, String encoding)
			throws ConfigurationException, IOException {
		PropertiesConfiguration pc = new PropertiesConfiguration();
		pc.setThrowExceptionOnMissing(false); // this is the default behavior. just for MOHKAM KARI!
		pc.setEncoding("UTF-8");
		if (basePath != null) {
			pc.setBasePath(basePath);
		}
		pc.load(configStream);
		configStream.close();
		return pc;
	}
}

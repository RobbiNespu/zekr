/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2006
 */
package net.sf.zekr.engine.theme;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.util.ConfigUtils;

import org.apache.commons.configuration.MapConfiguration;

public class Theme {
	private ThemeData current;
	private Map themes = new HashMap();

	public Map commonProps = new LinkedHashMap();

	public void add(ThemeData td) {
		themes.put(td.id, td);
	}

	public ThemeData get(String themeId) {
		return (ThemeData) themes.get(themeId);
	}

	public Collection getAllThemes() {
		return themes.values();
	}

	public void setCurrent(ThemeData currentTheme) {
		current = currentTheme;
	}

	public ThemeData getCurrent() {
		return current;
	}

	/**
	 * Save a ThemeData configuration file.
	 * 
	 * @param td theme data object to be stored to the disk
	 * @throws IOException
	 */
	public static void save(ThemeData td) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
				ApplicationPath.THEME_DIR + "/" + td.id + "/" + ApplicationPath.THEME_DESC));
		LinkedHashMap map = new LinkedHashMap();
		map.put("name", td.name);
		map.put("author", td.author);
		map.putAll(td.props);
		ConfigUtils.write(new MapConfiguration(map), osw);
		osw.close();
	}
}
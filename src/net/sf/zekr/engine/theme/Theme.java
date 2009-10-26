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

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.ConfigUtils;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;

/**
 * This file holds all the theme data loaded at the startup. Themes are specific directories (now in
 * <tt>res/ui/theme/</tt>) with a <tt>theme.properties</tt> file in it. When importing themes, a zip file
 * ([theme ID].zip), is imported and extracted in theme location on base zekr installation directory, and a
 * copy of theme.properties is copied to user's zekr home.
 * 
 * @author Mohsen Saboorian
 */
public class Theme {
	private ThemeData current;
	private Map<String, ThemeData> themes = new HashMap<String, ThemeData>();

	public Map commonProps = new LinkedHashMap();

	public void add(ThemeData td) {
		themes.put(td.id, td);
	}

	public ThemeData get(String themeId) {
		return themes.get(themeId);
	}

	public Collection<ThemeData> getAllThemes() {
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
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(Naming.getThemePropsDir() + "/"
				+ td.fileName));
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", td.name);
		map.put("author", td.author);
		map.put("version", td.version);
		map.putAll(td.props);
		ConfigUtils.write(new MapConfiguration(map), osw);
		// ConfigurationUtils.dump(new MapConfiguration(map), new PrintWriter(osw));
		IOUtils.closeQuietly(osw);
	}
}

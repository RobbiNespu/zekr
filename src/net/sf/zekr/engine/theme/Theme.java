/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2006
 */
package net.sf.zekr.engine.theme;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.zekr.common.resource.TranslationData;

public class Theme {
	private ThemeData current;
	private Map themes = new HashMap();

	public Map commonProps = new HashMap();

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
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     ۲۰۰۸/۶/۲۷
 */
package net.sf.zekr.engine.common;

import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;

/**
 * This class can be extended by resources (like recitation, translation, revelations) in order to add
 * localization support to their name field. <br>
 * <br>
 * TODO: this feature is incomplete.
 * 
 * @author Mohsen Saboorian
 */
public class LocalizedResource {
	protected Map names = new HashMap();

	public Map getNames() {
		return names;
	}

	public String getName(String langCode) {
		return (String) names.get(langCode);
	}

	/**
	 * Cautious: this method should only be called upon instantiation of {@link ApplicationConfig}.
	 * 
	 * @return localized resource name
	 */
	public String getLocalizedName() {
		return getName(LanguageEngine.getInstance().getLocale().getLanguage());
	}
}

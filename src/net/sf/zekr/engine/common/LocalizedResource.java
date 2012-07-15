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
import java.util.Iterator;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * This class can be extended by resources (like recitation, translation, revelations) in order to add
 * localization support to their name field. <br>
 * <br>
 * TODO: this feature is incomplete.
 * 
 * @author Mohsen Saboorian
 */
public class LocalizedResource {
	/*** A map of language ISO code to localized name of reciter in that language */
	public Map<String, String> localizedNameMap = new HashMap<String, String>();
	public String name;
	public String language;

	
	public String getLanguage() {
		if(language==null) return "unknown";
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<String, String> getLocalizedNameMap() {
		return localizedNameMap;
	}

	public String getName(String langCode) {
		return localizedNameMap.get(langCode);
	}

	public String getName() {
		return name;
	}

	/**
	 * Cautious: this method should only be called upon instantiation of {@link ApplicationConfig}.
	 * 
	 * @return localized resource name (from {@link #localizedNameMap}) or {@link #name} if there is nothing
	 *         localized for this language.
	 */
	public String getLocalizedName() {
		String langCode = LanguageEngine.getInstance().getLocale().getLanguage();
		return localizedNameMap.containsKey(langCode) ? getName(langCode) : getName();
	}

	@SuppressWarnings("unchecked")
	public void loadLocalizedNames(PropertiesConfiguration pc, String namePrefix) {
		name = pc.getString(namePrefix);
		Iterator<String> keys = pc.getKeys(namePrefix);
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.equals(namePrefix)) {
				continue;
			}
			String lang = key.substring(namePrefix.length() + 1);
			localizedNameMap.put(lang, pc.getString(key));
		}
	}
}

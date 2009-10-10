/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 12, 2004
 */

package net.sf.zekr.engine.language;

import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationPath;

/**
 * This class is singleton.
 * 
 * @author Mohsen Saboorian
 */
public class Language {
	private static Language thisInstance;
	private LanguagePack currentLangPack;
	private Map<String, LanguagePack> languageMap = new HashMap<String, LanguagePack>();
	private String defaultPackId;

	private Language() {
	}

	public static Language getInstance() {
		if (thisInstance == null)
			thisInstance = new Language();
		return thisInstance;
	}

	public String getPackPath() {
		return getPackPath(currentLangPack);
	}

	public static String getPackPath(LanguagePack langPack) {
		return ApplicationPath.LANGUAGE_DIR + "/" + langPack.getFile();
	}

	public LanguagePack getActiveLanguagePack() {
		return currentLangPack;
	}

	public void setActiveLanguagePack(LanguagePack langPack) {
		this.currentLangPack = langPack;
	}

	public void setActiveLanguagePack(String langId) {
		setActiveLanguagePack(languageMap.get(langId));
	}

	public LanguagePack get(String id) {
		return languageMap.get(id);
	}

	public Map<String, LanguagePack> getLanguageMap() {
		return languageMap;
	}

	public void add(LanguagePack lp) {
		languageMap.put(lp.id, lp);
	}
}

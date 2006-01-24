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
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class Language {
	private String langPackId;
	private String currentLang;
	private String packId;
	private LanguagePack activeLangPack;
	private Map languageMap = new HashMap();
	private String defaultPackId;

//	private final static Logger logger = Logger.getLogger(Language.class);

//	/**
//	 * Instantiate a new language with current (last time set) language pack. If fails, will load
//	 * default language pack.
//	 */
	public Language() {
//		ApplicationConfig config = ApplicationConfig.getInsatnce();
//		langNode = config.getLanguageNode();
//		currentLang = XmlUtils.getAttr(langNode, ApplicationConfig.CURRENT_LANGUAGE_ATTR);
//		defaultLang = XmlUtils.getAttr(langNode, ApplicationConfig.DEFAULT_LANGUAGE_ATTR);
//		if (currentLang.equals(LanguageEngineNaming.UNDEFINED))
//			currentLang = RuntimeUtilities.USER_LANGUAGE;
//		langPackNode = XmlUtils.getNodeByNamedAttr(langNode.getChildNodes(),
//			ApplicationConfig.LANGUAGE_PACK_TAG, ApplicationConfig.ID_ATTR, currentLang);
//		if (langPackNode == null) { // language pack details not found
//			logger.error("Can not find the language pack with id=\"" + currentLang + "\".");
//			logger.error("Will load the default language pack instead.");
//			langPackNode = XmlUtils.getNodeByNamedAttr(langNode.getChildNodes(),
//				ApplicationConfig.LANGUAGE_PACK_TAG, ApplicationConfig.ID_ATTR, defaultLang);
//			langPackId = defaultLang;
//		} else {
//			langPackId = currentLang;
//		}
	}

//	/**
//	 * Instantiate a new language with the given <code>langName</code>.
//	 * 
//	 * @param langId
//	 *            the 2-character language pack ID which should be unique (eg. fa for
//	 *            Farsi, or en for English)
//	 * @throws LanguagePackException
//	 *             if the language pack with ID equal to <code>langId</code> does not
//	 *             exists
//	 */
//	public Language(String langId) throws LanguagePackException {
//		ApplicationConfig config = ApplicationConfig.getInsatnce();
//		langNode = config.getLanguageNode();
//		langPackNode = XmlUtils.getNodeByNamedAttr(langNode.getChildNodes(),
//			ApplicationConfig.LANGUAGE_PACK_TAG, ApplicationConfig.ID_ATTR, langId);
//		langPackId = langId;
//		if (langPackNode == null)
//			throw new LanguagePackException("Can not find language pack for language " + langId);
//	}

	public String getPackPath() {
		return getPackPath(activeLangPack);
	}
	
	public static String getPackPath(LanguagePack langPack) {
		return ApplicationPath.LANGUAGE_DIR + langPack.getFile();		
	}

	public LanguagePack getActiveLanguagePack() {
		return activeLangPack;
	}

	public void setActiveLanguagePack(LanguagePack langPack) {
		this.activeLangPack = langPack;
	}

	public void setActiveLanguagePack(String langId) {
		setActiveLanguagePack((LanguagePack) languageMap.get(langId));
	}

	public LanguagePack getDefaultLanguagePack() {
		return (LanguagePack) languageMap.get(defaultPackId);
	}
	
	public void setDefaultLanguagePackId(String defaultId) {
		defaultPackId = defaultId;
	}

	public Map getLanguageMap() {
		return languageMap;
	}
}

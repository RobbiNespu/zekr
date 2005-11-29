/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 10, 2004
 */

package net.sf.zekr.common.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import net.sf.zekr.common.resource.dynamic.QuranHTMLRepository;
import net.sf.zekr.common.runtime.InitRuntime;
import net.sf.zekr.common.runtime.RuntimeConfig;
import net.sf.zekr.common.runtime.RuntimeUtilities;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguageEngineNaming;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;
import net.sf.zekr.engine.xml.XmlWriter;

import org.w3c.dom.Node;

/**
 * This singleton class reads the config files by the first invocation of
 * <code>getInsatnce()</code>. You can then read any option by using available getter
 * methods.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class ApplicationConfig extends ZekrConfigNaming {
	private RuntimeConfig runtimeConfig = new RuntimeConfig();
	
    // TODO: make objects for each major xml tag (i.e. view) so that holds 
    // values corresponding to its descendents
	private XmlReader configReader;
	private LanguageEngine langEngine;
	private Language language;
	private ArrayList availableLanguages;

	private static final Logger logger = Logger.getLogger(ApplicationConfig.class);
	private static ApplicationConfig thisInstance;

	Node langNode;
	Node quranNode;
	Node viewNode;

	private ApplicationConfig() {
		logger.info("Initializing application configurations...");
		configReader = new XmlReader(ApplicationPath.CONFIG_FILE);
		langNode = configReader.getNode(LANGUAGE_TAG);
		quranNode = configReader.getNode(QURAN_TAG);
		viewNode = configReader.getNode(VIEW_TAG);
		extractLangProp();
		extractRuntimeConfig();
	}

	public static ApplicationConfig getInsatnce() {
		if (thisInstance == null)
			thisInstance = new ApplicationConfig();
		return thisInstance;
	}
	
	/**
	 * Will be called when any setXXX() method has been called to be taken effect.
	 */
	public void reload() {
		thisInstance = new ApplicationConfig();
	}

	/**
	 * This method extracts the application runtime configurations and store it into
	 * RuntimeConfig bean.
	 */
	private void extractRuntimeConfig() {
		runtimeConfig.setLanguage(XmlUtils.getAttr(langNode, ApplicationConfig.CURRENT_LANGUAGE_ATTR));
		runtimeConfig.setTextLayout(getQuranTextLayout());
	}

	/**
	 * This method extracts language properties from the corresponding node in the config
	 * file.
	 */
	private void extractLangProp() {
		language = new Language();

		logger.info("Loading language pack info.");
		NodeList list = XmlUtils.getNodes(langNode, LANGUAGE_PACK_TAG);
		LanguagePack pack;
		Node node;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			node = (Node) iter.next();
			pack = new LanguagePack();
			pack.setId(XmlUtils.getAttr(node, ID_ATTR));
			pack.setName(XmlUtils.getAttr(node, NAME_ATTR));
			pack.setLatinName(XmlUtils.getAttr(node, LATIN_NAME_ATTR));
			pack.setFile(XmlUtils.getAttr(node, FILE_ATTR));
			pack.setIcon(XmlUtils.getAttr(node, ICON_ATTR));
			language.getLanguageMap().put(pack.getId(), pack);
		}
		logger.info("Available languages are: " + language.getLanguageMap().entrySet());

		String currentLangId = XmlUtils.getAttr(langNode, CURRENT_LANGUAGE_ATTR);
		String defaultLangId = XmlUtils.getAttr(langNode, DEFAULT_LANGUAGE_ATTR);
		if (defaultLangId == null) 
			logger.error("Default language pack should be set.");
		language.setDefaultLanguagePackId(defaultLangId);
		if (currentLangId.equals(LanguageEngineNaming.UNDEFINED))
			currentLangId = RuntimeUtilities.USER_LANGUAGE;
		Node langPackNode = XmlUtils.getNodeByNamedAttr(langNode.getChildNodes(), LANGUAGE_PACK_TAG, ID_ATTR, currentLangId);
		if (langPackNode == null) { // language pack details not found
			logger.error("Can not find the language pack with id=\"" + currentLangId + "\".");
			logger.error("Will load the default language pack instead.");
			langPackNode = XmlUtils.getNodeByNamedAttr(langNode.getChildNodes(),
					LANGUAGE_PACK_TAG, ID_ATTR, defaultLangId);
			language.setActiveLanguagePack((LanguagePack) language.getLanguageMap().get(defaultLangId));
		} else {
			language.setActiveLanguagePack((LanguagePack) language.getLanguageMap().get(currentLangId));
		}
	}
	
	public Collection getAvailableLanguages() {
		if (availableLanguages != null)
			return availableLanguages;

		Map langMap = language.getLanguageMap();
		availableLanguages = new ArrayList();
		for (Iterator iter = langMap.entrySet().iterator(); iter.hasNext();) {
			Entry element = (Entry) iter.next();
			availableLanguages.add(element.getValue());
		}
		return availableLanguages;
	}

	public Node getLanguageNode() {
		return langNode;
	}

	/**
	 * @return application language engine
	 * @see net.sf.zekr.engine.language#getInstance()
	 */
	public LanguageEngine getLanguageEngine() {
		if (langEngine == null)
			langEngine = LanguageEngine.getInstance();
		return langEngine;
	}
	
	public void setCurrentLanguage(String langId) {
		language.setActiveLanguagePack(langId);
		LanguageEngine.getInstance().reload();
		try {
			InitRuntime.recreateHtmlCache();
		} catch (IOException e) {
			logger.log(e);
		}
		XmlUtils.setAttr(langNode, CURRENT_LANGUAGE_ATTR, langId);
	}

	/**
	 * @param id config file id
	 * @return
	 */
	public String getConfigFile(String id) {
		Node node = XmlUtils.getNodeByNamedAttr(XmlUtils.getNodes(quranNode, QURAN_CONFIG_TAG),
			QURAN_CONFIG_TAG, ID_ATTR, id);
		return ApplicationPath.RESOURCE_DIR + XmlUtils.getAttr(node, FILE_ATTR);
	}

	public String getQuranText() {
		Node node = XmlUtils.getNodeByNamedAttr(XmlUtils.getNodes(quranNode, QURAN_TEXT_TAG),
			QURAN_TEXT_TAG, ID_ATTR, QURAN_ARABIC_TEXT_ID);

		return ApplicationPath.RESOURCE_DIR + XmlUtils.getAttr(node, FILE_ATTR);
	}

	public String getQuranTextLayout() {
		Node node = XmlUtils.getNodeByNamedAttr(XmlUtils.getNodes(viewNode, ITEM_TAG), ITEM_TAG,
			ID_ATTR, TEXT_LAYOUT_ID);
		return XmlUtils.getAttr(node, VALUE_ATTR);
	}
	
	public void setQuranTextLayout(String newLayout) {
		Node n = XmlUtils.getNodeByNamedAttr(XmlUtils.getNodes(viewNode, ITEM_TAG), ITEM_TAG,
				ID_ATTR, TEXT_LAYOUT_ID);
		XmlUtils.setAttr(n, VALUE_ATTR, newLayout);
	}

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public Language getLanguage() {
		return language;
	}

	public void updateFile() {
		try {
			XmlWriter.writeXML(configReader.getDocument(), new File(ApplicationPath.CONFIG_FILE));
		} catch (TransformerException e) {
			logger.log(e);
		}
	}
}

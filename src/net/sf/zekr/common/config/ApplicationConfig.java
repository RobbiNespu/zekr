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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.TransformerException;

import net.sf.zekr.common.resource.Translation;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.runtime.ApplicationRuntime;
import net.sf.zekr.common.runtime.RuntimeConfig;
import net.sf.zekr.common.util.QuranLocation;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.xml.NodeList;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;
import net.sf.zekr.engine.xml.XmlWriter;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This singleton class reads the config files by the first invocation of
 * <code>getInstance()</code>. You can then read any option by using available getter
 * methods.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ApplicationConfig extends ZekrConfigNaming {
	private final Logger logger = Logger.getLogger(this.getClass());
	private static ApplicationConfig thisInstance;

	private RuntimeConfig runtimeConfig = new RuntimeConfig();

	private XmlReader configReader;
	private LanguageEngine langEngine;
	private Language language;
	private ArrayList availableLanguages;

	private Element langElem;
	private Element quranElem;
	private Element viewElem;
	private Element transElem;

	private Translation translation = new Translation();
	private Theme theme = new Theme();
	private ApplicationRuntime runtime;
	private QuranLocation quranLocation;

	private ApplicationConfig() {
		logger.info("Initializing application configurations...");
		configReader = new XmlReader(ApplicationPath.CONFIG_FILE);
		langElem = configReader.getElement(LANGUAGE_TAG);
		quranElem = configReader.getElement(QURAN_TAG);
		viewElem = configReader.getElement(VIEW_TAG);
		transElem = configReader.getElement(TRANSLATIONS_TAG);
		extractLangProps();
		extractRuntimeConfig();
		extractTransProps();
		extractViewProps();

		runtime = new ApplicationRuntime();
	}

	public static ApplicationConfig getInstance() {
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
		runtimeConfig.setLanguage(langElem.getAttribute(ApplicationConfig.CURRENT_LANGUAGE_ATTR));
		runtimeConfig.setTextLayout(getQuranLayout());
	}

	/**
	 * This method extracts language properties from the corresponding node in the config
	 * file.
	 */
	private void extractLangProps() {
		language = new Language();
		boolean update = false;

		logger.info("Loading language pack info.");
		NodeList list = XmlUtils.getNodes(langElem, LANGUAGE_PACK_TAG);
		LanguagePack pack;
		Node node;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			node = (Node) iter.next();
			pack = new LanguagePack();
			pack.setId(XmlUtils.getAttr(node, ID_ATTR));
			pack.setName(XmlUtils.getAttr(node, NAME_ATTR));
			pack.setLatinName(XmlUtils.getAttr(node, LATIN_NAME_ATTR));
			pack.setFile(XmlUtils.getAttr(node, FILE_ATTR));
			language.getLanguageMap().put(pack.getId(), pack);
		}
		logger.info("Available languages are: " + language.getLanguageMap().values());

		String currentLangId = langElem.getAttribute(CURRENT_LANGUAGE_ATTR);
		String defaultLangId = langElem.getAttribute(DEFAULT_ATTR);
		if ("".equals(langElem.getAttribute(DEFAULT_ATTR))) {
			logger.warn("Can not find default language pack. will set default to \"en\".");
			langElem.setAttribute(DEFAULT_ATTR, "en_US");
			defaultLangId = "en_US";
			update = true;
		}
		language.setDefaultLanguagePackId(defaultLangId);
		if ("".equals(langElem.getAttribute(CURRENT_LANGUAGE_ATTR))) {
			currentLangId = GlobalConfig.USER_LANGUAGE + "_" + GlobalConfig.USER_COUNTRY;
			logger.warn("Current language will be set to \"" + currentLangId + "\".");
			langElem.setAttribute(CURRENT_LANGUAGE_ATTR, currentLangId);
			update = true;
		}
		Node langPackNode = XmlUtils.getElementByNamedAttr(langElem.getChildNodes(),
				LANGUAGE_PACK_TAG, ID_ATTR, currentLangId);
		if (langPackNode == null) { // language pack details not found
			logger.error("Can not find the language pack with id=\"" + currentLangId + "\".");
			logger.error("Will load the default language pack instead.");
			langPackNode = XmlUtils.getElementByNamedAttr(langElem.getChildNodes(),
					LANGUAGE_PACK_TAG, ID_ATTR, defaultLangId);
			language.setActiveLanguagePack((LanguagePack) language.getLanguageMap().get(
					defaultLangId));
		} else {
			language.setActiveLanguagePack((LanguagePack) language.getLanguageMap().get(
					currentLangId));
		}

		if (update)
			updateFile();
	}

	/**
	 * This method extracts translation properties from the corresponding node in the
	 * config file.
	 */
	private void extractTransProps() {
		String def = transElem.getAttribute(DEFAULT_ATTR);
		File transDir = new File(ApplicationPath.TRANSLATION_DIR);
		logger.info("Loading translation files info from \"" + transDir + "\".");
		FileFilter filter = new FileFilter() { // accept zip files
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".zip"))
					return true;
				return false;
			}
		};
		File[] trans = transDir.listFiles(filter);
		TranslationData td;
		ZipFile zipFile = null;
		for (int i = 0; i < trans.length; i++) {
			try {
				zipFile = new ZipFile(trans[i]);
				InputStream is = zipFile.getInputStream(new ZipEntry(
						ApplicationPath.TRANSLATION_DESC));
				// ZipInputStream zis = new ZipInputStream();
				if (is == null) {
					logger.warn("Ignoring invalid translation archive \"" + zipFile + "\".");
					continue;
				}
				Reader reader = new InputStreamReader(is, "UTF8");
				PropertiesConfiguration pc = new PropertiesConfiguration();
				pc.load(reader);
				td = new TranslationData();
				td.id = pc.getString(ID_ATTR);
				td.locale = new Locale(pc.getString(LANG_ATTR), pc.getString(COUNTRY_ATTR));
				td.encoding = pc.getString(ENCODING_ATTR);
				td.direction = pc.getString(DIRECTION_ATTR);
				td.file = pc.getString(FILE_ATTR);
				td.name = pc.getString(NAME_ATTR);
				td.localizedName = pc.getString(LOCALIZED_NAME_ATTR);
				td.archiveFile = zipFile;
				td.lineDelimiter = pc.getString(LINE_DELIMITER_ATTR);
				if (td.localizedName == null)
					td.localizedName = td.name;

				translation.add(td);
				if (td.id.equals(def))
					translation.setDefault(td);

			} catch (Exception e) {
				logger.warn("Can not load translation pack \"" + zipFile
						+ "\" properly because of the following exception:");
				logger.implicitLog(e);
			}
		}

		// for (Iterator iter = list.iterator(); iter.hasNext();) {
		// Element elem = (Element) iter.next();
		// td = new TranslationData();
		// td.locale = new Locale(elem.getAttribute(LANG_ATTR),
		// elem.getAttribute(COUNTRY_ATTR));
		// td.transId = elem.getAttribute(TRANS_ID_ATTR);
		// td.encoding = elem.getAttribute(ENCODING_ATTR);
		// td.file = elem.getAttribute(FILE_ATTR);
		// td.name = elem.getAttribute(NAME_ATTR);
		// td.localizedName = elem.getAttribute(LOCALIZED_NAME_ATTR);
		// if (td.localizedName == null)
		// td.localizedName = td.name;
		// translation.add(td);
		// if (td.transId.equals(def))
		// translation.setDefault(td);
		// }
	}

	private void extractViewProps() {
		String def = viewElem.getAttribute(THEME_ATTR);
		File themeDir = new File(ApplicationPath.BASE_THEME_DIR);
		logger.info("Loading theme files info from \"" + themeDir + "\".");
		File[] themes = themeDir.listFiles();
		ThemeData td;

		NodeList nl = XmlUtils.getNodes(viewElem, PROP_TAG);
		for (int i = 0; i < nl.getLength(); i++) {
			Element el = (Element) nl.item(i);
			theme.commonProps.put(el.getAttribute(NAME_ATTR), el.getAttribute(VALUE_ATTR));
		}

		Reader reader;
		for (int i = 0; i < themes.length; i++) {
			if (themes[i].isFile())
				continue;
			try {
				File themeDesc = new File(themes[i] + File.separator + ApplicationPath.THEME_DESC);
				if (!themeDesc.exists())
					continue;
				reader = new InputStreamReader(new FileInputStream(themeDesc), "UTF8");
				PropertiesConfiguration pc = new PropertiesConfiguration();
				pc.load(reader);
				PropertiesConfiguration.setDelimiter('@'); // TODO!
				td = new ThemeData();
				for (Iterator iter = pc.getKeys(); iter.hasNext();) {
					String key = (String) iter.next();
					td.props.put(key, pc.getString(key));
				}

				td.author = pc.getString("author");
				td.name = pc.getString("name");
				td.id = themes[i].getName();
				td.props.remove("author");
				td.props.remove("name");

				theme.add(td);
				logger.info("Theme \"" + td + "\" was loaded successfully.");
				if (td.id.equals(def))
					theme.setCurrent(td);
			} catch (Exception e) {
				logger.warn("Can not load theme \"" + themes[i].getName()
						+ "\" properly because of the following exception:");
				logger.implicitLog(e);
			}
		}
		if (theme.getCurrent() == null) {
			logger.warn("No default theme was set in main configuration file.");
			theme.setCurrent(theme.get("default"));
		}
	}

	public List getAvailableLanguages() {
		if (availableLanguages != null)
			return availableLanguages;

		Map langMap = language.getLanguageMap();
		availableLanguages = new ArrayList();
		for (Iterator iter = langMap.entrySet().iterator(); iter.hasNext();) {
			Entry elem = (Entry) iter.next();
			availableLanguages.add(elem.getValue());
		}
		return availableLanguages;
	}

	public Node getLanguageNode() {
		return langElem;
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
		logger.info("Set current language to " + langId);
		language.setActiveLanguagePack(langId);
		LanguageEngine.getInstance().reload();
		try {
			runtime.recreateCache();
		} catch (IOException e) {
			logger.log(e);
		}
		langElem.setAttribute(CURRENT_LANGUAGE_ATTR, langId);
	}

	public void setCurrentTheme(String themeId) {
		logger.info("Set current theme to " + themeId);
		theme.setCurrent(theme.get(themeId));
		try {
			runtime.recreateCache();
		} catch (IOException e) {
			logger.log(e);
		}
		viewElem.setAttribute(THEME_ATTR, themeId);
	}

	public void setCurrentTranslation(String transId) {
		logger.info("Set current translation to " + transId);
		translation.setDefault(getTranslation().get(transId));
		try {
			runtime.recreateCache();
		} catch (IOException e) {
			logger.log(e);
		}
		transElem.setAttribute(DEFAULT_ATTR, transId);
	}

	/**
	 * @param id config file id
	 * @return config file path
	 */
	public String getConfigFile(String id) {
		Element elem = XmlUtils.getElementByNamedAttr(XmlUtils
				.getNodes(quranElem, QURAN_CONFIG_TAG), QURAN_CONFIG_TAG, ID_ATTR, id);
		return ApplicationPath.RESOURCE_DIR + "/" + elem.getAttribute(FILE_ATTR);
	}

	public String getQuranText() {
		Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(quranElem, QURAN_TEXT_TAG),
				QURAN_TEXT_TAG, ID_ATTR, QURAN_ARABIC_TEXT_ID);

		return ApplicationPath.RESOURCE_DIR + "/" + elem.getAttribute(FILE_ATTR);
	}

	public static String getQuranTrans(String name) {
		return ApplicationPath.TRANSLATION_DIR + "/" + name;
	}

	public String getQuranLayout() {
		return (String) theme.commonProps.get(QURAN_LAYOUT);
	}

	public void setQuranLayout(String newLayout) {
		Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
				PROP_TAG, NAME_ATTR, QURAN_LAYOUT);
		elem.setAttribute(VALUE_ATTR, newLayout);
		theme.commonProps.put(QURAN_LAYOUT, newLayout);
	}

	public QuranLocation getQuranLocation() {
		// return quranLocation;
		return new QuranLocation(theme.commonProps.get(QURAN_LOCATION));
	}

	public void setQuranLocation(QuranLocation quranLocation) {
		Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
				PROP_TAG, NAME_ATTR, QURAN_LOCATION);
		elem.setAttribute(VALUE_ATTR, quranLocation.toString());
		theme.commonProps.put(QURAN_LOCATION, quranLocation.toString());
		// this.quranLocation = quranLocation;
	}

	public String getTransLayout() {
		return (String) theme.commonProps.get(TRANS_LAYOUT);
	}

	public void setTransLayout(String newLayout) {
		Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
				PROP_TAG, NAME_ATTR, TRANS_LAYOUT);
		elem.setAttribute(VALUE_ATTR, newLayout);
		theme.commonProps.put(TRANS_LAYOUT, newLayout);
	}

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public Language getLanguage() {
		return language;
	}

	public void updateFile() {
		logger.info("Update XML configuration file.");
		try {
			XmlWriter.writeXML(configReader.getDocument(), new File(ApplicationPath.CONFIG_FILE));
		} catch (TransformerException e) {
			logger.log(e);
		}
	}

	public Translation getTranslation() {
		return translation;
	}

	public Theme getTheme() {
		return theme;
	}

	public ApplicationRuntime getRuntime() {
		return runtime;
	}

	public void setRuntime(ApplicationRuntime runtime) {
		this.runtime = runtime;
	}

}

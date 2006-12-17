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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.Translation;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.runtime.ApplicationRuntime;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.runtime.RuntimeConfig;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkSetGroup;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.xml.XmlReader;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

/**
 * This singleton class reads the config files by the first invocation of <code>getInstance()</code>. You
 * can then read any option by using available getter methods.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ApplicationConfig extends ConfigNaming {
	private final Logger logger = Logger.getLogger(this.getClass());
	private static ApplicationConfig thisInstance;

	private RuntimeConfig runtimeConfig = new RuntimeConfig();

	private XmlReader configReader;
	private LanguageEngine langEngine;
	private Language language = new Language();

	// private Element langElem;
	// private Element quranElem;
	// private Element viewElem;
	// private Element transElem;

	private Translation translation = new Translation();
	private Theme theme = new Theme();
	private ApplicationRuntime runtime;
	private QuranLocation quranLocation;
	private PropertiesConfiguration props;
	private BookmarkSet bookmarkSet;
	private BookmarkSetGroup bookmarkSetGroup;

	private ApplicationConfig() {
		logger.info("Initializing application configurations...");
		// configReader = new XmlReader(ApplicationPath.XML_CONFIG);
		// langElem = configReader.getElement(LANGUAGE_TAG);
		// quranElem = configReader.getElement(QURAN_TAG);
		// viewElem = configReader.getElement(VIEW_TAG);
		// transElem = configReader.getElement(TRANSLATIONS_TAG);
		loadConfig();
		loadBookmarkSetGroup();
		extractLangProps();
		extractRuntimeConfig();
		extractTransProps();
		extractViewProps();
		runtime = new ApplicationRuntime();
		logger.info("Application configurations initialized.");
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

	private void loadConfig() {
		logger.info("Load Zekr configuration file.");
		File uc = new File(ApplicationPath.USER_CONFIG);
		String conf = ApplicationPath.USER_CONFIG;
		boolean createConfig = false;
		if (!uc.exists()) {
			logger.info("User config does not exist at " + ApplicationPath.USER_CONFIG);
			logger.info("Will make user config with default values at " + ApplicationPath.MAIN_CONFIG);
			conf = ApplicationPath.MAIN_CONFIG;
			createConfig = true;
		}
		try {
			InputStream fis = new FileInputStream(conf);
			Reader reader = new InputStreamReader(fis, "UTF8");
			props = new PropertiesConfiguration();
			props.load(reader);
			if (!GlobalConfig.ZEKR_VERSION.equals(props.getString("version"))) {
				logger.info("User config version does not match with " + GlobalConfig.ZEKR_VERSION);
				logger.info("Will initialize user config with default values from "
						+ ApplicationPath.MAIN_CONFIG);
				conf = ApplicationPath.MAIN_CONFIG;
				fis = new FileInputStream(conf);
				reader = new InputStreamReader(fis, "UTF8");
				props = new PropertiesConfiguration();
				props.load(reader);
				createConfig = true;
			}
		} catch (Exception e) {
			logger.warn("IO Error in loading/reading config file " + ApplicationPath.MAIN_CONFIG);
			logger.implicitLog(e);
		}

		if (createConfig)
			saveConfig();
	}

	private void loadBookmarkSetGroup() {
		File bookmarkDir = new File(Naming.BOOKMARK_PATH);

		// bookmarks
		try {
			File bmDir = new File(Naming.BOOKMARK_PATH);
			if (!bmDir.exists() || !bmDir.isDirectory() || bmDir.list().length == 0)
				FileUtils.deleteDirectory(bmDir);
				FileUtils.copyDirectory(new File(ResourceManager.getInstance().getString("bookmark.baseDir")), 
						bmDir);
		} catch (IOException e) {
			logger.implicitLog(e);
		}
		
		FileFilter filter = new FileFilter() { // accept xml files
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".xml"))
					return true;
				return false;
			}
		};
		File[] bookmarkSets = bookmarkDir.listFiles(filter);
		for (int i = 0; i < bookmarkSets.length; i++) {
			bookmarkSetGroup.addBookmarkSet(new BookmarkSet(bookmarkSets[i]));
		}
		String def = props.getString("bookmark.default");
		bookmarkSetGroup.setAsDefault(def);
		loadDefaultBookmarkSet();
	}

	private void loadDefaultBookmarkSet() {
		bookmarkSetGroup.getDefault().load();
//		File userBM = new File(Naming.BOOKMARK_PATH + "/" + props.getString("bookmark.default"));
//		if (!userBM.exists()) {
//			logger.info("Create bookrmark file.");
//			try {
//				FileUtils.copyFile(new File(ApplicationPath.MAIN_BOOKMARK), userBM);
//			} catch (IOException e) {
//				logger.error("Error while copying the original bookmark file to user config directory.");
//				logger.log(e);
//			}
//		}
//		logger.info("Load bookmarks.");
//		bookmarkSet = new BookmarkSet(new File(ApplicationPath.BUILTIN_USER_BOOKMARK));
		
	}

	/**
	 * Save properties configuration file, wich was read into <code>props</code>, to
	 * <code>ApplicationPath.USER_CONFIGwh</code>.
	 */
	public void saveConfig() {
		try {
			logger.info("Save user config file to " + ApplicationPath.USER_CONFIG);
			props.save(new OutputStreamWriter(new FileOutputStream(ApplicationPath.USER_CONFIG), "UTF8"));
		} catch (Exception e) {
			logger.error("Error while saving config to " + ApplicationPath.USER_CONFIG);
		}
	}

	/**
	 * @return User configuration properties
	 */
	public PropertiesConfiguration getProps() {
		return props;
	}

	/**
	 * This method extracts the application runtime configurations and store it into RuntimeConfig bean.
	 */
	private void extractRuntimeConfig() {
		// runtimeConfig.setLanguage(langElem.getAttribute(ApplicationConfig.CURRENT_LANGUAGE_ATTR));
		runtimeConfig.setTextLayout(getQuranLayout());
	}

	/**
	 * This method extracts language properties from the corresponding node in the config file.
	 */
	private void extractLangProps() {
		boolean update = false;

		String def = props.getString("lang.default");
		File langDir = new File(ApplicationPath.LANGUAGE_DIR);
		logger.info("Loading language pack files info");
		logger.info("Default language pack is " + def);
		FileFilter filter = new FileFilter() { // accept xml files
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".xml"))
					return true;
				return false;
			}
		};
		File[] langs = langDir.listFiles(filter);
		LanguagePack lp;

		logger.info("Found these language packs: " + Arrays.asList(langs));

		for (int i = 0; i < langs.length; i++) {
			XmlReader reader = null;
			try {
				reader = new XmlReader(langs[i]);
			} catch (Exception e) {
				if (langs[i].getName().endsWith("english.xml"))
					logger.doFatal(e);
				else {
					logger.warn("Cannot open language pack " + def + " due to the following error:");
					logger.log(e);
					update = true;
					props.setProperty("lang.default", "en_US");
					def = "en_US";
					logger.warn("Default language pack set to: " + def);
				}
			}
			lp = new LanguagePack();
			lp.file = langs[i].getName();
			Element locale = reader.getElement("locale");
			lp.localizedName = locale.getAttribute("localizedName");
			lp.name = locale.getAttribute("name");
			lp.id = locale.getAttribute("id");
			lp.direction = locale.getAttribute("direction");
			lp.author = reader.getDocumentElement().getAttribute("creator");
			if (lp.localizedName == null)
				lp.localizedName = lp.name;
			language.add(lp);
			if (lp.id.equals(def))
				language.setActiveLanguagePack(def);
		}

		if (update)
			updateFile();
	}

	/**
	 * This method extracts translation properties from the corresponding node in the config file.
	 */
	private void extractTransProps() {
		String def = props.getString("trans.default");
		File transDir = new File(ApplicationPath.TRANSLATION_DIR);
		logger.info("Loading translation files info from \"" + transDir + "\".");
		logger.info("Default translation is " + def);
		FileFilter filter = new FileFilter() { // accept zip files
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".zip"))
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
				InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.TRANSLATION_DESC));
				if (is == null) {
					logger.warn("Will ignore invalid translation archive \"" + zipFile.getName() + "\".");
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
	}

	private void extractViewProps() {
		String def = props.getString("theme.default");
		File themeDir = new File(ApplicationPath.THEME_DIR);
		logger.info("Loading theme files info from \"" + themeDir + "\".");
		File[] themes = themeDir.listFiles();
		ThemeData td;

		Reader reader;
		for (int i = 0; i < themes.length; i++) {
			if (themes[i].isFile()) {
				continue;
			}
			try {
				File themeDesc = new File(themes[i] + File.separator + ApplicationPath.THEME_DESC);
				if (!themeDesc.exists()) {
					logger.warn("\"" + themes[i] + "\" is not a standard theme! Will ignore it.");
					continue;
				}
				reader = new InputStreamReader(new FileInputStream(themeDesc), "UTF8");
				PropertiesConfiguration pc = new PropertiesConfiguration();
				pc.load(reader);
				td = new ThemeData();
				td.props = new LinkedHashMap(); // order is important for options table!
				for (Iterator iter = pc.getKeys(); iter.hasNext();) {
					String key = (String) iter.next();
					td.props.put(key, CollectionUtils.toString(pc.getList(key), ", "));
				}
				td.author = pc.getString("author");
				td.name = pc.getString("name");
				td.id = themes[i].getName();
				td.props.remove("author");
				td.props.remove("name");

				// extractTransProps must be called before it!
				td.process(getTranslation().getDefault().locale.getLanguage());

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
			theme.setCurrent(theme.get("default")); // FIXME ?!!
		}
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
		props.setProperty("lang.default", langId);
		// try {
		// runtime.recreateCache();
		// } catch (IOException e) {
		// logger.log(e);
		// }
		// langElem.setAttribute(CURRENT_LANGUAGE_ATTR, langId);
	}

	public void setCurrentTheme(String themeId) {
		logger.info("Set current theme to " + themeId);
		theme.setCurrent(theme.get(themeId));
		props.setProperty("theme.default", themeId);
		// try {
		// runtime.recreateCache();
		// } catch (IOException e) {
		// logger.log(e);
		// }
		// viewElem.setAttribute(THEME_ATTR, themeId);
	}

	public void setCurrentTranslation(String transId) {
		logger.info("Set current translation to " + transId);
		translation.setDefault(getTranslation().get(transId));
		props.setProperty("trans.default", transId);
		try {
			runtime.recreateCache();
		} catch (IOException e) {
			logger.log(e);
		}
		// transElem.setAttribute(DEFAULT_ATTR, transId);
	}

	public static String getQuranTrans(String name) {
		return ApplicationPath.TRANSLATION_DIR + "/" + name;
	}

	public String getViewProp(String propKey) {
		// return (String) theme.commonProps.get(propKey);
		return props.getString(propKey);
	}

	public void setViewProp(String propKey, String value) {
		// Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
		// PROP_TAG, NAME_ATTR, propKey);
		// elem.setAttribute(VALUE_ATTR, value);
		// theme.commonProps.put(propKey, value);
		props.setProperty(propKey, value);
	}

	public String getQuranLayout() {
		// return (String) theme.commonProps.get(QURAN_LAYOUT);
		return props.getString("view.quranLayout");
	}

	public void setQuranLayout(String newLayout) {
		// Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
		// PROP_TAG, NAME_ATTR, QURAN_LAYOUT);
		// elem.setAttribute(VALUE_ATTR, newLayout);
		// theme.commonProps.put(QURAN_LAYOUT, newLayout);
		props.setProperty("view.quranLayout", newLayout);
	}

	public QuranLocation getQuranLocation() {
		// return new QuranLocation(theme.commonProps.get(QURAN_LOCATION));
		return new QuranLocation(props.getString("view.quranLoc"));
	}

	public void setQuranLocation(QuranLocation quranLocation) {
		// Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
		// PROP_TAG, NAME_ATTR, QURAN_LOCATION);
		// elem.setAttribute(VALUE_ATTR, quranLocation.toString());
		// theme.commonProps.put(QURAN_LOCATION, quranLocation.toString());
		props.setProperty("view.quranLoc", quranLocation);
	}

	public String getTransLayout() {
		// return (String) theme.commonProps.get(TRANS_LAYOUT);
		return props.getString("view.transLayout");
	}

	public void setTransLayout(String newLayout) {
		// Element elem = XmlUtils.getElementByNamedAttr(XmlUtils.getNodes(viewElem, PROP_TAG),
		// PROP_TAG, NAME_ATTR, TRANS_LAYOUT);
		// elem.setAttribute(VALUE_ATTR, newLayout);
		// theme.commonProps.put(TRANS_LAYOUT, newLayout);
		props.setProperty("view.transLayout", newLayout);
	}

	public void setViewLayout(String layout) {
		props.setProperty("view.viewLayout", layout);
	}

	public String getViewLayout() {
		return props.getString("view.viewLayout");
	}

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public Language getLanguage() {
		return language;
	}

	public void updateFile() {
		logger.info("Update configuration file.");
		saveConfig();
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

	public BookmarkSet getBookmark() {
		return bookmarkSet;
	}

}

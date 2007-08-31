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
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.ZekrBaseException;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.runtime.ApplicationRuntime;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.runtime.RuntimeConfig;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.bookmark.BookmarkException;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkSetGroup;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.lucene.IndexCreator;
import net.sf.zekr.engine.search.lucene.IndexingException;
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.Translation;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Element;

/**
 * This singleton class reads the config files by the first invocation of <code>getInstance()</code>. You can then
 * read any option by using available getter methods.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ApplicationConfig implements ConfigNaming {
	private final static Logger logger = Logger.getLogger(ApplicationConfig.class);
	private static ApplicationConfig thisInstance;

	private RuntimeConfig runtimeConfig = new RuntimeConfig();

	private XmlReader configReader;
	private LanguageEngine langEngine;
	private Language language;

	private Translation translation = new Translation();
	private Theme theme = new Theme();
	private ApplicationRuntime runtime;
	private QuranLocation quranLocation;
	private PropertiesConfiguration props;
	private BookmarkSet bookmarkSet;
	private BookmarkSetGroup bookmarkSetGroup = new BookmarkSetGroup();
	private Thread httpServerThread;

	private ApplicationConfig() {
		logger.info("Initializing application configurations...");

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Initializing Language Engine");
		language = Language.getInstance();

		runtime = new ApplicationRuntime();
		// try {
		// runtime.configure();
		// } catch (IOException e) {
		// logger.log(e);
		// }

		// language packs should be loaded before bookmarks
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Configuration Files");
		loadConfig();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Language Packs");
		extractLangProps();

		// extractRuntimeConfig();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Bookmark Sets");
		loadBookmarkSetGroup();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Translation Packs");
		extractTransProps();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading View Properties");
		extractViewProps();

		logger.info("Application configurations initialized.");
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Application UI");

		startHttpServer();
	}

	private void startHttpServer() {
		logger.info("Start HTTP server on port: " + getHttpServerPort());
		httpServerThread = new Thread(HttpServer.getInstance());
		httpServerThread.setDaemon(true);
		httpServerThread.start();
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
			Reader reader = new InputStreamReader(fis, "UTF-8");
			props = new PropertiesConfiguration();
			props.load(reader);
			if (!GlobalConfig.ZEKR_VERSION.equals(props.getString("version"))) {
				logger.info("User config version does not match with " + GlobalConfig.ZEKR_VERSION);
				logger.info("Will initialize user config with default values from " + ApplicationPath.MAIN_CONFIG);
				conf = ApplicationPath.MAIN_CONFIG;
				fis = new FileInputStream(conf);
				reader = new InputStreamReader(fis, "utf-8");
				props = new PropertiesConfiguration();
				props.load(reader);
				createConfig = true;
			}
		} catch (Exception e) {
			logger.warn("IO Error in loading/reading config file " + ApplicationPath.MAIN_CONFIG);
			logger.log(e);
		}

		if (createConfig) {
			// runtime.recreateThemePropertiesDirectory();
			runtime.clearAll();
			saveConfig();
		}
	}

	private void loadBookmarkSetGroup() {
		File bookmarkDir = new File(Naming.getBookmarkDir());
		File origBookmarkDir = new File(ResourceManager.getInstance().getString("bookmark.baseDir"));

		FileFilter xmlFilter = new FileFilter() { // accept .xml files
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(".xml"))
					return true;
				return false;
			}
		};

		// bookmarks
		try {
			if (!bookmarkDir.exists() || !bookmarkDir.isDirectory()) {
				logger.info("Copy all bookmarks to " + Naming.getBookmarkDir());
				FileUtils.copyDirectory(origBookmarkDir, bookmarkDir);
			} else {
				File bookmarkFolderAlreadyCopied = new File(Naming.getBookmarkDir() + "/.DONOTDELETE");
				if (!bookmarkFolderAlreadyCopied.exists()) {
					File[] origs = origBookmarkDir.listFiles(xmlFilter);
					for (int i = 0; i < origs.length; i++) {
						File destFile = new File(bookmarkDir + "/" + origs[i].getName());
						if (!destFile.exists()) {
							logger.info("Copy bookmark " + origs[i] + " to " + Naming.getBookmarkDir());
							FileUtils.copyFile(origs[i], destFile);
						}
					}
				}
			}
		} catch (IOException e) {
			logger.log(e);
		}

		String def = props.getString("bookmark.default");
		File[] bookmarkSets = bookmarkDir.listFiles(xmlFilter);
		for (int i = 0; i < bookmarkSets.length; i++) {
			// bookmarks should be lazily loaded
			BookmarkSet bms = new BookmarkSet(Naming.getBookmarkDir() + "/" + bookmarkSets[i].getName());
			bookmarkSetGroup.addBookmarkSet(bms);
			if (bms.getId().equals(def))
				bookmarkSetGroup.setAsDefault(bms);
		}
		if (bookmarkSetGroup.getDefault() == null) {
			logger.doFatal(new BookmarkException("No default bookmark set, or cannot load the default bookmark set: "
					+ def));
		}
		bookmarkSetGroup.getDefault().load();
	}

	/**
	 * Save properties configuration file, which was read into <code>props</code>, to
	 * <code>ApplicationPath.USER_CONFIG</code>.
	 */
	public void saveConfig() {
		try {
			logger.info("Save user config file to " + ApplicationPath.USER_CONFIG);
			props.save(new OutputStreamWriter(new FileOutputStream(ApplicationPath.USER_CONFIG), "UTF-8"));
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
	// private void extractRuntimeConfig() {
	// runtimeConfig.setLanguage(langElem.getAttribute(ApplicationConfig.CURRENT_LANGUAGE_ATTR));
	// runtimeConfig.setTextLayout(getQuranLayout());
	// }
	/**
	 * This method extracts language properties from the corresponding node in the config file.
	 */
	private void extractLangProps() {
		boolean update = false;

		String def = props.getString("lang.default");
		File langDir = new File(ApplicationPath.LANGUAGE_DIR);
		logger.info("Loading language pack files info");
		logger.info("Default language pack is " + def);
		FileFilter filter = new FileFilter() { // accept .xml files
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
	 * This method extracts translation properties from the corresponding node in the config file.<br>
	 * Will first look inside global translations, and then user-specific ones, overwriting global translations with
	 * user-defined ones if duplicates found.
	 */
	private void extractTransProps() {
		String def = props.getString("trans.default");
		logger.info("Default translation is: " + def);

		String[] paths = { ApplicationPath.TRANSLATION_DIR, Naming.getTransDir() };
		for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
			File transDir = new File(paths[pathIndex]);
			if (!transDir.exists())
				continue;

			logger.info("Loading translation files info from: " + transDir);
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

			for (int transIndex = 0; transIndex < trans.length; transIndex++) {
				try {
					zipFile = new ZipFile(trans[transIndex]);
					InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.TRANSLATION_DESC));
					if (is == null) {
						logger.warn("Will ignore invalid translation archive \"" + zipFile.getName() + "\".");
						continue;
					}
					Reader reader = new InputStreamReader(is, "UTF-8");
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
					if (td.id.equals(def)) {
						translation.setDefault(td);
					}

				} catch (Exception e) {
					logger.warn("Can not load translation pack \"" + zipFile
							+ "\" properly because of the following exception:");
					logger.log(e);
				}
			}
		}
		if (translation.getDefault() == null) {
			logger.error(new ZekrBaseException("Could not find default translation: " + def));
			logger.warn("Will use any English or other translations found.");
			for (Iterator iter = translation.getAllTranslation().iterator(); iter.hasNext();) {
				TranslationData transData = (TranslationData) iter.next();
				if (transData.locale.getLanguage().equalsIgnoreCase("en")) {
					logger.info("Default translation set to: " + transData.getId());
					translation.setDefault(transData);
					props.setProperty("trans.default", translation.getDefault().id);
					break;
				}
			}
			if (translation.getDefault() == null) {
				logger.warn("No default translation found! Will start without any translation. "
						+ "As a result some features will be disabled.");
				// logger.doFatal(new ZekrBaseException("Could not find Shakir translation."));
				// translation.setDefault(TranslationUtils.getDummyTranslationData());
				Iterator iter = translation.getAllTranslation().iterator();
				if (iter.hasNext()) {
					translation.setDefault((TranslationData) iter.next());
					props.setProperty("trans.default", translation.getDefault().id);
					logger.info("Default translation set to: " + translation.getDefault().getId());
				}
			}
		}

		if (translation.getDefault() != null) {
			// load default translation
			translation.getDefault().load();

			// load custom translation list
			logger.info("Load custom translation list.");
			List customList = translation.getCustomGroup();
			List customs = props.getList("trans.custom");
			for (int i = 0; i < customs.size(); i++) {
				String tid = (String) customs.get(i);
				TranslationData td = translation.get(tid);
				if (td == null) {
					logger.error("No such translation: " + tid);
					continue;
				}
				td.load();
				customList.add(td);
			}
		} else {
			logger.warn("No translation found!");
		}
	}

	private void extractViewProps() {
		ThemeData td;
		Reader reader;
		String def = props.getString("theme.default");
		logger.info("Loading theme properties files.");

		String[] paths = { ApplicationPath.THEME_DIR, Naming.getThemeDir() };
		for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
			File targetThemeDir = new File(paths[pathIndex]);
			if (!targetThemeDir.exists())
				continue;

			logger.info("Loading theme files info from \"" + paths[pathIndex]);
			File[] targetThemes = targetThemeDir.listFiles();

			File origThemeDir = new File(paths[pathIndex]);
			File[] origThemes = origThemeDir.listFiles();
			for (int i = 0; i < origThemes.length; i++) {
				String targetThemeDesc = Naming.getThemePropsDir() + "/" + origThemes[i].getName() + ".properties";
				File origThemeDesc = new File(origThemes[i] + "/" + ApplicationPath.THEME_DESC);
				File targetThemeFile = new File(targetThemeDesc);

				if (!origThemeDesc.exists()) {
					logger.warn("\"" + origThemes[i] + "\" is not a standard theme! Will ignore it.");
					continue;
				}

				try {
					if (!targetThemeFile.exists()) {
						logger.info("Copy theme " + origThemes[i].getName() + " to " + Naming.getThemePropsDir());
						FileUtils.copyFile(origThemeDesc, targetThemeFile);
					}
					reader = new InputStreamReader(new FileInputStream(targetThemeFile), "UTF-8");
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
					td.id = origThemes[i].getName();
					td.fileName = targetThemeFile.getName();
					td.baseDir = paths[pathIndex];
					td.props.remove("author");
					td.props.remove("name");

					// extractTransProps must be called before it!
					if (getTranslation().getDefault() != null)
						td.process(getTranslation().getDefault().locale.getLanguage());
					else
						td.process("en");

					theme.add(td);

					if (td.id.equals(def))
						theme.setCurrent(td);
				} catch (Exception e) {
					logger.warn("Can not load theme \"" + targetThemes[i].getName()
							+ "\", because of the following exception:");
					logger.log(e);
				}
			}
		}
		if (theme.getCurrent() == null) {
			logger.doFatal(new ZekrBaseException("Could not find default theme: " + def));
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
		langEngine.reload();
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
		logger.info("Change translation to " + transId);
		TranslationData newTrans = getTranslation().get(transId);
		translation.setDefault(newTrans);
		newTrans.load();
		props.setProperty("trans.default", transId);
		try {
			runtime.recreateCache();
		} catch (IOException e) {
			logger.log(e);
		}
	}

	public String getViewProp(String propKey) {
		return props.getString(propKey);
	}

	public void setViewProp(String propKey, String value) {
		props.setProperty(propKey, value);
	}

	public String getQuranLayout() {
		// return (String) theme.commonProps.get(QURAN_LAYOUT);
		return props.getString("view.quranLayout");
	}

	public void setQuranLayout(String newLayout) {
		props.setProperty("view.quranLayout", newLayout);
	}

	public IQuranLocation getQuranLocation() {
		// return new QuranLocation(theme.commonProps.get(QURAN_LOCATION));
		return new QuranLocation(props.getString("view.quranLoc"));
	}

	public void setQuranLocation(QuranLocation quranLocation) {
		props.setProperty("view.quranLoc", quranLocation);
	}

	public String getTransLayout() {
		return props.getString("view.transLayout");
	}

	public void setTransLayout(String newLayout) {
		props.setProperty("view.transLayout", newLayout);
	}

	public void setViewLayout(String layout) {
		props.setProperty("view.viewLayout", layout);
	}

	public String getViewLayout() {
		return props.getString("view.viewLayout");
	}

	public boolean isHttpServerEnabled() {
		return props.getBoolean("server.http.enable");
	}

	/**
	 * @return HTTP server port or -1 if nothing found.
	 */
	public int getHttpServerPort() {
		String port = props.getString("server.http.port");
		return port == null ? -1 : Integer.parseInt(port);
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

	public BookmarkSetGroup getBookmarkSetGroup() {
		return bookmarkSetGroup;
	}

	public BookmarkSet getBookmark() {
		return bookmarkSetGroup.getDefault();
	}

	/**
	 * @return <code>true</code> if an instance of this class is initialized, and <code>false</code> otherwise.
	 */
	public static boolean isFullyInitialized() {
		return thisInstance != null;
	}

	public void setShowSplash(boolean showSplash) {
		File splashFile = new File(Naming.getConfigDir() + "/.DONTSHOWSPASH");
		if (showSplash) {
			splashFile.delete();
		} else {
			try {
				splashFile.createNewFile();
			} catch (IOException e) {
				logger.error("Error changing show splash property: " + e.getMessage());
			}
		}
	}

	public boolean getShowSplash() {
		File splashFile = new File(Naming.getConfigDir() + "/.DONTSHOWSPASH");
		return !splashFile.exists();
	}

	/**
	 * @return A list of <code>TranslationData</code>
	 */
	public List getCustomTranslationList() {
		return translation.getCustomGroup();
	}

	/**
	 * @param newIdList
	 *           a list of new translation data IDs (list contains Strings).
	 */
	public void setCustomTranslationList(List newIdList) {
		List newList = new ArrayList();
		for (int i = 0; i < newIdList.size(); i++) {
			String id = (String) newIdList.get(i);
			TranslationData td = translation.get(id);
			td.load();
			newList.add(td);
		}
		translation.setCustomGroup(newList);

		props.setProperty("trans.custom", newIdList);
		saveConfig();
	}

	/**
	 * This method first checks if the Quran is previously indexed (first in user home and then in installation
	 * directory). If not, it will try to index it, asking user where to index. If this is already indexed, returns the
	 * directory where Quran indices are.
	 * 
	 * @return directory of the Quran index if indexing was successful (or found index somewhere), <code>null</code>
	 *         otherwise
	 */
	public String createQuranIndex() {
		if (props.getProperty("index.quran.done") != null && props.getBoolean("index.quran.done")) {
			File meOnlyIndex = new File(Naming.getQuranIndexDir());
			File allUsersIndex = new File(ApplicationPath.QURAN_INDEX_DIR);
			if (meOnlyIndex.exists() && meOnlyIndex.isDirectory() && meOnlyIndex.list().length != 0)
				return meOnlyIndex.getAbsolutePath();
			if (allUsersIndex.exists() && allUsersIndex.isDirectory() && allUsersIndex.list().length != 0)
				return allUsersIndex.getAbsolutePath();

			// otherwise do index again...
		}
		// not indexed yet, try to index now
		IndexCreator indexCreator = new IndexCreator(Display.getCurrent());
		boolean succeed = indexCreator.indexQuranText();
		if (succeed) {
			props.setProperty("index.quran.done", "true");
			return indexCreator.getIndexDir();
		} else
			return null;
	}

	/**
	 * This method silently indexes Quran text. It should only be used for command line indexing. This method sets
	 * <tt>index.quran.done</tt> property to <code>true</code> if indexing finished without throwing
	 * <code>IndexingException</code>.
	 * 
	 * @param mode
	 *           can be <code>IndexCreator.ME_ONLY</code>, <code>IndexCreator.ALL_USERS</code>, or
	 *           <code>IndexCreator.CUSTOM_PATH</code>. If mode is equal to <code>CUSTOM_PATH</code>, path
	 *           parameter is also used, otherwise this parameter is unused.
	 * @param path
	 *           path for creating indices in. Used iff mode is equal to <code>CUSTOM_PATH</code>
	 * @param stdout
	 *           standard output to write progressing data to
	 * @throws IndexingException
	 */
	public void createQuranIndex(int mode, String path, PrintStream stdout) throws IndexingException {
		IndexCreator indexCreator = new IndexCreator(Display.getCurrent());
		indexCreator.indexQuranTextSilently(mode, path, stdout);
		props.setProperty("index.quran.done", "true");
		saveConfig();
	}
}

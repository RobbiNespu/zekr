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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.ZekrBaseException;
import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.runtime.ApplicationRuntime;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.audio.Audio;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.bookmark.BookmarkException;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkSetGroup;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.page.CustomPagingData;
import net.sf.zekr.engine.page.FixedAyaPagingData;
import net.sf.zekr.engine.page.HizbQuarterPagingData;
import net.sf.zekr.engine.page.IPagingData;
import net.sf.zekr.engine.page.JuzPagingData;
import net.sf.zekr.engine.page.QuranPaging;
import net.sf.zekr.engine.page.SuraPagingData;
import net.sf.zekr.engine.revelation.Revelation;
import net.sf.zekr.engine.revelation.RevelationData;
import net.sf.zekr.engine.root.QuranRoot;
import net.sf.zekr.engine.search.SearchInfo;
import net.sf.zekr.engine.search.lucene.LuceneIndexManager;
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.server.HttpServerFactory;
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.Translation;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.engine.translation.TranslationException;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

/**
 * This singleton class reads the config files by the first invocation of <code>getInstance()</code>. You can
 * then read any option by using available getter methods.
 * 
 * @author Mohsen Saboorian
 */
public class ApplicationConfig implements ConfigNaming {
	private final static Logger logger = Logger.getLogger(ApplicationConfig.class);
	private static ApplicationConfig thisInstance;

	private XmlReader configReader;
	private LanguageEngine langEngine;
	private Language language;

	private Translation translation = new Translation();
	private Theme theme = new Theme();
	private Audio audio = new Audio();
	private Revelation revelation = new Revelation();
	private QuranPaging quranPaging = new QuranPaging();
	private ApplicationRuntime runtime;
	private IQuranLocation quranLocation;
	private PropertiesConfiguration props;
	private BookmarkSet bookmarkSet;
	private BookmarkSetGroup bookmarkSetGroup = new BookmarkSetGroup();
	private Thread httpServerThread;
	private IUserView userViewController;
	private HttpServer httpServer;
	private LuceneIndexManager luceneIndexManager;
	private SearchInfo searchInfo;
	private QuranRoot quranRoot;

	private ApplicationConfig() {
		logger.info("Initializing application configurations...");

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Initializing Language Engine");
		language = Language.getInstance();

		runtime = new ApplicationRuntime();

		// language packs should be loaded before bookmarks
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Configuration Files");
		loadConfig();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Language Packs");
		extractLangProps();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Bookmark Sets");
		loadBookmarkSetGroup();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Translation Packs");
		extractTransProps();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading View Properties");
		extractViewProps();

		// EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Initializing Audio Data");
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Audio packs");
		extractAudioProps();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Revelation orders");
		extractRevelOrderInfo();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Paging data");
		extractPagingDataProps();

		if (isHttpServerEnabled()) {
			EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Start HTTP server");
			startHttpServer();
		}

		// #extractPagingDataProps() should be called before this method
		initViewController();

		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading search metadata");
		initSearchInfo();

		luceneIndexManager = new LuceneIndexManager(props);

		if (isRootDatabaseEnabled()) {
			EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Quran root database");
			loadRootList();
		}

		logger.info("Application configurations initialized.");
		EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Application UI");
	}

	private void initSearchInfo() {
		logger.info("Load search info...");
		searchInfo = new SearchInfo();
		Configuration stopWordConf = props.subset("search.stopword");
		List defaultStopWord = props.getList("search.stopword");
		Configuration replacePatternConf = props.subset("search.pattern.replace");
		List defaultReplacePattern = props.getList("search.pattern.replace");

		searchInfo.setDefaultStopWord(defaultStopWord);
		for (Iterator iterator = stopWordConf.getKeys(); iterator.hasNext();) {
			String langCode = (String) iterator.next();
			if (langCode.length() <= 0) // default value
				continue;
			logger.debug("\tAdd stop words for: " + langCode);
			searchInfo.addStopWord(langCode, stopWordConf.getList(langCode));
		}

		searchInfo.setDefaultReplacePattern(defaultReplacePattern);
		for (Iterator iterator = replacePatternConf.getKeys(); iterator.hasNext();) {
			String langCode = (String) iterator.next();
			if (langCode.length() <= 0) // default value
				continue;
			logger.debug("\tAdd replace patterns for: " + langCode);
			searchInfo.addReplacePattern(langCode, replacePatternConf.getList(langCode));
		}
	}

	private void loadRootList() {
		try {
			logger.info("Loading Quran root word database...");
			ResourceManager res = ResourceManager.getInstance();
			String rootFile = res.getString("text.quran.root");
			String rootRawStr = net.sf.zekr.common.util.FileUtils.readFully(new FileInputStream(rootFile), (int) new File(
					rootFile).length());
			Date date1 = new Date();
			quranRoot = new QuranRoot(rootRawStr);
			Date date2 = new Date();
			logger.debug("Took " + (date2.getTime() - date1.getTime()) + " ms.");
		} catch (IOException ioe) {
			logger.log(ioe);
		}
	}

	private void initViewController() {
		userViewController = new UserViewController(quranPaging);
		userViewController.setLocation(getQuranLocation());
		userViewController.synchPage();
	}

	private void startHttpServer() {
		logger.info("Start HTTP server daemon on port: " + getHttpServerPort());
		httpServer = HttpServerFactory.createHttpServer(props);
		httpServer.run();
		// httpServerThread = new Thread(httpServer);
		// httpServerThread.setDaemon(true);
		// httpServerThread.start();
	}

	public static ApplicationConfig getInstance() {
		if (thisInstance == null)
			thisInstance = new ApplicationConfig();
		return thisInstance;
	}

	private void loadConfig() {
		logger.info("Load Zekr configuration file.");
		File uc = new File(ApplicationPath.USER_CONFIG);
		boolean createConfig = false;
		String confFile = ApplicationPath.USER_CONFIG;
		if (!uc.exists()) {
			logger.info("User config does not exist at " + ApplicationPath.USER_CONFIG);
			logger.info("Will make user config with default values at " + ApplicationPath.MAIN_CONFIG);
			confFile = ApplicationPath.MAIN_CONFIG;
			createConfig = true;
		}
		try {
			InputStream fis = new FileInputStream(confFile);
			// Reader reader = new InputStreamReader(fis, "UTF-8");
			props = new PropertiesConfiguration();
			props.setBasePath(ApplicationPath.CONFIG_DIR);
			props.setEncoding("UTF-8");
			props.load(fis, "UTF-8");
			// reader.close();
			fis.close();

			if (!GlobalConfig.ZEKR_VERSION.equals(props.getString("version"))) {
				logger.info("User config version (" + props.getString("version") + ") does not match with "
						+ GlobalConfig.ZEKR_VERSION);

				String ver = props.getString("version");
				InputStreamReader reader;
				if (!ver.startsWith("0.7")) { // config file is too old
					logger.info("Previous version was too old: " + ver);
					logger.info("Cannot migrate old settings. Will reset settings.");

					fis = new FileInputStream(ApplicationPath.MAIN_CONFIG);
					reader = new InputStreamReader(fis, "UTF-8");
					props = new PropertiesConfiguration();
					props.load(reader);
					reader.close();
					fis.close();
				} else {
					logger.info("Will initialize user config with default values, overriding with old config.");

					PropertiesConfiguration oldProps = props;
					fis = new FileInputStream(ApplicationPath.MAIN_CONFIG);
					reader = new InputStreamReader(fis, "UTF-8");
					props = new PropertiesConfiguration();
					props.load(reader);
					reader.close();
					fis.close();

					for (Iterator iter = oldProps.getKeys(); iter.hasNext();) {
						String key = (String) iter.next();
						if (key.equals("version"))
							continue;
						props.setProperty(key, oldProps.getProperty(key));
					}
				}
				createConfig = true;
			}
		} catch (Exception e) {
			logger.warn("IO Error in loading/reading config file " + ApplicationPath.MAIN_CONFIG);
			logger.log(e);
		}
		if (createConfig) {
			runtime.clearAll();
			// create config dir
			new File(Naming.getConfigDir()).mkdirs();
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
	 * {@link ApplicationPath#USER_CONFIG}.
	 */
	public void saveConfig() {
		try {
			logger.info("Save user config file to " + ApplicationPath.USER_CONFIG);
			props.save(new FileOutputStream(ApplicationPath.USER_CONFIG), "UTF-8");
		} catch (Exception e) {
			logger.error("Error while saving config to " + ApplicationPath.USER_CONFIG + ": " + e);
		}
	}

	/**
	 * @return User configuration properties
	 */
	public PropertiesConfiguration getProps() {
		return props;
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
	 * Will first look inside global translations, and then user-specific ones, overwriting global translations
	 * with user-defined ones if duplicates found.
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
					if (pathname.getName().toLowerCase().endsWith(ApplicationPath.TRANS_PACK_SUFFIX))
						return true;
					return false;
				}
			};
			File[] trans = transDir.listFiles(filter);

			TranslationData td;

			for (int transIndex = 0; transIndex < trans.length; transIndex++) {
				ZipFile zipFile = null;
				try {
					td = loadTranslationData(trans[transIndex]);
					if (td == null)
						continue;
					translation.add(td);
					if (td.id.equals(def)) {
						try {
							td.load();
							logger.info("Default translation is: " + td);
							translation.setDefault(td);
						} catch (TranslationException e) {
							logger.warn("Cannot load default translation: " + e);
						}
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
					logger.info("Trying to set default translation to: " + transData.getId());
					try {
						transData.load();
						translation.setDefault(transData);
						props.setProperty("trans.default", translation.getDefault().id);
						break;
					} catch (TranslationException e) {
						logger.warn("Cannot load default translation: " + e);
					}
				}
			}
			if (translation.getDefault() == null) {
				logger.warn("No default translation found! Will start without any translation. "
						+ "As a result some features will be disabled.");
				Iterator iter = translation.getAllTranslation().iterator();
				if (iter.hasNext()) {
					TranslationData td = (TranslationData) iter.next();
					try {
						td.load();
						translation.setDefault(td);
						props.setProperty("trans.default", translation.getDefault().id);
						logger.info("Default translation set to: " + translation.getDefault().getId());
					} catch (TranslationException e) {
						logger.warn("Cannot load default translation: " + e);
					}
				}
			}
		}

		if (translation.getDefault() != null) {
			// load custom translation list
			logger.info("Load custom translation list.");
			List customList = translation.getCustomGroup();
			List customs = props.getList("trans.custom");
			for (int i = 0; i < customs.size(); i++) {
				String tid = (String) customs.get(i);
				if (tid == null || "".equals(tid.trim())) {
					logger.info("No custom translation list to load.");
					continue;
				}
				TranslationData td = translation.get(tid);
				if (td == null) {
					logger.error("No such translation: " + tid);
					continue;
				}
				try {
					td.load();
					customList.add(td);
				} catch (TranslationException e) {
					logger.warn("Invalid translation will be removed from the multi-translation list: " + e);
					customs.remove(i);
				}
			}
		} else {
			logger.warn("No translation found!");
		}
	}

	public TranslationData loadTranslationData(File transZipFile) throws IOException, ConfigurationException {
		ZipFile zipFile = new ZipFile(transZipFile);
		InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.TRANSLATION_DESC));
		if (is == null) {
			logger.warn("Will ignore invalid translation archive \"" + zipFile.getName() + "\".");
			return null;
		}
		Reader reader = new InputStreamReader(is, "UTF-8");
		PropertiesConfiguration pc = new PropertiesConfiguration();
		pc.load(reader);
		reader.close();
		is.close();
		zipFile.close();

		TranslationData td = new TranslationData();
		td.version = pc.getString(VERSION_ATTR);
		td.id = pc.getString(ID_ATTR);
		td.locale = new Locale(pc.getString(LANG_ATTR, "en"), pc.getString(COUNTRY_ATTR, "US"));
		td.encoding = pc.getString(ENCODING_ATTR, "ISO-8859-1");
		td.direction = pc.getString(DIRECTION_ATTR, "ltr");
		td.file = pc.getString(FILE_ATTR);
		td.name = pc.getString(NAME_ATTR);
		td.localizedName = pc.getString(LOCALIZED_NAME_ATTR, td.name);
		td.archiveFile = transZipFile;
		td.delimiter = pc.getString(LINE_DELIMITER_ATTR, "\n");
		String sig = pc.getString(SIGNATURE_ATTR);
		td.signature = sig == null ? null : Base64.decodeBase64(sig.getBytes("US-ASCII"));

		if (StringUtils.isBlank(td.id) || StringUtils.isBlank(td.name) || StringUtils.isBlank(td.file)
				|| StringUtils.isBlank(td.version)) {
			logger.warn("Invalid translation: \"" + td + "\".");
			return null;
		}
		return td;
	}

	private void extractViewProps() {
		ThemeData td;
		Reader reader;
		String def = props.getString("theme.default");
		logger.info("Loading theme .properties files.");

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
					if (!targetThemeFile.exists() || FileUtils.isFileNewer(origThemeDesc, targetThemeFile)) {
						logger.info("Copy theme " + origThemes[i].getName() + " to " + Naming.getThemePropsDir());
						FileUtils.copyFile(origThemeDesc, targetThemeFile);
					}
					FileInputStream fis = new FileInputStream(targetThemeFile);
					reader = new InputStreamReader(fis, "UTF-8");
					PropertiesConfiguration pc = new PropertiesConfiguration();
					pc.load(reader);
					reader.close();
					fis.close();

					td = new ThemeData();
					td.props = new LinkedHashMap(); // order is important for options table!
					for (Iterator iter = pc.getKeys(); iter.hasNext();) {
						String key = (String) iter.next();
						td.props.put(key, CollectionUtils.toString(pc.getList(key), ", "));
					}
					td.author = pc.getString("author");
					td.name = pc.getString("name");
					td.version = pc.getString("version");
					td.id = origThemes[i].getName();
					td.fileName = targetThemeFile.getName();
					td.baseDir = paths[pathIndex];
					td.props.remove("author");
					td.props.remove("name");
					td.props.remove("version");

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

	private void extractAudioProps() {
		Reader reader;
		String def = props.getString("audio.default");
		logger.info("Loading audio .properties files.");

		String[] paths = { ApplicationPath.AUDIO_DIR, Naming.getAudioDir() };
		for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
			File audioDir = new File(paths[pathIndex]);
			if (!audioDir.exists())
				continue;

			logger.info("Loading audio files info from: " + audioDir);
			FileFilter filter = new FileFilter() { // accept .properties files
				public boolean accept(File pathname) {
					if (pathname.getName().toLowerCase().endsWith(".properties"))
						return true;
					return false;
				}
			};
			File[] audioPropFiles = audioDir.listFiles(filter);

			AudioData audioData;

			FileInputStream fis;
			for (int audioIndex = 0; audioIndex < audioPropFiles.length; audioIndex++) {
				try {
					fis = new FileInputStream(audioPropFiles[audioIndex]);
					reader = new InputStreamReader(fis, "UTF-8");
					PropertiesConfiguration pc = new PropertiesConfiguration();
					pc.load(reader);
					reader.close();
					fis.close();

					audioData = new AudioData();
					audioData.setId(pc.getString("audio.id"));
					audioData.setName(pc.getString("audio.name"));
					audioData.setLicense(pc.getString("audio.license"));
					audioData.setLocale(new Locale(pc.getString("audio.language"), pc.getString("audio.country")));
					audioData.setReciter(pc.getString("audio.reciter"));

					audioData.setPlaylistMode(pc.getString("audio.playlist.mode"));
					audioData.setPlaylistBaseUrl(pc.getString("audio.playlist.baseUrl"));
					audioData.setPlaylistFileName(pc.getString("audio.playlist.fileName"));
					audioData.setPlaylistSuraPad(pc.getString("audio.playlist.fileName.suraPad"));
					audioData.setPlaylistProvider(pc.getString("audio.playlist.provider"));

					audioData.setAudioBaseUrl(pc.getString("audio.baseUrl"));
					audioData.setAudioServerUrl(pc.getString("audio.serverUrl"));
					audioData.setAudioFileName(pc.getString("audio.fileName"));
					audioData.setAudioFileSuraPad(pc.getString("audio.fileName.suraPad"));
					audioData.setAudioFileAyaPad(pc.getString("audio.fileName.ayaPad"));

					audioData.setPrestartFileName(pc.getString("audio.prestartFileName"));
					audioData.setStartFileName(pc.getString("audio.startFileName"));
					audioData.setEndFileName(pc.getString("audio.endFileName"));

					audio.add(audioData);
					if (audioData.getId().equals(def)) {
						logger.info("Default recitation is: " + audioData);
						audio.setCurrent(audioData);
					}
				} catch (Exception e) {
					logger.warn("Can not load audio pack \"" + audioPropFiles[audioIndex]
							+ "\" properly because of the following exception:");
					logger.log(e);
				}
			}
		}

		if (audio.getCurrent() == null) {
			logger.error("No default recitation found: " + def);
			if (audio.getAllAudio().size() > 0) {
				audio.setCurrent((AudioData) audio.getAllAudio().iterator().next());
				props.setProperty("audio.default", audio.getCurrent().getId());
				logger.warn("Setting another recitation as default: " + audio.getCurrent());
			} else {
				logger.warn("No other recitation found. Audio will be disabled.");
			}
		}
	}

	private void extractRevelOrderInfo() {
		String def = props.getString("revel.default");
		logger.info("Default revelation package is: " + def);

		File revelDir = new File(ApplicationPath.REVELATION_DIR);
		if (!revelDir.exists()) {
			logger.debug("No revelation data pack found.");
			return;
		}

		logger.info("Loading revelation data packs from: " + revelDir);
		FileFilter filter = new FileFilter() { // accept zip files
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(ApplicationPath.REVEL_PACK_SUFFIX))
					return true;
				return false;
			}
		};
		File[] revelFiles = revelDir.listFiles(filter);

		RevelationData rd;
		for (int revelIndex = 0; revelIndex < revelFiles.length; revelIndex++) {
			ZipFile zipFile = null;
			try {
				rd = loadRevelationData(revelFiles[revelIndex]);
				if (rd == null)
					continue;
				revelation.add(rd);
				if (rd.id.equals(def)) {
					rd.load();
					logger.info("Default revelation data is: " + rd);
					revelation.setDefault(rd);
				}
			} catch (Exception e) {
				logger.warn("Can not load revelation data pack \"" + zipFile
						+ "\" properly because of the following exception:");
				logger.log(e);
			}
		}
	}

	public RevelationData loadRevelationData(File revelZipFile) throws IOException, ConfigurationException {
		ZipFile zipFile = new ZipFile(revelZipFile);
		InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.REVELATION_DESC));
		if (is == null) {
			logger.warn("Will ignore invalid revelation data archive \"" + zipFile.getName() + "\".");
			return null;
		}
		Reader reader = new InputStreamReader(is, "UTF-8");
		PropertiesConfiguration pc = new PropertiesConfiguration();
		pc.load(reader);
		reader.close();
		is.close();
		zipFile.close();

		RevelationData rd = new RevelationData();

		int len;
		if ("aya".equals(pc.getString("mode", "sura"))) {
			len = QuranPropertiesUtils.QURAN_AYA_COUNT;
			rd.mode = RevelationData.AYA_MODE;
		} else {
			len = 114;
			rd.mode = RevelationData.SURA_MODE;
		}
		rd.orders = new int[len];
		rd.years = new int[len];

		rd.version = pc.getString("version");
		String zipFileName = revelZipFile.getName();
		rd.id = zipFileName.substring(0, zipFileName.length() - ApplicationPath.REVEL_PACK_SUFFIX.length());
		rd.archiveFile = revelZipFile;
		rd.delimiter = pc.getString("delimiter", "\n");
		String sig = pc.getString("signature");

		byte[] sigBytes = sig.getBytes("US-ASCII");
		rd.signature = sig == null ? null : Base64.decodeBase64(sigBytes);

		Iterator nameIter = pc.getKeys("name");
		for (Iterator iterator = nameIter; iterator.hasNext();) {
			String key = (String) iterator.next();
			rd.getNames().put(new String(key.substring(4)), pc.getString(key));
		}

		if (StringUtils.isBlank(rd.id) || rd.getNames().size() == 0 || StringUtils.isBlank(rd.version)) {
			logger.warn("Invalid revelation data package: \"" + rd + "\".");
			return null;
		}
		return rd;
	}

	private void extractPagingDataProps() {
		String def = props.getString("view.pagingMode");
		logger.info("Default paging mode is: " + def);

		File pagingDir = new File(ApplicationPath.PAGING_DIR);
		if (!pagingDir.exists()) {
			logger.debug("No paging data found.");
			return;
		}

		logger.info("Loading paging data from: " + pagingDir);
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().toLowerCase().endsWith(ApplicationPath.PAGING_PACK_SUFFIX))
					return true;
				return false;
			}
		};
		File[] pagingFiles = pagingDir.listFiles(filter);

		// add built-in paging implementations
		quranPaging.add(new SuraPagingData());
		quranPaging.add(new FixedAyaPagingData(props.getInt("view.pagingMode.ayaPerPage", 20)));
		quranPaging.add(new HizbQuarterPagingData());
		quranPaging.add(new JuzPagingData());

		CustomPagingData cpd;
		for (int i = 0; i < pagingFiles.length; i++) {
			cpd = new CustomPagingData();
			String name = pagingFiles[i].getName();
			cpd.setId(name.substring(0, name.indexOf(ApplicationPath.PAGING_PACK_SUFFIX)));
			cpd.file = pagingFiles[i];
			quranPaging.add(cpd);
		}
		IPagingData ipd = (IPagingData) quranPaging.get(def);
		if (ipd != null) {
			try {
				logger.info("Default paging data is: " + ipd);
				ipd.load();
				logger.info("Default paging data loaded successfully: " + ipd);
				quranPaging.setDefault(ipd);
			} catch (Exception e) {
				logger.warn("Can not load paging data \"" + ipd + "\" properly because of the following exception:");
				logger.log(e);
				logger.debug("Set default paging data to: sura.");
				// set default paging model to sura, if nothing is set.
				quranPaging.setDefault(quranPaging.get(SuraPagingData.ID));
				props.setProperty("view.pagingMode", quranPaging.getDefault().getId());
			}
		}
		if (quranPaging.getDefault() == null) {
			logger.warn("No default paging data found. Will load Hizb Quarter paging data.");
			quranPaging.setDefault(quranPaging.get(HizbQuarterPagingData.ID));
		}
	}

	/**
	 * @return application language engine
	 * @see Language#getInstance()
	 */
	public synchronized LanguageEngine getLanguageEngine() {
		if (langEngine == null)
			langEngine = LanguageEngine.getInstance();
		return langEngine;
	}

	public void setCurrentLanguage(String langId) {
		logger.info("Set current language to " + langId);
		language.setActiveLanguagePack(langId);
		langEngine.reload();
		logger.debug("Update localized sura names if available.");
		QuranPropertiesUtils.updateLocalizedSuraNames();
		props.setProperty("lang.default", langId);
	}

	public void setCurrentTheme(String themeId) {
		logger.info("Set current theme to " + themeId);
		theme.setCurrent(theme.get(themeId));
		props.setProperty("theme.default", themeId);
	}

	public void setCurrentTranslation(String transId) throws TranslationException {
		boolean unloadPrevTrans = true;
		String defId = translation.getDefault().id;

		if (defId.equals(transId)) {
			logger.info("Translation is already selected: " + transId);
		}

		logger.info("Change default translation: " + defId + " => " + transId);

		for (Iterator iterator = translation.getCustomGroup().iterator(); iterator.hasNext();) {
			TranslationData td = (TranslationData) iterator.next();
			if (td.id.equals(defId)) {
				unloadPrevTrans = false;
				break;
			}
		}

		TranslationData newTrans = getTranslation().get(transId);
		newTrans.load();
		translation.setDefault(newTrans);
		props.setProperty("trans.default", transId);

		if (unloadPrevTrans) {
			logger.info("Unload previous selected translation which is not used anymore: " + translation.getDefault());
			translation.getDefault().unload();
		}

		try {
			runtime.recreateViewCache();
		} catch (IOException e) {
			logger.log(e);
		}
	}

	public void setCurrentAudio(String audioId) {
		logger.info("Change current audio pack to " + audioId);
		audio.setCurrent(audio.get(audioId));
		props.setProperty("audio.default", audioId);
		try {
			runtime.recreateViewCache();
			runtime.recreatePlaylistCache(); // not really needed
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
		return props.getString("view.quranLayout");
	}

	public void setQuranLayout(String newLayout) {
		props.setProperty("view.quranLayout", newLayout);
	}

	public int getPageNum() {
		return props.getInt("view.page", 1);
	}

	public IQuranLocation getQuranLocation() {
		return new QuranLocation(props.getString("view.quranLoc"));
	}

	public void setQuranLocation(IQuranLocation quranLocation) {
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

	public void setPagingMode(String pagingModeId) {
		try {
			IPagingData pagingData = getQuranPaging().get(pagingModeId);
			if (pagingData == null) {
				logger.warn("No such paging data: " + pagingModeId);
				return;
			}
			logger.info("Change current paging mode to to " + pagingModeId);
			pagingData.load(); // ensure that paging data is loaded
			quranPaging.setDefault(pagingData);
			props.setProperty("view.pagingMode", pagingModeId);

			runtime.recreateViewCache(); // HTML files are not valid anymore from paging POV
			runtime.recreatePlaylistCache(); // playlists are not valid anymore from paging POV
		} catch (Exception e) {
			logger.log(e);
		}
	}

	public String getPagingMode() {
		return props.getString("view.pagingMode");
	}

	public boolean isHttpServerEnabled() {
		return props.getBoolean("server.http.enable");
	}

	public boolean isRootDatabaseEnabled() {
		return props.getBoolean("root.enable", true);
	}

	public boolean useMozilla() {
		return props.getBoolean("options.browser.useMozilla");
	}

	/**
	 * @return HTTP server port or -1 if nothing found.
	 */
	public int getHttpServerPort() {
		String port = props.getString("server.http.port");
		return port == null ? -1 : Integer.parseInt(port);
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

	public Audio getAudio() {
		return audio;
	}

	public Revelation getRevelation() {
		return revelation;
	}

	public QuranPaging getQuranPaging() {
		return quranPaging;
	}

	public QuranRoot getQuranRoot() {
		return quranRoot;
	}

	public SearchInfo getSearchInfo() {
		return searchInfo;
	}

	public HttpServer getHttpServer() {
		return httpServer;
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

	public IUserView getUserViewController() {
		return userViewController;
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
	 * @param newIdList a list of new translation data IDs (list contains Strings).
	 * @throws TranslationException
	 */
	public void setCustomTranslationList(List newIdList) throws TranslationException {
		List newList = new ArrayList();

		// load new translation packs
		for (int i = 0; i < newIdList.size(); i++) {
			String id = (String) newIdList.get(i);
			TranslationData td = translation.get(id);
			td.load();
			newList.add(td);
		}

		String defaultId = translation.getDefault().id;

		// unload old translation packs (which are not included in the new list)
		List oldCustomList = translation.getCustomGroup();
		for (int i = 0; i < oldCustomList.size(); i++) {
			TranslationData oldTd = (TranslationData) oldCustomList.get(i);
			if (!newIdList.contains(oldTd.id) && !oldTd.id.equals(defaultId)) {
				logger.info("Unload previous selected translation which is not used anymore: " + oldTd);
				oldTd.unload();
			}
		}

		translation.setCustomGroup(newList);

		props.setProperty("trans.custom", newIdList);
		saveConfig();
	}

	public LuceneIndexManager getLuceneIndexManager() {
		return luceneIndexManager;
	}

	public boolean isAudioEnabled() {
		return props.getBoolean("audio.enable");
	}

	/**
	 * This method is used to add a new translation during runtime. It loads translation metadata and adds it
	 * to the list of translations. If translation pack is not authentic, it throws a ZekrMessageException just
	 * to inform user.
	 * 
	 * @param transFile a translation zip archive to be loaded
	 * @throws ZekrMessageException with the proper message key and parameters if any exception occurred
	 */
	public boolean addNewTranslation(File transFile) throws ZekrMessageException {
		logger.debug("Add new translation: " + transFile);
		try {
			TranslationData td = loadTranslationData(transFile);
			if (td == null) {
				throw new ZekrMessageException("INVALID_TRANSLATION_FORMAT", new String[] { transFile.getName() });
			}
			translation.add(td);
			return td.verify();
		} catch (ZekrMessageException zme) {
			throw zme;
		} catch (Exception e) {
			throw new ZekrMessageException("TRANSLATION_LOAD_FAILED", new String[] { transFile.getName(), e.toString() });
		}
	}
}

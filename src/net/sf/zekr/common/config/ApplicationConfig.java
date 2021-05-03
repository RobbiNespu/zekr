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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import net.sf.zekr.common.util.CommonUtils;
import net.sf.zekr.common.util.ConfigUtils;
import net.sf.zekr.common.util.IntallationProgressListener;
import net.sf.zekr.common.util.ZipUtils;
import net.sf.zekr.engine.addonmgr.AddOnManagerUtils;
import net.sf.zekr.engine.addonmgr.CandidateResource;
import net.sf.zekr.engine.addonmgr.InvalidResourceException;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.audio.Audio;
import net.sf.zekr.engine.audio.AudioCacheManager;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.audio.DefaultPlayerController;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.RecitationPackConverter;
import net.sf.zekr.engine.bookmark.BookmarkException;
import net.sf.zekr.engine.bookmark.BookmarkSet;
import net.sf.zekr.engine.bookmark.BookmarkSetGroup;
import net.sf.zekr.engine.common.LocalizedResource;
import net.sf.zekr.engine.language.Language;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguagePack;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.network.NetworkController;
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
import net.sf.zekr.engine.theme.Theme;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.Translation;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.engine.translation.TranslationException;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.engine.xml.XmlUtils;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This singleton class reads the config files by the first invocation of <code>getInstance()</code>. You can then read any option
 * by using available getter methods.
 * 
 * @author Mohsen Saboorian
 */
public class ApplicationConfig implements ConfigNaming {
   private final static Logger logger = Logger.getLogger(ApplicationConfig.class);
   private final static ResourceManager res = ResourceManager.getInstance();
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
   private PropertiesConfiguration props, searchProps;
   private BookmarkSet bookmarkSet;
   private BookmarkSetGroup bookmarkSetGroup = new BookmarkSetGroup();
   // private Thread httpServerThread;
   private IUserView userViewController;
   // private HttpServer httpServer;
   private LuceneIndexManager luceneIndexManager;
   private SearchInfo searchInfo;
   private QuranRoot quranRoot;
   private AudioCacheManager audioCacheManager;
   private PlayerController playerController, searchPlayerController;
   private NetworkController networkController;
   private KeyboardShortcut shortcut;

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
      setupAudioManager();

      EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Revelation suraOrders");
      extractRevelOrderInfo();

      EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Loading Paging data");
      extractPagingDataProps();

      initNetworkController();
      /*
      if (isHttpServerEnabled()) {
      	EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS + ":" + "Start HTTP server");
      }
      startHttpServer();
       */

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

   @SuppressWarnings("unchecked")
   private void initSearchInfo() {
      try {
         logger.info("Load search info...");

         File usi = new File(ApplicationPath.USER_SEARCH_INFO);
         if (!usi.exists()) {
            logger.info("User search info does not exist at " + ApplicationPath.USER_SEARCH_INFO);
            logger.info("Will make user search info with default values at " + ApplicationPath.MAIN_SEARCH_INFO);
            String searchInfoFile = ApplicationPath.MAIN_SEARCH_INFO;
            try {
               logger.info("Save user search info file to " + ApplicationPath.USER_CONFIG);
               FileUtils.copyFile(new File(searchInfoFile), usi);

               logger.debug("Load " + searchInfoFile);
               FileInputStream fis = new FileInputStream(searchInfoFile);
               searchProps = ConfigUtils.loadConfig(fis, "UTF-8");
            } catch (Exception e) {
               logger.error("Error loading search info file " + searchInfoFile);
               logger.implicitLog(e);
            }
         } else {
            String searchInfoFile = ApplicationPath.USER_SEARCH_INFO;
            try {
               String ver = null;
               boolean error = false;
               try {
                  FileInputStream fis = new FileInputStream(searchInfoFile);
                  searchProps = ConfigUtils.loadConfig(fis, "UTF-8", ApplicationPath.CONFIG_DIR);
                  ver = searchProps.getString("search.version");
               } catch (Exception e) {
                  logger.error(String
                        .format(
                              "Error loading user search info file %s." + " Will replace it with original search info file %s.",
                              searchInfoFile, ApplicationPath.MAIN_SEARCH_INFO), e);
                  error = true;
               }
               if (!GlobalConfig.ZEKR_VERSION.equals(ver) || error) {
                  searchInfoFile = ApplicationPath.USER_CONFIG;
                  searchProps = ConfigUtils.loadConfig(new FileInputStream(searchInfoFile), "UTF-8");
                  String newName = String.format("%s_%s", res.getString("config.searchInfo.file"),
                        String.format(ver == null ? "old" : ver));
                  logger.info(String.format("Migrate search info from version %s to %s. Will rename old file to %s.", ver,
                        GlobalConfig.ZEKR_VERSION, newName));
                  FileUtils.copyFile(usi, new File(usi.getParent(), newName));
                  FileUtils.copyFile(new File(ApplicationPath.MAIN_SEARCH_INFO), usi);
               }
            } catch (Exception e) {
               logger.error("Error loading search info file " + searchInfoFile);
               logger.implicitLog(e);
            }
         }

         searchInfo = new SearchInfo();
         Configuration stopWordConf = searchProps.subset("search.stopword");
         List<String> defaultStopWord = searchProps.getList("search.stopword");
         Configuration replacePatternConf = searchProps.subset("search.pattern.replace");
         List<String> defaultReplacePattern = searchProps.getList("search.pattern.replace");
         Configuration punctuationConf = searchProps.subset("search.pattern.punct");
         String defaultPunctuation = searchProps.getString("search.pattern.punct");
         Configuration diacriticsConf = searchProps.subset("search.pattern.diacr");
         String defaultDiacritics = searchProps.getString("search.pattern.diacr");
         Configuration letterConf = searchProps.subset("search.pattern.letter");

         searchInfo.setDefaultStopWord(defaultStopWord);
         for (Iterator<String> iterator = stopWordConf.getKeys(); iterator.hasNext();) {
            String langCode = iterator.next();
            if (langCode.length() <= 0) {
               continue;
            }
            logger.debug("\tAdd stop words for: " + langCode);
            searchInfo.addStopWord(langCode, stopWordConf.getList(langCode));
         }

         searchInfo.setDefaultReplacePattern(defaultReplacePattern);
         for (Iterator<String> iterator = replacePatternConf.getKeys(); iterator.hasNext();) {
            String langCode = iterator.next();
            if (langCode.length() <= 0) {
               continue;
            }
            logger.debug("\tAdd replace patterns for: " + langCode);
            searchInfo.addReplacePattern(langCode, replacePatternConf.getList(langCode));
         }

         if (defaultPunctuation != null) {
            searchInfo.setDefaultPunctuation(Pattern.compile(defaultPunctuation));
         }
         for (Iterator<String> iterator = punctuationConf.getKeys(); iterator.hasNext();) {
            String langCode = iterator.next();
            if (langCode.length() <= 0) {
               continue;
            }
            logger.debug("\tAdd punctuation pattern for: " + langCode);
            searchInfo.setPunctuation(langCode, Pattern.compile(punctuationConf.getString(langCode)));
         }

         if (defaultDiacritics != null) {
            searchInfo.setDefaultDiacritic(Pattern.compile(defaultDiacritics));
         }
         for (Iterator<String> iterator = diacriticsConf.getKeys(); iterator.hasNext();) {
            String langCode = iterator.next();
            if (langCode.length() <= 0) {
               continue;
            }
            logger.debug("\tAdd diacritics pattern for: " + langCode);
            searchInfo.setDiacritic(langCode, Pattern.compile(diacriticsConf.getString(langCode)));
         }

         for (Iterator<String> iterator = letterConf.getKeys(); iterator.hasNext();) {
            String langCode = iterator.next();
            if (langCode.length() <= 0) {
               continue;
            }
            logger.debug("\tAdd letters range pattern for: " + langCode);
            searchInfo.setLetter(langCode, Pattern.compile(letterConf.getString(langCode)));
         }
      } catch (Exception ex) {
         logger.error("Search info not initialized correctly because of the next error." + " Zekr, however, will be launched.");
         logger.implicitLog(ex);
      }
   }

   private void loadRootList() {
      try {
         logger.info("Loading Quran root word database...");
         String rootFile = res.getString("text.quran.root");
         String rootRawStr = FileUtils.readFileToString(new File(rootFile), "UTF-8");
         Date date1 = new Date();
         quranRoot = new QuranRoot(rootRawStr);
         Date date2 = new Date();
         logger.debug("Took " + (date2.getTime() - date1.getTime()) + " ms.");
      } catch (IOException ioe) {
         logger.log(ioe);
      }
   }

   private void initViewController() {
      logger.debug("Initialize view controller.");
      userViewController = new UserViewController(quranPaging);
      userViewController.setLocation(getQuranLocation());
      userViewController.synchPage();
   }

   private void initNetworkController() {
      logger.debug("Initialize network controller.");
      networkController = new NetworkController(props);
   }

   /*
   private void startHttpServer() {
   	logger.info("Start HTTP server daemon on port: " + getHttpServerPort());
   	httpServer = HttpServerFactory.createHttpServer(props);
   	if (isHttpServerEnabled()) {
   		httpServer.run();
   	}
   }
   */

   public static ApplicationConfig getInstance() {
      if (thisInstance == null) {
         thisInstance = new ApplicationConfig();
      }
      return thisInstance;
   }

   @SuppressWarnings("unchecked")
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
         logger.debug("Load " + confFile);
         props = ConfigUtils.loadConfig(new File(confFile), ApplicationPath.CONFIG_DIR, "UTF-8");

         String version = props.getString("version");
         if (!GlobalConfig.ZEKR_VERSION.equals(version)) {
            logger.info("User config version (" + version + ") does not match " + GlobalConfig.ZEKR_VERSION);

            if (StringUtils.isBlank(version) || !isCompatibleVersion(version)) { // config file is too old
               logger.info(String.format("Previous version (%s) is too old and not compatible with %s", version,
                     GlobalConfig.ZEKR_VERSION));
               logger.info("Cannot migrate old settings. Will reset settings.");

               props = ConfigUtils.loadConfig(new File(ApplicationPath.MAIN_CONFIG), "UTF-8");
            } else {
               logger.info("Will initialize user config with default values, overriding with old config.");

               PropertiesConfiguration oldProps = props;
               props = ConfigUtils.loadConfig(new File(ApplicationPath.MAIN_CONFIG), "UTF-8");

               for (Iterator<String> iter = oldProps.getKeys(); iter.hasNext();) {
                  String key = iter.next();
                  if (key.equals("version")) {
                     continue;
                  }
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

      // load shortcuts
      logger.info("Loading keyboard shortcuts.");
      File userShortcut = new File(ApplicationPath.USER_SHORTCUT);
      Document doc = null;
      if (userShortcut.exists()) {
         try {
            logger.info("Loading user keyboard shortcuts: " + ApplicationPath.USER_SHORTCUT);
            Document userDoc = new XmlReader(userShortcut).getDocument();
            String version = userDoc.getDocumentElement().getAttribute("version");
            if (GlobalConfig.ZEKR_VERSION.equals(version)) {
               doc = userDoc;
            } else {
               logger.info("User shortcut file version (" + version + ") does not match with " + GlobalConfig.ZEKR_VERSION);

               List<String> userList = new ArrayList<String>();
               Element userRoot = userDoc.getDocumentElement();
               NodeList userMappings = userRoot.getElementsByTagName("mapping");
               for (int i = 0; i < userMappings.getLength(); i++) {
                  Element mapping = (Element) userMappings.item(i);
                  String action = mapping.getAttribute("action");
                  userList.add(action);
               }

               File mainShortcut = new File(ApplicationPath.MAIN_SHORTCUT);
               Element mainRoot = new XmlReader(mainShortcut).getDocument().getDocumentElement();
               NodeList mainMappings = mainRoot.getElementsByTagName("mapping");
               for (int i = 0; i < mainMappings.getLength(); i++) {
                  Element mapping = (Element) mainMappings.item(i);
                  String action = mapping.getAttribute("action");
                  if (!userList.contains(action)) {
                     logger.debug("Adding new shortcut mapping for action: " + action);
                     Element newMapping = userDoc.createElement("mapping");
                     newMapping.setAttribute("action", mapping.getAttribute("action"));
                     newMapping.setAttribute("key", mapping.getAttribute("key"));
                     newMapping.setAttribute("rtlKey", mapping.getAttribute("rtlKey"));
                     userRoot.appendChild(newMapping);
                  }
               }
               userRoot.setAttribute("version", GlobalConfig.ZEKR_VERSION);
               doc = userDoc;
               XmlUtils.writeXml(userDoc, userShortcut);
            }
         } catch (Exception e) {
            logger.warn("Error loading user shortcuts: " + ApplicationPath.USER_SHORTCUT);
            logger.log(e);
         }
      } else {
         try {
            logger.info("Loading keyboard shortcuts from original location: " + ApplicationPath.MAIN_SHORTCUT);
            File mainShortcut = new File(ApplicationPath.MAIN_SHORTCUT);
            doc = new XmlReader(mainShortcut).getDocument();
            FileUtils.copyFile(mainShortcut, new File(ApplicationPath.USER_SHORTCUT));
         } catch (Exception e) {
            logger.log(e);
         }
      }
      if (doc != null) {
         logger.info("Initialize keyboard shortcuts and mappings.");
         shortcut = new KeyboardShortcut(props, doc);
         shortcut.init();
      }
   }

   /**
    * A threshold version is checked here. If user config version is newer or equal to this version, then config file can be
    * migrated. Otherwise, it's reset.
    * 
    * @param version
    * @return
    */
   private boolean isCompatibleVersion(String version) {
      try {
         Pattern regex = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*"); // e.g. 0.7.6 or 0.7.5beta2
         Matcher m = regex.matcher(version);
         if (m.find()) {
            String versionPart = m.group(1);
            return CommonUtils.compareVersions(versionPart, "0.7.5") >= 0;
         }
      } catch (Exception e) {
         logger.implicitLog(e);
         return false;
      }
      return false;
   }

   private void loadBookmarkSetGroup() {
      File bookmarkDir = new File(Naming.getBookmarkDir());
      File origBookmarkDir = new File(res.getString("bookmark.baseDir"));

      FileFilter xmlFilter = new FileFilter() { // accept .xml files
         public boolean accept(File pathname) {
            if (pathname.getName().toLowerCase().endsWith(".xml")) {
               return true;
            }
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
         if (bms.getId().equals(def)) {
            bookmarkSetGroup.setAsDefault(bms);
         }
      }
      if (bookmarkSetGroup.getDefault() == null) {
         logger.doFatal(new BookmarkException("No default bookmark set, or cannot load the default bookmark set: " + def));
      }
      bookmarkSetGroup.getDefault().load();
   }

   /**
    * Save properties configuration file, which was read into <code>props</code>, to {@link ApplicationPath#USER_CONFIG}.
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
            if (pathname.getName().toLowerCase().endsWith(".xml")) {
               return true;
            }
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
            if (langs[i].getName().endsWith("english.xml")) {
               logger.doFatal(e);
            } else {
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
         if (lp.localizedName == null) {
            lp.localizedName = lp.name;
         }
         language.add(lp);
         if (lp.id.equals(def)) {
            language.setActiveLanguagePack(def);
         }
      }

      if (update) {
         updateFile();
      }
   }

   /**
    * This method extracts translation properties from the corresponding node in the config file.<br>
    * Will first look inside global translations, and then user-specific ones, overwriting global translations with user-defined
    * ones if duplicates found.
    */
   @SuppressWarnings("unchecked")
   private void extractTransProps() {
      String def = props.getString("trans.default");
      logger.info("Default translation is: " + def);

      String[] paths = { ApplicationPath.TRANSLATION_DIR, Naming.getTransDir() };
      for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
         File transDir = new File(paths[pathIndex]);
         if (!transDir.exists()) {
            continue;
         }

         logger.info("Loading translation files info from: " + transDir);
         FileFilter filter = new FileFilter() { // accept zip files
            public boolean accept(File pathname) {
               if (pathname.getName().toLowerCase().endsWith(ApplicationPath.TRANS_PACK_SUFFIX)) {
                  return true;
               }
               return false;
            }
         };
         File[] trans = transDir.listFiles(filter);

         TranslationData td;

         for (int transIndex = 0; transIndex < trans.length; transIndex++) {
            ZipFile zipFile = null;
            try {
               td = loadTranslationData(trans[transIndex]);
               if (td == null) {
                  continue;
               }
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
               logger.warn("Can not load translation pack \"" + zipFile + "\" properly because of the following exception:");
               logger.log(e);
            }
         }
      }
      if (translation.getDefault() == null) {
         logger.error(new ZekrBaseException("Could not find default translation: " + def));
         logger.warn("Will use any English or other translations found.");
         for (TranslationData translationData : translation.getAllTranslation()) {
            if (translationData.locale.getLanguage().equalsIgnoreCase("en")) {
               logger.info("Trying to set default translation to: " + translationData.getId());
               try {
                  translationData.load();
                  translation.setDefault(translationData);
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
            Iterator<TranslationData> iter = translation.getAllTranslation().iterator();
            if (iter.hasNext()) {
               TranslationData td = iter.next();
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
         List<TranslationData> customList = translation.getCustomGroup();
         List<String> customs = props.getList("trans.custom");
         for (int i = 0; i < customs.size(); i++) {
            String tid = customs.get(i);
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
      TranslationData td = null;
      ZipFile zipFile = null;
      try {
         zipFile = new ZipFile(transZipFile);
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

         td = new TranslationData();
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

         //create a LocalizedInstance for this translation.
         // <patch>
         LocalizedResource localizedResource = new LocalizedResource();
         localizedResource.loadLocalizedNames(pc, NAME_ATTR);
         localizedResource.setLanguage(td.locale.getLanguage());
         td.setLocalizedResource(localizedResource);
         td.setFile(transZipFile);
         // </patch>

         if (StringUtils.isBlank(td.id) || StringUtils.isBlank(td.name) || StringUtils.isBlank(td.file)
               || StringUtils.isBlank(td.version)) {
            logger.warn("Invalid translation: \"" + td + "\".");
            return null;
         }

         if (zipFile.getEntry(td.file) == null) {
            logger.warn("Invalid translation format. File not exists in the archive: " + td.file);
            return null;
         }
      } finally {
         if (zipFile != null) {
            ZipUtils.closeQuietly(zipFile);
         }
      }

      return td;
   }

   @SuppressWarnings("unchecked")
   private void extractViewProps() {
      ThemeData td;
      Reader reader;
      String def = props.getString("theme.default");
      logger.info("Loading theme .properties files.");

      String[] paths = { ApplicationPath.THEME_DIR, Naming.getThemeDir() };
      for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
         File targetThemeDir = new File(paths[pathIndex]);
         if (!targetThemeDir.exists()) {
            continue;
         }

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
               td.props = new LinkedHashMap<String, String>(); // order is important for options table!
               for (Iterator<String> iter = pc.getKeys(); iter.hasNext();) {
                  String key = iter.next();
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
               if (getTranslation().getDefault() != null) {
                  td.process(getTranslation().getDefault().locale.getLanguage());
               } else {
                  td.process("en");
               }

               theme.add(td);

               if (td.id.equals(def)) {
                  theme.setCurrent(td);
               }
            } catch (Exception e) {
               logger.warn("Can not load theme \"" + targetThemes[i].getName() + "\", because of the following exception:");
               logger.log(e);
            }
         }
      }
      if (theme.getCurrent() == null) {
         logger.doFatal(new ZekrBaseException("Could not find default theme: " + def));
      }
   }

   @SuppressWarnings("unchecked")
   private void extractAudioProps() {
      String def = props.getString("audio.default");
      List<String> selectedList = props.getList("audio.default");
      if (org.apache.commons.collections.CollectionUtils.isNotEmpty(selectedList) && selectedList.size() > 1) {
         def = selectedList.get(0);
      }
      logger.info("Loading audio .properties files.");

      String[] paths = { ApplicationPath.AUDIO_DIR, Naming.getAudioDir() };
      for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
         File audioDir = new File(paths[pathIndex]);
         if (!audioDir.exists()) {
            continue;
         }

         logger.info("Loading audio files info from: " + audioDir);
         FileFilter filter = new FileFilter() { // accept .properties files
            public boolean accept(File pathname) {
               if (pathname.getName().toLowerCase().endsWith(".properties")) {
                  return true;
               }
               return false;
            }
         };
         File[] audioPropFiles = audioDir.listFiles(filter);

         for (int audioIndex = 0; audioIndex < audioPropFiles.length; audioIndex++) {
            try {
               AudioData audioData = loadAudioData(audioPropFiles[audioIndex], true);
               if (audioData == null || audioData.getId() == null) {
                  continue;
               }

               audio.add(audioData);
               if (audioData.id.equals(def)) {
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
            for (AudioData ad : audio.getAllAudio()) {
               if ("offline".equals(ad.type)) {
                  audio.setCurrent(ad);
                  props.setProperty("audio.default", ad.id);
                  logger.warn("Setting another recitation as default: " + audio.getCurrent());
                  break;
               }
            }
            if (audio.getCurrent() == null) {
               audio.setCurrent(audio.getAllAudio().iterator().next());
               props.setProperty("audio.default", audio.getCurrent().id);
               logger.warn("Setting another recitation as default: " + audio.getCurrent());
            }
         } else {
            logger.warn("No other recitation found. Audio will be disabled.");
         }
      }

      // load if list of default audio data
      if (audio.getCurrent() != null) {
         // audio.getCurrentList().add(audio.getCurrent());
         for (String audioId : selectedList) {
            AudioData ad = audio.get(audioId);
            if (ad != null) {
               audio.getCurrentList().add(ad);
            }
         }
         if (audio.getCurrentList().size() <= 0) {
            audio.getCurrentList().add(audio.getCurrent());
         }
      }
   }

   @SuppressWarnings("unchecked")
   public AudioData loadAudioData(File audioFile, boolean convertOldFormat) throws FileNotFoundException,
         UnsupportedEncodingException, ConfigurationException, IOException {
      PropertiesConfiguration pc = ConfigUtils.loadConfig(audioFile, "UTF-8");

      AudioData audioData;
      audioData = new AudioData();
      audioData.id = pc.getString("audio.id");
      audioData.file = audioFile;
      // note that audio.version should be made up of digits and dots only, so 0.7.5beta1 is invalid.
      audioData.version = pc.getString("audio.version");
      if (StringUtils.isBlank(audioData.version)) { // old format
         logger.warn("Not a valid recitation file. No version specified: " + audioFile);
         if (convertOldFormat) {
            logger.info("Will try to convert recitation file: " + audioFile);
            audioData = RecitationPackConverter.convert(audioFile);
            if (audioData == null) {
               logger.info("Conversion failed for " + audioFile);
               return null;
            }
            File destDir = new File(FilenameUtils.getFullPath(audioFile.getAbsolutePath()) + "old-recitation-files");
            logger.info(String.format("Move %s to %s.", audioFile, destDir));
            FileUtils.moveFileToDirectory(audioFile, destDir, true);

            Writer w = new FileWriter(audioFile);
            StringWriter sw = new StringWriter();
            audioData.save(sw);
            w.write(sw.toString());
            IOUtils.closeQuietly(w);
            return audioData;
         } else {
            return null;
         }
      } else if (CommonUtils.compareVersions(audioData.version, AudioData.BASE_VALID_VERSION) < 0) {
         logger.warn(String.format(
               "Version is not supported anymore: %s. Zekr supports a recitation file of version %s or newer.",
               audioData.version, AudioData.BASE_VALID_VERSION));
         return null;
      }
      audioData.lastUpdate = pc.getString("audio.lastUpdate");
      audioData.quality = pc.getString("audio.quality", "?");

      // audioData.name = pc.getString("audio.name");
      audioData.license = pc.getString("audio.license");
      audioData.locale = new Locale(pc.getString("audio.language"), pc.getString("audio.country"));
      audioData.type = pc.getString("audio.type", "online");

      audioData.setLanguage(audioData.locale.getDisplayLanguage());//this will make it accessible from LocateResource super class.
      audioData.loadLocalizedNames(pc, "audio.reciter");

      Iterator<String> keys = pc.getKeys("audio.reciter");
      while (keys.hasNext()) {
         String key = keys.next();
         if (key.equals("audio.reciter")) {
            continue;
         }
         String lang = key.substring("audio.reciter".length() + 1);
         audioData.localizedNameMap.put(lang, pc.getString(key));
      }

      audioData.offlineUrl = pc.getString("audio.offlineUrl");
      audioData.onlineUrl = pc.getString("audio.onlineUrl");

      audioData.onlineAudhubillah = pc.getString("audio.onlineAudhubillah");
      // keep backward compatibility for old typo in files (bismillam instead of bismillah)
      audioData.onlineBismillah = pc.getString("audio.onlineBismillah", pc.getString("audio.onlineBismillam"));
      // keep backward compatibility for old typo in files (saghaghallah instead of sadaghallah)
      audioData.onlineSadaghallah = pc.getString("audio.onlineSadaghallah", pc.getString("audio.onlineSaghaghallah"));

      audioData.offlineAudhubillah = pc.getString("audio.offlineAudhubillah");
      // keep backward compatibility for old typo in files (bismillam instead of bismillah)
      audioData.offlineBismillah = pc.getString("audio.offlineBismillah", pc.getString("audio.offlineBismillam"));
      // keep backward compatibility for old typo in files (saghaghallah instead of sadaghallah)
      audioData.offlineSadaghallah = pc.getString("audio.offlineSadaghallah", pc.getString("audio.offlineSaghaghallah"));
      return audioData;
   }

   private void setupAudioManager() {
      audioCacheManager = new AudioCacheManager(props);
      // long period = props.getLong("audio.cache.timerPeriod", 3600000);
      // start after one minute, run every audio.cache.timerPeriod milliseconds
      // logger.debug("Setup audio cache timer task.");
      // new Timer("Audio Cache Task", true).schedule(new AudioCacheManagerTimerTask(audioCacheManager), 60000, period);

      logger.debug("Initialize player controller.");
      playerController = new DefaultPlayerController(props);
      searchPlayerController = new DefaultPlayerController(props);
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
            if (pathname.getName().toLowerCase().endsWith(ApplicationPath.REVEL_PACK_SUFFIX)) {
               return true;
            }
            return false;
         }
      };
      File[] revelFiles = revelDir.listFiles(filter);

      RevelationData rd;
      for (int revelIndex = 0; revelIndex < revelFiles.length; revelIndex++) {
         ZipFile zipFile = null;
         try {
            rd = loadRevelationData(revelFiles[revelIndex]);
            if (rd == null) {
               continue;
            }
            revelation.add(rd);
            if (rd.id.equals(def)) {
               rd.load();
               logger.info("Default revelation data is: " + rd);
               revelation.setDefault(rd);
            }
         } catch (Exception e) {
            logger.warn("Can not load revelation data pack \"" + zipFile + "\" properly because of the following exception:");
            logger.log(e);
         }
      }
   }

   private RevelationData loadRevelationData(File revelZipFile) throws IOException, ConfigurationException {
      ZipFile zipFile = new ZipFile(revelZipFile);
      InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.REVELATION_DESC));
      if (is == null) {
         logger.warn("Will ignore invalid revelation data archive \"" + zipFile.getName() + "\".");
         return null;
      }
      PropertiesConfiguration pc = ConfigUtils.loadConfig(is, "UTF-8");
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
      rd.suraOrders = new int[len];
      rd.orders = new int[len];
      // rd.years = new int[len]; // not used for now

      rd.version = pc.getString("version");
      String zipFileName = revelZipFile.getName();
      rd.id = zipFileName.substring(0, zipFileName.length() - ApplicationPath.REVEL_PACK_SUFFIX.length());
      rd.archiveFile = revelZipFile;
      rd.delimiter = pc.getString("delimiter", "\n");
      String sig = pc.getString("signature");

      byte[] sigBytes = sig.getBytes("US-ASCII");
      rd.signature = sig == null ? null : Base64.decodeBase64(sigBytes);

      rd.loadLocalizedNames(pc, "name");

      if (StringUtils.isBlank(rd.id) || rd.localizedNameMap.size() == 0 || StringUtils.isBlank(rd.version)) {
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
            if (pathname.getName().toLowerCase().endsWith(ApplicationPath.PAGING_PACK_SUFFIX)) {
               return true;
            }
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
      if (langEngine == null) {
         langEngine = LanguageEngine.getInstance();
      }
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

      for (Iterator<TranslationData> iterator = translation.getCustomGroup().iterator(); iterator.hasNext();) {
         TranslationData td = iterator.next();
         if (td.id.equals(defId)) {
            unloadPrevTrans = false;
            break;
         }
      }

      TranslationData oldTd = translation.getDefault();
      TranslationData newTrans = getTranslation().get(transId);
      newTrans.load();
      translation.setDefault(newTrans);
      props.setProperty("trans.default", transId);

      if (unloadPrevTrans) {
         logger.info("Unload previous selected translation which is not used anymore: " + oldTd);
         oldTd.unloadTranslationDataFile();
      }

      try {
         runtime.recreateViewCache();
      } catch (IOException e) {
         logger.log(e);
      }
   }

   /**
    * @param audioId pass null to remove this audio
    * @param reciterIndex
    */
   public void setSelectedAudio(String audioId, int reciterIndex) {
      AudioData ad;
      if (audioId != null) { // add
         logger.info(String.format("Set selected recitation to: %s, index: %s", audioId, reciterIndex));
         ad = audio.get(audioId);

         if (reciterIndex == 0) {
            audio.setCurrent(ad);
         }

         // ensure size
         while (audio.getCurrentList().size() < reciterIndex + 1) {
            /*if (audio.getCurrentList().size() < reciterIndex) {*/
            audio.getCurrentList().add(null);
            /*}*/
         }
         audio.getCurrentList().set(reciterIndex, ad);
      } else { // remove
         assert reciterIndex < audio.getCurrentList().size() : "reciter index to remove is larger than selected recitation list size";
         if (reciterIndex <= 0) {
            throw new IllegalArgumentException("First recitation cannot be deleted");
         }
         ad = audio.getCurrentList().get(reciterIndex);
         logger.info(String.format("Remove selected recitation from index: %s, id: %s", reciterIndex,
               audio.getCurrentList().get(reciterIndex).id));
         audio.getCurrentList().remove(reciterIndex);
      }

      // props.setProperty("audio.default", audioId);
      props.setProperty("audio.default", audio.getCurrentIdList());

      try {
         // runtime.recreateViewCache(); // this is probably historical and is no more needed
         // runtime.recreatePlaylistCache(); // not really needed
      } catch (Exception e) {
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
      // return props.getBoolean("server.http.enable");
      return false;
   }

   public boolean isRootDatabaseEnabled() {
      return props.getBoolean("root.enable", true);
   }

   public boolean useMozilla() {
      // TODO: remove this property and use something like options.browser.mode = mozilla, webkit, etc.
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
      // return httpServer;
      return null;
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
   public List<TranslationData> getCustomTranslationList() {
      return translation.getCustomGroup();
   }

   /**
    * @param newIdList a list of new translation data IDs (list contains Strings).
    * @throws TranslationException
    */
   public void setCustomTranslationList(List<String> newIdList) throws TranslationException {
      List<TranslationData> newList = new ArrayList<TranslationData>();

      // load new translation packs
      for (int i = 0; i < newIdList.size(); i++) {
         String id = newIdList.get(i);
         TranslationData td = translation.get(id);
         td.load();
         newList.add(td);
      }

      String defaultId = translation.getDefault().id;

      // unload old translation packs (which are not included in the new list)
      List<TranslationData> oldCustomList = translation.getCustomGroup();
      for (int i = 0; i < oldCustomList.size(); i++) {
         TranslationData oldTd = oldCustomList.get(i);
         if (!newIdList.contains(oldTd.id) && !oldTd.id.equals(defaultId)) {
            logger.info("Unload previous selected translation which is not used anymore: " + oldTd);
            oldTd.unloadTranslationDataFile();
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
    * This method is used to add a new translation during runtime. It loads translation metadata and adds it to the list of
    * translations. If translation pack is not authentic, it throws a ZekrMessageException just to inform user.
    * 
    * @param transFile a translation zip archive to be loaded
    * @throws ZekrMessageException with the proper message key and parameters if any exception occurred
    */
   public TranslationData addNewTranslation(File transFile) throws ZekrMessageException {
      logger.debug("Add new translation: " + transFile);
      try {
         TranslationData td = loadTranslationData(transFile);
         if (td == null) {
            throw new ZekrMessageException("INVALID_TRANSLATION_FORMAT", new String[] { transFile.getName() });
         }
         translation.add(td);
         if (!td.verify())
            throw new InvalidResourceException("Translation failed to verify");
         else
            return td;
      } catch (ZekrMessageException zme) {
         throw zme;
      } catch (Exception e) {
         throw new ZekrMessageException("TRANSLATION_LOAD_FAILED", new String[] { transFile.getName(), e.toString() });
      }

   }

   public AudioData addNewRecitationPack(File zipFileToImport, String destDir, IntallationProgressListener progressListener)
         throws ZekrMessageException {
      try {
         ZipFile zipFile = new ZipFile(zipFileToImport);
         InputStream is = zipFile.getInputStream(new ZipEntry(ApplicationPath.RECITATION_DESC));
         if (is == null) {
            logger.debug(String.format("Could not find recitation descriptor %s in the root of the zip archive %s.",
                  zipFileToImport, ApplicationPath.RECITATION_DESC));
            throw new ZekrMessageException("INVALID_RECITATION_FORMAT", new String[] { zipFileToImport.getName() });
         }

         String tempFileName = System.currentTimeMillis() + "-" + ApplicationPath.RECITATION_DESC;
         tempFileName = System.getProperty("java.io.tmpdir") + "/" + tempFileName;
         File recitPropsFile = new File(tempFileName);
         OutputStreamWriter output = null;
         InputStreamReader input = null;
         try {
            output = new OutputStreamWriter(new FileOutputStream(recitPropsFile), "UTF-8");
            input = new InputStreamReader(is, "UTF-8");
            IOUtils.copy(input, output);
         } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
         }
         logger.debug("Add new recitation: " + recitPropsFile);

         AudioData newAudioData = loadAudioData(recitPropsFile, false);
         if (newAudioData == null || newAudioData.getId() == null) {
            logger.debug("Invalid recitation descriptor: " + recitPropsFile);
            throw new ZekrMessageException("INVALID_RECITATION_FORMAT", new String[] { zipFileToImport.getName() });
         }
         File newRecitPropsFile = new File(destDir, newAudioData.id + ".properties");

         if (newRecitPropsFile.exists()) {
            newRecitPropsFile.delete();
         }
         FileUtils.moveFile(recitPropsFile, newRecitPropsFile);

         /*
         ZipEntry recFolderEntry = zipFile.getEntry(newAudioData.id);
         if (recFolderEntry == null || !recFolderEntry.isDirectory()) {
         	logger.warn(String.format("Recitation audio folder (%s) doesn't exist in the root of archive %s.",
         			newAudioData.id, zipFileToImport));
         	throw new ZekrMessageException("INVALID_RECITATION_FORMAT", new String[] { zipFileToImport.getName() });
         }
         */

         AudioData installedAudioData = audio.get(newAudioData.id);
         if (installedAudioData != null) {
            if (newAudioData.compareTo(installedAudioData) < 0) {
               throw new ZekrMessageException("NEWER_VERSION_INSTALLED", new String[] { recitPropsFile.toString(),
                     newAudioData.lastUpdate, installedAudioData.lastUpdate });
            }
         }

         newAudioData.file = newRecitPropsFile;

         logger.info(String.format("Start uncompressing recitation: %s with size: %s to %s.", zipFileToImport.getName(),
               FileUtils.byteCountToDisplaySize(zipFileToImport.length()), destDir));

         boolean result;
         try {
            result = ZipUtils.extract(zipFileToImport, destDir, progressListener);
         } finally {
            File file = new File(newRecitPropsFile.getParent(), ApplicationPath.RECITATION_DESC);
            if (file.exists()) {
               FileUtils.deleteQuietly(file);
            }
         }
         if (result) {
            logger.info("Uncompressing process done: " + zipFileToImport.getName());
            audio.add(newAudioData);
         } else {
            logger.info("Uncompressing process intrrrupted: " + zipFileToImport.getName());
         }

         // FileUtils.deleteQuietly(new File(newRecitPropsFile.getParent(), ApplicationPath.RECITATION_DESC));

         progressListener.finish(newAudioData);

         return result ? newAudioData : null;
      } catch (ZekrMessageException zme) {
         throw zme;
      } catch (Exception e) {
         logger.error("Error occurred while adding new recitation archive.", e);
         throw new ZekrMessageException("RECITATION_LOAD_FAILED", new String[] { zipFileToImport.getName(), e.toString() });
      }
   }

   public AudioData addNewRecitation(File recitFile) throws ZekrMessageException {
      logger.debug("Add new recitation: " + recitFile);
      try {
         AudioData newAudioData = loadAudioData(recitFile, true);
         if (newAudioData == null || newAudioData.getId() == null) {
            throw new ZekrMessageException("INVALID_RECITATION_FORMAT", new String[] { recitFile.getName() });
         }
         AudioData installedAudioData = audio.get(newAudioData.id);
         if (installedAudioData != null) {
            if (newAudioData.compareTo(installedAudioData) < 0) {
               throw new ZekrMessageException("NEWER_VERSION_INSTALLED", new String[] { recitFile.toString(),
                     newAudioData.lastUpdate, installedAudioData.lastUpdate });
            }
         }
         audio.add(newAudioData);
         return newAudioData;
      } catch (ZekrMessageException zme) {
         throw zme;
      } catch (Exception e) {
         throw new ZekrMessageException("RECITATION_LOAD_FAILED", new String[] { recitFile.getName(), e.toString() });
      }
   }

   public AudioCacheManager getAudioCacheManager() {
      return audioCacheManager;
   }

   public PlayerController getPlayerController() {
      return playerController;
   }

   public PlayerController getSearchPlayerController() {
      return searchPlayerController;
   }

   public NetworkController getNetworkController() {
      return networkController;
   }

   public KeyboardShortcut getShortcut() {
      return shortcut;
   }

   // <patch>
   /**
    * @param r
    * @return
    */
   /*@SuppressWarnings("unchecked")
   synchronized public boolean isCurrentlyInstalled(Resource r) {
   	String configurationKey = "resources." + r.getType().getSimpleName();
   	List idList = props.getList(configurationKey);
   	if (idList.contains(r.getId()))
   		return true;
   	else
   		return false;
   }
   */
   synchronized public Resource installResource(CandidateResource r, IntallationProgressListener progressListener)
         throws ZekrMessageException {
      File newInstalledFile = null;
      try {
         newInstalledFile = new File(r.getInstallationFolder() + "/" + r.getFile().getName());
         FileUtils.copyFile(r.getFile(), newInstalledFile);
         if (r.getType().equals(TranslationData.class)) {
            r.setInstalledResource(addNewTranslation(newInstalledFile));
         } else if (r.getType().equals(AudioData.class)) {
            if (r.getFile().getName().contains("offline"))//a little bit of a hack ;)
               r.setInstalledResource(addNewRecitationPack(newInstalledFile, ApplicationPath.AUDIO_DIR, progressListener));
            else if (r.getFile().getName().contains("online"))
               r.setInstalledResource(addNewRecitation(newInstalledFile));
         } else
            throw new InvalidParameterException("ResourceType not been implement yet");

         /*String configurationKey = "resources." + r.getType().getSimpleName();
         List idList = props.getList(configurationKey);
         if (!idList.contains(r.getInstalledResource().getId()))
         	idList.add(r.getInstalledResource().getId());
         props.setProperty(configurationKey, idList);
         saveConfig();*/
         return r.getInstalledResource();
      } catch (IOException e) {
         if (newInstalledFile != null)
            FileUtils.deleteQuietly(newInstalledFile);
         e.printStackTrace();
         throw new ZekrMessageException(e);
      }
      /*} catch (ConfigurationException e) {
      	e.printStackTrace();
      	throw new ZekrMessageException(e);
      }*/
   }

   @SuppressWarnings("rawtypes")
   synchronized public void unistallResource(Resource r, IntallationProgressListener progressListener) {
      AddOnManagerUtils.unload(r);

      if (r.getType().equals(TranslationData.class)) {
         translation.getCustomGroup().remove(r);
         List idList = props.getList("trans.custom");
         if (idList.contains(r.getId()))
            idList.remove(r.getId());
         props.setProperty("trans.custom", idList);
         saveConfig();
      } else if (r.getType().equals(AudioData.class)) {
         /*some task when un-installing recitations*/
      } else
         throw new InvalidParameterException("ResourceType not been implement yet");

      /*String configurationKey = "resources." + r.getType().getSimpleName();
      List idList = props.getList(configurationKey);
      if (idList.contains(r.getId()))
      	idList.remove(r.getId());
      props.setProperty(configurationKey, idList);
      saveConfig();*/

      FileUtils.deleteQuietly(r.getFile());
      progressListener.finish(r);
   }
   // </patch>
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.engine.translation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.AbstractQuranText;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.util.CryptoUtils;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.engine.addonmgr.AddOnManagerUtils;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.common.LocalizedResource;
import net.sf.zekr.engine.common.Signable;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class TranslationData extends AbstractQuranText implements Signable, Resource {
   private static final Logger logger = Logger.getLogger(TranslationData.class);

   /** Translation Id. */
   public String id;

   /** Translation English name */
   public String name;

   /** Translation localized name */
   public String localizedName;

   /** Language (locale) Id (e.g. en_US) */
   public Locale locale;

   /** Text direction: ltr or rtl */
   public String direction;

   /** String encoding */
   public String encoding;

   /** Line delimiter String (each line contains an aya) */
   public String delimiter;

   public File archiveFile;

   /** Text file name */
   public String file;

   private String[][] transText;
   private String[] fullTransText;

   /** signature of the text file */
   public byte[] signature;

   public boolean verified = false;
   private boolean loaded = false;

   /** descriptor version */
   public String version;

   private int verificationResult = UNKNOWN;

   private LocalizedResource localizedResource;
   private File resourceFile;

   public TranslationData() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sf.zekr.common.util.IQuranText#get(int, int)
    */
   public String get(int suraNum, int ayaNum) {
      return transText[suraNum - 1][ayaNum - 1];
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sf.zekr.common.util.IQuranText#getSura(int)
    */
   public String[] getSura(int suraNum) {
      return transText[suraNum - 1];
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sf.zekr.common.util.IQuranText#getFullText()
    */
   public String[][] getFullText() {
      return transText;
   }

   public String toString() {
      return id + "(" + locale + "):(" + archiveFile.getName() + ")";
   }

   /**
    * Loads the tranalation data file, if not already loaded.
    */
   public void load() throws TranslationException {
      if (!loaded) {
         Date date1 = new Date();
         loadAndVerify();
         Date date2 = new Date();
         logger.debug("Loading translation \"" + id + "\" took " + (date2.getTime() - date1.getTime()) + " ms.");
         loaded = true;
      } else {
         logger.debug("Translation already loaded: " + id);
      }
   }

   /**
    * Unloads the content of translation in order to let Java free more memory.
    */
   public void unloadTranslationDataFile() {
      fullTransText = null;
      transText = null;
      loaded = false;
   }

   private void loadAndVerify() throws TranslationException {
      ZipFile zf = null;
      try {
         logger.info("Loading translation pack " + this + "...");
         zf = new ZipFile(archiveFile);
         ZipEntry ze = zf.getEntry(file);
         if (ze == null) {
            logger.error("Load failed. No proper entry found in \"" + archiveFile.getName() + "\".");
            return;
         }

         byte[] textBuf = new byte[(int) ze.getSize()];
         if (!verify(zf.getInputStream(ze), textBuf))
            logger.warn("Unauthorized translation data pack: " + this);
         // throw new TranslationException("INVALID_TRANSLATION_SIGNATURE", new String[] { name });

         refineText(new String(textBuf, encoding));

         logger.log("Translation pack " + this + " loaded successfully.");
      } catch (IOException e) {
         logger.error("Problem while loading translation pack " + this + ".");
         logger.log(e);
         throw new TranslationException(e);
      } finally {
         try {
            zf.close();
         } catch (Exception e) {
            // do nothing
         }
      }
   }

   /**
    * Verify the zip archive and close the zip file handle finally.
    * 
    * @return <code>true</code> if translation verified, <code>false</code> otherwise.
    * @throws IOException
    */
   public boolean verify() throws IOException {
      ZipFile zf = new ZipFile(archiveFile);
      ZipEntry ze = zf.getEntry(file);
      if (ze == null) {
         logger.error("Load failed. No proper entry found in \"" + archiveFile.getName() + "\".");
         return false;
      }

      byte[] textBuf = new byte[(int) ze.getSize()];
      boolean result;
      result = verify(zf.getInputStream(ze), textBuf);
      zf.close();
      return result;
   }

   private boolean verify(InputStream is, byte[] textBuf) throws IOException {
      BufferedInputStream bis = new BufferedInputStream(is, 262144);
      bis.read(textBuf, 0, textBuf.length);

      logger.debug("Verifying translation text.");
      try {
         verified = CryptoUtils.verify(textBuf, signature);
      } catch (Exception e) {
         logger.warn("Error occurred during translation text verification. Text cannot be verified.", e);
      }
      if (verified) {
         logger.debug("Translation is valid");
         verificationResult = AUTHENTIC;
      } else {
         logger.debug("Translation is not valid.");
         verificationResult = NOT_AUTHENTIC;
      }
      return verified;
   }

   private void refineText(String rawText) {
      QuranProperties quranProps = QuranProperties.getInstance();
      String[] sura;
      fullTransText = rawText.split(delimiter);
      transText = new String[114][];
      int ayaTotalCount = 0;
      for (int i = 0; i < 114; i++) {
         int ayaCount = quranProps.getSura(i + 1).getAyaCount();
         sura = new String[ayaCount];
         for (int j = 0; j < ayaCount; j++) {
            sura[j] = fullTransText[ayaTotalCount + j];
         }
         transText[i] = sura;
         ayaTotalCount += ayaCount;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sf.zekr.common.resource.IQuranText#get(int)
    */
   public String get(int absoluteAyaNum) {
      return fullTransText[absoluteAyaNum - 1];
   }

   public boolean isTranslation() {
      return true;
   }

   public String getLanguage() {
      return locale.getLanguage();
   }

   /**
    * This method always returns the first aya of sura 1 (Al-Fatiha).
    * 
    * @param suraNum sura number, counted from 1
    * @return sura 1, aya 1
    */
   public String getBismillah(int suraNum) {
      return get(1, 1);
   }

   public String toText() {
      return localizedName + " / " + name;
   }

   public String getDirection() {
      return direction;
   }

   public Locale getLocale() {
      return locale;
   }

   public String getId() {
      return id;
   }

   public byte[] getSignature() {
      return signature;
   }

   public int getVerificationResult() {
      return verificationResult;
   }

   public int getMode() {
      throw new UnsupportedOperationException("Method not implemented.");
   }

   public void setLocalizedResource(LocalizedResource r) {
      this.localizedResource = r;
   }

   /* Methods from the Resource interface
    * 
    */

   public LocalizedResource getLocalizedResource() {
      return localizedResource;
   }

   public String getDescription() {
      return AddOnManagerUtils.getResourceDescription(this);
   }

   public boolean isCurrent() {
      return AddOnManagerUtils.isCurrent(this);
   }

   public boolean isLoaded() {
      return AddOnManagerUtils.isLoaded(this);
   }

   @SuppressWarnings("rawtypes")
   public Class getType() {
      return this.getClass();
   }

   public File getFile() {
      return resourceFile;
   }

   public void setFile(File resourceFile) {
      this.resourceFile = resourceFile;
   }

   public Boolean isShared() {
      return AddOnManagerUtils.isResourceShared(this);
   }

   public String getInstallationFolder() {
      return AddOnManagerUtils.getInstallationFolder(this);
   }

   public void setIsShared(Boolean b) {
      throw new RuntimeException("You cannot call this method on a installed resource " + this.getDescription());
   }

   public String getName(String transNameMode, boolean rtl) {
      String s = "english".equals(transNameMode) ? name : localizedName;
      s = StringUtils.abbreviate((rtl ? I18N.RLE + "" : "") + "[" + locale.getLanguage() + "]" + " " + (rtl ? I18N.RLM + "" : "")
            + s, GlobalConfig.MAX_MENU_STRING_LENGTH);

      return s;
   }

   public String getName() {
      return name;
   }

   public String getLocalizedName() {
      return localizedName;
   }

}

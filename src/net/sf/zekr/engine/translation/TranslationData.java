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
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.util.CryptoUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class TranslationData implements IQuranText {
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
	public String lineDelimiter;

	public ZipFile archiveFile;

	/** Text file name */
	public String file;

	private String[][] transText;

	/** signature of the text file */
	public byte[] signature;

	public boolean verified = false;

	/** descriptor version */
	public String version;

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
	public void load() {
		if (!loaded()) {
			Date date1 = new Date();
			loadAndVerify();
			Date date2 = new Date();
			logger.debug("Loading translation \"" + id + "\" took " + (date2.getTime() - date1.getTime()) + " ms.");
		} else {
			logger.debug("Translation already loaded:" + id);
		}
	}

	public boolean loaded() {
		return transText != null;
	}

	private void loadAndVerify() {
		try {
			logger.info("Loading translation pack " + this + "...");
			ZipEntry ze = archiveFile.getEntry(file);
			if (ze == null) {
				logger.error("Load failed. No proper entry found in \"" + archiveFile.getName() + "\".");
				return;
			}

			BufferedInputStream bis = new BufferedInputStream(archiveFile.getInputStream(ze), 262144);
			byte textBuf[] = new byte[(int) ze.getSize()];
			bis.read(textBuf, 0, (int) ze.getSize());

			logger.info("Verifying translation text.");
			try {
				verified = CryptoUtils.verify(textBuf, signature);
			} catch (GeneralSecurityException e) {
				logger.error("Error occurred during translation text verification. Text cannot be verified.");
				logger.error(e);
			}
			if (verified)
				logger.info("Translation text is valid");
			else
				logger.info("Translation text is not valid.");

			refineText(new String(textBuf, encoding));

			logger.log("Translation pack " + this + " loaded successfully.");
		} catch (IOException e) {
			logger.error("Problem while loading translation pack " + this + ".");
			logger.log(e);
		}
	}

	private void refineText(String rawText) {
		QuranProperties quranProps = QuranProperties.getInstance();
		String[] sura;
		String[] fullTrans = rawText.split(lineDelimiter);
		transText = new String[114][];
		int ayaTotalCount = 0;
		for (int i = 0; i < 114; i++) {
			int ayaCount = quranProps.getSura(i + 1).getAyaCount();
			sura = new String[ayaCount];
			for (int j = 0; j < ayaCount; j++) {
				sura[j] = fullTrans[ayaTotalCount + j];
			}
			transText[i] = sura;
			ayaTotalCount += ayaCount;
		}
	}

	public TranslationData getTranslationData() {
		return this;
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
}

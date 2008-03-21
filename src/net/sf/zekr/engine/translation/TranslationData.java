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
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.resource.AbstractQuranText;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.util.CryptoUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 */
public class TranslationData extends AbstractQuranText {
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

	public File archiveFile;

	/** Text file name */
	public String file;

	private String[][] transText;
	private String[] fullTransText;

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
	public void load() throws TranslationException {
		if (!loaded()) {
			Date date1 = new Date();
			loadAndVerify();
			Date date2 = new Date();
			logger.debug("Loading translation \"" + id + "\" took " + (date2.getTime() - date1.getTime()) + " ms.");
		} else {
			logger.debug("Translation already loaded: " + id);
		}
	}

	public boolean loaded() {
		return transText != null;
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
				throw new TranslationException("INVALID_TRANSLATION_SIGNATURE", new String[] { name });

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
		boolean result = verify(zf.getInputStream(ze), textBuf);
		zf.close();
		return result;
	}

	private boolean verify(InputStream is, byte[] textBuf) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is, 262144);
		bis.read(textBuf, 0, textBuf.length);

		logger.debug("Verifying translation text.");
		try {
			verified = CryptoUtils.verify(textBuf, signature);
		} catch (GeneralSecurityException e) {
			logger.error("Error occurred during translation text verification. Text cannot be verified.");
			logger.error(e);
		}
		if (verified)
			logger.debug("Translation is valid");
		else
			logger.debug("Translation is not valid.");
		return verified;
	}

	private void refineText(String rawText) {
		QuranProperties quranProps = QuranProperties.getInstance();
		String[] sura;
		fullTransText = rawText.split(lineDelimiter);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.resource.IQuranText#getTranslationData()
	 */
	public TranslationData getTranslationData() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.resource.IQuranText#getBismillah(int)
	 */
	public String getBismillah(int suraNum) {
		return null;
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

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.common.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.3
 */
public class TranslationData implements IQuranText {
	private final static Logger logger = Logger.getLogger(TranslationData.class);

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
		if (!loaded())
			loadFile();
	}

	public boolean loaded() {
		return transText != null;
	}

	public void loadFile() {
		try {
			logger.info("Loading translation pack " + this + "...");
			ZipEntry ze = archiveFile.getEntry(file);
			if (ze == null) {
				logger.error("Load failed. No proper entry found in \"" + archiveFile.getName() + "\".");
				return;
			}
			Reader reader;
			reader = new InputStreamReader(archiveFile.getInputStream(ze), encoding);
			loadTranslation(reader, (int) ze.getSize());
			logger.log("Translation pack " + this + " loaded successfully.");
		} catch (IOException e) {
			logger.error("Problem while loading translation pack " + this + ".");
			logger.log(e);
		}
	}

	private void loadTranslation(Reader reader, int length) throws IOException {
		char buffer[] = new char[16384];
		StringBuffer strBuf = new StringBuffer();
		while (reader.read(buffer) != -1) {
			strBuf.append(buffer);
		}

		refineText(strBuf.toString());
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
}

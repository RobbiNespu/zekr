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

import net.sf.zekr.common.util.IQuranTranslation;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class TranslationData implements IQuranTranslation {
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

	public String get(int suraNum, int ayaNum) {
		return transText[suraNum - 1][ayaNum - 1];
	}

	public String[] getSura(int suraNum) {
		return transText[suraNum - 1];
	}

	public String[][] getFullText() {
		return transText;
	}

	public String toString() {
		return id + " (" + locale + ")";
	}

	public void load() {
		if (!loaded())
			loadFile();
	}

	public boolean loaded() {
		return transText != null;
	}

	public void loadFile() {
		try {
			logger.log("Loading translation pack " + this + "...");
			ZipEntry ze = archiveFile.getEntry(file);
			if (ze == null) {
				logger.error("Load failed. No proper entry found in \"" + archiveFile.getName()
						+ "\".");
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
		char[] buf = new char[length];
		reader.read(buf); // read the translation text fully
		String rawText = new String(buf);
		refineText(rawText);
	}

	private void refineText(String rawText) {
		QuranProperties quranProps = QuranProperties.getInstance();
		StringTokenizer st = new StringTokenizer(rawText, lineDelimiter);
		String[] sura;
		transText = new String[114][];

		for (int i = 1; st.hasMoreTokens() && i <= 114; i++) {
			sura = new String[quranProps.getSura(i).getAyaCount()];
			for (int j = 0; j < sura.length; j++) {
				sura[j] = st.nextToken();
			}
			transText[i - 1] = sura;
		}
	}

}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 3, 2004
 */

package net.sf.zekr.common.resource;

import java.io.IOException;
import java.io.RandomAccessFile;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.ResourceManager;

import org.apache.commons.collections.MapUtils;

// TODO: All singleton classes of this kind should be gradually moved as a non-singleton class under
// ApplicationConfig. Once ApplicationConfig is initialized, a single instance of these class will be stored
// under it.

/**
 * This class is a repository for the whole quran text. All public methods act as 1-relative arrays. This
 * class acts as a pool for two types of Quran text file: "simple" and "uthmani".
 * 
 * @author Mohsen Saboorian
 */
public class QuranText extends AbstractQuranText {
	private static QuranText simpleInstance = null;
	private static QuranText uthmaniInstance = null;

	/** raw quran text written in the file. */
	private String rawText;

	/** Quran text in a sura-aya 2D array */
	private String[][] quranText = new String[114][];

	/** full Quran text as a 1D array */
	private String[] fullQuran;

	private final static ApplicationConfig config = ApplicationConfig.getInstance();
	private final static ResourceManager resource = ResourceManager.getInstance();

	private int mode = SIMPLE_MODE;

	/**
	 * The private constructor, which loads the whole Quran text from file into memory (<code>quranText</code>
	 * ).
	 * 
	 * @param textType can be either UTHMANI_MODE or SIMPLE_MODE
	 * @throws IOException
	 */
	protected QuranText(int textType) throws IOException {
		mode = textType;
		String qFile = ApplicationPath.SIMPLE_QURAN_TEXT_FILE;
		if (textType == UTHMANI_MODE) {
			qFile = ApplicationPath.UTHMANI_QURAN_TEXT_FILE;
		}

		RandomAccessFile raf = new RandomAccessFile(qFile, "r");
		byte[] buf = new byte[(int) raf.length()];
		raf.readFully(buf);
		rawText = new String(buf, config.getProps().getString("quran.text.encoding"));
		refineRawText();
		raf.close();
	}

	/**
	 * @return either simple or Uthmani Quran text based on the current theme
	 * @throws IOException
	 */
	public static QuranText getInstance() throws IOException {
		boolean uthmani = MapUtils.getBooleanValue(config.getTheme().getCurrent().props, "quran_uthmaniTextFile");
		return getInstance(uthmani ? UTHMANI_MODE : SIMPLE_MODE);
	}

	/**
	 * @return either simple or uthmani Quran text based on the current theme
	 * @param mode
	 * @throws IOException
	 */
	public static QuranText getInstance(int mode) throws IOException {
		if (mode == UTHMANI_MODE) {
			return getUthmaniTextInstance();
		} else {
			return getSimpleTextInstance();
		}
	}

	/**
	 * @return simple Quran text instance
	 * @throws IOException
	 */
	public static QuranText getSimpleTextInstance() throws IOException {
		if (simpleInstance == null) {
			simpleInstance = new QuranText(SIMPLE_MODE);
		}
		return simpleInstance;
	}

	/**
	 * @return Uthmani Quran text instance
	 * @throws IOException
	 */
	public static QuranText getUthmaniTextInstance() throws IOException {
		if (uthmaniInstance == null) {
			uthmaniInstance = new QuranText(UTHMANI_MODE);
		}
		return uthmaniInstance;
	}

	/**
	 * This private method refines the raw Quran, and stores it in a 2D array.
	 */
	private void refineRawText() {
		QuranProperties quranProps = QuranProperties.getInstance();
		String delim = config.getProps().getString("quran.text.delim");
		fullQuran = rawText.split(delim);
		String[] sura;
		quranText = new String[114][];
		int ayaTotalCount = 0;
		for (int i = 0; i < 114; i++) {
			int ayaCount = quranProps.getSura(i + 1).getAyaCount();
			sura = new String[ayaCount];
			for (int j = 0; j < ayaCount; j++) {
				sura[j] = fullQuran[ayaTotalCount + j];
			}
			quranText[i] = sura;
			ayaTotalCount += ayaCount;
		}
	}

	/**
	 * @return <code>UTHMANI_MODE</code> or <code>SIMPLE_MODE</code>
	 */
	public int getMode() {
		return mode;
	}

	public String get(int suraNum, int ayaNum) {
		return quranText[suraNum - 1][ayaNum - 1];
	}

	public String get(int absoluteAyaNum) {
		return fullQuran[absoluteAyaNum - 1];
	}

	public String[] getSura(int suraNum) {
		return quranText[suraNum - 1];

	}

	public String[][] getFullText() {
		return quranText;
	}

	public boolean isTranslation() {
		return false;
	}

	public String getLanguage() {
		return "ar";
	}

	public String getBismillah(int suraNum) {
		return get(1, 1);
	}

	public String toString() {
		return (mode == SIMPLE_MODE ? "Simple" : "Uthmani") + " Quran";
	}
}

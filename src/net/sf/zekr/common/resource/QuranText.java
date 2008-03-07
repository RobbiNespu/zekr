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
import net.sf.zekr.engine.translation.TranslationData;

// TODO: All singleton classes of this kind should be gradually moved as a non-singleton class under
// ApplicationConfig. Once ApplicationConfig is initialized, a single instance of these class will be stored
// under it.

/**
 * This class is a repository for the whole quran text. All public methods act as 1-relative arrays. This
 * class acts as a pool for two types of Quran text file: "simple" and "detailed".
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranText implements IQuranText {
	private static QuranText simpleInstance = null;
	private static QuranText detailedInstance = null;

	/** raw quran text written in the file. */
	private String rawText;

	/** full simple Quran text */
	private String[][] simpleQuranText = new String[114][];

	/** full detailed Quran text */
	private String[][] detailedQuranText = new String[114][];
	private final static ApplicationConfig config = ApplicationConfig.getInstance();
	private final static ResourceManager resource = ResourceManager.getInstance();

	private final static int DETAILED = 0;
	private final static int SIMPLE = 1;

	/**
	 * The private constructor, which loads the whole Quran text from file into memory (<code>quranText</code>).
	 * 
	 * @param textType can be either DETAILED or SIMPLE
	 * @throws IOException
	 */
	private QuranText(int textType) throws IOException {
		String qFile = ApplicationPath.SIMPLE_QURAN_TEXT_FILE;
		if (textType == DETAILED)
			qFile = ApplicationPath.DETILAED_QURAN_TEXT_FILE;

		RandomAccessFile raf = new RandomAccessFile(qFile, "r");
		byte[] buf = new byte[(int) raf.length()];
		raf.readFully(buf);
		rawText = new String(buf, config.getProps().getString("quran.text.encoding"));
		refineRawText();
	}

	/**
	 * @return either simple or detailed Quran text based on the current theme
	 * @throws IOException
	 */
	public static QuranText getInstance() throws IOException {
		Boolean detailed = Boolean.valueOf(config.getTheme().getCurrent().props.get("quran_detailedTextFile").toString());
		if (detailed.booleanValue())
			return getDetailedTextInstance();
		else
			return getSimpleTextInstance();
	}

	/**
	 * @return simple Quran text instance
	 * @throws IOException
	 */
	public static QuranText getSimpleTextInstance() throws IOException {
		if (simpleInstance == null)
			simpleInstance = new QuranText(SIMPLE);
		return simpleInstance;
	}

	/**
	 * @return detailed Quran text instance
	 * @throws IOException
	 */
	public static QuranText getDetailedTextInstance() throws IOException {
		if (detailedInstance == null)
			detailedInstance = new QuranText(DETAILED);
		return detailedInstance;
	}

	/**
	 * This private method refines the raw Quran, and stores it in a 2D array.
	 */
	private void refineRawText() {
		QuranProperties quranProps = QuranProperties.getInstance();
		String delim = config.getProps().getString("quran.text.delim");
		String[] fullQuran = rawText.split(delim);
		String[] sura;
		simpleQuranText = new String[114][];
		int ayaTotalCount = 0;
		for (int i = 0; i < 114; i++) {
			int ayaCount = quranProps.getSura(i + 1).getAyaCount();
			sura = new String[ayaCount];
			for (int j = 0; j < ayaCount; j++) {
				sura[j] = fullQuran[ayaTotalCount + j];
			}
			simpleQuranText[i] = sura;
			ayaTotalCount += ayaCount;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#get(int, int)
	 */
	public String get(int suraNum, int ayaNum) {
		return simpleQuranText[suraNum - 1][ayaNum - 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#getSura(int)
	 */
	public String[] getSura(int suraNum) {
		return simpleQuranText[suraNum - 1];

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#getFullText()
	 */
	public String[][] getFullText() {
		return simpleQuranText;
	}

	public TranslationData getTranslationData() {
		// do nothing
		return null;
	}
}

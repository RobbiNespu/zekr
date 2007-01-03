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
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * This class is a repository for the whole quran text. All public methods act as 1-relative arrays.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class QuranText implements IQuranText {
	private static QuranText thisInstance = null;
	/** The raw quran text written in the file. */
	private String rawText;

	/** The full quran text */
	private String[][] quranText = new String[114][];
	private ApplicationConfig config = ApplicationConfig.getInstance();
	private ResourceManager resource = ResourceManager.getInstance();

	/**
	 * The private constructor, which loads the whole Quran text from file into memory (<code>quranText</code>).
	 * It will also encode the read data as UTF-16 (for java <code>String</code>)
	 * 
	 * @throws IOException
	 */
	private QuranText() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(resource.getString("quran.text.file"), "r");
		byte[] buf = new byte[(int) raf.length()];
		raf.readFully(buf);
		rawText = new String(buf, config.getProps().getString("quran.text.encoding"));
		refineRawText();
	}

	public static QuranText getInstance() throws IOException {
		if (thisInstance == null)
			thisInstance = new QuranText();
		return thisInstance;
	}

	/**
	 * This private method refines the raw Quran, and stores it in a 2D array.
	 */
	private void refineRawText() {
		QuranProperties quranProps = QuranProperties.getInstance();
		String delim = config.getProps().getString("quran.text.delim");
		String[] fullQuran = rawText.split(delim);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#get(int, int)
	 */
	public String get(int suraNum, int ayaNum) {
		return quranText[suraNum - 1][ayaNum - 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#getSura(int)
	 */
	public String[] getSura(int suraNum) {
		return quranText[suraNum - 1];

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranText#getFullText()
	 */
	public String[][] getFullText() {
		return quranText;
	}

	public TranslationData getTranslationData() {
		// do nothing
		return null;
	}
}

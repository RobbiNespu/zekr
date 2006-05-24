/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 3, 2004
 */

package net.sf.zekr.common.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.util.IQuranText;

/**
 * This class is a repository for the whole quran text. All public methods act as
 * 1-relative arrays.
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
	private ApplicationConfig appConfig = ApplicationConfig.getInstance();
//	private QuranTextProperties textProps = QuranTextProperties.getInstance();
	private ResourceManager resource = ResourceManager.getInstance();


	/**
	 * The private constructor, which loads the whole Quran text from file into memory (
	 * <code>rawText</code>), refine it and stores it again in an
	 * <code>ArrayList</code>, to be accessed more easily.
	 * 
	 * @throws IOException
	 */
	private QuranText() throws IOException {
//		File file = new File(appConfig.getQuranText());
		File file = new File(resource.getString("quran.text.file"));
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), resource.getString("quran.text.encoding"));
		char[] buf = new char[(int) file.length()];
		isr.read(buf); // read the Quran text fully
		rawText = new String(buf);
		refineRawText();
	}

	public static QuranText getInstance() throws IOException {
		if (thisInstance == null)
			thisInstance = new QuranText();
		return thisInstance;
	}

	/**
	 * This private method refines the raw Quran, and stores it in an ArrayList. It will
	 * also encode the read data as UTF-16 (for java <code>String</code>).
	 */
	private void refineRawText() {
//		QuranTextProperties textProp = QuranTextProperties.getInstance();
//		QuranProperties quranProps = QuranProperties.getInstance();
//		StringTokenizer suraTokenizer = new StringTokenizer(rawText, textProp.getLineBreakString());
//		String sura;
//
//		suraTokenizer.nextToken(); // ignore sura title
//		// the first sura (Fatiha) has a Bismillah as its first aya.
//		sura = suraTokenizer.nextToken() + " ";
//		sura += suraTokenizer.nextToken();
//		quranText[0] = getAyas(sura, quranProps.getSura(1).getAyaCount());
//
//		for (int i = 2; suraTokenizer.hasMoreTokens(); i++) {
//			sura = suraTokenizer.nextToken(); // ignore title
//			if (i != 9) // Sura Tawbah has no Bismillah
//				sura = suraTokenizer.nextToken(); // ignore Bismillah
//			sura = suraTokenizer.nextToken();
//			quranText[i - 1] = getAyas(sura, quranProps.getSura(i).getAyaCount());
//		}
		QuranProperties quranProps = QuranProperties.getInstance();
		StringTokenizer st = new StringTokenizer(rawText, "\n");
		String[] sura;
		quranText = new String[114][];
		
		for (int i = 1; st.hasMoreTokens() && i <= 114; i++) {
			sura = new String[quranProps.getSura(i).getAyaCount()];
			for (int j = 0; j < sura.length; j++) {
				sura[j] = st.nextToken();
			}
			quranText[i - 1] = sura;
		}

	}

//	private String[] getAyas(String suraText, int ayaCount) {
//		String[] ayas = new String[ayaCount];
//		StringTokenizer ayaTokenizer = new StringTokenizer(suraText, textProps.getAyaSignLeftString()
//				+ textProps.getAyaSignRightString());
//		int i;
//		for (i = 0; ayaTokenizer.hasMoreTokens() && i < ayaCount; i++) {
//			ayas[i] = new String(ayaTokenizer.nextToken().trim());
//			ayaTokenizer.nextToken();
//		}
//		return ayas;
//	}

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

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.util.IQuranText#getFullText()
	 */
	public String[][] getFullText() {
		return quranText;
	}
}

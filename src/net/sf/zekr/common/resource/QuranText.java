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
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.engine.log.Logger;

/**
 * This class is a repository for the whole quran text. All public methods act as
 * 1-relative arrays.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranText implements IQuranText {
	private static QuranText thisInstance = null;
	/** The raw quran text written in the file. */
	private String rawText;

	/** The full quran text */
	private String[][] quranText = new String[114][];
	private ApplicationConfig appConfig = ApplicationConfig.getInsatnce();
	private QuranTextProperties textProps = QuranTextProperties.getInstance();


	/**
	 * The private constructor, which loads the whole Quran text from file into memory (
	 * <code>rawText</code>), refine it and stores it again in an
	 * <code>ArrayList</code>, to be accessed more easily.
	 * 
	 * @throws IOException
	 */
	private QuranText() throws IOException { // TODO: use java.io.*Reader for reading with encoding
		RandomAccessFile file = new RandomAccessFile(appConfig.getQuranText(), "r");
		byte[] b = new byte[(int) file.length()];
		file.read(b);
		rawText = new String(b);
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
		QuranTextProperties textProp = QuranTextProperties.getInstance();
		QuranProperties quranProps = QuranProperties.getInstance();
		StringTokenizer suraTokenizer = new StringTokenizer(rawText, textProp.getLineBreakString());
		String sura;

		suraTokenizer.nextToken(); // ignore sura title
		// the first sura (Fatiha) has a Bismillah as its first aya.
		sura = suraTokenizer.nextToken() + " ";
		sura += suraTokenizer.nextToken();
		quranText[0] = getAyas(sura, quranProps.getSura(1).getAyaCount());

		for (int i = 2; suraTokenizer.hasMoreTokens(); i++) {
			sura = suraTokenizer.nextToken(); // ignore title
			if (i != 9) // Sura Tawbah has no Bismillah
				sura = suraTokenizer.nextToken(); // ignore Bismillah
			sura = suraTokenizer.nextToken();
			quranText[i - 1] = getAyas(sura, quranProps.getSura(i).getAyaCount());
		}
	}

	private String[] getAyas(String suraText, int ayaCount) {
		// QuranProperties quranProp = QuranProperties.getInstance();
		// int ayaCount =
		// Integer.parseInt(quranProp.getSura(suraNum).getAyaCount(suraNum));
		String[] ayas = new String[ayaCount];
		StringTokenizer ayaTokenizer = new StringTokenizer(suraText, textProps.getAyaSignLeftString()
				+ textProps.getAyaSignRightString());
		int i;
		for (i = 0; ayaTokenizer.hasMoreTokens() && i < ayaCount; i++) {
			try {
				ayas[i] = new String(ayaTokenizer.nextToken().trim().getBytes(), textProps.getCharset());
			} catch (UnsupportedEncodingException e) {
				Logger.getLogger(this.getClass()).log(e);
			}
			ayaTokenizer.nextToken();
		}
		return ayas;
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

}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 13, 2008
 */
package net.sf.zekr.common.resource;

import java.io.IOException;

import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.common.resource.filter.QuranFilter;
import net.sf.zekr.common.resource.filter.QuranFilterContext;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * @author Mohsen Saboorian
 */
public class FilteredQuranText implements IQuranText {
	private static FilteredQuranText thisInstance;
	private static QuranText quranText;
	private IQuranFilter filter = new QuranFilter();

	private FilteredQuranText() throws IOException {
		quranText = QuranText.getInstance();
	}

	public static FilteredQuranText getInstance() throws IOException {
		if (thisInstance == null)
			thisInstance = new FilteredQuranText();
		return thisInstance;
	}

	public static IQuranText getSimpleTextInstance() throws IOException {
		quranText = QuranText.getSimpleTextInstance();
		return getInstance();
	}

	public static IQuranText getDetailedTextInstance() throws IOException {
		quranText = QuranText.getDetailedTextInstance();
		return getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranText#get(int, int)
	 */
	public String get(int suraNum, int ayaNum) {
		return filter(suraNum, ayaNum);
	}

	/**
	 * Returns the raw Quran text (unfiltered).
	 */
	public String[][] getFullText() {
		return quranText.getFullText();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranText#getSura(int)
	 */
	public String[] getSura(int suraNum) {
		String[][] qt = quranText.getFullText();
		String[] text = new String[qt[suraNum - 1].length];
		for (int i = 0; i < text.length; i++) {
			text[i] = filter(suraNum, i + 1);
		}
		return text;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranText#get(int)
	 */
	public String get(int absoluteAyaNum) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranText#getTranslationData()
	 */
	public TranslationData getTranslationData() {
		return null;
	}

	private String filter(int suraNum, int ayaNum) {
		return filter.filter(new QuranFilterContext(quranText.get(suraNum, ayaNum), suraNum, ayaNum));
	}

	/**
	 * @param suraNum 1-base sura number
	 * @return Bismillah (the beginning part of the sura)
	 */
	public String getBismillah(int suraNum) {
		String aya1 = quranText.get(suraNum, 1);
		int sp = -1;
		for (int i = 0; i < 4; i++) { // pass 4 whitespaces.
			sp = aya1.indexOf(' ', sp + 1);
		}
		return aya1.substring(0, sp + 1);
	}

}

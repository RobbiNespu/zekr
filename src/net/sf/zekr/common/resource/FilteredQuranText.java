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
import net.sf.zekr.common.resource.filter.QuranFilterContext;
import net.sf.zekr.common.resource.filter.QuranWriterFilter;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * @author Mohsen Saboorian
 */
public class FilteredQuranText extends AbstractQuranText {
	private IQuranText quranText;
	private IQuranFilter filter;

	public FilteredQuranText() throws IOException {
		this(QuranText.getInstance());
	}

	public FilteredQuranText(IQuranText quranText) throws IOException {
		this(quranText, new QuranWriterFilter());
	}

	public FilteredQuranText(int mode) throws IOException {
		this(new QuranWriterFilter(), mode);
	}

	public FilteredQuranText(IQuranFilter filter, int mode) throws IOException {
		this(QuranText.getInstance(mode), filter);
	}

	public FilteredQuranText(IQuranText quranText, IQuranFilter filter) throws IOException {
		this.quranText = quranText;
		this.filter = filter;
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
		QuranFilterContext qfc = new QuranFilterContext(quranText.get(suraNum, ayaNum), suraNum, ayaNum);
		qfc.params = quranText.getMode() == UTHMANI_MODE ? IQuranFilter.UTHMANI_TEXT : 0;
		return filter.filter(qfc);
	}

	private String filter(String str) {
		QuranFilterContext qfc = new QuranFilterContext(str, -1, -1);
		qfc.params = quranText.getMode() == UTHMANI_MODE ? IQuranFilter.UTHMANI_TEXT : 0;
		return filter.filter(qfc);
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
		return filter(aya1.substring(0, sp + 1));
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranText#getMode()
	 */
	public int getMode() {
		return quranText.getMode();
	}

	public String toString() {
		return "Filtered (" + quranText.toString() + ")";
	}
}

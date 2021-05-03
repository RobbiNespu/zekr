/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.engine.search.SearchScope;

/**
 * @author Mohsen Saboorian
 */
public class RangedQuranText extends AbstractRangedQuranText {
	private IQuranText quran;
	private IQuranLocation loc;
	private SearchScope searchScope;
	private boolean[][] quranMatched = new boolean[114][];

	/**
	 * Constructs a new instance of this class.
	 * 
	 * @param quran the Quran/translation text
	 * @param searchScope the scope object. Set <code>null</code>, if you don't need any scope constraint.
	 */
	public RangedQuranText(IQuranText quran, SearchScope searchScope) {
		this.quran = quran;
		this.searchScope = searchScope;
		if (searchScope != null) {
			reset();
			init();
		}
	}

	public void setQuran(IQuranText quran) {
		this.quran = quran;
	}

	public IQuranText getQuran() {
		return quran;
	}

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	public SearchScope getSearchScope() {
		return searchScope;
	}

	/**
	 * Initialize and cache appropriate (matching in scope) Quran ayas. This method should be called once on
	 * each search scope (after <code>new</code>ing an instance, before call to any other method).
	 */
	protected void init() {
		for (int i = 0; i <= 113; i++) {
			int ayaCount = QuranPropertiesUtils.getSura(i + 1).getAyaCount();
			quranMatched[i] = new boolean[ayaCount];
			for (int j = 0; j < ayaCount; j++) {
				if (searchScope.includes(i + 1, j + 1)) {
					quranMatched[i][j] = true;
				}
			}
		}
	}

	public void reset() {
		loc = new QuranLocation(1, 1);
		while (loc != null) {
			if (searchScope.includes(loc))
				break;
			loc = loc.getNext();
		}
	}

	public IQuranLocation getCurrentLocation() {
		return loc;
	}

	public final boolean findNext() {
		if (loc == null)
			return false;
		loc = loc.getNext();
		while (loc != null) {
			if (quranMatched[loc.getSura() - 1][loc.getAya() - 1])
				break;
			loc = loc.getNext();
		}

		// no more match
		if (loc == null)
			return false;

		return true;
	}

	public String currentAya() {
		if (loc == null)
			return null;
		return quran.get(loc.getSura(), loc.getAya());
	}

	public String get(int suraNum, int ayaNum) {
		return quran.get(suraNum, ayaNum);
	}

	public String[] getSura(int suraNum) {
		return quran.getSura(suraNum);
	}

	public String[][] getFullText() {
		return quran.getFullText();
	}

	public boolean isTranslation() {
		return quran.isTranslation();
	}

	public String getLanguage() {
		return quran.getLanguage();
	}

	public String getBismillah(int suraNum) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	public String get(int absoluteAyaNum) {
		throw new UnsupportedOperationException("Method not implemented.");
	}

	public int getMode() {
		return quran.getMode();
	}

	public String toString() {
		return "RangedQuranText (" + quran.toString() + ")";
	}
}

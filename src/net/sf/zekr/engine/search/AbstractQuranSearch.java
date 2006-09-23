/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 21, 2006
 */
package net.sf.zekr.engine.search;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranLocation;
import net.sf.zekr.common.util.Range;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public abstract class AbstractQuranSearch {

	protected IQuranText quran;
	protected int resultCount;
	protected int maxAyaMatch = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");
	protected Finder finder;

	/**
	 * Call <code>this(quran, false, true)</code>
	 * 
	 * @param quran the Quran text to search on
	 */
	protected AbstractQuranSearch(IQuranText quran) {
		this(quran, false, false, null);
	}

	/**
	 * @param quran the Quran text to search on
	 * @param matchDiac will ignore diacritics in search for both keyword and text if <code>false</code>
	 * @param matchCase specifies whether search should match the case or not. Only applicable to translation.
	 * @param locale text locale
	 */
	protected AbstractQuranSearch(IQuranText quran, boolean matchDiac, final boolean matchCase,
			final Locale locale) {
		this.quran = quran;
		if (matchDiac) {
			finder = new Finder() {
				public Range indexOf(String src, String key) {
					return SearchUtils.indexOfMatchDiacritic(src, key, matchCase, locale);
				}
			};
		} else {
			finder = new Finder() {
				public Range indexOf(String src, String key) {
					return SearchUtils.indexOfIgnoreDiacritic(src, key, matchCase, locale);
				}
			};
		}
	}

	/**
	 * Finds all occurrences of <code>keyword</code> in <code>IQuranText</code> and returns it as
	 * <code>result</code> <code>Map</code>.
	 * 
	 * @param result return result <code>Map</code>
	 * @param keyword keyword to be found
	 * @return <code>false</code> if too much results found (more than <code>maxAyaMatch</code>),
	 *         otherwise <code>true</code>.
	 */
	public final boolean findAll(Map result, String keyword) {
		String aya;
		int ayaNum;
		List l;
		for (int i = 1; i <= 114; i++) {
			ayaNum = quran.getSura(i).length;
			for (int j = 1; j <= ayaNum; j++) {
				aya = quran.get(i, j);
				if ((l = find(aya, keyword)) != null) {
					result.put(new QuranLocation(i, j), l);
					resultCount += l.size();
					if (result.size() >= maxAyaMatch)
						return false;
				}
			}
		}

		return true;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setMaxAyaMatch(int maxAyaMatch) {
		this.maxAyaMatch = maxAyaMatch;
	}

	public int getMaxAyaMatch() {
		return maxAyaMatch;
	}

	protected abstract List find(String src, String keyword);
}

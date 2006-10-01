/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 29, 2005
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SearchScopeItem {
	private final int suraFrom, ayaFrom;
	private final int suraTo, ayaTo;
	private final boolean exclusive;

	public SearchScopeItem() throws IllegalSearchScopeItemException {
		this(1, 1, 1, 1, false);
	}

	/**
	 * All parameters are behaved as 1-based. Note that no range check (for aya count, or sura number) is
	 * performed.
	 * 
	 * @throws IllegalSearchScopeItemException if from-to range is not considered.
	 */
	public SearchScopeItem(int suraFrom, int ayaFrom, int suraTo, int ayaTo, boolean exclusive)
			throws IllegalSearchScopeItemException {
		if ((suraFrom > suraTo) || (suraFrom == suraTo && ayaFrom > ayaTo))
			throw new IllegalSearchScopeItemException();
		this.suraFrom = suraFrom;
		this.ayaFrom = ayaFrom;
		this.suraTo = suraTo;
		this.ayaTo = ayaTo;
		this.exclusive = exclusive;
	}

	public int getAyaFrom() {
		return ayaFrom;
	}

	public int getAyaTo() {
		return ayaTo;
	}

	public int getSuraFrom() {
		return suraFrom;
	}

	public int getSuraTo() {
		return suraTo;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * @return <code>true</code> if obj is of type <code>SearchScopeItem</code> and all its properties are
	 *         equal to <code>this</code> properties.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof SearchScopeItem) {
			return _equals((SearchScopeItem) obj);
		} else {
			return super.equals(obj);
		}
	}

	/**
	 * Tests whether an aya of a sura is included in this scope (works only if this item is inclusive).
	 * 
	 * @param sura sura number
	 * @param aya aya number
	 * @return <code>true</code> if this search scope item explicitely includes the aya, <code>false</code>
	 *         otherwise.
	 */
	public final boolean includes(int sura, int aya) {
		if (!exclusive) {
			if (sura < suraFrom || sura > suraTo || (sura == suraFrom && aya < ayaFrom)
					|| (sura == suraTo && aya > ayaTo))
				return false;
		}
		return !exclusive;
	}

	/**
	 * Tests whether an aya of a sura is excluded from this scope (works only if this item is exclusive).
	 * 
	 * @param sura sura number
	 * @param aya aya number
	 * @return <code>true</code> if this search scope item excplicitely excludes the aya, <code>false</code>
	 *         otherwise.
	 */
	public final boolean excludes(int sura, int aya) {
		if (exclusive) {
			if (sura < suraFrom || sura > suraTo || (sura == suraFrom && aya < ayaFrom)
					|| (sura == suraTo && aya > ayaTo))
				return false;
		}
		return exclusive;
	}

	private final boolean _equals(SearchScopeItem item) {
		return item.suraFrom == suraFrom && item.ayaFrom == ayaFrom && item.suraTo == suraTo
				&& item.ayaTo == ayaTo && item.exclusive == exclusive;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		SuraProperties sura1 = QuranPropertiesUtils.getSura(suraFrom);
		SuraProperties sura2 = QuranPropertiesUtils.getSura(suraTo);
		String sign = exclusive ? "(-)" : "(+)";
		buf.append(sign).append("[").append(sura1.getName()).append(" ").append(ayaFrom).append("] - [")
				.append(sura2.getName()).append(" ").append(ayaTo).append("]");
		return buf.toString();
	}
}

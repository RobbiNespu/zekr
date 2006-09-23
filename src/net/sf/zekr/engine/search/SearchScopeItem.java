/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 29, 2005
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.common.util.SuraProperties;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SearchScopeItem {
	private int suraFrom, ayaFrom;
	private int suraTo, ayaTo;

	/**
	 * All parameters are behaved as 1-based.
	 */
	protected SearchScopeItem(int suraFrom, int ayaFrom, int suraTo, int ayaTo) {
		this.suraFrom = suraFrom;
		this.ayaFrom = ayaFrom;
		this.suraTo = suraTo;
		this.ayaTo = ayaTo;
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

	private final boolean _equals(SearchScopeItem item) {
		return item.suraFrom == suraFrom && item.ayaFrom == ayaFrom && item.suraTo == suraTo
				&& item.ayaTo == ayaTo;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		SuraProperties sura1 = QuranPropertiesUtils.getSura(suraFrom);
		SuraProperties sura2 = QuranPropertiesUtils.getSura(suraTo);
		buf.append("[").append(sura1.getName()).append(" ").append(ayaFrom).append("] - [").append(
				sura2.getName()).append(" ").append(ayaTo).append("]");
		return buf.toString();
	}
}

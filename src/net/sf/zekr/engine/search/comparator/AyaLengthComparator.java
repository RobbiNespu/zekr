/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.comparator;

import net.sf.zekr.engine.search.SearchResultItem;

/**
 * This comparator compares ayas based on their length (including the diacritics and signs).
 * 
 * @author Mohsen Saboorian
 */
public class AyaLengthComparator extends AbstractSearchResultComparator {
	public int compare(SearchResultItem sri1, SearchResultItem sri2) {
		int l1 = sri1.text.length();
		int l2 = sri2.text.length();
		return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
	}
}

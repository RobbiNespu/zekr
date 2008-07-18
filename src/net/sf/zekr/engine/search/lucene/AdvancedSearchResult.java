/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.util.List;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.search.AbstractSearchResult;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

public class AdvancedSearchResult extends AbstractSearchResult {
	/**
	 * @param quranText 
	 * @param results a list of {@link SearchResultItem}s
	 * @param clause a string representation of whole-words of all the matched text parts
	 * @param rawQuery raw user query
	 * @param totalMatch total count of all highlighted text parts
	 * @param ayaComparator the {@link AbstractSearchResultComparator} to be used for sorting results
	 * @param ascending 
	 */
	public AdvancedSearchResult(IQuranText quranText, List results, String clause, String rawQuery, int totalMatch,
			AbstractSearchResultComparator ayaComparator, boolean ascending) {
		super(quranText, results, clause, rawQuery, totalMatch, ayaComparator, ascending);
	}
}

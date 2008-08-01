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
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

public class AdvancedSearchResult extends SearchResultModel {
	/**
	 * Instances of this class hold search results including number of matched ayas, number of total matched
	 * elements and highlighted matched ayas.
	 * 
	 * @param quranText Quran or translation text on which search taken place
	 * @param results a list of {@link SearchResultItem}s
	 * @param clause a string representation of whole-words of all the matched text parts
	 * @param rawQuery raw user query
	 * @param totalMatch total count of all highlighted text parts
	 * @param ayaComparator the {@link AbstractSearchResultComparator} to be used for sorting results
	 * @param ascending
	 * @deprecated use {@link SearchResultModel} instead.
	 */
	public AdvancedSearchResult(IQuranText quranText, List results, String clause, String rawQuery, int totalMatch,
			AbstractSearchResultComparator ayaComparator, boolean ascending) {
		super(quranText, results, clause, rawQuery, totalMatch, ayaComparator, ascending);
	}
}

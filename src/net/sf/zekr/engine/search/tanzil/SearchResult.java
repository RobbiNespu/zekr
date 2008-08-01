/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.List;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

/**
 * Instances of this class hold search results including number of matched ayas, number of total matched
 * elements and highlighted matched ayas.
 * <p>
 * This class should actually implement {@link Iterable} if it was Java 5 dependent.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 * @deprecated use {@link SearchResultModel} instead.
 */
public class SearchResult extends SearchResultModel {
	public SearchResult(IQuranText quranText, List results, String clause, String rawQuery, int totalMatch,
			AbstractSearchResultComparator ayaComparator, boolean ascending) {
		super(quranText, results, clause, rawQuery, totalMatch, ayaComparator, ascending);
	}
}

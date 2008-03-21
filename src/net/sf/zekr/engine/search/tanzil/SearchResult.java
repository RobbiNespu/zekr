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

import net.sf.zekr.engine.search.AbstractSearchResult;

/**
 * Instances of this class hold search results including number of matched ayas, number of total matched
 * elements and highlighted matched ayas.
 * <p />
 * This class is <code>Iterable</code> (in a Java 1.4.2 manner).
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class SearchResult extends AbstractSearchResult {

	public SearchResult(List results, String clause, String rawQuery, int totalMatch, SearchResultComparator ayaComparator) {
		super(results, clause, rawQuery, totalMatch, ayaComparator);
	}

	SearchResult(List res, int total) {
		this(res, null, null, total, null);
	}

}

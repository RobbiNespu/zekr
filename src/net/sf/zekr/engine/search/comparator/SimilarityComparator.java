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
 * Compare search result items, based on their matching score.
 * 
 * @author Mohsen Saboorian
 */
public class SimilarityComparator extends AbstractSearchResultComparator {
	public int compare(SearchResultItem sri1, SearchResultItem sri2) {
		return sri1.score < sri2.score ? -1 : (sri1.score == sri2.score ? 0 : 1);
	}
}

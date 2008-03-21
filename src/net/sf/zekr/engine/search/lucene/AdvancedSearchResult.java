/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.util.Collections;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.AbstractSearchResult;
import net.sf.zekr.engine.search.tanzil.SearchResultComparator;

public class AdvancedSearchResult extends AbstractSearchResult {
	public AdvancedSearchResult(List results, String clause, String rawQuery, int totalMatch,
			SearchResultComparator ayaComparator) {
		super(results, clause, rawQuery, totalMatch, ayaComparator);
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2008
 */
package net.sf.zekr.engine.search.comparator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.revelation.RevelationData;
import net.sf.zekr.engine.search.SearchResultItem;

/**
 * Compare search result items, based on their revelation order using default revelation data pack. It returns
 * ayas in natural order if there is no revelation order data available.
 * 
 * @author Mohsen Saboorian
 */
public class RevelationOrderComparator extends AbstractSearchResultComparator {
	RevelationData rd = ApplicationConfig.getInstance().getRevelation().getDefault();

	public int compare(SearchResultItem sri1, SearchResultItem sri2) {
		if (rd == null) {
			return sri1.location.compareTo(sri2.location);
		}
		return rd.compare(sri1.location, sri2.location);
	}
}

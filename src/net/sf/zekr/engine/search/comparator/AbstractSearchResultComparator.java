/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2008
 */
package net.sf.zekr.engine.search.comparator;

import java.util.Comparator;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.search.SearchResultItem;

/**
 * The abstract search result comparator to be used for sorting search results.
 * 
 * @author Mohsen Saboorian
 */
public abstract class AbstractSearchResultComparator implements Comparator {
	abstract public int compare(SearchResultItem sri1, SearchResultItem sri2);

	public int compare(Object o1, Object o2) {
		return compare((IQuranLocation) o1, (IQuranLocation) o2);
	}
}

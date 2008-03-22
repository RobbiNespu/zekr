/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2008
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.ZekrBaseRuntimeException;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

public class SearchResultComparatorFactory {
	public static AbstractSearchResultComparator getComparator(String fqn) {
		try {
			return (AbstractSearchResultComparator) Class.forName(fqn).newInstance();
		} catch (Exception e) {
			throw new ZekrBaseRuntimeException(e);
		}
	}
}

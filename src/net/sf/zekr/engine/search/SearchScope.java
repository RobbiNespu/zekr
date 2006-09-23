/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 18, 2006
 */
package net.sf.zekr.engine.search;

import java.util.HashSet;
import java.util.Set;

import net.sf.zekr.common.util.Range;

public class SearchScope {
	Set set = new HashSet();

	public void add(SearchScopeItem item) {
		set.add(item);
	}

	public boolean contains(SearchScopeItem item) {
		return set.contains(item);
	}
}

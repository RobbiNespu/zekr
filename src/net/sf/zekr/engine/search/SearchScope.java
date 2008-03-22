/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 18, 2006
 */
package net.sf.zekr.engine.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

public class SearchScope {
	List list = new ArrayList();

	public void add(SearchScopeItem item) {
		int i = list.indexOf(item);
		if (i == -1)
			list.add(item);
		else {
			list.remove(i);
			list.add(item);
		}
	}

	public boolean contains(SearchScopeItem item) {
		return list.contains(item);
	}

	public List getScopeItems() {
		return list;
	}

	public String toString() {
		return list.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SearchScope) {
			SearchScope ss = (SearchScope) obj;
			ss.list.equals(this.list);
		}
		return false;
	}

	/**
	 * Tests whether an aya of a sura conforms to the search scope constraints.
	 * 
	 * @param quranLocation the sura-aya pair.
	 * @return <code>true</code> if this search scope includes the aya (constraints are applied
	 *         consecutively), <code>false</code> otherwise.
	 */
	public boolean includes(IQuranLocation quranLocation) {
		return includes(quranLocation.getSura(), quranLocation.getAya());
	}

	/**
	 * Tests whether an aya of a sura conforms to the search scope constraints.
	 * 
	 * @param sura sura number
	 * @param aya aya number
	 * @return <code>true</code> if this search scope includes the aya (constraints are applied
	 *         consecutively), <code>false</code> otherwise.
	 */
	public boolean includes(int sura, int aya) {
		if (list.size() == 0)
			return false;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			SearchScopeItem ssi = (SearchScopeItem) iter.next();
			if (ssi.excludes(sura, aya))
				return false;
		}
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			SearchScopeItem ssi = (SearchScopeItem) iter.next();
			if (ssi.includes(sura, aya))
				return true;
		}
		return false;
	}
}

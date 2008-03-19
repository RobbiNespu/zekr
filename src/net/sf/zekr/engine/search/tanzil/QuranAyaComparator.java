/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.Comparator;

import net.sf.zekr.common.resource.IQuranLocation;

public abstract class QuranAyaComparator implements Comparator {
	abstract public int compare(IQuranLocation ql1, IQuranLocation ql2);

	public int compare(Object o1, Object o2) {
		return compare((IQuranLocation) o1, (IQuranLocation) o2);
	}
}

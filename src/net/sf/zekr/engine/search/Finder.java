/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 21, 2006
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.util.Range;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
abstract class Finder {
	public abstract Range indexOf(String src, String key);

	public final Range indexOf(String src, String key, int off) {
		Range r = indexOf(src.substring(off), key);
		if (r == null)
			return null;
		return new Range(r.from + off, r.to + off);
	}
}

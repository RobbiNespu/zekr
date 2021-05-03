/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 8, 2005
 */
package net.sf.zekr.engine.search;

/**
 * This class is a helper for a range introduced by two parameters: <code>from</code> and <code>to</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
final public class Range {
	public final int from, to;

	public Range(int from, int to) {
		this.from = from;
		this.to = to;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.engine.search;

/**
 * Zero scorer.
 * 
 * @author Mohsen Saboorian
 */
public class ZeroScorer implements ISearchScorer {
	public double score(final SearchResultItem sri) {
		return 0;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2008
 */
package net.sf.zekr.engine.search;

/**
 * Search scorer interface.
 * 
 * @author Mohsen Saboorian
 */
public interface ISearchScorer {
	double score(SearchResultItem sri);
}

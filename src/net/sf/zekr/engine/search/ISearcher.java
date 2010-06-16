/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 15, 2010
 */
package net.sf.zekr.engine.search;

/**
 * @author Mohsen Saboorian
 */
public interface ISearcher {
	public SearchResultModel search(String rawQuery) throws SearchException;
}

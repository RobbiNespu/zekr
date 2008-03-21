/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2008
 */
package net.sf.zekr.engine.search.tanzil;

public interface ISearchHighlighter {
	public String highlight(String text, String pattern);
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 19, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import net.sf.zekr.engine.search.IZekrHighlighter;

/**
 * Search result highlighter interface. Implementations of this class highlight matched patterns in the text
 * based on the input <code>pattern</code> (regular expression) parameter.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public interface ISearchResultHighlighter extends IZekrHighlighter {
	public String highlight(String text, String pattern);
}

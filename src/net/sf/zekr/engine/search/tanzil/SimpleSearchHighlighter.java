/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 24, 2007
 */
package net.sf.zekr.engine.search.tanzil;

public class SimpleSearchHighlighter implements ISearchHighlighter {

	public String highlight(String text) {
		return highlight(text, null);
	}

	public String highlight(String text, String matchedQueryPart) {
		String title = " title=\"" + matchedQueryPart + "\"";
		return "<span" + (matchedQueryPart == null ? "" : title) + " class=\"highlight\">" + text + "</span>";
	}
}

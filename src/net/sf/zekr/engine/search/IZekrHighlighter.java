/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 11, 2010
 */
package net.sf.zekr.engine.search;

/**
 * @author Mohsen Saboorian
 */
public interface IZekrHighlighter {
	public static final String FORMAT_STRING = "<span class=\"highlight\">%s</span>";
	public static final String FORMAT_STRING_REGEX = "<span class=\"highlight\">$1</span>";
}

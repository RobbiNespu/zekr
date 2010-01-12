/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 1, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.engine.search.IZekrHighlighter;

import org.apache.lucene.search.highlight.Formatter;

public interface IExtendedFormatter extends Formatter, IZekrHighlighter {
	int getHighlightCount();
}

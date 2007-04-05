/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 24, 2007
 */
package net.sf.zekr.engine.search.lucene;

import org.apache.lucene.search.highlight.TokenGroup;

public class ZekrHighlightFormatter implements IExtendedFormatter {

	private int highlightCount = 0;

	public String highlightTerm(String originalText, TokenGroup tokenGroup) {
		if (tokenGroup.getTotalScore() <= 0) {
			return originalText;
		}
		highlightCount++; // update stats used in assertions
		return "<span class=\"highlight\">" + originalText + "</span>";
	}

	public int getHighlightCount() {
		return highlightCount;
	}
}

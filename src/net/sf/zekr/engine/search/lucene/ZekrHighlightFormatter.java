/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 24, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.common.config.ApplicationConfig;

import org.apache.lucene.search.highlight.TokenGroup;

/**
 * Default highlighter used for highlighting Lucene search results.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrHighlightFormatter implements IExtendedFormatter {
	private int highlightCount = 0;

	public String highlightTerm(String originalText, TokenGroup tokenGroup) {
		if (tokenGroup.getTotalScore() <= 0) {
			return originalText;
		}
		highlightCount++; // update stats used in assertions
		String format = FORMAT_STRING;
		try {
			format = ApplicationConfig.getInstance().getProps().getString("view.search.highlightFormat", FORMAT_STRING);
		} catch (Exception e) {
			// silently ignore it.
		}
		return String.format(format, originalText);
	}

	public int getHighlightCount() {
		return highlightCount;
	}
}

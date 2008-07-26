/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */
package net.sf.zekr.engine.search.lucene;

import java.io.IOException;

import net.sf.zekr.common.resource.filter.QuranFilterUtils;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author Mohsen Saboorian
 */
public class ArabicFilter extends TokenFilter {

	protected ArabicFilter(TokenStream input) {
		super(input);
	}

	public final Token next() throws IOException {
		final Token t = input.next();
		if (t == null)
			return null;
		// return a token with filtered characters.
		return new Token(simplify(new String(t.termBuffer(), 0, t.termLength())), t.startOffset(), t.endOffset(), t
				.type());
	}

	private static String simplify(String text) {
		return QuranFilterUtils.filterSimilarArabicCharacters(QuranFilterUtils.filterHarakat(text));
	}
}

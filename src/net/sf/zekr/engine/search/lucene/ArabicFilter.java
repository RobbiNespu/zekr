/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.engine.search.SearchUtils;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ArabicFilter extends TokenFilter {

	protected ArabicFilter(TokenStream input) {
		super(input);
	}

	public final Token next() throws java.io.IOException {
		final Token t = input.next();
		if (t == null)
			return null;
		// return a token with filtered characters.
		return new Token(SearchUtils.arabicSimplify(t.termText()), t.startOffset(), t.endOffset(), t.type());
	}
}

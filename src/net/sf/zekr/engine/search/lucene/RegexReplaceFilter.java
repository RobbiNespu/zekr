package net.sf.zekr.engine.search.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.zekr.engine.search.tanzil.RegexUtils;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author Mohsen Saboorian
 */
public final class RegexReplaceFilter extends TokenFilter {
	private Pattern pattern;
	private String replacement;
	private Map patternReplace;

	/**
	 * @param ts token stream
	 * @param patternReplaceMap a {@link Map} from {@link Pattern} to replace {@link String}.
	 */
	public RegexReplaceFilter(TokenStream ts, Map patternReplaceMap) {
		super(ts);
		this.patternReplace = patternReplaceMap;
	}

	public final Token next() throws IOException {
		Token t;
		do {
			t = input.next();
			if (t == null)
				return null;

			String str = new String(t.termBuffer(), 0, t.termLength());
			str = RegexUtils.replaceAll(patternReplace, str);
			t.setTermText(str);
		} while (t.termLength() == 0);
		return t;
	}
}

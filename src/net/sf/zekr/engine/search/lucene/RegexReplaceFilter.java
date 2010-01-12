package net.sf.zekr.engine.search.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.zekr.engine.search.tanzil.RegexUtils;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * @author Mohsen Saboorian
 */
public final class RegexReplaceFilter extends TokenFilter {
	private Pattern pattern;
	private String replacement;
	private Map<Pattern, String> patternReplace;

	/**
	 * @param ts token stream
	 * @param patternReplaceMap a {@link Map} from {@link Pattern} to replace {@link String}.
	 */
	public RegexReplaceFilter(TokenStream ts, Map<Pattern, String> patternReplaceMap) {
		super(ts);
		termAtt = (TermAttribute) addAttribute(TermAttribute.class);
		patternReplace = patternReplaceMap;
	}

	private TermAttribute termAtt;

	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			char[] buffer = termAtt.termBuffer();
			String s = RegexUtils.replaceAll(patternReplace, new String(buffer, 0, termAtt.termLength()));
			// termAtt.setTermLength(s.length());
			termAtt.setTermBuffer(s);
			return true;
		} else {
			return false;
		}
	}
}

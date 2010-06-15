/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.SearchInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * A wrapper class to analyze texts of different language. When using with some European languages,
 * {@link SnowballFilter} will be used inside. It uses the following set of filters and tokenizers:<br>
 * <ol>
 * <li>StandardTokenizer</li>
 * <li>StandardFilter</li>
 * <li>LowerCaseFilter</li>
 * <li>StopFilter</li>
 * <li>RegexReplaceFilter</li>
 * <li>SnowballFilter. It is used only if a snowball filter is already available</li>
 * </ol>
 * 
 * @author Mohsen Saboorian
 */
public class ZekrLuceneAnalyzer extends Analyzer {
	private ApplicationConfig conf = ApplicationConfig.getInstance();

	/**
	 * This constant is to be used as {@link ZekrLuceneAnalyzer} ID for instantiating a Quran (Arabic)
	 * Analyzer.
	 */
	public static final String QURAN_LANG_CODE = "ar";

	private String id;
	private String name;

	public ZekrLuceneAnalyzer(String langCode, String name) {
		id = langCode;
		this.name = name;
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream resultTokenStream = null;
		if (ArrayUtils.contains(new String[] { "fa", "pk", "ar", "ps", "ku", "hw" }, id)) { // non-European languages
			resultTokenStream = new WhitespaceTokenizer(reader);
		} else {
			resultTokenStream = new StandardTokenizer(Version.LUCENE_CURRENT, reader);
		}
		resultTokenStream = new StandardFilter(resultTokenStream);
		resultTokenStream = new LowerCaseFilter(resultTokenStream);
		SearchInfo searchInfo = conf.getSearchInfo();
		if (searchInfo.getStopWord(id) != null && searchInfo.getStopWord(id).size() > 0) {
			resultTokenStream = new StopFilter(false, resultTokenStream, searchInfo.getStopWord(id));
		}
		Map<Pattern, String> replacePattern = new LinkedHashMap<Pattern, String>(searchInfo.getReplacePattern(id));
		if (searchInfo.getDiacritic(id) != null) {
			replacePattern.put(searchInfo.getDiacritic(id), "");
		}
		if (searchInfo.getPunctuation(id) != null) {
			replacePattern.put(searchInfo.getPunctuation(id), "");
		}
		resultTokenStream = new RegexReplaceFilter(resultTokenStream, replacePattern);
		if (name != null) {
			resultTokenStream = new SnowballFilter(resultTokenStream, name);
		}
		return resultTokenStream;
	}
}

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * A wrapper class around {@link SnowballFilter} which also adds support to ArabicFilter when
 * {@link #QURAN_ANALYZER} is used as the ID.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrSnowballAnalyzer extends Analyzer {
	/**
	 * This constant is to be used as {@link ZekrSnowballAnalyzer} ID for instantiating a Quran (Arabic)
	 * Analyzer.
	 */
	public static final String QURAN_ANALYZER = "QURAN";
	private String id;

	public ZekrSnowballAnalyzer(String id) {
		this.id = id;
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream resultTokenStream = new StandardTokenizer(reader);
		resultTokenStream = new StandardFilter(resultTokenStream);
		resultTokenStream = new LowerCaseFilter(resultTokenStream);
		if (QURAN_ANALYZER.equals(id)) {
			resultTokenStream = new ArabicFilter(resultTokenStream);
		} else {
			resultTokenStream = new SnowballFilter(resultTokenStream, id);
		}
		return resultTokenStream;
	}
}

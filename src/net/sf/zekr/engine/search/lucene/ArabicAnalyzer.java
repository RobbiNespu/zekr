/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */
package net.sf.zekr.engine.search.lucene;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Filters {@link StandardTokenizer} with {@link ArabicFilter}, and {@link StopFilter} using a list of Arabic stop
 * words.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ArabicAnalyzer extends Analyzer {
	private static final String[] ARABIC_STOP_WORDS = {};

	private Set stopSet;

	/**
	 * An array containing some common English words that are usually not useful for searching.
	 */
	public static final String[] STOP_WORDS = ARABIC_STOP_WORDS;

	/** Builds an analyzer with the default stop words ({@link #STOP_WORDS}). */
	public ArabicAnalyzer() {
		this(STOP_WORDS);
	}

	/** Builds an analyzer with the given stop words. */
	public ArabicAnalyzer(Set stopWords) {
		stopSet = stopWords;
	}

	/** Builds an analyzer with the given stop words. */
	public ArabicAnalyzer(String[] stopWords) {
		stopSet = StopFilter.makeStopSet(stopWords);
	}

	/**
	 * Builds an analyzer with the stop words from the given file.
	 * 
	 * @see WordlistLoader#getWordSet(File)
	 */
	public ArabicAnalyzer(File stopwords) throws IOException {
		stopSet = WordlistLoader.getWordSet(stopwords);
	}

	/**
	 * Builds an analyzer with the stop words from the given reader.
	 * 
	 * @see WordlistLoader#getWordSet(Reader)
	 */
	public ArabicAnalyzer(Reader stopwords) throws IOException {
		stopSet = WordlistLoader.getWordSet(stopwords);
	}

	/**
	 * Constructs a {@link StandardTokenizer} filtered by a {@link StandardFilter}, a {@link LowerCaseFilter} and a
	 * {@link StopFilter}.
	 */
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(reader);
		result = new ArabicFilter(result);
		result = new StopFilter(result, stopSet);
		return result;
	}
}

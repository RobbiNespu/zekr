/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author Mohsen Saboorian
 */
public class LuceneAnalyzerFactory {
	/**
	 * This map contains ISO code for those stemmers available in snowball package.
	 */
	private final static Map<String, String> snowballLangMap = new HashMap<String, String>();
	static {
		snowballLangMap.put("da", "Danish");
		snowballLangMap.put("nl", "Dutch");
		snowballLangMap.put("en", "English");
		snowballLangMap.put("fi", "Finnish");
		snowballLangMap.put("fr", "French");
		snowballLangMap.put("de", "German");
		snowballLangMap.put("it", "Italian");
		snowballLangMap.put("no", "Norwegian");
		snowballLangMap.put("pt", "Portuguese");
		snowballLangMap.put("ru", "Russian");
		snowballLangMap.put("es", "Spanish");
		snowballLangMap.put("sv", "Swedish");
	}

	/**
	 * @param langCode can be either two-char language ISO code, {@link ZekrLuceneAnalyzer#QURAN_LANG_CODE}, or
	 *           either of the available snowball analyzers such as <tt>Lovins</tt>, <tt>Porter</tt> or
	 *           <tt>Kp</tt>.
	 * @return a new instance of ZekrLuceneAnalyzer
	 */
	public static Analyzer getAnalyzer(String langCode) {
		String langName = snowballLangMap.get(langCode);
		return new ZekrLuceneAnalyzer(langCode, langName);
	}

	/**
	 * This method decides to return based on the type of quranText parameter, whether a language-specific
	 * translation analyzer or a Quran analyzer.
	 * 
	 * @param quranText the abstract Quran text
	 * @return
	 */
	public static Analyzer getAnalyzer(IQuranText quranText) {
		if (quranText instanceof TranslationData) { // it is a translation
			return getAnalyzer(((TranslationData) quranText).getLocale().getLanguage());
		} else { // it should be Quran
			return getAnalyzer(ZekrLuceneAnalyzer.QURAN_LANG_CODE);
		}
	}
}

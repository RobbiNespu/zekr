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
	private final static Map langMap = new HashMap();
	static {
		langMap.put("da", "Danish");
		langMap.put("nl", "Dutch");
		langMap.put("en", "English");
		langMap.put("fi", "Finnish");
		langMap.put("fr", "French");
		langMap.put("de", "German");
		langMap.put("it", "Italian");
		langMap.put("no", "Norwegian");
		langMap.put("pt", "Portuguese");
		langMap.put("ru", "Russian");
		langMap.put("es", "Spanish");
		langMap.put("sv", "Swedish");
	}

	/**
	 * @param code can be either tho-char language ISO code, {@link ZekrSnowballAnalyzer#QURAN_ANALYZER}, or
	 *           either of the available snowball analyzers such as <tt>Lovins</tt>, <tt>Porter</tt> or
	 *           <tt>Kp</tt>.
	 * @return a new instance of ZekrSnowballAnalyzer
	 */
	public static Analyzer getAnalyzer(String code) {
		String langName = (String) langMap.get(code);
		if (langName != null)
			code = langName;
		return new ZekrSnowballAnalyzer(code);
	}

	/**
	 * This method decides to return based on the type of quranText parameter, whether a language-specific
	 * translation analyzer or a Quran analyzer.
	 * 
	 * @param quranText the abstract quran text
	 * @return
	 */
	public static Analyzer getAnalyzer(IQuranText quranText) {
		if (quranText instanceof TranslationData)
			return getAnalyzer(((TranslationData) quranText).getLocale().getLanguage());
		return getAnalyzer(ZekrSnowballAnalyzer.QURAN_ANALYZER);
	}
}

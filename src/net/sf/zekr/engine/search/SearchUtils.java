/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.search;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.search.tanzil.RegexUtils;

import org.apache.commons.lang.StringUtils;

/**
 * This file contains several useful <code>public static</code> methods for finding occurrences of a source
 * text in another text. Since the Arabic language has some <i>diacritics</i>, there is also functions to
 * ignore or match diacritics.
 * 
 * @author Mohsen Saboorian
 */
public class SearchUtils implements ArabicCharacters {
	/**
	 * Replace Farsi unicode <code>Yeh</code> with Arabic one, and so about <code>Kaf</code> (Farsi
	 * <code>Keheh</code>).
	 * 
	 * @param str
	 * @return updated <code>String</code> result
	 */
	public static String replaceLayoutSimilarCharacters(String str) {
		str = str.replace(FARSI_YEH, ARABIC_YEH);
		str = str.replace(ALEF_MAKSURA, ARABIC_YEH);
		str = str.replace(FARSI_KEHEH, ARABIC_KAF);
		return str;
	}

	/**
	 * This method removes specific diacritics form the string, and also replaces Hamza characters with their
	 * base character. It also replaces <tt>ARABIC_LETTER_TEH_MATBUTA</tt> with <tt>ARABIC_LETTER_TEH</tt>, and
	 * <tt>ARABIC_LETTER_ALEF_MAKSURA</tt> with <tt>ARABIC_LETTER_YEH</tt>.
	 * 
	 * @param str string to be simplified
	 * @return simplified form of the <code>str</code>
	 */
	public static String arabicSimplify4AdvancedSearch(String str) {
		// diacritics removal
		char[] arr = new char[] { SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN, SUPERSCRIPT_ALEF };
		for (int i = 0; i < arr.length; i++) {
			str = StringUtils.remove(str, arr[i]);
		}

		// YEH, ALEF, WAW, TEH replacements
		str = str.replace(ALEF_HAMZA_ABOVE, ALEF);
		str = str.replace(ALEF_HAMZA_BELOW, ALEF);
		str = str.replace(WAW_HAMZA_ABOVE, WAW);
		str = str.replace(YEH_HAMZA_ABOVE, ARABIC_YEH);
		str = str.replace(ALEF_MAKSURA, ARABIC_YEH);
		str = str.replace(TEH_MARBUTA, TEH);

		return str;
	}

	public static String simplifyAdvancedSearchQuery(String query) {
		// diacritics removal
		// TODO: sala, ghala, ...
		char[] arr = new char[] { SMALL_LOW_SEEN, SMALL_HIGH_MEEM, SMALL_WAW, SMALL_YEH, MADDAH_ABOVE, SMALL_ROUNDED_ZERO };
		for (int i = 0; i < arr.length; i++) {
			query = StringUtils.remove(query, arr[i]);
		}

		query = query.replaceAll("" + TATWEEL + SUPERSCRIPT_ALEF, "" + ALEF);
		query = query.replaceAll("" + ALEF_MAKSURA + HAMZA_BELOW, "" + YEH_HAMZA_ABOVE);
		query = query.replace(ARABIC_QUESION_MARK, '?');
		query = query.replace(ALEF_WASLA, ALEF);
		return replaceLayoutSimilarCharacters(arabicSimplify4AdvancedSearch(query));
	}

	/**
	 * Simplifies sura name text to be used in a suggestion list.
	 * 
	 * @param text original text
	 * @return simplified text
	 */
	public static String simplifySuranameText(String text) {
		ApplicationConfig conf = ApplicationConfig.getInstance();
		SearchInfo searchInfo = conf.getSearchInfo();
		Locale locale = QuranPropertiesUtils.getSuraNameModeLocale();
		String langCode = locale.getLanguage();
		text = text.toLowerCase(locale);
		Map<Pattern, String> rep = new LinkedHashMap<Pattern, String>(searchInfo.getDefaultReplacePattern());
		if (searchInfo.containsLanguageReplacePattern(langCode)) {
			rep.putAll(searchInfo.getReplacePattern(langCode));
		}
		Pattern punct = searchInfo.getPunctuation(langCode);
		if (punct != null) {
			rep.put(punct, "");
		}
		text = RegexUtils.replaceAll(rep, text);
		return text;
	}
}

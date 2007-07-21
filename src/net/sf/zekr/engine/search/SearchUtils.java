/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.search;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * This file contains several useful <code>public static</code> methods for finding occurrences of a source text in
 * another text. Since the Arabic language has some <i>diacritics</i>, there is also functions to ignore or
 * match diacritics.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SearchUtils {
	final public static char SUKUN = 0x652;
	final public static char SHADDA = 0x651;
	final public static char KASRA = 0x650;
	final public static char DAMMA = 0x64f;
	final public static char FATHA = 0x64e;

	final public static char KASRATAN = 0x64d;
	final public static char DAMMATAN = 0x64c;
	final public static char FATHATAN = 0x64b;

	final public static char HAMZA = 0x621;
	final public static char HAMZA_ABOVE = 0x654;
	final public static char HAMZA_BELOW = 0x655;
	
	final public static char MADDAH_ABOVE = 0x653;
	final public static char SMALL_LOW_SEEN = 0x6e3;
	final public static char SMALL_WAW = 0x6e5;
	final public static char SMALL_YEH = 0x6e6;
	final public static char SMALL_HIGH_MEEM = 0x6e2;

	final public static char SUPERSCRIPT_ALEF = 0x670;
	final public static char ALEF = 0x627;
	final public static char ALEF_MADDA = 0x622;
	final public static char ALEF_HAMZA_ABOVE = 0x623;
	final public static char ALEF_HAMZA_BELOW = 0x625;
	final public static char ALEF_WASLA = 0x671;

	final public static char SMALL_ROUNDED_ZERO = 0x6df;

	final public static char TATWEEL = 0x640;

	final public static char YEH_HAMZA_ABOVE = 0x626;
	final public static char WAW_HAMZA_ABOVE = 0x624;
	final public static char WAW = 0x648;
	
	final public static char TEH = 0x62a;
	final public static char TEH_MARBUTA = 0x629;

	final public static char ALEF_MAKSURA = 0x649;
	final public static char FARSI_YEH = 0x6cc;
	final public static char ARABIC_YEH = 0x64a;

	final public static char ARABIC_KAF = 0x643;
	final public static char FARSI_KEHEH = 0x6a9;
	
	final public static char ARABIC_QUESION_MARK = 0x61f;

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
	 * Replace similar arabic characters which are used commonly instead of others. This is a helper method for easing
	 * the search. This method should be applied on Quran text.<br>
	 * 
	 * Characters which are replaced are listed below:
	 * <ul>
	 * <li><tt>FARSI_YEH</tt> is replaced with <tt>ARABIC_YEH</tt></li>
	 * <li><tt>ALEF_MAKSURA</tt> is replaced with <tt>ARABIC_YEH</tt></li>
	 * <li><tt>ALEF_HAMZA_ABOVE</tt> is replaced with <tt>ALEF</tt></li>
	 * <li><tt>ALEF_HAMZA_BELOW</tt> is replaced with <tt>ALEF</tt></li>
	 * <li><tt>ALEF_MADDA</tt> is replaced with <tt>ALEF</tt></li>
	 * <li><tt>WAW_HAMZA_ABOVE</tt> is replaced with <tt>WAW</tt></li>
	 * <ul>
	 * 
	 * @param str
	 * @return updated <code>String</code> result
	 */
	public static String replaceSimilarArabic(String str) {
		str = str.replace(ALEF_MAKSURA, ARABIC_YEH);
		str = str.replace(FARSI_YEH, ARABIC_YEH);
		str = str.replace(ALEF_HAMZA_ABOVE, ALEF);
		str = str.replace(ALEF_HAMZA_BELOW, ALEF);
		str = str.replace(ALEF_MADDA, ALEF);
		str = str.replace(WAW_HAMZA_ABOVE, WAW);
		return str;
	}

	/**
	 * This method removes specific diacritics form the string. Also replaces incorrect characters (which are present
	 * due to keyboard layout problems) using <code>replaceLayoutSimilarCharacters()</code>.<br>
	 * <br>
	 * <b>NOTE:</b> This method is not complete. It is subject to change based other Arabic-based keyboard layout
	 * problems.
	 * 
	 * @param str
	 * @return simplified form of the <code>str</code>
	 */
	public static String arabicSimplify(String str) {
		// diacritics removal
		char[] arr = new char[] { SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN, SUPERSCRIPT_ALEF };
		for (int i = 0; i < arr.length; i++) {
			str = StringUtils.remove(str, arr[i]);
		}

		// YEH, ALEF, WAW replacements
		// str = str.replace(ALEF_HAMZA_ABOVE, ALEF);
		// str = str.replace(ALEF_HAMZA_BELOW, ALEF);
		// str = str.replace(ALEF_MADDA, ALEF);
		// str = str.replace(WAW_HAMZA_ABOVE, WAW);

		str = replaceLayoutSimilarCharacters(str);
		return str;
	}

	/**
	 * This method removes specific diacritics form the string, and also replaces Hamza characters with their base
	 * character. It also replaces <tt>ARABIC_LETTER_TEH_MATBUTA</tt> with <tt>ARABIC_LETTER_TEH</tt>, and
	 * <tt>ARABIC_LETTER_ALEF_MAKSURA</tt> with <tt>ARABIC_LETTER_YEH</tt>.
	 * 
	 * @param str
	 *           string to be simplified
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
		char[] arr = new char[] { SMALL_LOW_SEEN, SMALL_HIGH_MEEM, SMALL_WAW, SMALL_YEH, MADDAH_ABOVE, SMALL_ROUNDED_ZERO};
		for (int i = 0; i < arr.length; i++) {
			query = StringUtils.	remove(query, arr[i]);
		}

		query = query.replaceAll("" + TATWEEL + SUPERSCRIPT_ALEF, "" + ALEF);
		query = query.replaceAll("" + ALEF_MAKSURA + HAMZA_BELOW , "" + YEH_HAMZA_ABOVE);
		query = query.replace(ARABIC_QUESION_MARK, '?');
		query = query.replace(ALEF_WASLA, ALEF);
		return replaceLayoutSimilarCharacters(arabicSimplify4AdvancedSearch(query));
	}

	/**
	 * These characters are Arabic <i>Harakets</i> (diacritics):
	 * <ul>
	 * <li>Sukun</li>
	 * <li>Shadda</li>
	 * <li>Fatha</li>
	 * <li>Kasra</li>
	 * <li>Damma</li>
	 * <li>Fathatan</li>
	 * <li>Kasratan</li>
	 * <li>Dammatan</li>
	 * <li>Superscript alef</li>
	 * </ul>
	 * 
	 * @param ch
	 *           the character to be examined
	 * @return <code>true</code> if ch is an Arabic <i>Harakat</i>, otherwise <code>false</code>
	 */
	public static boolean isDiac(char ch) {
		return (ch == SUKUN) || (ch == SHADDA) || (ch == KASRA) || (ch == DAMMA) || (ch == FATHA) || (ch == KASRATAN)
				|| (ch == DAMMATAN) || (ch == FATHATAN) || (ch == SUPERSCRIPT_ALEF);
	}

	/**
	 * Will find a <code>Range</code> of the first occurrence of <code>key</code> in <code>src</code>. This method
	 * will ignore diacritics on both <code>src</code> and <code>key</code> strings.<br>
	 * This is a generic method, meaning it can be used to search on Quran text as well as translations.
	 * 
	 * @param src
	 *           source string to be searched on
	 * @param key
	 *           non-<code>null</code> target string to be found the first occurrence of which on the <code>src</code>
	 *           string
	 * @param matchCase
	 *           specifies whether to search in a case sensitive manner or not
	 * @param locale
	 *           the text locale (for casing conversion)
	 * @return a <code>Range</code> object from the previous space character just before the <code>key</code> (or
	 *         start of the source string if no space found) to the first space just after the <code>key</code> in
	 *         <code>src</code> (or end of src if no space found)
	 */
	public static Range indexOfIgnoreDiacritic(String src, String key, boolean matchCase, Locale locale) {
		key = arabicSimplify(key);
		src = replaceLayoutSimilarCharacters(src);
		src = replaceSimilarArabic(src);

		if (!matchCase) {
			if (locale != null) {
				key = key.toLowerCase(locale);
				src = src.toLowerCase(locale);
			} else {
				key = key.toLowerCase();
				src = src.toLowerCase();
			}
		}

		int k = 0, s = 0, start = -1;
		char[] source = src.toCharArray();
		char[] target = key.toCharArray();
		if (key.length() == 0)
			return null;
		while (s < src.length()) {
			if (k == key.length())
				break;

			if (charactersAreEquivalent(source[s], target[k])) {
				if (start == -1)
					start = s;
				s++;
				k++;
			} else {
				if (!isDiac(source[s])) {
					if (k != 0)
						s--;
					k = 0;
					start = -1;
				}
				s++;
			}
		}
		if (k == key.length()) { // fully matched
			int spaceBefore = (target[0] != ' ') ? src.substring(0, start).lastIndexOf(' ') : start;
			int spaceAfter = (target[key.length() - 1] != ' ') ? src.indexOf(' ', s) : s;
			if (spaceBefore == -1)
				start = 0;
			else
				start = spaceBefore + 1;
			if (spaceAfter == -1)
				s = src.length();
			else
				s = spaceAfter;
			return new Range(start, s);
		}
		return null;
	}

	/**
	 * Checks all the equivalency character checking tasks. These equivalency rules are applied in order to ease
	 * searching. There are currently two type of rules, one for Teh matching, and another for Hamza matching.<br>
	 * <br>
	 * <b>Teh rule</b>:<br>
	 * ARABIC_LETTER_TEH (&#1578;) and ARABIC_LETTER_TEH_MARBUTA (&#1577;) are regarded the same. <br>
	 * <b>Hamza rule</b>:<br>
	 * Asymmetric character matching, for special cases of Hamza matching.<br>
	 * If keyword is one of Hamza characters (&#1569;, &#1573;, &#1574;, &#1650;, or &#1572;), it will match with all
	 * other hamza characters.<br>
	 * For example if user entered <tt>ARABIC_LETTER_HAMZA</tt> (&#1569;), <tt>ARABIC_LETTER_ALEF_WITH_HAMZA_ABOVE</tt>
	 * (&#1650;), <tt>ARABIC_LETTER_ALEF_WITH_HAMZA_BELOW</tt> (&#1573;), <tt>ARABIC_LETTER_YEH_WITH_HAMZA_ABOVE</tt>
	 * (&#1574;), or <tt>ARABIC_LETTER_WAW_WITH_HAMZA_ABOVE</tt> (&#1572;), they will all match with each other (say
	 * <tt>ARABIC_LETTER_HAMZA</tt> will be matched with <tt>ARABIC_LETTER_ALEF_WITH_HAMZA_ABOVE</tt>,
	 * <tt>ARABIC_LETTER_YEH_WITH_HAMZA_BELOW</tt> and so on).<br>
	 * <br>
	 * Alternatively if user entered one of Hamza base characters (AKA Kursi): <tt>ARABIC_LETTER_ALEF</tt> (&#1575;),
	 * and <tt>ARABIC_LETTER_YEH</tt> (&#1610;), <tt>ARABIC_LETTER_WAW</tt> (&#1608;), they will only match with
	 * themselves as well as their corresponding Hamza character (or characters for Alef case). For example if user
	 * entered <tt>ARABIC_LETTER_ALEF</tt> (&#1575;), it will be matched with
	 * <tt>ARABIC_LETTER_ALEF_WITH_HAMZA_ABOVE</tt> (&#1650;), <tt>ARABIC_LETTER_ALEF_WITH_HAMZA_BELOW</tt>
	 * (&#1573;), and also <tt>ARABIC_LETTER_ALEF</tt> (&#1575;) itself.
	 * 
	 * @param source
	 *           source character of the Quran text
	 * @param keyword
	 *           keyword character entered by user
	 * @return <code>true</code> if two characters matched with special hamza matching rules, <code>false</code>
	 *         otherwise.
	 */
	private static final boolean charactersAreEquivalent(char source, char keyword) {
		if (source == keyword)
			return true;
		
		// TEH
		if (keyword == TEH || keyword == TEH_MARBUTA)
			return source == TEH || source == TEH_MARBUTA;

		// HAMZA
		if (keyword == WAW)
			return source == WAW_HAMZA_ABOVE;
		if (keyword == ARABIC_YEH)
			return source == YEH_HAMZA_ABOVE;
		if (keyword == ALEF)
			return source == ALEF_HAMZA_BELOW || source == ALEF_HAMZA_BELOW;
		if (keyword == ALEF_HAMZA_ABOVE || keyword == ALEF_HAMZA_BELOW || keyword == WAW_HAMZA_ABOVE
				|| keyword == YEH_HAMZA_ABOVE || keyword == HAMZA)
			return source == ALEF_HAMZA_ABOVE || source == ALEF_HAMZA_BELOW || source == WAW_HAMZA_ABOVE
					|| source == YEH_HAMZA_ABOVE || source == HAMZA;
		return false;
	}

	/**
	 * Will find a range of the first occurrence of <code>key</code> in <code>src</code>. This method will consider
	 * diacritics on both <code>src</code> and <code>key</code>.
	 * 
	 * @param src
	 *           source string to be searched on
	 * @param key
	 *           target string which is to search first occurrence of which on <code>src</code>
	 * @param matchCase
	 *           specifies whether to search in a case sensitive manner or not
	 * @param locale
	 *           the text locale (for casing conversion)
	 * @return a <code>Range</code> object from the previous space character just before the <code>key</code> (or
	 *         start of the source string if no space found) to the first space just after the <code>key</code> in
	 *         <code>src</code> (or end of src if no space found)
	 */
	public static Range indexOfMatchDiacritic(String src, String key, boolean matchCase, Locale locale) {
		key = replaceLayoutSimilarCharacters(key);
		src = replaceLayoutSimilarCharacters(src);
		if (!matchCase) {
			if (locale != null) {
				key = key.toLowerCase(locale);
				src = src.toLowerCase(locale);
			} else {
				key = key.toLowerCase();
				src = src.toLowerCase();
			}
		}

		int start = src.indexOf(key);
		if (start == -1)
			return null;

		int spaceBefore = (key.charAt(0) != ' ') ? src.substring(0, start).lastIndexOf(' ') : start;
		int spaceAfter = (key.charAt(key.length() - 1) != ' ') ? src.indexOf(' ', start + key.length()) : start
				+ key.length();
		if (spaceBefore == -1)
			spaceBefore = 0;
		else
			spaceBefore = spaceBefore + 1;
		if (spaceAfter == -1)
			spaceAfter = src.length();
		return new Range(spaceBefore, spaceAfter);
	}

}

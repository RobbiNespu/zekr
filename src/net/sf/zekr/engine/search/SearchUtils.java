/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.util.QuranLocation;
import net.sf.zekr.common.util.Range;

public class SearchUtils {
	final public static char SUKUN = 0x652;
	final public static char SHADDA = 0x651;
	final public static char KASRA = 0x650;
	final public static char DAMMA = 0x64f;
	final public static char FATHA = 0x64e;

	final public static char KASRATAN = 0x64d;
	final public static char DAMMATAN = 0x64c;
	final public static char FATHATAN = 0x64b;

	final public static char SUPERSCRIPT_ALEF = 0x670;

	final public static char HAMZA = 0x621;
	final public static char ALEF = 0x627;
	final public static char ALEF_MADDA = 0x622;
	final public static char ALEF_HAMZA_ABOVE = 0x623;
	final public static char ALEF_HAMZA_BELOW = 0x625;

	final public static char YEH_HAMZA_ABOVE = 0x626;
	final public static char WAW_HAMZA_ABOVE = 0x624;
	final public static char WAW = 0x648;

	final public static char ALEF_MAKSURA = 0x649;
	final public static char FARSI_YEH = 0x6cc;
	final public static char ARABIC_YEH = 0x64a;

	final public static char ARABIC_KAF = 0x643;
	final public static char FARSI_KEHEH = 0x6a9;

	/**
	 * @param suraNum 1-based sora number
	 * @param ayaNum 1-based aya number
	 * @return <code>suraNum + "-" + ayaNum</code>
	 */
	public static QuranLocation getKey(int suraNum, int ayaNum) {
		return new QuranLocation(suraNum, ayaNum);
	}


	/**
	 * Replace Farsi unicode <code>Yeh</code> with Arabic one, and so about
	 * <code>Kaf</code> (Farsi <code>Keheh</code>).
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceSimilarCharacters(String str) {
		str = str.replaceAll(FARSI_YEH + "", ARABIC_YEH + "");
		str = str.replaceAll(FARSI_KEHEH + "", ARABIC_KAF + "");
		return str;
	}

	/**
	 * Remove specific diacritics form the string. Also replaces incorrect charactrers
	 * using <code>replaceSimilarCharacters</code>.
	 * 
	 * @param str
	 * @return
	 */
	public static String arabicSimplify(String str) {
		// diacritics removal
		char[] arr = new char[] { SUKUN, SHADDA, KASRA, DAMMA, FATHA, KASRATAN, DAMMATAN, FATHATAN,
				SUPERSCRIPT_ALEF };
		for (int i = 0; i < arr.length; i++) {
			str = str.replaceAll("" + arr[i], "");
		}

		// YEH, ALEF replacements
		str = str.replaceAll(ALEF_MAKSURA + "", ARABIC_YEH + "");
		str = str.replaceAll(ALEF_HAMZA_ABOVE + "", ALEF + "");
		str = str.replaceAll(ALEF_HAMZA_BELOW + "", ALEF + "");
		str = str.replaceAll(ALEF_MADDA + "", ALEF + "");

		str = replaceSimilarCharacters(str);
		return str;
	}

	public static boolean isDiac(char ch) {
		return (ch == SUKUN) || (ch == SHADDA) || (ch == KASRA) || (ch == DAMMA) || (ch == FATHA)
				|| (ch == KASRATAN) || (ch == DAMMATAN) || (ch == FATHATAN)
				|| (ch == SUPERSCRIPT_ALEF);
	}

	/**
	 * Will find a range of the first occurrence of <code>key</code> in <code>src</code>.
	 * This method will ignore diacritics on both <code>src</code> and <code>key</code>.
	 * 
	 * @param src source string to be searched on
	 * @param key non-<code>null</code> target string to be found the first occurrence of which on the
	 *            <code>src</code> string
	 * @return a <code>Range</code> object from the previous space character just before
	 *         the <code>key</code> (or start of the source string if no space found) to
	 *         the first space just after the <code>key</code> in <code>src</code> (or
	 *         end of src if no space found)
	 */
	public static Range indexOfIgnoreDiacritic(String src, String key) {
		key = arabicSimplify(key);
		int k = 0, s = 0, start = -1;
		char[] source = src.toCharArray();
		char[] target = key.toCharArray();
		if (key.length() == 0)
			return null;
		while (s < src.length()) {
			if (k == key.length())
				break;

			if (source[s] == target[k]) {
				if (start == -1)
					start = s;
				s++;
				k++;
			} else if (target[k] == ALEF
					&& (source[s] == ALEF_HAMZA_ABOVE || source[s] == ALEF_HAMZA_BELOW || source[s] == ALEF_MADDA)) {
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
	 * Will find a range of the first occurrence of <code>key</code> in <code>src</code>.
	 * This method will consider diacritics on both <code>src</code> and <code>key</code>.
	 * 
	 * @param src source string to be searched on
	 * @param key target string which is to search first occurrence of which on
	 *            <code>src</code>
	 * @return a <code>Range</code> object from the previous space character just before
	 *         the <code>key</code> (or start of the source string if no space found) to
	 *         the first space just after the <code>key</code> in <code>src</code> (or
	 *         end of src if no space found)
	 */
	public static Range indexOfMatchDiacritic(String src, String key) {
		key = replaceSimilarCharacters(key);
		int start = src.indexOf(key);
		if (start == -1)
			return null;

		int spaceBefore = (key.charAt(0) != ' ') ? src.substring(0, start).lastIndexOf(' ') : start;
		int spaceAfter = (key.charAt(key.length() - 1) != ' ') ? src.indexOf(' ', start + key.length()) : start + key.length();
		if (spaceBefore == -1)
			spaceBefore = 0;
		else
			spaceBefore = spaceBefore + 1;
		if (spaceAfter == -1)
			spaceAfter = src.length();
		return new Range(spaceBefore, spaceAfter);
	}

}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

import java.util.regex.Pattern;

import net.sf.zekr.engine.search.ArabicCharacters;

import org.apache.commons.lang.StringUtils;

public class QuranWriterFilter implements IQuranFilter, ArabicCharacters {
	/** Show waqf sign if this option is set */
	public static final int SHOW_WAQF_SIGN = 1;

	/** Show small alef if this option is set */
	public static final int SHOW_SMALL_ALEF = 2;

	/** Will apply Uthmani text rules */
	public static final int UTHMANI_TEXT = 4;

	private static final int HIGHLIGHT = 4;

	public static final int HIGHLIGHT_WAQF_SIGN = SHOW_WAQF_SIGN | HIGHLIGHT;

	private static final String REPLACE_HIGHLIGHT = "<span class=\"waqfSign\">&nbsp;$1</span>";
	private static String WAQF_REGEX = ".([" + WAQF_SALA + WAQF_QALA + WAQF_SMALL_MEEM + WAQF_LA + WAQF_JEEM
			+ WAQF_THREE_DOT + WAQF_HIGH_SEEN + "])";

	private static final Pattern highlightRegex = Pattern.compile(WAQF_REGEX);

	public QuranWriterFilter() {
	}

	public String filter(QuranFilterContext qfc) {
		String str = qfc.text;
		if (qfc.ayaNum == 1 && qfc.suraNum != 1 && qfc.suraNum != 9) {
			int sp = -1;
			for (int i = 0; i < 4; i++) { // ignore 4 whitespaces.
				sp = str.indexOf(' ', sp + 1);
			}
			str = str.substring(sp + 1);
		}

		// remove SAJDA and HIZB sign
		str = StringUtils.remove(str, " " + SAJDA_PLACE);
		str = StringUtils.remove(str, RUB_EL_HIZB + " ");

		// Good for Uthmani text
		if ((qfc.params & UTHMANI_TEXT) == UTHMANI_TEXT)
			str = StringUtils.replace(str, String.valueOf(ALEF) + MADDA, String.valueOf(ALEF_MADDA));

		str = highlightRegex.matcher(str).replaceAll(REPLACE_HIGHLIGHT);

		return str;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 22, 2008
 */
package net.sf.zekr.common.resource.filter;

import java.util.regex.Pattern;

import net.sf.zekr.engine.search.tanzil.RegexUtils;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class QuranFilterUtils {
	private static Pattern SEARCH_RESULT_SIGN = Pattern.compile(RegexUtils.regTrans("[$HIGH_SALA-$RUB_EL_HIZB$SAJDAH]"));

	private static final String ALEFMADDA = RegexUtils.regTrans("$ALEF$MADDA");
	private static final String ALEF_WITH_MADDA_ABOVE = RegexUtils.regTrans("$ALEF_WITH_MADDA_ABOVE");

	public static final String filterSearchResult(String text) {
		text = SEARCH_RESULT_SIGN.matcher(text).replaceAll("");
		text = StringUtils.replace(text, ALEFMADDA, ALEF_WITH_MADDA_ABOVE);
		return filterExtraWhiteSpaces(text);
	}

	private static final Pattern HARAKA = Pattern.compile(RegexUtils.regTrans("[$HARAKA]"));
	private static final Pattern SIGN = Pattern.compile(RegexUtils.regTrans("[$HIGH_SALA-$LOW_MEEM]"));

	public static String filterHarakat(String text) {
		return HARAKA.matcher(text).replaceAll("");
	}

	public static String filterSign(String text) {
		return SIGN.matcher(text).replaceAll("");
	}

	private static final Pattern TEH = Pattern.compile(RegexUtils.regTrans("[$TEH$MARBUTA]"));
	private static final Pattern ALEF = Pattern.compile(RegexUtils.regTrans("[$ALEF$"
			+ "ALEF_WITH_MADDA_ABOVE$ALEF_WITH_HAMZA_ABOVE$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA]"));
	private static final Pattern ALEF_ROOT = Pattern.compile(RegexUtils.regTrans("[$ALEF$"
			+ "ALEF_WITH_HAMZA_ABOVE$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA]"));
	private static final Pattern WAW = Pattern.compile(RegexUtils.regTrans("[$WAW$WAW_WITH_HAMZA_ABOVE$SMALL_WAW]"));
	private static final Pattern WAW_ROOT = Pattern.compile(RegexUtils.regTrans("[$WAW$SMALL_WAW]"));
	private static final Pattern YEH = Pattern.compile(RegexUtils.regTrans("[$YEH$ALEF_MAKSURA"
			+ "$YEH_WITH_HAMZA$SMALL_YEH$FARSI_YEH$YEH_BARREE]"));
	private static final Pattern YEH_ROOT = Pattern.compile(RegexUtils.regTrans("[$YEH$ALEF_MAKSURA"
			+ "$SMALL_YEH$FARSI_YEH$YEH_BARREE]"));
	private static final Pattern KAF = Pattern.compile(RegexUtils.regTrans("[$KAF$FARSI_KEHEH" + "$SWASH_KAF]"));

	private static final Pattern SPACE = Pattern.compile("\\s+");

	public static String filterSimilarArabicCharactersForRootSearch(String text) {
		text = RegexUtils.pregReplace(text, TEH, "$TEH");
		text = RegexUtils.pregReplace(text, ALEF_ROOT, "$ALEF");
		text = RegexUtils.pregReplace(text, WAW_ROOT, "$WAW");
		text = RegexUtils.pregReplace(text, YEH_ROOT, "$YEH");
		text = RegexUtils.pregReplace(text, KAF, "$KAF");
		return text;
	}

	public static String filterExtraWhiteSpaces(String text) {
		return SPACE.matcher(text).replaceAll(" ");
	}
}

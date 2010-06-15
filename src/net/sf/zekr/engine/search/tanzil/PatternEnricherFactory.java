/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 25, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.Map;
import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.SearchInfo;

/**
 * @author Mohsen Saboorian
 */
public class PatternEnricherFactory {
	public static class QuranPatternEnricher extends PatternEnricher {
		public static final String IGNORE_HARAKA = "IGNORE_HARAKA";

		/**
		 * Enrich Arabic search pattern. It adds all possible Arabic diacritics after each character of the
		 * pattern. The resulting string is a valid regexp which can be used to match the Quran text.
		 * <p/>
		 * Parameters:<br/>
		 * IGNORE_HARAKA (Boolean): if <code>true</code>, any no diacritic (haraka) on the original pattern is
		 * taken into consideration
		 * 
		 * @param pattern the plain pattern to be enriched
		 * @return enriched pattern
		 */
		public String enrich(String pattern) {
			if (Boolean.TRUE.equals(getParameter(IGNORE_HARAKA))) {
				pattern = RegexUtils.pregReplace(pattern, "$HARAKA", "");
			}

			pattern = RegexUtils.pregReplace(pattern, "$TATWEEL", "");
			pattern = RegexUtils.regTrans(pattern); // allows using letter constants in pattern
			pattern = RegexUtils.handleSpaces(pattern);
			pattern = RegexUtils.applyRules(pattern, RegexUtils.preProcess);
			pattern = RegexUtils.applyRules(pattern, RegexUtils.arabicWildcardRegs);

			// add haraka between letters
			pattern = RegexUtils.pregReplace(pattern, "(.)", "$1$HARAKA*");

			pattern = RegexUtils.applyRules(pattern, RegexUtils.matchingRules);
			pattern = RegexUtils.applyRules(pattern, RegexUtils.arabicWildcards);
			return pattern;
		}
	}

	public static class GenericPatternEnricher extends PatternEnricher {
		private String langCode;
		private ApplicationConfig conf;

		public GenericPatternEnricher(String langCode) {
			conf = ApplicationConfig.getInstance();
			this.langCode = langCode;
		}

		public String enrich(String pattern) {
			SearchInfo searchInfo = conf.getSearchInfo();
			Map<Pattern, String> replacePatternMap = searchInfo.getReplacePattern(langCode);
			// pattern = RegexUtils.replaceAll(replacePatternMap, pattern);
			pattern = RegexUtils.replaceAllForEnrichment(replacePatternMap, pattern);

			// handle wildcards
			pattern = RegexUtils.replaceAll(RegexUtils.genericWildcardRegs, pattern);
			// pattern = RegexUtils.applyRules(pattern, RegexUtils.genericWildcardRegs);
			// pattern = RegexUtils.applyRules(pattern, RegexUtils.genericWildcards);

			pattern = RegexUtils.handleSpaces(pattern);

			String diacr = searchInfo.getDiacritic(langCode);
			if (diacr != null) {
				String letterRange = searchInfo.getLetter(langCode);
				if (letterRange == null) {
					letterRange = "\\p{L}";
				}
				pattern = pattern.replaceAll("(" + letterRange + ")", "$1" + diacr + "*");
			}

			String punct = searchInfo.getPunctuation(langCode);
			if (punct != null) {
				punct = punct.replace("\\", "\\\\");
				pattern = pattern.replaceAll("([ \"\\+])?([^ \"\\+]+)", "$1" + punct + "*$2" + punct + "*");
			}

			return pattern;
		}
	}

	/**
	 * @param langCode ISO language code
	 * @param isQuran
	 * @return {@link PatternEnricher} instance for this language code, or {@link QuranPatternEnricher} if
	 *         isQuran is true
	 */
	public static PatternEnricher getEnricher(String langCode, boolean isQuran) {
		if (isQuran) {
			return new QuranPatternEnricher();
		} else {
			return new GenericPatternEnricher(langCode);
		}
	}
}

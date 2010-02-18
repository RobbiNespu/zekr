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

/**
 * @author Mohsen Saboorian
 */
public class PatternEnricherFactory {
	public static class ArabicPatternEnricher extends PatternEnricher {
		public static final String IGNORE_HARAKA = "IGNORE_HARAKA";

		public String enrich(String pattern) {
			if (((Boolean) getParameter(IGNORE_HARAKA)).booleanValue()) {
				pattern = RegexUtils.pregReplace(pattern, "$HARAKA", "");
			}

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

		public GenericPatternEnricher(String langCode) {
			this.langCode = langCode;
		}

		public String enrich(String pattern) {
			ApplicationConfig conf = ApplicationConfig.getInstance();

			Map<Pattern, String> replacePatternMap = conf.getSearchInfo().getReplacePattern(langCode);
			pattern = RegexUtils.replaceAll(replacePatternMap, pattern);

			pattern = RegexUtils.applyRules(pattern, RegexUtils.genericWildcardRegs);
			pattern = RegexUtils.applyRules(pattern, RegexUtils.genericWildcards);

			pattern = RegexUtils.handleSpaces(pattern);

			return pattern;
		}
	}

	/**
	 * @param langCode ISO language code
	 * @return
	 */
	public static PatternEnricher getEnricher(String langCode) {
		if ("ar".equalsIgnoreCase(langCode)) {
			return new ArabicPatternEnricher();
		} else {
			return new GenericPatternEnricher(langCode);
		}
	}
}

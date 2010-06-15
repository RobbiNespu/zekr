/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 24, 2008
 */
package net.sf.zekr.engine.search;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.zekr.engine.log.Logger;

/**
 * This class holds some language-specific info for replacing non-searchable or equivalent characters.
 * 
 * @author Mohsen Saboorian
 */
public class SearchInfo {
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * A map of language ISO code to a {@link Set} of stop words.
	 */
	private Map<String, Set<String>> stopWordMap = new LinkedHashMap<String, Set<String>>();

	/**
	 * A map of language ISO codes to another map in which there are a number of {@link Pattern} to
	 * {@link String} pairs (the former is a regex while the later is replace string.
	 */
	private Map<String, Map<Pattern, String>> replacePatternMap = new LinkedHashMap<String, Map<Pattern, String>>();
	/**
	 * A map of language ISO codes to a regular expressions matching language's punctuation marks.
	 */
	private Map<String, Pattern> punctuationMap = new LinkedHashMap<String, Pattern>();
	/**
	 * A map of language ISO codes to a regular expressions matching diacritics of the language.
	 */
	private Map<String, Pattern> diacriticMap = new LinkedHashMap<String, Pattern>();
	/**
	 * A map of language ISO codes to a regular expression matching letter range of that language.
	 */
	private Map<String, Pattern> letterMap = new LinkedHashMap<String, Pattern>();

	private Set<String> defaultStopWordSet = new LinkedHashSet<String>();
	private Map<Pattern, String> defaultReplacePatternMap = new LinkedHashMap<Pattern, String>();
	private Pattern defaultPunctuation;
	private Pattern defaultDiactitic;

	public void addReplacePattern(String langCode, List<String> replacePatternList) {
		Map<Pattern, String> patterns = addReplacePatterns(replacePatternList);
		replacePatternMap.put(langCode, patterns);
	}

	private Map<Pattern, String> addReplacePatterns(List<String> replacePatternList) {
		Map<Pattern, String> patterns = new LinkedHashMap<Pattern, String>();
		for (int i = 0; i < replacePatternList.size(); i++) {
			String p = replacePatternList.get(i);
			String[] patternsArray = p.split("=");
			if (patternsArray.length >= 1) {
				try {
					patterns.put(Pattern.compile(patternsArray[0]), patternsArray.length >= 2 ? patternsArray[1] : "");
				} catch (PatternSyntaxException pse) {
					logger.warn("Invalid pattern syntax: " + patternsArray[0]);
					logger.warn(pse.toString());
				}
			} else {
				logger.warn("Invalid pattern pair: " + p);
			}
		}
		return patterns;
	}

	public void addStopWord(String langCode, List<String> stopWordList) {
		Set<String> stopWordSet = new LinkedHashSet<String>(stopWordList);
		stopWordMap.put(langCode, stopWordSet);
	}

	public void setPunctuation(String langCode, Pattern punctuationPattern) {
		punctuationMap.put(langCode, punctuationPattern);
	}

	public void setDiacritic(String langCode, Pattern diacriticsPattern) {
		diacriticMap.put(langCode, diacriticsPattern);
	}

	public void setLetter(String langCode, Pattern letterRange) {
		letterMap.put(langCode, letterRange);
	}

	public void setDefaultDiacritic(Pattern defaultDiactitics) {
		this.defaultDiactitic = defaultDiactitics;
	}

	public void setDefaultPunctuation(Pattern defaultPunctuation) {
		this.defaultPunctuation = defaultPunctuation;
	}

	public void setDefaultStopWord(List<String> defaultStopWord) {
		defaultStopWordSet = new LinkedHashSet<String>(defaultStopWord);
	}

	public void setDefaultReplacePattern(List<String> defaultReplacePattern) {
		defaultReplacePatternMap = addReplacePatterns(defaultReplacePattern);
	}

	/**
	 * @param langCode the ISO language code to search for its specific replace patterns.
	 * @return a {@link Map} of replace patterns, which maps a {@link Pattern} to a replace {@link String} for
	 *         the given language, or default replace patterns if no replace pattern is specified for this
	 *         language
	 */
	public Map<Pattern, String> getReplacePattern(String langCode) {
		Map<Pattern, String> ret = replacePatternMap.get(langCode);
		if (ret == null) {
			ret = defaultReplacePatternMap;
		}
		return ret;
	}

	public boolean containsLanguageReplacePattern(String langCode) {
		return replacePatternMap.containsKey(langCode);
	}

	/**
	 * Default replace pattern will be used when no language-specific replace pattern is specified in search
	 * info file.
	 * 
	 * @return
	 */
	public Map<Pattern, String> getDefaultReplacePattern() {
		return defaultReplacePatternMap;
	}

	/**
	 * @param langCode the ISO language code to search for its specific replace patterns.
	 * @return a {@link Set} of stop words for the given language, or default stop words if no stopword is
	 *         specified for this language
	 */
	public Set<String> getStopWord(String langCode) {
		Set<String> ret = stopWordMap.get(langCode);
		if (ret == null) {
			ret = defaultStopWordSet;
		}
		return ret;
	}

	public Pattern getDiacritic(String langCode) {
		Pattern ret = diacriticMap.get(langCode);
		if (ret == null) {
			ret = defaultDiactitic;
		}
		return ret;
	}

	public Pattern getPunctuation(String langCode) {
		Pattern ret = punctuationMap.get(langCode);
		if (ret == null) {
			ret = defaultPunctuation;
		}
		return ret;
	}

	public Pattern getLetter(String langCode) {
		return letterMap.get(langCode);
	}
}

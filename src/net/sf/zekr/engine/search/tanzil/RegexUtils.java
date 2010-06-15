/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 17, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class RegexUtils extends LetterConstants {
	private static Pattern REGTRANS_PATTERN = Pattern.compile("\\$([A-Z_]+)");

	// matching rules
	static Map<String, String> matchingRules = new LinkedHashMap<String, String>();
	static {
		matchingRules.put("$HAMZA_SHAPE", "$HAMZA_SHAPE");
		matchingRules.put("$ALEF_MAKSURA", "YY");
		matchingRules.put("$ALEF", "[$ALEF$ALEF_MAKSURA$ALEF_WITH_MADDA_ABOVE$ALEF_WITH_HAMZA_ABOVE"
				+ "$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA]");
		matchingRules.put("[$TEH$MARBUTA]", "[$TEH$MARBUTA]");
		matchingRules.put("$HEH", "[$HEH$MARBUTA]");
		matchingRules.put("$WAW", "[$WAW$WAW_WITH_HAMZA_ABOVE$SMALL_WAW]");
		matchingRules.put("$YEH", "[$YEH$ALEF_MAKSURA$YEH_WITH_HAMZA$SMALL_YEH]");
		matchingRules.put("YY", "[$ALEF_MAKSURA$YEH$ALEF]");
		matchingRules.put(" ", "$SPACE");
	}

	// wildcards
	static Map<String, String> arabicWildcardRegs = new LinkedHashMap<String, String>();
	static {
		arabicWildcardRegs.put("\\.", "P");
		arabicWildcardRegs.put("\\*", "S");
		arabicWildcardRegs.put("[?؟]", "Q");
		arabicWildcardRegs.put("[QS]*S[QS]*", "S");
	}

	// wildcards
	static Map<String, String> arabicWildcards = new LinkedHashMap<String, String>();
	static {
		arabicWildcards.put("S", "$LETTER_HARAKA*");
		// wildcards.put("S", "($LETTER|$HARAKA)*");
		arabicWildcards.put("Q", "$LETTER?");
		arabicWildcards.put("P", "$LETTER");
	}

	static Map<Pattern, String> genericWildcardRegs = new LinkedHashMap<Pattern, String>();
	static {
		genericWildcardRegs.put(Pattern.compile("\\."), "\\\\p{L}");
		genericWildcardRegs.put(Pattern.compile("\\*"), "\\\\p{L}*");
		genericWildcardRegs.put(Pattern.compile("[?؟]"), "\\\\p{L}?");
		// genericWildcardRegs.put("(__QQ__|__SS__)*__S__(__QQ__|__SS__)*", "__SS__");
	}
	/*
		static Map<String, String> genericWildcards = new LinkedHashMap<String, String>();
		static {
			genericWildcards.put("__SS__", "\\\\p{L}*");
			genericWildcards.put("__QQ__", "\\\\p{L}?");
			genericWildcards.put("__PP__", "\\\\p{L}");
		}
	*/
	static Map<String, String> preProcess = new LinkedHashMap<String, String>();
	static {
		preProcess.put("[$FARSI_YEH$YEH_BARREE]", "$YEH");
		preProcess.put("[$FARSI_KEHEH$SWASH_KAF]", "$KAF");
		preProcess.put("$NOON$SUKUN", "$NOON");
		preProcess.put("([$KASRA$KASRATAN])($SHADDA)", "$2$1");
	}

	static {
		for (Entry<String, String> entry : GROUPS.entrySet()) {
			entry.setValue(RegexUtils.regTrans(entry.getValue()));
		}
	}

	/**
	 * Translate a symbolic regular expression into a legal one.
	 * 
	 * @param str symbolic regex
	 * @return legal regex
	 */
	public static final String regTrans(String str) {
		StringBuffer ret = new StringBuffer();
		Matcher matcher = REGTRANS_PATTERN.matcher(str);

		int lastEnd = 0;
		while (matcher.find()) {
			String group = matcher.group(1);

			String replacement;
			if (GROUPS.containsKey(group))
				replacement = (String) GROUPS.get(group);
			else if (CHARS.containsKey(group))
				replacement = ((Character) CHARS.get(group)).toString();
			else
				continue;
			ret.append(str.substring(lastEnd, matcher.start()));
			ret.append(replacement);
			lastEnd = matcher.end();
		}
		ret.append(str.substring(lastEnd));
		return ret.toString();
	}

	// simulate preg_replace
	public static final String pregReplace(String str, String fromExp, String toExp) {
		fromExp = regTrans(fromExp);
		toExp = regTrans(toExp);
		return str.replaceAll(fromExp, toExp);
	}

	public static final String pregReplace(String str, Pattern fromExp, String toExp) {
		toExp = regTrans(toExp);
		return fromExp.matcher(str).replaceAll(toExp);
	}

	static final String applyRules(String str, Map<String, String> rule) {
		for (Entry<String, String> entry : rule.entrySet()) {
			str = pregReplace(str, entry.getKey().toString(), entry.getValue().toString());
		}
		return str;
	}

	static final String handleSpaces(String pattern) {
		if ("".equals(pattern)) {
			return pattern;
		}
		pattern = pattern.replaceAll("\\s+", " ");

		// Zekr is not Java 1.4.2-compatible anymore
		// replace spaces outside of quotations with + (Java 1.4 has problems dealing with equivalent Tanzil regex)
		boolean openquote = false;
		StringBuffer buf = new StringBuffer(pattern.length());
		char[] chars = pattern.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '"')
				openquote = !openquote;
			else if (chars[i] == ' ' && !openquote)
				chars[i] = '+';
			buf.append(chars[i]);
		}
		pattern = buf.toString();

		//pattern = pattern.replaceAll("^(([^\"]*\"[^\"]*\")*)([^\"\\s]*) ", "$1$3+");
		// pattern = StringUtils.replace(pattern, "_", " ");
		pattern = pattern.replace('_', ' ');
		pattern = pattern.replace('"', ' ');
		// pattern = StringUtils.replace(pattern, "\"", " ");
		// pattern = pattern.replace('+', ' ');
		// pattern = pattern.replaceAll("\\+", "");
		// pattern = pattern.replaceAll("\"", "+");

		// remove extra operators
		pattern = pattern.replaceAll("^[+|]+", "").replaceAll("[+|!]+$", "");
		pattern = pattern.replaceAll("\\+*([+|!])\\+*", "$1");
		return pattern;
	}

	/**
	 * Enrich arabic search pattern. It adds all possible Arabic diacritics after each character of the
	 * pattern. The resulting string is a valid regexp which can be used to match the Quran text.
	 * 
	 * @param pattern the plain pattern to be enriched
	 * @param ignoreHaraka if <code>true</code>, any no diacritic (haraka) on the original pattern is taken
	 *           into consideration
	 * @return enriched pattern
	 */
	static String enrichPattern(String pattern, boolean ignoreHaraka) {
		if (ignoreHaraka) {
			pattern = pregReplace(pattern, "$HARAKA", "");
		}

		pattern = pregReplace("$TATWEEL", "", pattern);
		pattern = regTrans(pattern); // allows using letter constants in pattern
		pattern = handleSpaces(pattern);
		pattern = applyRules(pattern, preProcess);
		pattern = applyRules(pattern, arabicWildcardRegs);

		// add haraka between letters
		pattern = pregReplace(pattern, "(.)", "$1$HARAKA*");

		pattern = applyRules(pattern, matchingRules);
		pattern = applyRules(pattern, arabicWildcards);
		return pattern;
	}

	/**
	 * @param replacePatternMap a {@link Map} from {@link Pattern}s to replace {@link String}s
	 * @param src original string to apply replace all on.
	 * @return the resulting string after replacing patterns
	 */
	public static String replaceAll(Map<Pattern, String> replacePatternMap, String src) {
		for (Entry<Pattern, String> entry : replacePatternMap.entrySet()) {
			Matcher m = ((Pattern) entry.getKey()).matcher(src);
			src = m.replaceAll((String) entry.getValue());
		}
		return src;
	}

	public static String replaceAllForEnrichment(Map<Pattern, String> replacePatternMap, String src) {
		for (Entry<Pattern, String> entry : replacePatternMap.entrySet()) {
			if (StringUtils.isBlank(entry.getValue())) {
				src = src.replaceAll("(" + entry.getKey().pattern() + ")", "$1*");
			} else {
				src = src.replaceAll(entry.getKey().pattern() + "|" + entry.getValue(), "(" + entry.getKey().pattern()
						+ "|" + entry.getValue() + ")");
			}
		}
		return src;
	}

	public static void main(String[] args) {
		// System.out.println(regTrans("[$FARSI_YEH$YEH_BARREE]"));
		// System.out.println(handleSpaces("sadsa \"asdfasdf asdf asdf\"sdf as \"sdf sdf\" "));
		// System.out.println(regTrans("$ALEF$ALEF_MAKSURA$ALEF_WITH_MADDA_ABOVE$ALEF_WITH_HAMZA_ABOVE$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA"));
		// System.out.println(enrichPattern("salam?", false));
		System.out.println(regTrans("$ARABIC_COMMA"));
		System.out.println(enrichPattern("\"salamon\" ala", true));
		//		System.out.println(enrichPattern("\"salamon\" ala", false));
		System.out.println(enrichPattern("\"سلامٔ \" عَلی", false));
		System.out.println(enrichPattern("\"سلامٔ \" عَلی", true));
		// System.out.println("salam azizam".replaceAll("(.)", "'$1'"));
	}
}

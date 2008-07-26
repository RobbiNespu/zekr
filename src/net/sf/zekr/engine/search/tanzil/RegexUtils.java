/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 17, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class RegexUtils extends LetterConstants {
	private static Pattern REGTRANS_PATTERN = Pattern.compile("\\$([A-Z_]+)");

	// matching rules
	static Map matchingRules = new LinkedHashMap();
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
	static Map wildcardRegs = new LinkedHashMap();
	static {
		wildcardRegs.put("\\.", "P");
		wildcardRegs.put("\\*", "S");
		wildcardRegs.put("[?؟]", "Q");
		wildcardRegs.put("[QS]*S[QS]*", "S");
		wildcardRegs.put("^\\s*[QS]*", "");
	}

	// wildcards
	static Map wildcards = new LinkedHashMap();
	static {
		wildcards.put("S", "$LETTER_HARAKA*");
		// wildcards.put("S", "($LETTER|$HARAKA)*");
		wildcards.put("Q", "$LETTER?");
		wildcards.put("P", "$LETTER");
	}

	static Map preProcess = new LinkedHashMap();
	static {
		preProcess.put("[$FARSI_YEH$YEH_BARREE]", "$YEH");
		preProcess.put("[$FARSI_KEHEH$SWASH_KAF]", "$KAF");
	}

	static {
		for (Iterator iterator = GROUPS.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			entry.setValue(RegexUtils.regTrans((String) entry.getValue()));
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

	static final String applyRules(String str, Map rule) {
		for (Iterator iterator = rule.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			str = pregReplace(str, entry.getKey().toString(), entry.getValue().toString());
		}
		return str;
	}

	static final String handleSpaces(String pattern) {
		if ("".equals(pattern))
			return pattern;
		pattern = pattern.replaceAll("\\s+", " ");

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

		//	pattern = pattern.replaceAll("^(([^\"]*\"[^\"]*\")*)([^\"\\s]*) ", "$1$3+");
		// pattern = StringUtils.replace(pattern, "_", " ");
		pattern = pattern.replace('_', ' ');
		// pattern = StringUtils.replace(pattern, "\"", " ");
		// pattern = pattern.replace('+', ' ');
		pattern = pattern.replaceAll("\\+", "");

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
		if (ignoreHaraka)
			pattern = pregReplace(pattern, "$HARAKA", "");

		pattern = regTrans(pattern); // allows using letter constants in pattern
		pattern = handleSpaces(pattern);
		pattern = applyRules(pattern, preProcess);
		pattern = applyRules(pattern, wildcardRegs);

		// add haraka between letters
		pattern = pregReplace(pattern, "(.)", "$1$HARAKA*");

		pattern = applyRules(pattern, matchingRules);
		pattern = applyRules(pattern, wildcards);
		return pattern;
	}

	/**
	 * @param replacePatternMap a {@link Map} from {@link Pattern}s to replace {@link String}s
	 * @param src original string to apply replace all on.
	 * @return the resulting string after replacing patterns
	 */
	public static String replaceAll(Map replacePatternMap, String src) {
		for (Iterator iterator = replacePatternMap.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			Matcher m = ((Pattern) entry.getKey()).matcher(src);
			src = m.replaceAll((String) entry.getValue());
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

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 17, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.HashMap;
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
public class RegexSeachUtils extends LetterConstants {

	// matching rules
	public static Map matchingRules = new LinkedHashMap();
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
	public static Map wildcardRegs = new LinkedHashMap();
	static {
		wildcardRegs.put("\\.", "P");
		wildcardRegs.put("\\*", "S");
		wildcardRegs.put("[?؟]", "Q");
	}

	// wildcards
	public static Map wildcards = new LinkedHashMap();
	static {
		wildcards.put("S", "($LETTER|$HARAKA)*");
		wildcards.put("Q", "$LETTER?");
		wildcards.put("P", "$LETTER");
	}

	public static Map preProcess = new LinkedHashMap();
	static {
		preProcess.put("[$FARSI_YEH$YEH_BARREE]", "$YEH");
		preProcess.put("[$FARSI_KEHEH$SWASH_KAF]", "$KAF");
	}

	/**
	 * Translate a symbolic regular expression into a legal one.
	 * 
	 * @param str symbolic regex
	 * @return legal regex
	 */
	static final String regTrans(String str) {
		StringBuffer ret = new StringBuffer();
		Pattern regex = Pattern.compile("\\$([A-Z_]+)");
		Matcher matcher = regex.matcher(str);

		int lastEnd = 0;
		while (matcher.find()) {
			String group = matcher.group(1);

			String replacement;
			if (UGroups.containsKey(group))
				replacement = (String) UGroups.get(group);
			else if (UChars.containsKey(group))
				replacement = ((Character) UChars.get(group)).toString();
			else
				continue;
			ret.append(str.substring(lastEnd, matcher.start()));
			ret.append(replacement);
			// str = str.replaceAll(matcher.group(0), replacement);
			// matcher = regex.matcher(str);
			lastEnd = matcher.end();
		}
		ret.append(str.substring(lastEnd));
		return ret.toString();
		// return str;
	}

	// simulate preg_replace
	private static final String pregReplace(String fromExp, String toExp, String str) {
		fromExp = regTrans(fromExp);
		toExp = regTrans(toExp);
		return str.replaceAll(fromExp, toExp);
	}

	private static final String applyRules(Map rule, String str) {
		for (Iterator iterator = rule.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			str = pregReplace(entry.getKey().toString(), entry.getValue().toString(), str);
		}
		return str;
	}

	private static final String handleSpaces(String pattern) {
		String prev = "";
		if ("".equals(pattern))
			return pattern;
		pattern = pattern.replaceAll("\\s+", " ");
		while (!pattern.equals(prev)) {
			prev = pattern;
			pattern = pattern.replaceAll("(([^\"]*\"[^\"]*\")*)([^\"\\s]*) ", "$1$3+");
			System.out.println(pattern);
			// pattern = pattern.replaceAll("([^\"]*)(\"[^\"]*\")*([^\"\\s]*) ", "$1$3+"); // ^ removed from the first of pattern to be java 1.4.2 compatible
		}

		pattern = pattern.replaceAll("_", " ");
		pattern = pattern.replaceAll("\"", "+");
		// pattern = pattern.replaceAll("\\+", "");

		// remove extra operators
		pattern = pattern.replaceAll("^[+|]+", "").replaceAll("[+|!]+$", "");
		pattern = pattern.replaceAll("\\+*([+|!])\\+*", "$1");

		return pattern;
	}

	// enrich arabic search pattern
	public static String enrichPattern(String pattern, boolean ignoreHaraka) {
		if (ignoreHaraka)
			pattern = pregReplace("$HARAKA", "", pattern);

		pattern = regTrans(pattern); // allows using letter constants in pattern
		pattern = handleSpaces(pattern);
		pattern = applyRules(preProcess, pattern);
		pattern = applyRules(wildcardRegs, pattern);

		// add haraka between letters
		pattern = pregReplace("(.)", "$1$HARAKA*", pattern);

		pattern = applyRules(matchingRules, pattern);
		pattern = applyRules(wildcards, pattern);

		return pattern;
	}

	public static void main(String[] args) {
		// System.out.println(regTrans("[$FARSI_YEH$YEH_BARREE]"));
		// System.out.println(handleSpaces("sadsa \"asdfasdf asdf asdf\"sdf as \"sdf sdf\" "));
		// System.out.println(regTrans("$ALEF$ALEF_MAKSURA$ALEF_WITH_MADDA_ABOVE$ALEF_WITH_HAMZA_ABOVE$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA"));
		// System.out.println(enrichPattern("salam?", false));
		System.out.println(enrichPattern("سلام علی", false));
		// System.out.println("salam azizam".replaceAll("(.)", "'$1'"));
	}
}

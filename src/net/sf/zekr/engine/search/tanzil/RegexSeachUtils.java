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
		wildcardRegs.put("[QPS]*S[QPS]*", "S");
		wildcardRegs.put("^\\s*[QPS]*", "");
	}

	// wildcards
	public static Map wildcards = new LinkedHashMap();
	static {
		wildcards.put("S", "$LETTER_HARAKA*");
		// wildcards.put("S", "($LETTER|$HARAKA)*");
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
	public static final String regTrans(String str) {
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
	public static final String pregReplace(String str, String fromExp, String toExp) {
		fromExp = regTrans(fromExp);
		toExp = regTrans(toExp);
		return str.replaceAll(fromExp, toExp);
	}

	public static final String pregReplace(String str, Pattern fromExp, String toExp) {
		toExp = regTrans(toExp);
		return fromExp.matcher(str).replaceAll(toExp);
	}

	private static final String applyRules(String str, Map rule) {
		for (Iterator iterator = rule.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			str = pregReplace(str, entry.getKey().toString(), entry.getValue().toString());
		}
		return str;
	}

	private static final String handleSpaces(String pattern) {
		if ("".equals(pattern))
			return pattern;
		pattern = pattern.replaceAll("\\s+", " ");

		/*
		String prev = "";
		while (!pattern.equals(prev)) {
			prev = pattern;
			pattern = pattern.replaceAll("^(([^\"]*\"[^\"]*\")*)([^\"\\s]*) ", "$1$3+");
		}*/

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

	public static void main(String[] args) {
		// System.out.println(regTrans("[$FARSI_YEH$YEH_BARREE]"));
		// System.out.println(handleSpaces("sadsa \"asdfasdf asdf asdf\"sdf as \"sdf sdf\" "));
		// System.out.println(regTrans("$ALEF$ALEF_MAKSURA$ALEF_WITH_MADDA_ABOVE$ALEF_WITH_HAMZA_ABOVE$ALEF_WITH_HAMZA_BELOW$ALEF_WASLA"));
		// System.out.println(enrichPattern("salam?", false));
		System.out.println(enrichPattern("سلام علی", false));
		// System.out.println("salam azizam".replaceAll("(.)", "'$1'"));
	}
}

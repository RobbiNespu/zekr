/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 17, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LetterConstants {
	public static Map UChars = new HashMap();
	static {
		// letters used in Quran text
		UChars.put("HAMZA", new Character('\u0621'));
		UChars.put("ALEF_WITH_MADDA_ABOVE", new Character('\u0622'));
		UChars.put("ALEF_WITH_HAMZA_ABOVE", new Character('\u0623'));
		UChars.put("WAW_WITH_HAMZA_ABOVE", new Character('\u0624'));
		UChars.put("ALEF_WITH_HAMZA_BELOW", new Character('\u0625'));
		UChars.put("YEH_WITH_HAMZA", new Character('\u0626'));
		UChars.put("ALEF", new Character('\u0627'));
		UChars.put("BEH", new Character('\u0628'));
		UChars.put("MARBUTA", new Character('\u0629'));
		UChars.put("TEH", new Character('\u062A'));
		UChars.put("THEH", new Character('\u062B'));
		UChars.put("JEMM", new Character('\u062C'));
		UChars.put("HAH", new Character('\u062D'));
		UChars.put("KHAH", new Character('\u062E'));
		UChars.put("DAL", new Character('\u062F'));
		UChars.put("THAL", new Character('\u0630'));
		UChars.put("REH", new Character('\u0631'));
		UChars.put("ZAIN", new Character('\u0632'));
		UChars.put("SEEN", new Character('\u0633'));
		UChars.put("SHEEN", new Character('\u0634'));
		UChars.put("SAD", new Character('\u0635'));
		UChars.put("DAD", new Character('\u0636'));
		UChars.put("TAH", new Character('\u0637'));
		UChars.put("ZAH", new Character('\u0638'));
		UChars.put("AIN", new Character('\u0639'));
		UChars.put("GHAIN", new Character('\u063A'));
		UChars.put("TATWEEL", new Character('\u0640'));
		UChars.put("FEH", new Character('\u0641'));
		UChars.put("QAF", new Character('\u0642'));
		UChars.put("KAF", new Character('\u0643'));
		UChars.put("LAM", new Character('\u0644'));
		UChars.put("MEEM", new Character('\u0645'));
		UChars.put("NOON", new Character('\u0646'));
		UChars.put("HEH", new Character('\u0647'));
		UChars.put("WAW", new Character('\u0648'));
		UChars.put("ALEF_MAKSURA", new Character('\u0649'));
		UChars.put("YEH", new Character('\u064A'));
		UChars.put("FATHATAN", new Character('\u064B'));
		UChars.put("DAMMATAN", new Character('\u064C'));
		UChars.put("KASRATAN", new Character('\u064D'));
		UChars.put("FATHA", new Character('\u064E'));
		UChars.put("DAMMA", new Character('\u064F'));
		UChars.put("KASRA", new Character('\u0650'));
		UChars.put("SHADDA", new Character('\u0651'));
		UChars.put("SUKUN", new Character('\u0652'));
		UChars.put("MADDA", new Character('\u0653'));
		UChars.put("HAMZA_ABOVE", new Character('\u0654'));
		UChars.put("HAMZA_BELOW", new Character('\u0655'));
		UChars.put("SMALL_ALEF", new Character('\u065F'));
		UChars.put("SUPERSCRIPT_ALEF", new Character('\u0670'));
		UChars.put("ALEF_WASLA", new Character('\u0671'));
		UChars.put("HIGH_SALA", new Character('\u06D6'));
		UChars.put("HIGH_GHALA", new Character('\u06D7'));
		UChars.put("HIGH_MEEM_INITIAL_FORM", new Character('\u06D8'));
		UChars.put("HIGH_LA", new Character('\u06D9'));
		UChars.put("HIGH_JEMM", new Character('\u06DA'));
		UChars.put("HIGH_THREE_DOT", new Character('\u06DB'));
		UChars.put("HIGH_SEEN", new Character('\u06DC'));
		UChars.put("RUB_EL_HIZB", new Character('\u06DE'));
		UChars.put("HIGH_ROUNDED_ZERO", new Character('\u06DF'));
		UChars.put("HIGH_UPRIGHT_ZERO", new Character('\u06E0'));
		UChars.put("HIGH_MEEM", new Character('\u06E2'));
		UChars.put("LOW_SEEN", new Character('\u06E3'));
		UChars.put("SMALL_WAW", new Character('\u06E5'));
		UChars.put("SMALL_YEH", new Character('\u06E6'));
		UChars.put("HIGH_NOON", new Character('\u06E8'));
		UChars.put("SAJDAH", new Character('\u06E9'));
		UChars.put("LOW_STOP", new Character('\u06EA'));
		UChars.put("HIGH_STOP", new Character('\u06EB'));
		UChars.put("HIGH_STOP_FILLED", new Character('\u06EC'));
		UChars.put("LOW_MEEM", new Character('\u06ED'));

		// other letters
		UChars.put("FARSI_YEH", new Character('\u06CC'));
		UChars.put("FARSI_KEHEH", new Character('\u06A9'));
		UChars.put("SWASH_KAF", new Character('\u06AA'));
		UChars.put("YEH_BARREE", new Character('\u06D2'));
	}

	// letter groups
	public static Map UGroups = new HashMap();
	static {
		UGroups.put("LETTER", "[%HAMZA-%YEH]");
		UGroups.put("HARAKA", "[%FATHATAN-%MADDA%SUPERSCRIPT_ALEF]");
		UGroups.put("SPACE", "[\\s%HIGH_SALA-%LOW_MEEM]*\\s");
		UGroups.put("HAMZA_SHAPE", "[%HAMZA_ABOVE%HAMZA%ALEF_WITH_HAMZA_ABOVE-%YEH_WITH_HAMZA]");
	}

	static {
		for (Iterator iterator = UGroups.entrySet().iterator(); iterator.hasNext();) {
			Entry entry = (Entry) iterator.next();
			entry.setValue(RegexSeachUtils.regTrans((String) entry.getValue()));
		}
	}
}

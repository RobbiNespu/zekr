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

/**
 * Helper class for Regex-based search.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class LetterConstants {
	public static Map<String, Character> CHARS = new LinkedHashMap<String, Character>();
	static {
		// letters used in Quran text
		CHARS.put("HAMZA", new Character('\u0621'));
		CHARS.put("ALEF_WITH_MADDA_ABOVE", new Character('\u0622'));
		CHARS.put("ALEF_WITH_HAMZA_ABOVE", new Character('\u0623'));
		CHARS.put("WAW_WITH_HAMZA_ABOVE", new Character('\u0624'));
		CHARS.put("ALEF_WITH_HAMZA_BELOW", new Character('\u0625'));
		CHARS.put("YEH_WITH_HAMZA", new Character('\u0626'));
		CHARS.put("ALEF", new Character('\u0627'));
		CHARS.put("BEH", new Character('\u0628'));
		CHARS.put("MARBUTA", new Character('\u0629'));
		CHARS.put("TEH", new Character('\u062A'));
		CHARS.put("THEH", new Character('\u062B'));
		CHARS.put("JEMM", new Character('\u062C'));
		CHARS.put("HAH", new Character('\u062D'));
		CHARS.put("KHAH", new Character('\u062E'));
		CHARS.put("DAL", new Character('\u062F'));
		CHARS.put("THAL", new Character('\u0630'));
		CHARS.put("REH", new Character('\u0631'));
		CHARS.put("ZAIN", new Character('\u0632'));
		CHARS.put("SEEN", new Character('\u0633'));
		CHARS.put("SHEEN", new Character('\u0634'));
		CHARS.put("SAD", new Character('\u0635'));
		CHARS.put("DAD", new Character('\u0636'));
		CHARS.put("TAH", new Character('\u0637'));
		CHARS.put("ZAH", new Character('\u0638'));
		CHARS.put("AIN", new Character('\u0639'));
		CHARS.put("GHAIN", new Character('\u063A'));
		CHARS.put("TATWEEL", new Character('\u0640'));
		CHARS.put("FEH", new Character('\u0641'));
		CHARS.put("QAF", new Character('\u0642'));
		CHARS.put("KAF", new Character('\u0643'));
		CHARS.put("LAM", new Character('\u0644'));
		CHARS.put("MEEM", new Character('\u0645'));
		CHARS.put("NOON", new Character('\u0646'));
		CHARS.put("HEH", new Character('\u0647'));
		CHARS.put("WAW", new Character('\u0648'));
		CHARS.put("ALEF_MAKSURA", new Character('\u0649'));
		CHARS.put("YEH", new Character('\u064A'));
		CHARS.put("FATHATAN", new Character('\u064B'));
		CHARS.put("DAMMATAN", new Character('\u064C'));
		CHARS.put("KASRATAN", new Character('\u064D'));
		CHARS.put("FATHA", new Character('\u064E'));
		CHARS.put("DAMMA", new Character('\u064F'));
		CHARS.put("KASRA", new Character('\u0650'));
		CHARS.put("SHADDA", new Character('\u0651'));
		CHARS.put("SUKUN", new Character('\u0652'));
		CHARS.put("MADDA", new Character('\u0653'));
		CHARS.put("HAMZA_ABOVE", new Character('\u0654'));
		CHARS.put("HAMZA_BELOW", new Character('\u0655'));
		CHARS.put("SMALL_ALEF", new Character('\u065F'));
		CHARS.put("SUPERSCRIPT_ALEF", new Character('\u0670'));
		CHARS.put("ALEF_WASLA", new Character('\u0671'));
		CHARS.put("HIGH_SALA", new Character('\u06D6'));
		CHARS.put("HIGH_GHALA", new Character('\u06D7'));
		CHARS.put("HIGH_MEEM_INITIAL_FORM", new Character('\u06D8'));
		CHARS.put("HIGH_LA", new Character('\u06D9'));
		CHARS.put("HIGH_JEMM", new Character('\u06DA'));
		CHARS.put("HIGH_THREE_DOT", new Character('\u06DB'));
		CHARS.put("HIGH_SEEN", new Character('\u06DC'));
		CHARS.put("RUB_EL_HIZB", new Character('\u06DE'));
		CHARS.put("HIGH_ROUNDED_ZERO", new Character('\u06DF'));
		CHARS.put("HIGH_UPRIGHT_ZERO", new Character('\u06E0'));
		CHARS.put("HIGH_MEEM", new Character('\u06E2'));
		CHARS.put("LOW_SEEN", new Character('\u06E3'));
		CHARS.put("SMALL_WAW", new Character('\u06E5'));
		CHARS.put("SMALL_YEH", new Character('\u06E6'));
		CHARS.put("HIGH_NOON", new Character('\u06E8'));
		CHARS.put("SAJDAH", new Character('\u06E9'));
		CHARS.put("LOW_STOP", new Character('\u06EA'));
		CHARS.put("HIGH_STOP", new Character('\u06EB'));
		CHARS.put("HIGH_STOP_FILLED", new Character('\u06EC'));
		CHARS.put("LOW_MEEM", new Character('\u06ED'));
		CHARS.put("HAMZA_ABOVE_ALEF", new Character('\u0675'));
		CHARS.put("DOTLESS_BEH", new Character('\u066E'));
		CHARS.put("HIGH_YEH", new Character('\u06E7'));
		CHARS.put("ZWNJ", new Character('\u200C'));
		CHARS.put("NBSP", new Character('\u00A0'));
		CHARS.put("NNBSP", new Character('\u202F'));

		// other letters
		CHARS.put("ARABIC_COMMA", new Character('\u060C'));
		CHARS.put("ARABIC_SEMICOLON", new Character('\u061B'));
		
		CHARS.put("FARSI_YEH", new Character('\u06CC'));
		CHARS.put("FARSI_HIGH_HAMZA", new Character('\u0674'));
		CHARS.put("FARSI_KEHEH", new Character('\u06A9'));
		CHARS.put("SWASH_KAF", new Character('\u06AA'));
		CHARS.put("YEH_BARREE", new Character('\u06D2'));
	}

	// letter groups
	public static Map<String, String> GROUPS = new LinkedHashMap<String, String>();
	static {
		GROUPS.put("LETTER", "[$HAMZA-$YEH]");
		GROUPS.put("HARAKA", "[$FATHATAN-$MADDA$SUPERSCRIPT_ALEF]");
		GROUPS.put("SPACE", "[\\\\s$HIGH_SALA-$LOW_MEEM]*\\\\s");
		GROUPS.put("HAMZA_SHAPE", "[$HAMZA_ABOVE$HAMZA$ALEF_WITH_HAMZA_ABOVE-$YEH_WITH_HAMZA]");
		GROUPS.put("LETTER_HARAKA", "[$HAMZA-$ALEF_WASLA]");
	}
}

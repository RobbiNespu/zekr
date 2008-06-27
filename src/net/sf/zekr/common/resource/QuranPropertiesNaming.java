/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 23, 2004
 */

package net.sf.zekr.common.resource;

/**
 * This class consists of several <code>public static String</code>s related to the Quran properties XML
 * files.
 * 
 * @author Mohsen Saboorian
 */
public class QuranPropertiesNaming {

	public static final String SURA_DETAIL_TAG = "sura-detail";
	public static final String SURA_TAG = SURA_DETAIL_TAG + "." + "sura";

	public static final String JUZ_DETAIL_TAG = "juz-detail";
	public static final String JUZ_TAG = JUZ_DETAIL_TAG + "." + "juz";

	public static final String SAJDA_DETAIL_TAG = "sajda-detail";
	public static final String SAJDA_TAG = SAJDA_DETAIL_TAG + "." + "sajda";

	public static final String PARENT_NODE = "quran";
	public static final String INDEX_ATTR = "index";
	public static final String AYA_COUNT_ATTR = "ayaCount";
	public static final String AYA_NUM_ATTR = "aya";
	public static final String SURA_NUM_ATTR = "sura";
	public static final String DESCENT_ATTR = "descent";
	public static final String TYPE_ATTR = "type";
	public static final String NAME_ATTR = "name";
	public static final String NAME_TRANSLATED_ATTR = NAME_ATTR;
	public static final String NAME_TRANSLITERATED_ATTR = "tname";
	public static final String EN_NAME_ATTR = "en";
	public static final String PAGE_TAG = "page";

	/** sura name in Arabic */
	public static final String SURA_NAME_ARABIC = "arabic";
	/** sura name, translated in the default language */
	public static final String SURA_NAME_T9N = "t9n";
	/** sura name, transliterated in the default language */
	public static final String SURA_NAME_T13N = "t13n";
	/** sura name, translated in English */
	public static final String SURA_NAME_ENGLISH_T9N = "en-t9n";
	/** sura name, transliterated in English */
	public static final String SURA_NAME_ENGLISH_T13N = "en-t13n";

	public static final String MAKKI = "MAKKI";
	public static final String MADANI = "MADANI";
	public static final String RECOMMENDED_SAJDA = "RECOMMENDED"; // mustahab sajda
	public static final String MANDATORY_SAJDA = "MANDATORY"; // wajib (obligatory) sajda
}

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
 * This class consists of several <code>public static String</code>s related
 * to the Quran properties XML files.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
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
	public static final String AYA_NUM_ATTR = "ayaNumber";
	public static final String SURA_NUM_ATTR = "suraNumber";
	public static final String DESCENT_ATTR = "descent";
	public static final String TYPE_ATTR = "type";
	public static final String NAME_ATTR = "name";
	
	public static final String MAKKI = "MAKKI";
	public static final String MADANI = "MADANI";
	public static final String MINOR_SAJDA = "MINOR"; // Mustahab Sajda
	public static final String MAJOR_SAJDA = "MAJOR"; // Vajib (obligatory) Sajda
}
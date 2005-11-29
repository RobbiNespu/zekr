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
 * @see TODO
 * @version 0.1
 */
public class QuranPropertiesNaming {

	public static final String SOORA_DETAIL_TAG = "soora-detail";
	public static final String SOORA_TAG = SOORA_DETAIL_TAG + "." + "soora";

	public static final String JOZ_DETAIL_TAG = "joz-detail";
	public static final String JOZ_TAG = JOZ_DETAIL_TAG + "." + "joz";

	public static final String SUJDA_DETAIL_TAG = "sujda-detail";
	public static final String SUJDA_TAG = SUJDA_DETAIL_TAG + "." + "sujda";

	public static final String PARENT_NODE = "quran";
	public static final String INDEX_ATTR = "index";
	public static final String AYA_COUNT_ATTR = "ayaCount";
	public static final String AYA_NUM_ATTR = "ayaNumber";
	public static final String SOORA_NUM_ATTR = "sooraNumber";
	public static final String DESCENT_ATTR = "descent";
	public static final String TYPE_ATTR = "type";
	public static final String NAME_ATTR = "name";
	
	public static final String MAKKI = "MAKKI";
	public static final String MADANI = "MADANI";
	public static final String MINOR_SUJDA = "MINOR";
	public static final String MAJOR_SUJDA = "MAJOR";
}
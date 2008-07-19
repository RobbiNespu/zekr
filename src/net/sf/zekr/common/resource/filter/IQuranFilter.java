/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

public interface IQuranFilter {
	/** Show waqf sign if this option is set */
	public static final int SHOW_WAQF_SIGN = 1;

	//** Show small alef if this option is set */
	// public static final int SHOW_SMALL_ALEF = 2;

	/** Will apply Uthmani text rules */
	public static final int UTHMANI_TEXT = 4;

	/**
	 * This filter option already has the meaning of {@link #SHOW_WAQF_SIGN}.
	 */
	public static final int HIGHLIGHT_WAQF_SIGN = 8 | SHOW_WAQF_SIGN;

	/**
	 * Implementations of this method manipulates the input parameter as needed.
	 * 
	 * @param str input string to be manipulated
	 * @return manipulated string
	 */
	public String filter(QuranFilterContext quranFilterContext);
}

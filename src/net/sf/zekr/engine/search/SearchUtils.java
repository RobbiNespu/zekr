/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.util.QuranLocation;

public class SearchUtils {
	/**
	 * @param sooraNum 1-based sora number
	 * @param ayaNum 1-based aya number
	 * @return <code>sooraNum + "-" + ayaNum</code>
	 */
	public static QuranLocation getKey(int sooraNum, int ayaNum) {
		return new QuranLocation(sooraNum, ayaNum);
	}

}

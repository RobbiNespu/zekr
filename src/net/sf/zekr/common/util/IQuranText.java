/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2005
 */
package net.sf.zekr.common.util;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public interface IQuranText {

	/**
	 * @param suraNum
	 *            the sura number <b>counted from 1</b>
	 * @param ayaNum
	 *            the aya number <b>counted from 1</b>
	 * @return returns <code>ayaNum</code> aya of the <code>suraNum</code> sura of
	 *         the Quran.
	 */
	public String get(int suraNum, int ayaNum);

	/**
	 * <b>Note:</b> no range check is performed.
	 * 
	 * @param suraNum
	 *            indicates the sura number (which is counted from 1)
	 * @return An <code>String</code> array of the sura (#<code>suraNum</code>)
	 *         ayas.
	 */
	public String[] getSura(int suraNum);
}

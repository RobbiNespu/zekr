/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2005
 */
package net.sf.zekr.common.resource;

/**
 * This is the standard interface for a sura text. Suras in any language (translation)
 * may extend or implement this class.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public interface ISuraText {
	/**
	 * @param ayaNum
	 * @return aya #<code>ayaNum</code> in the sura
	 */
	public String getAya(int ayaNum);
}

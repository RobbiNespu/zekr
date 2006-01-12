/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 10, 2005
 */
package net.sf.zekr.common.util;


/**
 * Initial interface for properties of the Holy Quran
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public interface IQuranProperties {

	// public String getSooraName(int sooraNum);

	// public String getAyaCount(int sooraNum);

	// public String[] getSooraNames();

	// public String[] getAyaCounts();

	// public String[] getSooraAyasItem(int sooraNum);

	/**
	 * @param sooraNum
	 *            soora number (counted from 1)
	 * @return <code>SooraProperties</code> of the given soora number
	 */
	public SooraProperties getSoora(int sooraNum);

	/**
	 * @param jozNum
	 *            joz number (counted from 1)
	 * @return <code>JozProperties</code> of the given joz number
	 */
	public JozProperties getJoz(int jozNum);

	/**
	 * @param sajdaNum
	 *            sajda number (counted from 1)
	 * @return <code>SajdaProperties</code> of the given sajda number.
	 */
	public SajdaProperties getSajda(int sajdaNum);
	
}

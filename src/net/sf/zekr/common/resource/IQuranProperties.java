/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 10, 2005
 */
package net.sf.zekr.common.resource;

/**
 * Initial interface for properties of the Holy Quran
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface IQuranProperties {

	/**
	 * @param suraNum sura number (counted from 1)
	 * @return <code>SuraProperties</code> of the given sura number
	 */
	public SuraProperties getSura(int suraNum);

	/**
	 * @param juzNum juz number (counted from 1)
	 * @return <code>JuzProperties</code> of the given juz number
	 */
	public JuzProperties getJuz(int juzNum);

	/**
	 * @param sajdaNum sajda number (counted from 1)
	 * @return <code>SajdaProperties</code> of the given sajda number.
	 */
	public SajdaProperties getSajda(int sajdaNum);

}

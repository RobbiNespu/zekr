/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 10, 2005
 */
package net.sf.zekr.common.util;

import net.sf.zekr.common.resource.QuranProperties;

/**
 * This data structure is the primitive structure of addressing somewhere in the Quran. Addressing is possible
 * by just having aya number and sura number. <br>
 * Note that this class does not provide any range checkhing or explicit exception throwing for performance
 * purposes.<br>
 * Both sura and aya numbers are counted from 1.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranLocation implements IQuranLocation {
	private int sura;
	private int aya;

	/**
	 * @param sura counted from 1
	 * @param aya counted from 1
	 */
	public QuranLocation(int sura, int aya) {
		setAya(aya);
		setSura(sura);
	}

	/**
	 * load a QuranLocation with the format <tt>sura#-aya#</tt>. Sura and Aya numbers are both counted from
	 * 1. If <code>location</code> is not of format <tt>sura#-aya#</tt>, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param location <code>location.toString()</code> will be used
	 * @throws IllegalArgumentException if <code>location</code> is not well-formed, ie. <tt>sura#-aya#</tt>
	 */
	public QuranLocation(String location) {
		int i = location.indexOf('-');
		if (i == -1)
			throw new IllegalArgumentException(location.toString());
		setSura(Integer.parseInt(location.substring(0, i)));
		setAya(Integer.parseInt(location.substring(i + 1)));
	}

	public int getAya() {
		return aya;
	}

	public void setAya(int aya) {
		this.aya = aya;
	}

	public int getSura() {
		return sura;
	}

	public void setSura(int sura) {
		this.sura = sura;
	}

	public String getSuraName() {
		QuranProperties qp = QuranProperties.getInstance();
		return qp.getSura(sura).name;
	}

	/**
	 * Makes a string presentation of this class: <tt>sura#-aya#</tt>
	 */
	public String toString() {
		return new StringBuffer("" + sura).append("-").append(aya).toString();
	}
}

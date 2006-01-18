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
 * This data structure is the primitive structure of addressing somewhere in the Quran.
 * Addressing is possible by just having aya number and sura number. <br>
 * Note that this class does not provide any range checkhing or explicit exception throwing
 * for performance purposes.
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

	public String toString() {
		return new StringBuffer("" + sura).append("-").append(aya).toString();
	}
}

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
 * Addressing is possible by just having aya number and soora number. <br>
 * Note that this class does not provide any range checkhing or explicit exception throwing
 * for performance purposes.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranLocation implements IQuranLocation {
	private int soora;
	private int aya;

	/**
	 * @param soora counted from 1
	 * @param aya counted from 1
	 */
	public QuranLocation(int soora, int aya) {
		setAya(aya);
		setSoora(soora);
	}

	public int getAya() {
		return aya;
	}

	public void setAya(int aya) {
		this.aya = aya;
	}

	public int getSoora() {
		return soora;
	}

	public void setSoora(int soora) {
		this.soora = soora;
	}

	public String getSooraName() {
		QuranProperties qp = QuranProperties.getInstance();
		return qp.getSoora(soora).name;
	}

	public String toString() {
		return new StringBuffer("" + soora).append("-").append(aya).toString();
	}
}

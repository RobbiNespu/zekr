/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2005
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.engine.language.LanguageEngine;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class JuzProperties {
	private int index;

	/** juz start sura number */
	private int suraNumber;

	/** juz start aya number */
	private int ayaNumber;

	/** an 8-item array for addressing locations of each hizb quads in a juz */
	private QuranLocation[] hizbQuarters = new QuranLocation[8];

	public int getAyaNumber() {
		return ayaNumber;
	}

	public void setAyaNumber(int ayaNumber) {
		this.ayaNumber = ayaNumber;
	}

	/**
	 * 1-base juz index number.
	 * 
	 * @return juz index
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSuraNumber() {
		return suraNumber;
	}

	public void setSuraNumber(int suraNumber) {
		this.suraNumber = suraNumber;
	}

	/**
	 * @param hizb can be either 1 or 2
	 * @param quad can be either 1, 2, 3, or 4
	 */
	public void setHizbQuarters(int hizb, int quad, QuranLocation loc) {
		hizbQuarters[(hizb - 1) * 4 + (quad - 1)] = loc;
	}

	/**
	 * @return an 8-item array for addressing locations of each hizb quads in a juz. For example item 0 is the
	 *         first quranter of Hizb 1, item 4 is the first quarter of Hizb 2, and item 7 is the third quarter
	 *         of Hizb 2.
	 */
	public QuranLocation[] getHizbQuarters() {
		return hizbQuarters;
	}

	public String toString() {
		return LanguageEngine.getInstance().getMeaning("JUZ") + ": " + getIndex();
	}
}

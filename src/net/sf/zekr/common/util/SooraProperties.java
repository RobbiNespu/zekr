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
 * This class is an hold attributes of a soora of the Holy Quran.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class SooraProperties {
	String name;
	int ayaCount;
	boolean madani;
	int index;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return number of ayas in the soora
	 */
	public int getAyaCount() {
		return ayaCount;
	}

	/**
	 * @param ayaCount
	 *            The ayaCount to set.
	 */
	public void setAyaCount(int ayaCount) {
		this.ayaCount = ayaCount;
	}

	/**
	 * @return <code>true</code> if <code>soora</code> is <i>madani </i> or
	 *         (otherwise) <code>false</code> if it is <i>makki </i>
	 */
	public boolean isMadani() {
		return madani;
	}

	/**
	 * @param madani
	 *            The madani to set.
	 */
	public void setMadani(boolean madani) {
		this.madani = madani;
	}

	/**
	 * @return absolute soora number (counted from 1)
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            The index to set.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}

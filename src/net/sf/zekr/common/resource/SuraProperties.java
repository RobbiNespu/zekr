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
 * This class holds attributes of a sura of the Holy Quran.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SuraProperties {
	String name;
	int ayaCount;
	boolean madani;
	int index;

	/**
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return number of ayas in the sura.
	 */
	public int getAyaCount() {
		return ayaCount;
	}

	/**
	 * @param ayaCount the ayaCount to set.
	 */
	public void setAyaCount(int ayaCount) {
		this.ayaCount = ayaCount;
	}

	/**
	 * @return <code>true</code> if <code>sura</code> is <i>Madani</i> or (otherwise) <code>false</code>
	 *         if it is <i>Makki</i>
	 */
	public boolean isMadani() {
		return madani;
	}

	/**
	 * @param madani The madani to set.
	 */
	public void setMadani(boolean madani) {
		this.madani = madani;
	}

	/**
	 * @return absolute sura number (counted from 1)
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index The index to set.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}

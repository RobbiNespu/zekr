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
 */
public class SuraProperties {
	private String name;
	private int ayaCount;
	private boolean madani;
	private int index;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAyaCount() {
		return ayaCount;
	}

	public void setAyaCount(int ayaCount) {
		this.ayaCount = ayaCount;
	}

	/**
	 * @return <code>true</code> if <code>sura</code> is <i>Madani</i> or (otherwise) <code>false</code> if it is
	 *         <i>Makki</i>
	 */
	public boolean isMadani() {
		return madani;
	}

	public void setMadani(boolean madani) {
		this.madani = madani;
	}

	/**
	 * @return absolute sura number (counted from 1)
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}

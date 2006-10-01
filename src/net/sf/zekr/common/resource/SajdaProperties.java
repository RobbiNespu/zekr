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
public class SajdaProperties {
	public static final int MINOR = 0;
	public static final int MAJOR = 1;

	/** sajda sura number */
	private int suraNumber;

	/** sajda aya number */
	private int ayaNumber;

	/** sajda type: MINOR or MAJOR */
	private int type;

	/** sajda index (the absolute number of the sajda, counted from 1) */
	private int index;

	/**
	 * @return the ayaNumber.
	 */
	public int getAyaNumber() {
		return ayaNumber;
	}

	/**
	 * @param ayaNumber the ayaNumber to set.
	 */
	public void setAyaNumber(int ayaNumber) {
		this.ayaNumber = ayaNumber;
	}

	/**
	 * @return Returns the suraNumber.
	 */
	public int getSuraNumber() {
		return suraNumber;
	}

	/**
	 * @param suraNumber the suraNumber to set.
	 */
	public void setSuraNumber(int suraNumber) {
		this.suraNumber = suraNumber;
	}

	/**
	 * @return the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the index.
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

	public String toString() {
		LanguageEngine l = LanguageEngine.getInstance();
		return type == MINOR ? l.getMeaning("MINOR_SAJDA") : l.getMeaning("MAJOR_SAJDA");
	}
}

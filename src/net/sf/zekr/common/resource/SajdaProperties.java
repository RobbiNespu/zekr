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
 */
public class SajdaProperties {
	public static final int MINOR = 0;
	public static final int MAJOR = 1;

	/** sajda sura number */
	private int suraNumber;

	/** sajda aya number */
	private int ayaNumber;

	/** sajda type: MINOR (Mustahab) or MAJOR (Wajib) */
	private int type;

	/** sajda index (the absolute number of the sajda, counted from 1) */
	private int index;

	public int getAyaNumber() {
		return ayaNumber;
	}

	public void setAyaNumber(int ayaNumber) {
		this.ayaNumber = ayaNumber;
	}

	public int getSuraNumber() {
		return suraNumber;
	}

	public void setSuraNumber(int suraNumber) {
		this.suraNumber = suraNumber;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String toString() {
		LanguageEngine l = LanguageEngine.getInstance();
		return type == MINOR ? l.getMeaning("MINOR_SAJDA") : l.getMeaning("MAJOR_SAJDA");
	}
}

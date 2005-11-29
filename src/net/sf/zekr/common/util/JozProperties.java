/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2005
 */
package net.sf.zekr.common.util;

import net.sf.zekr.engine.language.LanguageEngine;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class JozProperties {
	private int index;

	/** joz start soora number */
	private int sooraNumber;

	/** joz start aya number */
	private int ayaNumber;

	/**
	 * @return Returns the ayaNumber.
	 */
	public int getAyaNumber() {
		return ayaNumber;
	}

	/**
	 * @param ayaNumber
	 *            The ayaNumber to set.
	 */
	public void setAyaNumber(int ayaNumber) {
		this.ayaNumber = ayaNumber;
	}

	/**
	 * @return Returns the index.
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

	/**
	 * @return Returns the sooraNumber.
	 */
	public int getSooraNumber() {
		return sooraNumber;
	}

	/**
	 * @param sooraNumber
	 *            The sooraNumber to set.
	 */
	public void setSooraNumber(int sooraNumber) {
		this.sooraNumber = sooraNumber;
	}

	public String toString() {
		return LanguageEngine.getInstance().getMeaning("JOZ") + ": " + getIndex();
	}
}

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
public class SujdaProperties {
	public static final int MINOR = 0;
	public static final int MAJOR = 1;

	/** sujda soora number */
	private int sooraNumber;

	/** sujda aya number */
	private int ayaNumber;

	/** sujda type: MINOR or MAJOR */
	private int type;

	/** sujda index (the absolute number of the sujda, counted from 1) */
	private int index;

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

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
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
	
	public String toString(){
		LanguageEngine l = LanguageEngine.getInstance();
		return type == MINOR ? l.getMeaning("MINOR_SUJDA") : l.getMeaning("MAJOR_SUJDA");
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2005
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.engine.translation.TranslationData;

/**
 * A general interface for Qur'an text access. All classes/methods who need Qur'an access should have this
 * through <code>IQuranText</code> interface.<br>
 * This interface is applicable to translation as well as Qur'an.
 * 
 * @author Mohsen Saboorian
 */
public interface IQuranText {
	/** Uthmani Qur'an text */
	public static final int UTHMANI_MODE = 0;

	/** Simple (Imlaaei) Qur'an text */
	public static final int SIMPLE_MODE = 1;

	/**
	 * Returns the requested aya.
	 * 
	 * @param suraNum the sura number <b>counted from 1</b>
	 * @param ayaNum the aya number <b>counted from 1</b>
	 * @return returns <code>ayaNum</code>th aya of the <code>suraNum</code>th sura of the Quran.
	 */
	public String get(int suraNum, int ayaNum);

	/**
	 * Returns the requested absolute aya.
	 * 
	 * @param absoluteAyaNum absolute sura number counted from 1
	 * @return returns the requested aya
	 */
	public String get(int absoluteAyaNum);

	/**
	 * Returns the requested aya.
	 * 
	 * @param quranLocation sura-aya location of the Quran text to be retrieved
	 * @return returns requested Quran location
	 */
	public String get(IQuranLocation quranLocation);

	/**
	 * Returns the requested sura as an array of strings.<br>
	 * <b>Note:</b> no range check is performed.
	 * 
	 * @param suraNum indicates the sura number (which is counted from 1)
	 * @return a <code>String</code> array of the sura (#<code>suraNum</code>) ayas.
	 */
	public String[] getSura(int suraNum);

	/**
	 * @return Full text of the Quran/translation text as a 2D array.
	 */
	public String[][] getFullText();

	/**
	 * @return true if this is a {@link TranslationData} and false otherwise (if this is Arabic Qur'an text).
	 */
	public boolean isTranslation();

	/**
	 * @return language of this translation data (or "ar" if this is a Qur'an text).
	 */
	public String getLanguage();

	/**
	 * Return Bismillah (the beginning part of suras).<br />
	 * 
	 * @param suraNum sura number (1-based)
	 * @return Bismillah part
	 */
	public String getBismillah(int suraNum);

	/**
	 * Return Quran text mode.<br>
	 * <b>This method should only be implemented in subclasses which are aimed for Quran text.</b>
	 * 
	 * @return Quran text mode: {@link IQuranText#SIMPLE_MODE} or {@link IQuranText#UTHMANI_MODE}.
	 */
	public int getMode();
}

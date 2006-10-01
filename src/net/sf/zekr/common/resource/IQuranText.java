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
 * A general interface for Quran text access. All classes/methods who need Quran access should have this
 * through <code>IQuranText</code> interface.<br>
 * This interface is applicable to translation as well as Quran.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface IQuranText {

	/**
	 * Returns the requested aya.<br>
	 * <b>Note:</b> no range check is performed.
	 * 
	 * @param suraNum the sura number <b>counted from 1</b>
	 * @param ayaNum the aya number <b>counted from 1</b>
	 * @return returns <code>ayaNum</code>th aya of the <code>suraNum</code>th sura of the Quran.
	 */
	public String get(int suraNum, int ayaNum);

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
	 * This method should only be implemented with translation implementations of this interface.
	 */
	public TranslationData getTranslationData();
}

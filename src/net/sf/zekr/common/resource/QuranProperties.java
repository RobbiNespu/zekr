/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2005
 */
package net.sf.zekr.common.resource;

import java.util.List;

/**
 * @author Mohsen Saboorian
 */
public class QuranProperties implements IQuranProperties {
	private static QuranProperties thisInstance;
	QuranPropertiesReader quranPropsReader;

	private QuranProperties() {
		quranPropsReader = new QuranPropertiesReader();
	}

	public static QuranProperties getInstance() {
		if (thisInstance == null)
			thisInstance = new QuranProperties();
		return thisInstance;
	}

	public final List getSuraList() {
		return quranPropsReader.suraProp;
	}

	public final List getJuzList() {
		return quranPropsReader.juzProp;
	}

	public final List getSajdaList() {
		return quranPropsReader.sajdaProp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getSura(int)
	 */
	public SuraProperties getSura(int suraNum) {
		return (SuraProperties) quranPropsReader.suraProp.get(suraNum - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getJuz(int)
	 */
	public JuzProperties getJuz(int juzNum) {
		return (JuzProperties) quranPropsReader.juzProp.get(juzNum - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getSajda(int)
	 */
	public SajdaProperties getSajda(int sajdaNum) {
		return (SajdaProperties) quranPropsReader.sajdaProp.get(sajdaNum - 1);
	}
}

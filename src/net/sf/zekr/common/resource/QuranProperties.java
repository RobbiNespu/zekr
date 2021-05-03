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

	public final List<SuraProperties> getSuraList() {
		return quranPropsReader.suraProp;
	}

	public final List<JuzProperties> getJuzList() {
		return quranPropsReader.juzProp;
	}

	public final List<SajdaProperties> getSajdaList() {
		return quranPropsReader.sajdaProp;
	}

	public SuraProperties getSura(int suraNum) {
		return (SuraProperties) quranPropsReader.suraProp.get(suraNum - 1);
	}

	public JuzProperties getJuz(int juzNum) {
		return (JuzProperties) quranPropsReader.juzProp.get(juzNum - 1);
	}

	public SajdaProperties getSajda(int sajdaNum) {
		return (SajdaProperties) quranPropsReader.sajdaProp.get(sajdaNum - 1);
	}
}

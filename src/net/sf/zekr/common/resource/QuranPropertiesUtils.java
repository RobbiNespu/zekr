/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2005
 */
package net.sf.zekr.common.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.language.LanguageEngine;

/**
 * This class tightly depends on the class <code>QuranProperties</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranPropertiesUtils {
	private static String[][] suraAyas = new String[114][];
	private static String[] suraNames = new String[114];
	private static String[] indexedSuraNames = new String[114];
	private static List[] juzInside = new ArrayList[114];
	private static List[] sajdaInside = new ArrayList[114];

	public static final int getSajdaType(String sajda) {
		return QuranPropertiesNaming.MINOR_SAJDA.equalsIgnoreCase(sajda) ? SajdaProperties.MINOR
				: SajdaProperties.MAJOR;
	}

	public static final boolean isMadani(String descent) {
		return QuranPropertiesNaming.MADANI.equalsIgnoreCase(descent);
	}

	/**
	 * @param suraNum sura number (counted from 1)
	 * @return a String array of aya numbers in a sura. e.g. ["1", "2", "3", ...]
	 */
	public static final String[] getSuraAyas(int suraNum) {
		int ayas;
		if (suraAyas[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (int i = 0; i < 114; i++) {
				SuraProperties sura = props.getSura(i + 1);
				ayas = sura.getAyaCount();
				suraAyas[i] = new String[ayas];
				for (int j = 0; j < ayas; j++)
					suraAyas[i][j] = String.valueOf(j + 1);
			}
		}
		return suraAyas[suraNum - 1];
	}

	/**
	 * @return zero relative array of sura names
	 */
	public static final String[] getSuraNames() {
		if (suraNames[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSuraList().iterator(); iter.hasNext();) {
				SuraProperties element = (SuraProperties) iter.next();
				suraNames[element.getIndex() - 1] = element.getName();
			}
		}
		return suraNames;
	}

	/**
	 * @param suraNum (1-based)
	 * @return the <code>SuraProperties</code> object corresponding to <code>suraNum</code>
	 */
	public static final SuraProperties getSura(int suraNum) {
		QuranProperties props = QuranProperties.getInstance();
		return props.getSura(suraNum);
	}

	public static final String[] getIndexedSuraNames() {
		if (indexedSuraNames[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSuraList().iterator(); iter.hasNext();) {
				SuraProperties element = (SuraProperties) iter.next();
				indexedSuraNames[element.getIndex() - 1] = element.getIndex() + " - " + element.getName();
			}
		}
		return indexedSuraNames;
	}

	/**
	 * If there is any juz start within the sura, it will be returned. <br>
	 * This method is the same as <code>getJuzInside()</code>, but with a different return type.
	 * 
	 * @param suraNum sura number (counted from 1)
	 * @return a <code>List</code> of <code>JuzProperties</code>
	 */
	public static final List getJuzInsideList(int suraNum) {
		if (juzInside[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSuraList().iterator(); iter.hasNext();) {
				SuraProperties sura = (SuraProperties) iter.next();
				List juzNum = new ArrayList();
				List list = props.getJuzList();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JuzProperties juz = (JuzProperties) iterator.next();
					if (juz.getSuraNumber() == sura.getIndex())
						juzNum.add(juz);
				}
				juzInside[sura.getIndex() - 1] = juzNum;
			}
		}
		return juzInside[suraNum - 1];
	}

	/**
	 * If there is any juz start within the sura, it will be returned.
	 * 
	 * @param suraNum sura number (counted from 1)
	 * @return <code>int</code> array of juz numbers
	 */
	public static final int[] getJuzInside(int suraNum) {
		return getJuz(getJuzInsideList(suraNum));
	}

	private static final int[] getJuz(List list) {
		int[] juzArray = new int[list.size()];
		JuzProperties juz;
		for (int i = 0; i < list.size(); i++) {
			juz = (JuzProperties) list.get(i);
			juzArray[i] = juz.getIndex();
		}
		return juzArray;
	}

	public static final int getFirstJuzOf(int suraNum) {
		QuranProperties props = QuranProperties.getInstance();
		List list = props.getJuzList();

		Iterator iter = list.iterator();
		JuzProperties juz1 = (JuzProperties) iter.next();
		while (iter.hasNext()) {
			JuzProperties juz2 = (JuzProperties) iter.next();
			if (suraNum >= juz1.getSuraNumber() && suraNum <= juz2.getSuraNumber()) {
				if (juz2.getSuraNumber() == suraNum && juz2.getAyaNumber() == 1)
					return juz2.getIndex();
				return juz1.getIndex();
			}
			juz1 = juz2;
		}
		return juz1.getIndex(); // 30th juz
	}

	/**
	 * @param suraNum sura number (counted from 1)
	 * @return <code>List</code> of <code>SajdaProperties</code> inside <code>suraNum</code>
	 */
	public static final List getSajdaInsideList(int suraNum) {
		if (sajdaInside[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSuraList().iterator(); iter.hasNext();) {
				SuraProperties sura = (SuraProperties) iter.next();
				List sajdaList = new ArrayList();
				List list = props.getSajdaList();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					SajdaProperties sajda = (SajdaProperties) iterator.next();
					if (sajda.getSuraNumber() == sura.getIndex())
						sajdaList.add(sajda);
				}
				sajdaInside[sura.getIndex() - 1] = sajdaList;
			}
		}
		return sajdaInside[suraNum - 1];
	}

	/**
	 * @param suraNum sura number (counted from 1)
	 * @return Sura properties as a <code>Map</code>
	 */
	public static final Map getSuraPropMap(int suraNum) {
		LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();
		QuranPropertiesUtils.getSuraNames(); // load!
		SuraProperties sura = QuranProperties.getInstance().getSura(suraNum);
		Map map = new LinkedHashMap();
		map.put(dict.getMeaning("NAME"), suraNames[suraNum - 1]);
		map.put(dict.getMeaning("NUMBER"), new Integer(sura.getIndex()));
		map.put(dict.getMeaning("AYA_COUNT"), new Integer(sura.getAyaCount()));
		map.put(dict.getMeaning("TYPE"), getSuraDescent(sura.isMadani()));
		map.put(dict.getMeaning("JUZ"), getSuraJuz(suraNum));

		return map;
	}

	/**
	 * @param isMadani
	 * @return localized <code>String</code> of descent type of a sura (Makki or Madani)
	 */
	public static final String getSuraDescent(boolean isMadani) {
		LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();
		return isMadani ? dict.getMeaning("MADANI") : dict.getMeaning("MAKKI");
	}

	public static final String getSuraJuz(int suraNum) {
		int[] juzList = getJuzInside(suraNum);
		int firstJuz = getFirstJuzOf(suraNum);
		if (juzList.length == 0) {
			juzList = new int[1];
			juzList[0] = firstJuz;
		} else if (juzList[0] != firstJuz) {
			juzList = CollectionUtils.concat(new int[] { firstJuz }, juzList);
		}
		return CollectionUtils.getLocalizedList(juzList);
	}

}

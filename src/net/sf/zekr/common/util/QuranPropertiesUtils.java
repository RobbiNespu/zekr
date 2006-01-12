/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2005
 */
package net.sf.zekr.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesNaming;
import net.sf.zekr.engine.language.LanguageEngine;

/**
 * This class tightly depends on the class <code>QuranProperties</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranPropertiesUtils {
	private static String[][] sooraAyas = new String[114][];
	private static String[] sooraNames = new String[114];
	private static String[] indexedSooraNames = new String[114];
	private static List[] jozInside = new ArrayList[114];
	private static List[] sajdaInside = new ArrayList[114];

	public static int getSajdaType(String sajda) {
		return QuranPropertiesNaming.MINOR_SAJDA.equalsIgnoreCase(sajda) ? SajdaProperties.MINOR
				: SajdaProperties.MAJOR;
	}

	public static boolean isMadani(String descent) {
		return QuranPropertiesNaming.MADANI.equalsIgnoreCase(descent);
	}

	/**
	 * @param sooraNum soora number (counted from 1)
	 * @return a String array of aya numbers in a soora. e.g. ["1", "2", "3", ...]
	 */
	public static String[] getSooraAyas(int sooraNum) {
		int ayas;
		if (sooraAyas[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (int i = 0; i < 114; i++) {
				SooraProperties soora = props.getSoora(i + 1);
				ayas = soora.getAyaCount();
				sooraAyas[i] = new String[ayas];
				for (int j = 0; j < ayas; j++)
					sooraAyas[i][j] = String.valueOf(j + 1);
			}
		}
		return sooraAyas[sooraNum - 1];
	}

	/**
	 * @return zero relative array of soora names
	 */
	public static String[] getSooraNames() {
		if (sooraNames[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSooraList().iterator(); iter.hasNext();) {
				SooraProperties element = (SooraProperties) iter.next();
				sooraNames[element.getIndex() - 1] = element.getName();
			}
		}
		return sooraNames;
	}

	public static String[] getIndexedSooraNames() {
		if (indexedSooraNames[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSooraList().iterator(); iter.hasNext();) {
				SooraProperties element = (SooraProperties) iter.next();
				indexedSooraNames[element.getIndex() - 1] = element.getIndex() + " - " + element.getName();
			}
		}
		return indexedSooraNames;
	}

	/**
	 * If there is any joz start within the soora, it will be returned. <br>
	 * This method is the same as <code>getJozInside</code>, but with a different
	 * return type.
	 * 
	 * @param sooraNum soora number (counted from 1)
	 * @return a <code>List</code> of JozProperties
	 */
	public static List getJozInsideList(int sooraNum) {
		if (jozInside[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSooraList().iterator(); iter.hasNext();) {
				SooraProperties soora = (SooraProperties) iter.next();
				List jozNum = new ArrayList();
				List list = props.getJozList();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JozProperties joz = (JozProperties) iterator.next();
					if (joz.getSooraNumber() == soora.getIndex())
						jozNum.add(joz);
				}
				jozInside[soora.getIndex() - 1] = jozNum;
			}
		}
		return jozInside[sooraNum - 1];
	}

	/**
	 * If there is any joz start within the soora, it will be returned.
	 * 
	 * @param sooraNum soora number (counted from 1)
	 * @return <code>int</code> array of joz numbers
	 */
	public static int[] getJozInside(int sooraNum) {
		if (jozInside[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSooraList().iterator(); iter.hasNext();) {
				SooraProperties soora = (SooraProperties) iter.next();
				List jozList = new ArrayList();
				List list = props.getJozList();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JozProperties joz = (JozProperties) iterator.next();
					if (joz.getSooraNumber() == soora.getIndex())
						jozList.add(joz);
				}
				jozInside[soora.getIndex() - 1] = jozList;
			}
		}
		return getJoz(jozInside[sooraNum - 1]);
	}

	private static int[] getJoz(List list) {
		int[] jozArray = new int[list.size()];
		JozProperties joz;
		for (int i = 0; i < list.size(); i++) {
			joz = (JozProperties) list.get(i);
			jozArray[i] = joz.getIndex();
		}
		return jozArray;
	}

	public static int getFirstJozOf(int sooraNum) {
		QuranProperties props = QuranProperties.getInstance();
		List list = props.getJozList();

		Iterator iter = list.iterator();
		JozProperties joz1 = (JozProperties) iter.next();
		while (iter.hasNext()) {
			JozProperties joz2 = (JozProperties) iter.next();
			if (sooraNum >= joz1.getSooraNumber() && sooraNum <= joz2.getSooraNumber()) {
				if (joz2.getSooraNumber() == sooraNum && joz2.getAyaNumber() == 1)
					return joz2.getIndex();
				return joz1.getIndex();
			}
			joz1 = joz2;
		}
		return joz1.getIndex(); // 30th joz
	}

	/**
	 * @param sooraNum soora number (counted from 1)
	 * @return <code>List</code> of <code>SajdaProperties</code> inside
	 *         <code>sooraNum</code>
	 */
	public static List getSajdaInsideList(int sooraNum) {
		if (sajdaInside[0] == null) { // not loaded yet
			QuranProperties props = QuranProperties.getInstance();
			for (Iterator iter = props.getSooraList().iterator(); iter.hasNext();) {
				SooraProperties soora = (SooraProperties) iter.next();
				List sajdaList = new ArrayList();
				List list = props.getSajdaList();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					SajdaProperties sajda = (SajdaProperties) iterator.next();
					if (sajda.getSooraNumber() == soora.getIndex())
						sajdaList.add(sajda);
				}
				sajdaInside[soora.getIndex() - 1] = sajdaList;
			}
		}
		return sajdaInside[sooraNum - 1];
	}

	/**
	 * @param sooraNum soora number (counted from 1)
	 * @return Soora properties as a <code>Map</code>
	 */
	public static Map getSooraPropMap(int sooraNum) {
		LanguageEngine dict = ApplicationConfig.getInsatnce().getLanguageEngine();
		QuranPropertiesUtils.getSooraNames(); // load!
		SooraProperties soora = QuranProperties.getInstance().getSoora(sooraNum);
		Map map = new HashMap();
		map.put(dict.getMeaning("NUMBER"), new Integer(soora.getIndex()));
		map.put(dict.getMeaning("NAME"), sooraNames[sooraNum - 1]);
		map.put(dict.getMeaning("TYPE"), getSooraDescent(soora.isMadani()));
		map.put(dict.getMeaning("AYA_COUNT"), new Integer(soora.getAyaCount()));
		map.put(dict.getMeaning("JOZ"), getSooraJoz(sooraNum));

		return map;
	}

	/**
	 * @param isMadani
	 * @return localized <code>String</code> of descent type of a soora (Makki or
	 *         Madani)
	 */
	public static String getSooraDescent(boolean isMadani) {
		LanguageEngine dict = ApplicationConfig.getInsatnce().getLanguageEngine();
		return isMadani ? dict.getMeaning("MADANI") : dict.getMeaning("MAKKI");
	}

	public static String getSooraJoz(int sooraNum) {
		int[] jozList = getJozInside(sooraNum);
		int firstJoz = getFirstJozOf(sooraNum);
		if (jozList.length == 0) {
			jozList = new int[1];
			jozList[0] = firstJoz;
		} else if (jozList[0] != firstJoz) {
			jozList = CollectionUtils.concat(new int[] { firstJoz }, jozList);
		}
		return CollectionUtils.getLocalizedList(jozList);
	}

}

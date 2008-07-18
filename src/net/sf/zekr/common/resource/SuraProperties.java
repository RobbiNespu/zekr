/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2005
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.common.config.ApplicationConfig;

/**
 * This class holds attributes of a sura of the Holy Quran.
 * 
 * @author Mohsen Saboorian
 */
public class SuraProperties {
	static String l10nName[] = new String[114];
	static String l10nTransliterate[] = new String[114];

	private String name;
	private String englishTrans;
	private String englishT13N;
	private int ayaCount;
	private boolean madani;
	private int index;

	public String getName() {
		return name;
	}

	/**
	 * Set Arabic sura name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishTrans() {
		return englishTrans;
	}

	/**
	 * Set English translation of sura name.
	 * 
	 * @param englishName
	 */
	public void setEnglishTrans(String englishName) {
		this.englishTrans = englishName;
	}

	public String getEnglishT13N() {
		return englishT13N;
	}

	/**
	 * Set English transliteration of sura name.
	 * 
	 * @param englishT13N
	 */
	public void setEnglishT13N(String englishT13N) {
		this.englishT13N = englishT13N;
	}

	public String getLocalizedTrans() {
		return l10nName[index - 1];
	}

	public String getLocalizedT13N() {
		return l10nTransliterate[index - 1];
	}

	public int getAyaCount() {
		return ayaCount;
	}

	public void setAyaCount(int ayaCount) {
		this.ayaCount = ayaCount;
	}

	/**
	 * @return <code>true</code> if <code>sura</code> is <i>Madani</i> or (otherwise) <code>false</code> if it
	 *         is <i>Makki</i>
	 */
	public boolean isMadani() {
		return madani;
	}

	public void setMadani(boolean madani) {
		this.madani = madani;
	}

	/**
	 * @return absolute sura number (counted from 1)
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * There are currently 5 modes to show sura name. These modes are specified in Zekr configuration
	 * properties under <tt>view.sura.name</tt> property.
	 * <ol>
	 * <li>{@link QuranPropertiesNaming#SURA_NAME_ARABIC}</li>
	 * <li>{@link QuranPropertiesNaming#SURA_NAME_ENGLISH_T9N}</li>
	 * <li>{@link QuranPropertiesNaming#SURA_NAME_ENGLISH_T13N}</li>
	 * <li>{@link QuranPropertiesNaming#SURA_NAME_T9N}</li>
	 * <li>{@link QuranPropertiesNaming#SURA_NAME_T13N}</li>
	 * </ol>
	 * 
	 * @return the sura name, in the format specified by <tt>view.sura.name</tt> properties.
	 */
	public String toText() {
		ApplicationConfig config = ApplicationConfig.getInstance();
		String suraNameMode = config.getProps().getString("view.sura.name");
		if (QuranPropertiesNaming.SURA_NAME_ARABIC.equals(suraNameMode)) {
			return getName();
		} else if (QuranPropertiesNaming.SURA_NAME_ENGLISH_T9N.equals(suraNameMode)) {
			return getEnglishTrans();
		} else if (QuranPropertiesNaming.SURA_NAME_ENGLISH_T13N.equals(suraNameMode)) {
			return getEnglishT13N();
		} else if (QuranPropertiesNaming.SURA_NAME_T9N.equals(suraNameMode)) {
			return getLocalizedTrans();
		} else if (QuranPropertiesNaming.SURA_NAME_T13N.equals(suraNameMode)) {
			return getLocalizedT13N();
		}
		return getEnglishTrans();
	}
}

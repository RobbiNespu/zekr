/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 1, 2007
 */

package net.sf.zekr.common.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class I18N {
	private final static Map baseMap;
	private Locale locale;

	private static I18N thisInstance;
	static {
		baseMap = new HashMap();
		baseMap.put("fa", new Integer(0x6f0)); // Farsi (Persian)
		baseMap.put("ur", new Integer(0x6f0)); // Urdu (numbers are the same as Persian)
		baseMap.put("ar", new Integer(0x660)); // Indo-Arabic: SA, Syria, Egypt, UAE, Iraq, ...
		baseMap.put("th", new Integer(0xe50)); // Thai: Thailand
		baseMap.put("gu", new Integer(0xae6)); // Gujarati: India
		baseMap.put("bn", new Integer(0x9e6)); // Bengali: India
		baseMap.put("bo", new Integer(0xf20)); // Tibetan: Tibet
		baseMap.put("lo", new Integer(0xed0)); // Lao: Laos
	}

	public I18N(Locale locale) {
		this.locale = locale;
	}

	public I18N() {
		this(Locale.getDefault());
	}

	/**
	 * This method makes a localized number based on the input number and selected or default locale.
	 * 
	 * @param number
	 *           the input number
	 * @return localized number as <code>String</code>
	 */
	public String localize(Number number) {
		return localize(number, locale.getLanguage());
	}

	/**
	 * This method makes a localized number based on the input number and the locale parameter.
	 * 
	 * @param number
	 *           the input number
	 * @param langCode
	 *           target language code (e.g. en, fr, ar, ...) to be used for making numbers
	 * @return localized number as <code>String</code>
	 */
	public String localize(Number number, String langCode) {
		String s = number.toString();
		int base;
		if (baseMap.get(langCode) != null)
			base = ((Integer) baseMap.get(langCode)).intValue();
		else
			return number.toString();

		StringBuffer ret = new StringBuffer();
		char[] sc = s.toCharArray();
		for (int i = 0; i < sc.length; i++) {
			if (sc[i] > '9' || sc[i] < '0')
				ret.append(sc[i]);
			else
				ret.append((char) (base + (sc[i] - '0')));
		}
		return ret.toString();
	}

	/**
	 * @return the <code>java.util.Locale</code> instance used for encoding withing this object.
	 */
	public Locale getLocale() {
		return locale;
	}
}

/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 2, 2007
 */
package net.sf.zekr.engine.audio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Mohsen Saboorian
 */
public class AudioData implements Comparable<AudioData> {
	private final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy", new Locale("en", "US"));
	/**
	 * Smallest version which is still valid for this build. For example It may be Zekr 0.8.0, but if this
	 * constant is 0.7.5, then 0.7.5 is also an acceptable recitation pack.
	 */
	public static final String BASE_VALID_VERSION = "0.7.5";
	public String id;
	/** Recitation file format version */
	public String version;
	public String lastUpdate;

	public String name;
	public String license;
	public String reciter;
	public String type;
	/*** A map of language ISO code to localized name of reciter in that language */
	public Map<String, String> reciterLocalizedName = new HashMap<String, String>();

	public Locale locale;

	public String onlineAudhubillah;
	public String onlineBismillah;
	public String onlineSadaghallah;

	public String offlineAudhubillah;
	public String offlineBismillah;
	public String offlineSadaghallah;

	public String onlineUrl;
	public String offlineUrl;

	public String getReciter(String langCode) {
		return reciterLocalizedName.get(langCode) == null ? reciter : reciterLocalizedName.get(langCode);
	}

	public String getOnlineUrl(int sura, int aya) {
		return String.format(onlineUrl, sura, aya);
	}

	public String getOfflineUrl(int sura, int aya) {
		return String.format(offlineUrl, sura, aya);
	}

	public String toString() {
		return String.format("%s - %s", name, reciter);
	}

	public int compareTo(AudioData o) {
		try {
			return DATE_FORMATTER.parse(this.lastUpdate).compareTo(DATE_FORMATTER.parse(o.lastUpdate));
		} catch (ParseException e) {
			return 0;
		}
	}
}

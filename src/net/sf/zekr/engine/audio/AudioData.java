/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 2, 2007
 */
package net.sf.zekr.engine.audio;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Mohsen Saboorian
 */
public class AudioData {
	private String id;
	private String name;
	private String reciter;
	/**
	 * A map of language ISO code to localized name of reciter in that language
	 */
	private Map<String, String> reciterLocalizedName = new HashMap<String, String>();
	private String license;
	private Locale locale;

	private String audhubillah;
	private String bismillah;
	private String sadaghallah;

	private String onlineUrl;
	private String offlineUrl;

	public AudioData() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReciter() {
		return reciter;
	}

	public void setReciter(String reciter) {
		this.reciter = reciter;
	}

	public String getReciter(String langCode) {
		return reciterLocalizedName.get(langCode) == null ? reciter : reciterLocalizedName.get(langCode);
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getAudhubillah() {
		return audhubillah;
	}

	public void setAudhubillah(String audhubillah) {
		this.audhubillah = audhubillah;
	}

	public String getBismillah() {
		return bismillah;
	}

	public void setBismillah(String bismillah) {
		this.bismillah = bismillah;
	}

	public String getSadaghallah() {
		return sadaghallah;
	}

	public void setSadaghallah(String sadaghallah) {
		this.sadaghallah = sadaghallah;
	}

	public String getOnlineUrl() {
		return onlineUrl;
	}

	public void setOnlineUrl(String onlineUrl) {
		this.onlineUrl = onlineUrl;
	}

	public String getOfflineUrl() {
		return offlineUrl;
	}

	public void setOfflineUrl(String offlineUrl) {
		this.offlineUrl = offlineUrl;
	}

	public Map<String, String> getReciterLocalizedName() {
		return reciterLocalizedName;
	}

	public void setReciterLocalizedName(Map<String, String> reciterLocalizedName) {
		this.reciterLocalizedName = reciterLocalizedName;
	}

	public String getOnlineUrl(int sura, int aya) {
		return String.format(onlineUrl, sura, aya);
	}

	public String getOfflineUrl(int sura, int aya) {
		return String.format(offlineUrl, sura, aya);
	}

	public String toString() {
		return String.format("%s - %s", getName(), getReciter());
	}
}
